package com.collagemaker_makeanything.Activity

import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.collagemaker_makeanything.R
import com.collagemaker_makeanything.databinding.ActivitySaveBinding

class SaveActivity : BaseActivity() {

    private var overlayView: ViewGroup? = null


    lateinit var binding: ActivitySaveBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySaveBinding.inflate(layoutInflater)
        setContentView(binding.root)

        intiView()
    }

    private fun intiView()
    {
        val editedImageUriString = intent.getStringExtra("imageUri")
        if (editedImageUriString != null) {
            val editedImageUri = Uri.parse(editedImageUriString)
            val bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(editedImageUri))
            binding.lnrExport.setImageBitmap(bitmap) // Assuming you have an ImageView with id lnrExport
        }

        binding.imgback.setOnClickListener {
            onBackPressed()
        }

        binding.lnrMakeAnother.setOnClickListener {
            showCustomDialog()
        }
    }



    private fun showCustomDialog() {
        // Inflate the custom dialog layout
        val dialogView = layoutInflater.inflate(R.layout.editing_result_fedback_dialog, null)

        // Create the AlertDialog builder and set the custom view
        val builder = AlertDialog.Builder(this,R.style.CustomDialogTheme)
        builder.setView(dialogView)




        // Create and show the dialog
        val dialog = builder.create()

//        val width = resources.getDimensionPixelSize(R.dimen.dialog_width)
//        val height = resources.getDimensionPixelSize(R.dimen.dialog_height)
//        dialog.window?.setLayout(width, height)

        // Show the overlay
        showOverlay()

        dialog.setOnDismissListener {
            // Remove the overlay when the dialog is dismissed
            removeOverlay()
        }

        dialog.show()

        // Find and set up the views in the custom dialog
        val Good = dialogView.findViewById<LinearLayout>(R.id.lnrGood)
        val NotReally = dialogView.findViewById<LinearLayout>(R.id.lnrNotreally)

        Good.setOnClickListener {
            dialog.dismiss()
        }

        NotReally.setOnClickListener {
            dialog.dismiss()
        }
    }

    private fun showOverlay() {
        if (overlayView == null) {
            overlayView = LayoutInflater.from(this).inflate(R.layout.overlay_view, null) as ViewGroup
            val decorView = window.decorView as ViewGroup
            decorView.addView(overlayView)
        }
    }

    private fun removeOverlay() {
        overlayView?.let {
            val decorView = window.decorView as ViewGroup
            decorView.removeView(it)
            overlayView = null
        }
    }
}