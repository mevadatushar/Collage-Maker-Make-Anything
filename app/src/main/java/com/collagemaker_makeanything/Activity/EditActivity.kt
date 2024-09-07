package com.collagemaker_makeanything.Activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.collagemaker_makeanything.R
import com.collagemaker_makeanything.databinding.ActivityEditBinding

class EditActivity : BaseActivity() {
    lateinit var binding: ActivityEditBinding
    private lateinit var imageUri: String
    private var croppedImageUri: String? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Receive the data from the previous activity
        imageUri = intent.getStringExtra("imageUri") ?: ""
        croppedImageUri = intent.getStringExtra("croppedImageUri")

        initView()
    }

    private fun initView() {
        loadSelectedImage()

        with(binding) {

            imgBack.setOnClickListener {
                onBackPressed()
            }

            rlCrop.setOnClickListener {
                val intent = Intent(this@EditActivity, CropActivity::class.java)
                intent.putExtra("imageUri", croppedImageUri ?: imageUri)
                startActivityForResult(intent, CROP_IMAGE_REQUEST_CODE)
            }

            rlFrame.setOnClickListener {
                val intent = Intent(this@EditActivity , FrameActivity::class.java)
                intent.putExtra("imageUri", croppedImageUri ?: imageUri)
                startActivityForResult(intent, CROP_IMAGE_REQUEST_CODE)
            }

//            rlFilter.setOnClickListener {
//                val intent = Intent(this@EditActivity , FilterActivity::class.java)
//                intent.putExtra("imageUri", croppedImageUri ?: imageUri)
//                startActivityForResult(intent, CROP_IMAGE_REQUEST_CODE)
//            }

            rlFilter.setOnClickListener {
                val intent = Intent(this@EditActivity, FilterActivity::class.java)
                intent.putExtra("imageUri", croppedImageUri ?: imageUri)
                startActivityForResult(intent, FILTER_IMAGE_REQUEST_CODE)
            }

        }
    }

    private fun loadSelectedImage() {
        val uriToLoad = croppedImageUri ?: imageUri
        if (uriToLoad.isNotEmpty()) {
            Log.d("EditActivity", "Loading image URI: $uriToLoad")
            Glide.with(this)
                .load(uriToLoad)
                .into(binding.collageContainer)
        } else {
            Log.d("EditActivity", "No image URI found.")
            Toast.makeText(this, "No image found to load", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CROP_IMAGE_REQUEST_CODE && resultCode == RESULT_OK) {
            croppedImageUri = data?.getStringExtra("croppedImageUri")
            loadSelectedImage()
        } else if (requestCode == FILTER_IMAGE_REQUEST_CODE && resultCode == RESULT_OK) {
            croppedImageUri = data?.getStringExtra("filteredImageUri")
            loadSelectedImage()
        }
    }

    companion object {
        private const val CROP_IMAGE_REQUEST_CODE = 1001
        private const val FILTER_IMAGE_REQUEST_CODE = 1002

    }
}
