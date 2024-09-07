package com.collagemaker_makeanything.Activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.collagemaker_makeanything.R

class SplashScreenActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        initview()

    }

    private fun initview() {

        Handler().postDelayed({
            val mainIntent = Intent(this, DashboardActivity::class.java)
            startActivity(mainIntent)
            finish()
        }, 2000)

    }
}