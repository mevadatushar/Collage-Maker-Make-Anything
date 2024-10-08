package com.collagemaker_makeanything.Activity

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.google.android.material.internal.EdgeToEdgeUtils.setLightStatusBar

open class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Lock orientation to portrait
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        // Set the flags for full-screen layout
//        window.setFlags(
//            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
//            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
//        )

// Enable full-screen mode
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Hide the system bars (navigation bar and status bar)
        hideNavigationBar()

        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Set the status bar text color to black
        setLightStatusBar(true)

    }

    private fun setLightStatusBar(isLight: Boolean) {
        val decorView = window.decorView
        val windowInsetsController = ViewCompat.getWindowInsetsController(decorView)
        windowInsetsController?.isAppearanceLightStatusBars = isLight
    }

    private fun hideNavigationBar() {
        val decorView = window.decorView
        val windowInsetsController = ViewCompat.getWindowInsetsController(decorView)

        windowInsetsController?.hide(WindowInsets.Type.navigationBars())
    }
}
