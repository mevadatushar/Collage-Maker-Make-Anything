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
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
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
    private var imagePickLimit = 100  // Default limit is 100
    private lateinit var modeType: String  // Add a variable for mode type


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

        // Get the image pick limit from the intent
        imagePickLimit = intent.getIntExtra("imagePickLimit", 100)  // Default is 100

        modeType = intent.getStringExtra("modeType") ?: "collage"  // Default to "collage" mode if not provided

        initView()
    }

    private fun initView() {
        binding.backButton.setOnClickListener {
            onBackPressed()
        }

        binding.imageDelete.setOnClickListener {
            clearSelectedImages()
        }

        progressBar = binding.progressBar

//        binding.relativeNextButton.setOnClickListener {
//            val intent = Intent(this, CollagePhotoEditingActivity::class.java)
//            intent.putStringArrayListExtra("imageUris", ArrayList(selectedImagesList.map { it.toString() }))
//            Log.d(TAG, "initView:${selectedImagesList}")
//            startActivity(intent)
//            finish()
//        }

        binding.relativeNextButton.setOnClickListener {
            // Check which mode we are in
            if (modeType == "collage") {
                // If the user is in collage mode, go to CollagePhotoEditingActivity
                val intent = Intent(this, CollagePhotoEditingActivity::class.java)
                intent.putStringArrayListExtra("imageUris", ArrayList(selectedImagesList.map { it.toString() }))
                startActivity(intent)
            } else if (modeType == "multifit") {
                // If the user is in multifit mode, go to MultifitActivity
                val intent = Intent(this, MultiFitActivity::class.java)
                intent.putStringArrayListExtra("imageUris", ArrayList(selectedImagesList.map { it.toString() }))
                startActivity(intent)
            }
            finish()
        }


        val galleryGrid: GridView = findViewById(R.id.gallery_grid)
        adapter = ImagePickerAdapter(this, imageList, selectedImagesList)
        galleryGrid.adapter = adapter

//        galleryGrid.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
//            if (position == 0) {
//                checkCameraPermissionAndOpenCamera()
//            } else {
//                val actualPosition = position - 1
//                val selectedUri = imageList[actualPosition]
//
//                if (selectedImagesList.contains(selectedUri)) {
//                    selectedImagesList.remove(selectedUri)
//                    Log.d(TAG, "Image removed: $selectedUri")
//                } else {
//                    selectedImagesList.add(selectedUri)
//                    Log.d(TAG, "Image added: $selectedUri")
//                }
//
//                adapter.notifyDataSetChanged()
//                selectedImagesAdapter.notifyDataSetChanged()
//
//                updateNextButtonVisibility()
//                updateImageCount()
//            }
//        }

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
                    // Check if the pick limit is reached
                    if (selectedImagesList.size < imagePickLimit) {
                        selectedImagesList.add(selectedUri)
                        Log.d(TAG, "Image added: $selectedUri")
                    } else {
                        // Show a toast message that the limit is reached
                        Toast.makeText(this, "You can only select up to $imagePickLimit images.", Toast.LENGTH_SHORT).show()
                        Log.d(TAG, "Image pick limit reached: $imagePickLimit")
                    }
                }

                adapter.notifyDataSetChanged()
                selectedImagesAdapter.notifyDataSetChanged()

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
        selectedImagesRecyclerView.layoutManager = GridLayoutManager(this, 4, LinearLayoutManager.VERTICAL, false)
    }

    private fun removeSelectedImage(imageUri: Uri) {
        selectedImagesList.remove(imageUri)
        selectedImagesAdapter.notifyDataSetChanged()
        adapter.notifyDataSetChanged()
        updateNextButtonVisibility()
        updateImageCount()
    }

    private fun clearSelectedImages() {
        // Clear the selected images list
        selectedImagesList.clear()

        // Notify both adapters to update the views
        selectedImagesAdapter.notifyDataSetChanged()  // Updates the RecyclerView
        adapter.notifyDataSetChanged()  // Updates the GridView to remove checkmarks

        // Update UI elements to reflect the cleared selection
        updateNextButtonVisibility()
        updateImageCount()
    }

    private fun updateNextButtonVisibility() {
        if (selectedImagesList.isEmpty()) {
            binding.relativeNextButton.visibility = View.GONE
        } else {
            binding.relativeNextButton.visibility = View.VISIBLE
        }
    }

//    private fun updateImageCount() {
//        val count = selectedImagesList.size
//        binding.imageCountTextView.text = "Select 1 - 100 Photos ($count)"
//    }


    private fun updateImageCount() {
        val count = selectedImagesList.size
        // If no images are selected, always show "Select 1 - [imagePickLimit]"
        if (count == 0) {
            binding.imageCountTextView.text = "Select 1 - $imagePickLimit Photos"
        } else {
            binding.imageCountTextView.text = "Select 1 - $imagePickLimit Photos ($count)"
        }
    }



    private fun checkCameraPermissionAndOpenCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION)
        } else {
            openCamera()
        }
    }

    private fun openCamera() {
        val photoFile: File = createImageFile()
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
        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
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
        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
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