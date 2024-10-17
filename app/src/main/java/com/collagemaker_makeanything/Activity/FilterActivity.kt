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
import android.widget.SeekBar
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
    private lateinit var imageUris: String

    private lateinit var originalBitmap: Bitmap
    private lateinit var filteredBitmap: Bitmap
    private var selectedButton: LinearLayout? = null



    private var brightnessValue = 1.0f
    private var contrastValue = 1.0f
    private var warmthValue = 1.0f
    private var saturationValue = 1.0f
    private var fadeValue = 0.0f
    private var highlightValue = 1.0f
    private var shadowValue = 0.0f
    private var tintValue = 0.0f
    private var hueValue = 0.0f
    private var grainValue = 0.0f


    private var currentAdjustment = AdjustmentType.NONE

    private val uriToLoad: String
        get() = croppedImageUri ?: imageUri ?: imageUris

    companion object {
        private const val CROP_IMAGE_REQUEST_CODE = 1001
        private const val FILTER_IMAGE_REQUEST_CODE = 1002
    }

    enum class AdjustmentType {
        NONE, BRIGHTNESS, CONTRAST, WARMTH, SATURATION, FADE, HIGHLIGHT, SHADOW, TINT, HUE, GRAIN
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFilterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        imageUri = intent.getStringExtra("imageUri") ?: ""
        imageUris = intent.getStringExtra("imageUris") ?: ""
        croppedImageUri = intent.getStringExtra("croppedImageUri")


        initView()
        setFilterListeners()
        setAdjustListeners()
    }

    private fun setAdjustListeners() {

        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val normalizedValue = progress / 100f  // Normalize the seekbar value between 0 and 1
                binding.seekBarValueText.text = "$progress" // Update the TextView
                when (currentAdjustment) {
                    AdjustmentType.BRIGHTNESS -> {
                        brightnessValue = 1f + (normalizedValue - 0.5f) // Range: 0.5 - 1.5
                        applyBrightness(brightnessValue)
                    }
                    AdjustmentType.CONTRAST -> {
                        contrastValue = 1f + (normalizedValue - 0.5f) // Range: 0.5 - 1.5
                        applyContrast(contrastValue)
                    }
                    AdjustmentType.WARMTH -> {
                        warmthValue = 1f + (normalizedValue - 0.5f) // Range: 0.5 - 1.5
                        applyWarmth(warmthValue)
                    }
                    AdjustmentType.SATURATION -> {
                        saturationValue = normalizedValue * 2f // Range: 0 - 2
                        applySaturation(saturationValue)
                    }
                    AdjustmentType.FADE -> {
                        fadeValue = normalizedValue // Range: 0 - 1
                        applyFade(fadeValue)
                    }
                    AdjustmentType.HIGHLIGHT -> {
                        highlightValue = normalizedValue // Range: 0 - 1
                        applyHighlight(highlightValue)
                    }
                    AdjustmentType.SHADOW -> {
                        shadowValue = normalizedValue // Range: 0 - 1
                        applyShadow(shadowValue)
                    }
                    AdjustmentType.TINT -> {
                        tintValue = normalizedValue // Range: 0 - 1
                        applyTint(tintValue)
                    }
                    AdjustmentType.HUE -> {
                        hueValue = normalizedValue * 360f // Range: 0 - 360 (degrees)
                        applyHue(hueValue)
                    }
                    AdjustmentType.GRAIN -> {
                        grainValue = normalizedValue // Range: 0 - 1
                        applyGrain(grainValue)
                    }
                    else -> {}
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })



        binding.imgAdjustSelectBrightness.setOnClickListener {
            currentAdjustment = AdjustmentType.BRIGHTNESS
            binding.seekBar.progress = ((brightnessValue - 1f) * 100f + 50).toInt() // Set the seekbar to the saved brightness value
        }

        binding.imgAdjustSelectContrast.setOnClickListener {
            currentAdjustment = AdjustmentType.CONTRAST
            binding.seekBar.progress = ((contrastValue - 1f) * 100f + 50).toInt() // Set the seekbar to the saved contrast value
        }

        binding.imgAdjustSelectWarmth.setOnClickListener {
            currentAdjustment = AdjustmentType.WARMTH
            binding.seekBar.progress = ((warmthValue - 1f) * 100f + 50).toInt() // Set the seekbar to the saved warmth value
        }

        binding.imgAdjustSelectSaturation.setOnClickListener {
            currentAdjustment = AdjustmentType.SATURATION
            binding.seekBar.progress = (saturationValue * 50f).toInt() // Set the seekbar to the saved saturation value
        }

        binding.imgAdjustSelectFade.setOnClickListener {
            currentAdjustment = AdjustmentType.FADE
            binding.seekBar.progress = (fadeValue * 100).toInt() // Set the seekbar to the saved fade value
        }

        binding.imgAdjustSelectHighlight.setOnClickListener {
            currentAdjustment = AdjustmentType.HIGHLIGHT
            binding.seekBar.progress = (highlightValue * 100).toInt() // Set the seekbar to the saved highlight value
        }

        binding.imgAdjustSelectShadow.setOnClickListener {
            currentAdjustment = AdjustmentType.SHADOW
            binding.seekBar.progress = (shadowValue * 100).toInt()  // Set the seekbar to the saved shadow value
        }

        binding.imgAdjustSelectTint.setOnClickListener {
            currentAdjustment = AdjustmentType.TINT
            binding.seekBar.progress = (tintValue * 100).toInt() // Set the seekbar to the saved tint value
        }

        binding.imgAdjustSelectHue.setOnClickListener {
            currentAdjustment = AdjustmentType.HUE
            binding.seekBar.progress = (hueValue * 100).toInt() // Set the seekbar to the saved hue value
        }

        binding.imgAdjustSelectGrain.setOnClickListener {
            currentAdjustment = AdjustmentType.GRAIN
            binding.seekBar.progress = (grainValue * 100).toInt() // Set the seekbar to the saved grain value
        }



    }




    private fun applyBrightness(brightness: Float) {
        val colorMatrix = ColorMatrix().apply {
            setScale(brightness, brightness, brightness, 1f)
        }
        applyColorFilter(colorMatrix)
    }

    private fun applyContrast(contrast: Float) {
        val colorMatrix = ColorMatrix().apply {
            set(floatArrayOf(
                contrast, 0f, 0f, 0f, 0f,
                0f, contrast, 0f, 0f, 0f,
                0f, 0f, contrast, 0f, 0f,
                0f, 0f, 0f, 1f, 0f
            ))
        }
        applyColorFilter(colorMatrix)
    }

    private fun applyWarmth(warmth: Float) {
        val colorMatrix = ColorMatrix().apply {
            set(floatArrayOf(
                warmth, 0f, 0f, 0f, 0f,
                0f, warmth, 0f, 0f, 0f,
                0f, 0f, warmth, 0f, 0f,
                0f, 0f, 0f, 1f, 0f
            ))
        }
        applyColorFilter(colorMatrix)
    }

    private fun applySaturation(saturation: Float) {
        val colorMatrix = ColorMatrix().apply {
            setSaturation(saturation)
        }
        applyColorFilter(colorMatrix)
    }

    private fun applyFade(fade: Float) {
        // Apply fade by reducing contrast and saturation
        val colorMatrix = ColorMatrix().apply {
            setSaturation(1f - fade)
            val contrast = 1f - fade
            set(floatArrayOf(
                contrast, 0f, 0f, 0f, 0f,
                0f, contrast, 0f, 0f, 0f,
                0f, 0f, contrast, 0f, 0f,
                0f, 0f, 0f, 1f, 0f
            ))
        }
        applyColorFilter(colorMatrix)
    }

    private fun applyHighlight(highlight: Float) {
        // Adjust highlight by tweaking brightness
        applyBrightness(1f + highlight)
    }

    private fun applyShadow(shadow: Float) {
        val colorMatrix = ColorMatrix().apply {
            setScale(1f - shadow, 1f - shadow, 1f - shadow, 1f)  // Reducing brightness
        }
        applyColorFilter(colorMatrix)
    }

    private fun applyTint(tint: Float) {
        val tintColorMatrix = ColorMatrix().apply {
            set(
                floatArrayOf(
                    1f, 0f, 0f, 0f, tint * 255,  // Adjust red channel
                    0f, 1f, 0f, 0f, tint * 255,  // Adjust green channel
                    0f, 0f, 1f, 0f, tint * 255,  // Adjust blue channel
                    0f, 0f, 0f, 1f, 0f
                )
            )
        }
        applyColorFilter(tintColorMatrix)
    }

    private fun applyHue(hue: Float) {
        val colorMatrix = ColorMatrix()
        colorMatrix.setRotate(0, hue)  // Rotate red channel
        colorMatrix.setRotate(1, hue)  // Rotate green channel
        colorMatrix.setRotate(2, hue)  // Rotate blue channel
        applyColorFilter(colorMatrix)
    }

    private fun applyGrain(grain: Float) {
        // Adding grain as noise can be a bit more complex.
        // You would typically blend a noise bitmap over the image.
        // Here is a simple approximation using ColorMatrix for illustration.

        val grainMatrix = ColorMatrix().apply {
            set(floatArrayOf(
                1f, 0f, 0f, 0f, grain * 50,  // Adding grain as brightness variation
                0f, 1f, 0f, 0f, grain * 50,
                0f, 0f, 1f, 0f, grain * 50,
                0f, 0f, 0f, 1f, 0f
            ))
        }
        applyColorFilter(grainMatrix)
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
               // Toast.makeText(this@FilterActivity, "Filter Applied Successfully", Toast.LENGTH_SHORT).show()
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
