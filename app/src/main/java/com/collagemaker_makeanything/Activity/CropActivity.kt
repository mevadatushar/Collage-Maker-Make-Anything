package com.collagemaker_makeanything.Activity

import android.animation.ObjectAnimator
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.canhub.cropper.CropImageView
import com.collagemaker_makeanything.Class.RulerScaleView
import com.collagemaker_makeanything.R
import com.collagemaker_makeanything.databinding.ActivityCropBinding
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class CropActivity : BaseActivity() {
    lateinit var binding: ActivityCropBinding
    private lateinit var imageUri: String
    private var selectedButton: LinearLayout? = null
    private lateinit var imageBitmap: Bitmap
    private var currentRotation = 0f
    private var isFlippedHorizontal = false
    private var isFlippedVertical = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCropBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Receive the data from the previous activity
        imageUri = intent.getStringExtra("imageUri") ?: ""

        initView()
    }

    private fun initView() {
        with(binding) {
            imgBack.setOnClickListener {
                onBackPressed()
            }

            imgDone.setOnClickListener {
                saveCroppedImage()
            }

            findViewById<LinearLayout>(R.id.lnrCustom).setOnClickListener {
                binding.imgCropSelectImage.setFixedAspectRatio(false)
            }

            findViewById<LinearLayout>(R.id.lnrLG11).setOnClickListener {
                binding.imgCropSelectImage.setAspectRatio(1, 1)
            }

            findViewById<LinearLayout>(R.id.lnrLG45).setOnClickListener {
                binding.imgCropSelectImage.setAspectRatio(4, 5)
            }

            findViewById<ImageView>(R.id.img54).setOnClickListener {
                binding.imgCropSelectImage.setAspectRatio(5, 4)
            }

            findViewById<ImageView>(R.id.img34).setOnClickListener {
                binding.imgCropSelectImage.setAspectRatio(3, 4)
            }

            findViewById<ImageView>(R.id.img43).setOnClickListener {
                binding.imgCropSelectImage.setAspectRatio(4, 3)
            }

            findViewById<ImageView>(R.id.imgA4).setOnClickListener {
                binding.imgCropSelectImage.setAspectRatio(2, 3)
            }

            findViewById<LinearLayout>(R.id.lnrCover).setOnClickListener {
                binding.imgCropSelectImage.setAspectRatio(16, 9)
            }

            findViewById<LinearLayout>(R.id.lnrDevice).setOnClickListener {
                binding.imgCropSelectImage.setAspectRatio(9, 16)
            }

            findViewById<ImageView>(R.id.img23).setOnClickListener {
                binding.imgCropSelectImage.setAspectRatio(2, 3)
            }

            findViewById<ImageView>(R.id.img32).setOnClickListener {
                binding.imgCropSelectImage.setAspectRatio(3, 2)
            }

            findViewById<ImageView>(R.id.img12).setOnClickListener {
                binding.imgCropSelectImage.setAspectRatio(1, 2)
            }

            findViewById<LinearLayout>(R.id.lnrPost).setOnClickListener {
                binding.imgCropSelectImage.setAspectRatio(16, 9)
            }

            findViewById<LinearLayout>(R.id.lnrHeader).setOnClickListener {
                binding.imgCropSelectImage.setAspectRatio(3, 1)
            }

            if (imageUri != null) {
                val imageUri = Uri.parse(imageUri)
                val imageView = findViewById<CropImageView>(R.id.imgCropSelectImage)

                if (imageView != null) {
                    try {
                        imageUri?.let { uri ->
                            Glide.with(this@CropActivity).asBitmap().load(imageUri)
                                .into(object : CustomTarget<Bitmap>() {
                                    override fun onResourceReady(
                                        resource: Bitmap, transition: Transition<in Bitmap>?
                                    ) {
                                        // Set the Bitmap into CropImageView
                                        binding.imgCropSelectImage.setImageBitmap(resource)
                                        imageBitmap = resource // Initialize imageBitmap here
                                    }

                                    override fun onLoadCleared(placeholder: Drawable?) {
                                        // Handle any cleanup here
                                    }
                                })
                        }
                    } catch (e: Exception) {
                        logErrorAndFinish("Glide error: ${e.message}")
                    }
                } else {
                    logErrorAndFinish("ImageView not found in layout")
                }
            } else {
                logErrorAndFinish("Image URI string is null")
            }

            binding.lnrHorizontal1.setOnClickListener {
                binding.RsvScale.setOnSelectedIndexChangeListener(object :
                    RulerScaleView.OnSelectedIndexChangeListener {
                    override fun onSelectedIndexChange(index: Int) {
                        updateCropImageViewRotation(index)
                    }
                })
            }

            binding.lnrVertical1.setOnClickListener {
                binding.RsvScale.setOnSelectedIndexChangeListener(object :
                    RulerScaleView.OnSelectedIndexChangeListener {
                    override fun onSelectedIndexChange(index: Int) {
                        updateCropImageViewRotationAndVerticalScale(index)
                    }
                })
            }

            binding.RsvScale.setOnClickListener {
                binding.RsvScale.setSelectedIndex(0) // Set the selected index to 10 (adjust as needed)
            }

            binding.lnrRight90.setOnClickListener {
                rotateImageRight()
            }

            binding.lnrLeft90.setOnClickListener {
                rotateImageLeft()
            }

            binding.lnrHorizontal.setOnClickListener {
                flipImageHorizontally()
            }

            binding.lnrVertical.setOnClickListener {
                flipImageVertically()
            }

            binding.lnrRotate.setOnClickListener { toggleButton(binding.lnrRotate) }
            binding.lnrPerspective.setOnClickListener { toggleButton(binding.lnrPerspective) }
        }
    }

    private fun updateCropImageViewRotation(index: Int) {
        val rotationAngle = (index * 0.9f).coerceIn(-45f, 45f)
        binding.imgCropSelectImage.setAspectRatio(1, 1)
        binding.imgCropSelectImage.rotation = rotationAngle // No cast needed
    }

    private fun updateCropImageViewRotationAndVerticalScale(index: Int) {
        // Vertical rotation
        val verticalRotationAngle = (index * 0.9f).coerceIn(-45f, 45f)
        binding.imgCropSelectImage.setAspectRatio(1, 1)
        binding.imgCropSelectImage.rotationX = verticalRotationAngle
    }

    private fun rotateImageRight() {
        currentRotation += 90f // Increment the rotation angle by 90 degrees
        binding.imgCropSelectImage.animate().rotation(currentRotation)
            .setDuration(100) // Duration of the rotation animation
            .start()
    }

    private fun rotateImageLeft() {
        currentRotation -= 90f // Decrement the rotation angle by 90 degrees
        binding.imgCropSelectImage.animate().rotation(currentRotation)
            .setDuration(100) // Duration of the rotation animation
            .start()
    }

    private fun flipImageHorizontally() {
        val animator = ObjectAnimator.ofFloat(
            binding.imgCropSelectImage, "scaleX", if (isFlippedHorizontal) 1f else -1f
        )
        animator.duration = 100
        animator.start()
        isFlippedHorizontal = !isFlippedHorizontal
    }

    private fun flipImageVertically() {
        val animator = ObjectAnimator.ofFloat(
            binding.imgCropSelectImage, "scaleY", if (isFlippedVertical) 1f else -1f
        )
        animator.duration = 100
        animator.start()
        isFlippedVertical = !isFlippedVertical
    }

    private fun toggleButton(button: LinearLayout?) {
        if (selectedButton != button) {
            // Deselect previously selected button
            selectedButton?.let {
                it.backgroundTintList = ContextCompat.getColorStateList(this, R.color.off_gray)
                val prevTextView = it.findViewById<TextView>(R.id.txtRotate)
                    ?: it.findViewById<TextView>(R.id.txtPerspective)
                prevTextView?.setTextColor(ContextCompat.getColor(this, R.color.black))
            }

            // Toggle the state of the button
            selectedButton = button
            selectedButton?.backgroundTintList = ContextCompat.getColorStateList(this, R.color.blue)
            val newTextView = button?.findViewById<TextView>(R.id.txtRotate)
                ?: button?.findViewById<TextView>(R.id.txtPerspective)
            newTextView?.setTextColor(ContextCompat.getColor(this, R.color.white))

            // Handle actions based on selected button (if needed)
            when (selectedButton?.id) {
                R.id.lnrRotate -> {
                    binding.lnrlayout.visibility = View.VISIBLE
                    binding.lnrOrientation.visibility = View.GONE
                }

                R.id.lnrPerspective -> {
                    binding.lnrOrientation.visibility = View.VISIBLE
                    binding.lnrlayout.visibility = View.GONE
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Select a specific button in onResume (example: lnrFilter)
        toggleButton(binding.lnrRotate)
    }

    private fun logErrorAndFinish(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        finish() // Close the activity or handle it appropriately
    }

    private fun saveCroppedImage() {
        // Get the cropped image bitmap from CropImageView
        val croppedBitmap = binding.imgCropSelectImage.croppedImage

        // Apply transformations to the cropped bitmap
        val transformedBitmap = croppedBitmap?.let { applyTransformations(it) }

        // Save the transformed image to a file
        val uri = transformedBitmap?.let { saveBitmapToFile(it) }

        // Pass the URI back to the calling activity
        if (uri != null) {
            val resultIntent = Intent().apply {
                putExtra("croppedImageUri", uri.toString())
            }
            setResult(RESULT_OK, resultIntent)
        } else {
            setResult(RESULT_CANCELED)
        }
        finish()
    }


    private fun applyTransformations(bitmap: Bitmap): Bitmap {
        val matrix = Matrix().apply {
            // Apply current rotation
            postRotate(currentRotation)
            // Apply horizontal flip if needed
            if (isFlippedHorizontal) postScale(-1f, 1f)
            // Apply vertical flip if needed
            if (isFlippedVertical) postScale(1f, -1f)
            // Apply perspective transformations if needed
            // Add your custom perspective transformations here if required
        }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    private fun saveBitmapToFile(bitmap: Bitmap): Uri? {
        val filename = "cropped_image_${System.currentTimeMillis()}.jpg"
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
}
