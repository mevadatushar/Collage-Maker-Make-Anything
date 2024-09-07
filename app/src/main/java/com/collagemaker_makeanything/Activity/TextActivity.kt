package com.collagemaker_makeanything.Activity

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.collagemaker_makeanything.R
import com.collagemaker_makeanything.databinding.ActivityTextBinding

class TextActivity : BaseActivity() {
    lateinit var binding: ActivityTextBinding
    private lateinit var imageUris: ArrayList<String>
    private var layoutRes: Int = R.layout.item_gallery_image
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTextBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()

        // Receive the data from the previous activity
        imageUris = intent.getStringArrayListExtra("imageUris") ?: arrayListOf()
        layoutRes = intent.getIntExtra("layoutRes", R.layout.item_gallery_image)

        // Load the collage layout with the received images
        loadCollageLayout(layoutRes)

    }

    private fun initView() {
        with(binding){



            imgDone.setOnClickListener {
                onBackPressed()
            }

        }
    }

    private fun loadCollageLayout(layoutRes: Int) {
        val stub = layoutInflater.inflate(layoutRes, null)
        binding.collageContainer.removeAllViews()
        binding.collageContainer.addView(stub)

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
}