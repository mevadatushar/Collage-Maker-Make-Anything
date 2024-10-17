package com.collagemaker_makeanything.Activity

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.collagemaker_makeanything.R
import com.collagemaker_makeanything.databinding.ActivityMultiFitBinding

class MultiFitActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMultiFitBinding
    private val selectedImages = mutableListOf<Uri>() // List to hold selected image URIs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize the ViewBinding
        binding = ActivityMultiFitBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
    }

    private fun initView() {
        // Retrieve the selected images from the intent
        val imageUris = intent.getStringArrayListExtra("imageUris")
        imageUris?.let {
            for (uriString in it) {
                selectedImages.add(Uri.parse(uriString))
            }
        }

        // Loop through each selected image URI
        for (imageUri in selectedImages) {
            // Dynamically create an ImageView
            val newImageView = ImageView(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, // Changed from WRAP_CONTENT to specific size
                    LinearLayout.LayoutParams.MATCH_PARENT
                ).apply {
                    setMargins(40, 0, 40, 0) // Adjust margins if necessary
                }
                scaleType = ImageView.ScaleType.CENTER_INSIDE
            }

            // Add a placeholder and error handling for Glide image loading
            Glide.with(this)
                .load(imageUri)
              //  .placeholder(R.drawable.placeholder_image) // Placeholder while loading
              //  .error(R.drawable.error_image) // Error image if loading fails
                .into(newImageView)

            // Add the new ImageView to the LinearLayout using binding
            binding.backgroundImageView.addView(newImageView)

            // Log the URI to verify that images are being processed
            Log.e("selectedImages", "initView: $imageUri")
        }
    }


    fun onChangeBackgroundClick(view: View) {
        // Logic for changing the background color
    }
}
