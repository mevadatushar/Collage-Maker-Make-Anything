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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.collagemaker_makeanything.R
import com.collagemaker_makeanything.databinding.ActivityEditImagePickerBinding
import com.example.photoeditor.beautycamera.EditImagePickerAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EditImagePickerActivity : BaseActivity() {

    lateinit var binding: ActivityEditImagePickerBinding
    private lateinit var progressBar: ProgressBar
    private val imageList = arrayListOf<Uri>()
    private lateinit var adapter: EditImagePickerAdapter

    companion object {
        const val TAG = "CollageImageSelect"
        const val REQUEST_PERMISSIONS_CODE = 1
        const val REQUEST_CAMERA_CODE = 2
        const val REQUEST_CAMERA_PERMISSION = 3
        var currentPhotoPath: String? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditImagePickerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    private fun initView() {
        binding.backButton.setOnClickListener {
            onBackPressed()
        }

        progressBar = binding.progressBar

        val galleryGrid: GridView = findViewById(R.id.gallery_grid)
        adapter = EditImagePickerAdapter(this, imageList)
        galleryGrid.adapter = adapter

        galleryGrid.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            if (position == 0) {
                checkCameraPermissionAndOpenCamera()
            } else {
                val actualPosition = position - 1
                val selectedUri = imageList[actualPosition]

                val intent = Intent(this, EditActivity::class.java)
                intent.putExtra("imageUri", selectedUri.toString())
                Log.d(TAG, "Selected image URI: $selectedUri")
                startActivity(intent)
                finish()
            }
        }

        if (arePermissionsGranted()) {
            loadImagesFromGallery()
        } else {
            requestPermissions()
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

            val intent = Intent(this, EditActivity::class.java)
            intent.putExtra("imageUri", imageUri.toString())
            startActivity(intent)
            finish()
        }
    }
}
