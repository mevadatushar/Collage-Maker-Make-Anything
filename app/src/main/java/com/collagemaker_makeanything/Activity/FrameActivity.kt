package com.collagemaker_makeanything.Activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.collagemaker_makeanything.R
import com.collagemaker_makeanything.databinding.ActivityFrameBinding

class FrameActivity : BaseActivity() {
    lateinit var binding: ActivityFrameBinding

     lateinit var imageUris: ArrayList<String>
    private var croppedImageUri: String? = null
    private lateinit var imageUri: String


    private var layoutRes: Int = R.layout.item_gallery_image

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFrameBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // Receive the data from the previous activity
        imageUris = intent.getStringArrayListExtra("imageUris") ?: arrayListOf()
        layoutRes = intent.getIntExtra("layoutRes", R.layout.item_gallery_image)

        imageUri = intent.getStringExtra("imageUri") ?: ""
    //    croppedImageUri = intent.getStringExtra("croppedImageUri")


        initViews()

        // Load the collage layout with the received images
        loadCollageLayout(layoutRes)

    }

    private fun initViews() {

        loadSelectedImage()

        with(binding){




            /*  rlNext.setOnClickListener {
                  val intent = Intent(this@FrameActivity, CollagePhotoEditingActivity::class.java)
                  startActivity(intent)
              }*/

            backButton.setOnClickListener {
                onBackPressed()
            }

        }
    }

    private fun loadCollageLayout(layoutRes: Int) {
        val stub = layoutInflater.inflate(layoutRes, null)
       // binding.collageContainer.removeAllViews()
      //  binding.collageContainer.addView(stub)

        // Dynamically load images into the collage layout
        when (layoutRes) {
            R.layout.item_gallery_image -> {
                Log.d("CollagePhotoEditing", "Image URIs: $imageUris")
                if (imageUris.size > 0) Glide.with(this).load(imageUris[0]).into(stub.findViewById(R.id.img1))
                if (imageUris.size > 1) Glide.with(this).load(imageUris[1]).into(stub.findViewById(R.id.img2))
                if (imageUris.size > 2) Glide.with(this).load(imageUris[2]).into(stub.findViewById(R.id.img3))
                if (imageUris.size > 3) Glide.with(this).load(imageUris[3]).into(stub.findViewById(R.id.img4))
                if (imageUris.size > 4) Glide.with(this).load(imageUris[4]).into(stub.findViewById(R.id.img5))
                if (imageUris.size > 5) Glide.with(this).load(imageUris[5]).into(stub.findViewById(R.id.img6))
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
}