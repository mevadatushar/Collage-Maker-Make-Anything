package com.collagemaker_makeanything.Activity

import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.canhub.cropper.CropImageView
import com.collagemaker_makeanything.Adapter.TabAdapter
import com.collagemaker_makeanything.Fragment.RatioFragment
import com.collagemaker_makeanything.R
import com.collagemaker_makeanything.databinding.ActivityImageCollageBinding
import com.google.android.material.tabs.TabLayout

class ImageCollageActivity : BaseActivity(), RatioFragment.OnLayoutSelectedListener {

    lateinit var binding: ActivityImageCollageBinding

    private lateinit var imageUris: ArrayList<String>
    private var layoutRes: Int = R.layout.item_gallery_image

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageCollageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Receive the data from the previous activity
        imageUris = intent.getStringArrayListExtra("imageUris") ?: arrayListOf()
        layoutRes = intent.getIntExtra("layoutRes", R.layout.item_gallery_image)

        initView()
    }

    private fun initView() {
        val ratioFragment = RatioFragment.newInstance(imageUris.toString())
        ratioFragment.listener = this

        binding.imgDone.setOnClickListener {
            onBackPressed()
        }

        // Load default collage layout
        loadCollageLayout(layoutRes)

        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(R.string.tab_ratio))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(R.string.tab_layout))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(R.string.tab_margin))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(R.string.tab_border))

        binding.viewpager.addOnPageChangeListener(object :
            TabLayout.TabLayoutOnPageChangeListener(binding.tabLayout) {})

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab != null) {
                    binding.viewpager.currentItem = tab.position
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
        loadImageIntoCropView()
        val myAdapter = TabAdapter(supportFragmentManager)
        binding.viewpager.adapter = myAdapter
    }



    private fun loadCollageLayout(layoutRes: Int) {
        this.layoutRes = layoutRes
        val stub = LayoutInflater.from(this).inflate(layoutRes, null)
        binding.collageContainer.removeAllViews()
        binding.collageContainer.addView(stub)

        // Dynamically load images into the collage layout
        when (layoutRes) {
            R.layout.item_gallery_image -> {
                Log.d("ImageCollageActivity", "Image URIs: $imageUris")
                if (imageUris.size > 0) loadImageIntoView(imageUris[0], stub.findViewById(R.id.img1))
                if (imageUris.size > 1) loadImageIntoView(imageUris[1], stub.findViewById(R.id.img2))
                if (imageUris.size > 2) loadImageIntoView(imageUris[2], stub.findViewById(R.id.img3))
                if (imageUris.size > 3) loadImageIntoView(imageUris[3], stub.findViewById(R.id.img4))
                if (imageUris.size > 4) loadImageIntoView(imageUris[4], stub.findViewById(R.id.img5))
                if (imageUris.size > 5) loadImageIntoView(imageUris[5], stub.findViewById(R.id.img6))
            }
        }
    }


    private fun loadImageIntoCropView() {
        imageUris?.let { uri ->
            Glide.with(this)
                .asBitmap()
                .load(imageUris)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        // Set the Bitmap into CropImageView
                        binding.collageContainer.setImageBitmap(resource)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                        // Handle any cleanup here
                    }
                })
        }
    }

    private fun loadImageIntoView(uri: String, imageView: ImageView) {
        Glide.with(this)
            .load(uri)
            .into(imageView)
    }

    private fun underlineText(view: View) {
        if (view is TextView) {
            view.paintFlags = view.paintFlags or Paint.UNDERLINE_TEXT_FLAG
            view.setTextColor(ContextCompat.getColor(this, R.color.black))
        }
    }

    private fun resetUnderline(vararg views: TextView) {
        for (view in views) {
            view.paintFlags = view.paintFlags and Paint.UNDERLINE_TEXT_FLAG.inv()
            view.setTextColor(ContextCompat.getColor(this, R.color.off_white))
        }
    }

    override fun onLayoutSelected(aspectX: Int, aspectY: Int) {
        if (aspectX == 0 && aspectY == 0) {
            // Free cropping
            binding.collageContainer.setFixedAspectRatio(false)
        } else {
            binding.collageContainer.setAspectRatio(aspectX, aspectY)
        }
        Log.e("LayoutActivity", "Aspect Ratio Set: $aspectX:$aspectY")
    }



}
