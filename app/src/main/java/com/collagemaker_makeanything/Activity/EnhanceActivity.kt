package com.collagemaker_makeanything.Activity

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.collagemaker_makeanything.R
import com.collagemaker_makeanything.databinding.ActivityEnhanceBinding
import jp.co.cyberagent.android.gpuimage.GPUImage
import jp.co.cyberagent.android.gpuimage.filter.GPUImageBrightnessFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageSaturationFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageSharpenFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilterGroup

class EnhanceActivity : BaseActivity() {

    private lateinit var binding: ActivityEnhanceBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEnhanceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
    }

    private fun initView() {
        val imageUriString = intent.getStringExtra("imageUri")

        binding.txtCancle.setOnClickListener {
            finish()
        }


        if (!imageUriString.isNullOrEmpty()) {
            val imageUri = Uri.parse(imageUriString)
            val bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(imageUri))

            // Set original and enhanced images
            binding.originalImage.setImageBitmap(bitmap)
            binding.enhancedImage.setImageBitmap(enhanceImage(bitmap)) // This should apply your GPUImage filters
        }

        // Handle dragging the divider
        val divider = binding.divider
        val dividerHandle = binding.dividerHandle

        // Set a touch listener to the entire divider's parent layout to catch touches
        val parentLayout = binding.dividerHandle.parent as View
        parentLayout.setOnTouchListener(object : View.OnTouchListener {
            private var dX = 0f

            override fun onTouch(view: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        // Determine the center of the divider
                        val dividerCenterX = dividerHandle.x + dividerHandle.width / 2

                        // Check if touch is within the wider touch area (30 pixels on each side of the divider)
                        if (event.rawX in (dividerCenterX - 60)..(dividerCenterX + 60)) { // 60 pixels on either side
                            dX = dividerHandle.x - event.rawX
                            return true
                        }
                    }
                    MotionEvent.ACTION_MOVE -> {
                        val parentWidth = parentLayout.width
                        val newX = event.rawX + dX
                        // Ensure the divider doesn't go out of bounds
                        if (newX >= 0 && newX <= parentWidth - dividerHandle.width) {
                            dividerHandle.x = newX
                            divider.x = newX + dividerHandle.width / 2 - divider.width / 2
                            updateImageVisibility(newX)
                        }
                        return true
                    }
                }
                return false
            }
        })
    }


    // Function to update the clipping bounds of the images based on divider position
    private fun updateImageVisibility(dividerPosition: Float) {
        val width = binding.originalImage.width
        val ratio = dividerPosition / width
        val leftVisibleWidth = (width * ratio).toInt()

        // Clip the before and after images
        binding.originalImage.clipBounds = android.graphics.Rect(0, 0, leftVisibleWidth, binding.originalImage.height)
        binding.enhancedImage.clipBounds = android.graphics.Rect(leftVisibleWidth, 0, width, binding.enhancedImage.height)
    }

    // Function to enhance the image
    // Function to enhance the image using Brightness, Saturation, and Sharpness filters
    private fun enhanceImage(bitmap: Bitmap): Bitmap {
        val gpuImage = GPUImage(this)
        gpuImage.setImage(bitmap) // Set the original image

        // Create a filter group to combine multiple filters
        val filterGroup = GPUImageFilterGroup()

        // Apply brightness filter
        val brightnessFilter = GPUImageBrightnessFilter()
        brightnessFilter.setBrightness(0.05f) // Adjust brightness (+0.5 for increase)
        filterGroup.addFilter(brightnessFilter)

        // Apply saturation filter
        val saturationFilter = GPUImageSaturationFilter()
        saturationFilter.setSaturation(1.2f) // Increase saturation (default is 1.0)
        filterGroup.addFilter(saturationFilter)

        // Apply sharpen filter
        val sharpenFilter = GPUImageSharpenFilter()
        sharpenFilter.setSharpness(0.7f) // Adjust sharpness (default is 0.0)
        filterGroup.addFilter(sharpenFilter)

        // Set the combined filter group
        gpuImage.setFilter(filterGroup)

        // Apply the combined filters and get the enhanced image
        return gpuImage.bitmapWithFilterApplied
    }
}


