package com.collagemaker_makeanything.Activity

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.collagemaker_makeanything.R
import com.collagemaker_makeanything.databinding.ActivityFilterBinding
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class FilterActivity : BaseActivity() {
    lateinit var binding: ActivityFilterBinding
    private var croppedImageUri: String? = null
    private lateinit var imageUri: String

    private lateinit var originalBitmap: Bitmap
    private lateinit var filteredBitmap: Bitmap
    private var selectedButton: LinearLayout? = null

    private val uriToLoad: String
        get() = croppedImageUri ?: imageUri

    companion object {
        private const val CROP_IMAGE_REQUEST_CODE = 1001
        private const val FILTER_IMAGE_REQUEST_CODE = 1002
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFilterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        imageUri = intent.getStringExtra("imageUri") ?: ""
        croppedImageUri = intent.getStringExtra("croppedImageUri")

        initView()
        setFilterListeners()
    }

    private fun initView() {
        loadSelectedImage()

        binding.imgDone.setOnClickListener {
            saveFilteredImage()
        }

        binding.imgBack.setOnClickListener {
            onBackPressed()
        }


        val selectedImageUri = Uri.parse(uriToLoad)
        originalBitmap =
            BitmapFactory.decodeStream(contentResolver.openInputStream(selectedImageUri))
        filteredBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true)

        Glide.with(this)
            .asBitmap()
            .load(selectedImageUri)
            .apply(RequestOptions().override(800, 800)) // Adjust size as needed
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    originalBitmap = resource
                    filteredBitmap = originalBitmap.copy(originalBitmap.config, true)
                    binding.collageContainer.setImageBitmap(originalBitmap)
                }

                override fun onLoadCleared(placeholder: Drawable?) {}
            })

        // Loading the image into various views
        loadImageIntoViews(selectedImageUri)
    }

    private fun loadImageIntoViews(imageUri: Uri) {
        val glideRequest = Glide.with(this).load(imageUri).fitCenter()

        glideRequest.into(binding.imgAdjustSelectImages)
        glideRequest.into(binding.imgAdjustSelectBright)
        glideRequest.into(binding.imgAdjustSelectStory)
        glideRequest.into(binding.imgAdjustSelectNatural)
        glideRequest.into(binding.imgAdjustSelectWarm)
        glideRequest.into(binding.imgAdjustSelectDew)
        glideRequest.into(binding.imgAdjustSelectGold)
        glideRequest.into(binding.imgAdjustSelectLomo)
        glideRequest.into(binding.imgAdjustSelectPink)
    }

    private fun setFilterListeners() {
        binding.imgAdjustSelectImages.setOnClickListener {
            resetToOriginalImage()
            showSelectedCheckImage(binding.imgCheckOrignal)
        }

        binding.imgAdjustSelectBright.setOnClickListener {
            applyBrightFilter()
            showSelectedCheckImage(binding.imgCheckBright)
        }

        binding.imgAdjustSelectStory.setOnClickListener {
            applyStoryFilter()
            showSelectedCheckImage(binding.imgCheckStory)
        }

        binding.imgAdjustSelectNatural.setOnClickListener {
            applyNaturalFilter()
            showSelectedCheckImage(binding.imgCheckNatural)
        }

        binding.imgAdjustSelectWarm.setOnClickListener {
            applyWarmFilter()
            showSelectedCheckImage(binding.imgCheckWarm)
        }

        binding.imgAdjustSelectDew.setOnClickListener {
            applyDewFilter(Uri.parse(uriToLoad))
            showSelectedCheckImage(binding.imgCheckDew)
        }

        binding.imgAdjustSelectGold.setOnClickListener {
            applyGoldFilter()
            showSelectedCheckImage(binding.imgCheckGold)
        }

        binding.imgAdjustSelectLomo.setOnClickListener {
            applyLomoFilter()
            showSelectedCheckImage(binding.imgCheckLomo)
        }

        binding.imgAdjustSelectPink.setOnClickListener {
            applyPinkFilter()
            showSelectedCheckImage(binding.imgCheckPink)
        }
    }

    private fun showSelectedCheckImage(selectedImageView: ImageView) {
        // Hide all check images first
        binding.imgCheckOrignal.visibility = View.GONE
        binding.imgCheckBright.visibility = View.GONE
        binding.imgCheckStory.visibility = View.GONE
        binding.imgCheckNatural.visibility = View.GONE
        binding.imgCheckWarm.visibility = View.GONE
        binding.imgCheckDew.visibility = View.GONE
        binding.imgCheckGold.visibility = View.GONE
        binding.imgCheckLomo.visibility = View.GONE
        binding.imgCheckPink.visibility = View.GONE

        // Show only the selected check image
        selectedImageView.visibility = View.VISIBLE
    }


    private fun loadSelectedImage() {
        if (uriToLoad.isNotEmpty()) {
            Log.d("FilterActivity", "Loading image URI: $uriToLoad")
            Glide.with(this)
                .load(uriToLoad)
                .into(binding.collageContainer)
        } else {
            Log.d("FilterActivity", "No image URI found.")
            Toast.makeText(this, "No image found to load", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveFilteredImage() {
        // Save the filtered image to a file
        val uri = saveBitmapToFile(filteredBitmap)

        // Pass the URI back to the calling activity
        if (uri != null) {
            val resultIntent = Intent().apply {
                putExtra("filteredImageUri", uri.toString())
            }
            setResult(RESULT_OK, resultIntent)
        } else {
            setResult(RESULT_CANCELED)
        }
        finish()
    }

    private fun saveBitmapToFile(bitmap: Bitmap): Uri? {
        val filename = "filtered_image_${System.currentTimeMillis()}.jpg"
        val file = File(filesDir, filename)
        return try {
            FileOutputStream(file).use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            }
            Uri.fromFile(file)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }


    private fun toggleButton(button: LinearLayout?) {
        if (selectedButton != button) {
            selectedButton?.let {
                it.backgroundTintList = ContextCompat.getColorStateList(this, R.color.white)
                val prevTextView = it.findViewById<TextView>(R.id.txtFilter)
                    ?: it.findViewById<TextView>(R.id.txtAdjust)
                prevTextView?.setTextColor(ContextCompat.getColor(this, R.color.black))
            }

            selectedButton = button
            selectedButton?.backgroundTintList = ContextCompat.getColorStateList(this, R.color.blue)
            val newTextView = button?.findViewById<TextView>(R.id.txtFilter)
                ?: button?.findViewById<TextView>(R.id.txtAdjust)
            newTextView?.setTextColor(ContextCompat.getColor(this, R.color.white))

            when (selectedButton?.id) {
                R.id.llFilter -> {
                    binding.rtllayouts.visibility = View.VISIBLE
                    binding.lnrskbseek.visibility = View.GONE
                    binding.hsvlayout.visibility = View.GONE
                }

                R.id.lnrAdjust -> {
                    binding.lnrskbseek.visibility = View.VISIBLE
                    binding.hsvlayout.visibility = View.VISIBLE
                    binding.rtllayouts.visibility = View.GONE
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        toggleButton(binding.llFilter)
    }

    private fun logErrorAndFinish(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun applyBrightFilter() {
        val brightness = 1.2f
        val colorMatrix = ColorMatrix().apply {
            setScale(brightness, brightness, brightness, 1f)
        }
        applyColorFilter(colorMatrix)
    }

    private fun applyStoryFilter() {
        val contrast = 1.5f
        val contrastMatrix = ColorMatrix().apply {
            setSaturation(contrast)
        }
        applyColorFilter(contrastMatrix)
    }

    private fun applyNaturalFilter() {
        val colorMatrix = ColorMatrix().apply {
            setSaturation(1.2f)
            val contrast = 1.1f
            val brightness = 1.05f
            val scale = floatArrayOf(
                contrast, 0f, 0f, 0f, brightness,
                0f, contrast, 0f, 0f, brightness,
                0f, 0f, contrast, 0f, brightness,
                0f, 0f, 0f, 1f, 0f
            )
            postConcat(ColorMatrix(scale))
        }
        applyColorFilter(colorMatrix)
    }

    private fun applyWarmFilter() {
        val colorMatrix = ColorMatrix().apply {
            set(
                floatArrayOf(
                    1.2f, 0.0f, 0.0f, 0.0f, 0.0f,
                    0.0f, 1.1f, 0.0f, 0.0f, 0.0f,
                    0.0f, 0.0f, 0.9f, 0.0f, 0.0f,
                    0.0f, 0.0f, 0.0f, 1.0f, 0.0f
                )
            )
        }
        applyColorFilter(colorMatrix)
    }

    private fun applyDewFilter(imageUri: Uri) {
        val colorMatrix = ColorMatrix().apply {
            setSaturation(1.2f)
            val contrast = 1.1f
            val brightness = 1.05f
            val scale = floatArrayOf(
                contrast, 0f, 0f, 0f, brightness,
                0f, contrast, 0f, 0f, brightness,
                0f, 0f, contrast, 0f, brightness,
                0f, 0f, 0f, 1f, 0f
            )
            postConcat(ColorMatrix(scale))
        }
        applyColorFilter(colorMatrix)
    }

    private fun applyGoldFilter() {
        val colorMatrix = ColorMatrix().apply {
            set(
                floatArrayOf(
                    1.5f, 0f, 0f, 0f, 0f,
                    0f, 1.2f, 0f, 0f, 0f,
                    0f, 0f, 0.8f, 0f, 0f,
                    0f, 0f, 0f, 1f, 0f
                )
            )
        }
        applyColorFilter(colorMatrix)
    }

    private fun applyLomoFilter() {
        val colorMatrix = ColorMatrix().apply {
            set(
                floatArrayOf(
                    1.1f, 0f, 0f, 0f, 0f,
                    0f, 1.1f, 0f, 0f, 0f,
                    0f, 0f, 0.9f, 0f, 0f,
                    0f, 0f, 0f, 1f, 0f
                )
            )
        }
        applyColorFilter(colorMatrix)
    }

    private fun applyPinkFilter() {
        val colorMatrix = ColorMatrix().apply {
            set(
                floatArrayOf(
                    1f, 0.2f, 0.2f, 0f, 0f,
                    0.2f, 1f, 0.2f, 0f, 0f,
                    0.2f, 0.2f, 1f, 0f, 0f,
                    0f, 0f, 0f, 1f, 0f
                )
            )
        }
        applyColorFilter(colorMatrix)
    }

    private fun applyColorFilter(colorMatrix: ColorMatrix) {
        val paint = Paint().apply {
            colorFilter = ColorMatrixColorFilter(colorMatrix)
        }
        val canvas = Canvas(filteredBitmap)
        canvas.drawBitmap(originalBitmap, 0f, 0f, paint)
        binding.collageContainer.setImageBitmap(filteredBitmap)
    }

    private fun resetToOriginalImage() {
        binding.collageContainer.setImageBitmap(originalBitmap)
        filteredBitmap = originalBitmap.copy(originalBitmap.config, true)
    }


    fun onButtonClick(view: View) {
        when (view.id) {
            R.id.lnrAdjust -> toggleButton(binding.lnrAdjust)
            R.id.llFilter -> toggleButton(binding.llFilter)
            // Add other cases as needed
        }
    }

}
