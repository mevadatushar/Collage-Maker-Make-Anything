package com.collagemaker_makeanything.Activity

import android.graphics.PorterDuff
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.collagemaker_makeanything.Class.DrawingView
import com.collagemaker_makeanything.R
import com.collagemaker_makeanything.databinding.ActivityDrawBinding
import yuku.ambilwarna.AmbilWarnaDialog

class DrawActivity : BaseActivity() {

    private lateinit var binding: ActivityDrawBinding
    private var mDefaultColor = 0
    private lateinit var drawingView: DrawingView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDrawBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    private fun initView() {
        mDefaultColor = ContextCompat.getColor(this, R.color.white)

        binding.imgback.setOnClickListener {
            onBackPressed()
        }

        binding.lnrColor.setOnClickListener {
            openColorPickerDialog()
        }

        binding.civWhite.setOnClickListener { setColor(ContextCompat.getColor(this, R.color.white), it) }
        binding.civGrey.setOnClickListener { setColor(ContextCompat.getColor(this, R.color.lightgrey), it) }
        binding.civDarkGrey.setOnClickListener { setColor(ContextCompat.getColor(this, R.color.DarkGrey), it) }
        binding.civBlack.setOnClickListener { setColor(ContextCompat.getColor(this, R.color.black), it) }
        binding.civBrown.setOnClickListener { setColor(ContextCompat.getColor(this, R.color.Brown), it) }
        binding.civDarkBrown.setOnClickListener { setColor(ContextCompat.getColor(this, R.color.DarkBrown), it) }
        binding.civLightBrown.setOnClickListener { setColor(ContextCompat.getColor(this, R.color.LightBrown), it) }
        binding.civRed.setOnClickListener { setColor(ContextCompat.getColor(this, R.color.Red), it) }
        binding.civBlue.setOnClickListener { setColor(ContextCompat.getColor(this, R.color.blue), it) }

        drawingView = findViewById(R.id.drawing_view) // Make sure to find drawingView after setContentView
        setColor(ContextCompat.getColor(this, R.color.white), binding.civWhite)

        val imageUriString = intent.getStringExtra("imageUri")
        if (imageUriString != null) {
            val imageUri = Uri.parse(imageUriString)
            drawingView.setBackgroundImage(imageUri)
        } else {
            logErrorAndFinish("Image URI string is null")
        }

        binding.skbEraser.max = 100
        binding.skbEraser.progress = 5
        binding.skbEraser.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                drawingView.setStrokeWidth(progress.toFloat())
                binding.txtProgress.text = progress.toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // Do nothing
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // Do nothing
            }
        })

        binding.lnrEraser.setOnClickListener {
            drawingView.setEraser(true)
        }
    }

    private fun setColor(color: Int, selectedView: View) {
        drawingView.setColor(color)
        resetColorSelection()
        selectedView.setBackgroundResource(R.drawable.ring)

        // Reset the eraser mode when selecting a new color
        drawingView.setEraser(false)
    }

    private fun resetColorSelection() {
        binding.civWhite.setBackgroundResource(0)
        binding.civGrey.setBackgroundResource(0)
        binding.civDarkGrey.setBackgroundResource(0)
        binding.civBlack.setBackgroundResource(0)
        binding.civBrown.setBackgroundResource(0)
        binding.civDarkBrown.setBackgroundResource(0)
        binding.civLightBrown.setBackgroundResource(0)
        binding.civRed.setBackgroundResource(0)
        binding.civBlue.setBackgroundResource(0)
        binding.lnrColor.setBackgroundResource(0)
    }

    private fun openColorPickerDialog() {
        val colorPickerDialog = AmbilWarnaDialog(this, mDefaultColor,
            object : AmbilWarnaDialog.OnAmbilWarnaListener {
                override fun onCancel(dialog: AmbilWarnaDialog?) {}

                override fun onOk(dialog: AmbilWarnaDialog?, color: Int) {
                    mDefaultColor = color

                    setColor(color, binding.lnrColor)

                    val customBackgroundDrawable = ContextCompat.getDrawable(this@DrawActivity, R.drawable.custom_background)
                    customBackgroundDrawable?.setColorFilter(mDefaultColor, PorterDuff.Mode.SRC_ATOP)
                    binding.lnrColor.background = customBackgroundDrawable                }
            })
        colorPickerDialog.show()
    }

    private fun logErrorAndFinish(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        finish()
    }

}