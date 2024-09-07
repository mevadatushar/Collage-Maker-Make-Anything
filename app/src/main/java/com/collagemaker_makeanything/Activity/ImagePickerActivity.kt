package com.collagemaker_makeanything.Activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.GridView
import android.widget.ProgressBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.collagemaker_makeanything.R
import com.collagemaker_makeanything.databinding.ActivityImagePickerBinding
import com.example.photoeditor.beautycamera.ImagePickerAdapter
import com.example.photoeditor.beautycamera.collagemaker.SelectedImagesAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ImagePickerActivity : BaseActivity() {

    lateinit var binding: ActivityImagePickerBinding

    private lateinit var progressBar: ProgressBar
    private val imageList = arrayListOf<Uri>()
    private val selectedImagesList = arrayListOf<Uri>()
    private lateinit var adapter: ImagePickerAdapter
    private lateinit var selectedImagesAdapter: SelectedImagesAdapter

    companion object {
        const val TAG = "CollageImageSelect"
        const val REQUEST_PERMISSIONS_CODE = 1
        const val REQUEST_CAMERA_CODE = 2
        const val REQUEST_CAMERA_PERMISSION = 3
        var currentPhotoPath: String? = null

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImagePickerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    private fun initView() {
        binding.backButton.setOnClickListener {
            onBackPressed()
        }

        binding.imageDelete.setOnClickListener {
            clearSelectedImages()
        }

        // Initialize ProgressBar
        progressBar = binding.progressBar // Ensure you have a ProgressBar with this ID in your layout file



           binding.relativeNextButton.setOnClickListener {
               val intent = Intent(this, CollagePhotoEditingActivity::class.java)
               intent.putStringArrayListExtra("imageUris", ArrayList(selectedImagesList.map { it.toString() }))
               Log.d(TAG, "initView:${selectedImagesList}")
               startActivity(intent)
               finish()
           }

        val galleryGrid: GridView = findViewById(R.id.gallery_grid)
        adapter = ImagePickerAdapter(this, imageList, selectedImagesList)
        galleryGrid.adapter = adapter

        galleryGrid.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            if (position == 0) {
                checkCameraPermissionAndOpenCamera()
            } else {
                val actualPosition = position - 1
                val selectedUri = imageList[actualPosition]

                if (selectedImagesList.contains(selectedUri)) {
                    selectedImagesList.remove(selectedUri)
                    Log.d(TAG, "Image removed: $selectedUri")
                } else {
                    selectedImagesList.add(selectedUri)
                    Log.d(TAG, "Image added: $selectedUri")
                }

                // Notify adapters of the changes
                adapter.notifyDataSetChanged()
                selectedImagesAdapter.notifyDataSetChanged()

                // Update the UI
                updateNextButtonVisibility()
                updateImageCount()
            }
        }

        if (arePermissionsGranted()) {
            loadImagesFromGallery()
        } else {
            requestPermissions()
        }

        val selectedImagesRecyclerView: RecyclerView = findViewById(R.id.selected_image_recycler_view)
        selectedImagesAdapter = SelectedImagesAdapter(this, selectedImagesList) { imageUri ->
            removeSelectedImage(imageUri)
        }
        selectedImagesRecyclerView.adapter = selectedImagesAdapter
        selectedImagesRecyclerView.layoutManager = GridLayoutManager(this,
            4, LinearLayoutManager.VERTICAL, false)
    }

    private fun removeSelectedImage(imageUri: Uri) {
        selectedImagesList.remove(imageUri)
        selectedImagesAdapter.notifyDataSetChanged()
        adapter.notifyDataSetChanged() // Notify the PhotosAdapter to update the GridView
        updateNextButtonVisibility()
        updateImageCount()
    }

    private fun clearSelectedImages() {
        selectedImagesList.clear()
        selectedImagesAdapter.notifyDataSetChanged()
        updateNextButtonVisibility()
        updateImageCount()
        removeSelectedImage(Uri.EMPTY)

    }

    private fun updateNextButtonVisibility() {
        if (selectedImagesList.isEmpty()) {
            binding.relativeNextButton.visibility = View.GONE
        } else {
            binding.relativeNextButton.visibility = View.VISIBLE
        }
    }

    private fun updateImageCount() {
        val count = selectedImagesList.size
        binding.imageCountTextView.text = "Select 1 - 100 Photos ($count)"
    }

    private fun checkCameraPermissionAndOpenCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION)
        } else {
            openCamera()
        }
    }

    private fun openCamera() {
        val photoFile: File = createImageFile() // Method to create image file
        val photoURI: Uri = FileProvider.getUriForFile(
            this,
            "${applicationContext.packageName}.fileprovider",
            photoFile
        )
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
        }
        if (cameraIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(cameraIntent, REQUEST_CAMERA_CODE)
        }
    }

    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            currentPhotoPath = absolutePath
        }
    }

    private fun loadImagesFromGallery() {
        progressBar.visibility = View.VISIBLE

        CoroutineScope(Dispatchers.IO).launch {
            val imageUris = getImageUris()
            withContext(Dispatchers.Main) {
                progressBar.visibility = View.GONE
                imageList.addAll(imageUris)
                adapter.notifyDataSetChanged()
            }
        }
    }

    private fun getImageUris(): List<Uri> {
        val uriList = mutableListOf<Uri>()
        val projection = arrayOf(MediaStore.Images.Media._ID)
        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"
        val query = contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            sortOrder
        )

        query?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val uri = Uri.withAppendedPath(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    id.toString()
                )
                uriList.add(uri)
            }
        }
        return uriList
    }

    private fun arePermissionsGranted(): Boolean {
        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        } else {
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }
        return permissions.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestPermissions() {
        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        } else {
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }
        ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSIONS_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_PERMISSIONS_CODE -> {
                if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                    loadImagesFromGallery()
                }
            }
            REQUEST_CAMERA_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CAMERA_CODE && resultCode == RESULT_OK) {
            val file = File(currentPhotoPath)
            val imageUri = Uri.fromFile(file)
            imageList.add(0, imageUri)
            adapter.notifyDataSetChanged()

            selectedImagesList.add(0, imageUri)
            selectedImagesAdapter.notifyDataSetChanged()

            updateNextButtonVisibility()
            updateImageCount()
        }
    }




}