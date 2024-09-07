package com.collagemaker_makeanything.Activity

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsControllerCompat

open class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Lock orientation to portrait
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        // Set the flags for full-screen layout
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        // Set the status bar text color to black
        setLightStatusBar(true)
    }

    private fun setLightStatusBar(isLight: Boolean) {
        val decorView = window.decorView
        val windowInsetsController = ViewCompat.getWindowInsetsController(decorView)
        windowInsetsController?.isAppearanceLightStatusBars = isLight
    }
}
