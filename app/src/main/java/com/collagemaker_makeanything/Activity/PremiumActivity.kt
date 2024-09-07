package com.collagemaker_makeanything.Activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.collagemaker_makeanything.R
import com.collagemaker_makeanything.databinding.ActivityPremiumBinding

class PremiumActivity : BaseActivity() {
    lateinit var binding: ActivityPremiumBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPremiumBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()

    }

    private fun initView() {
        with(binding){
            imgBack.setOnClickListener {
                onBackPressed()
            }

            btnFreeTrial.setOnClickListener {
                val intent = Intent(this@PremiumActivity, DashboardActivity::class.java)
                startActivity(intent)
            }

        }
    }
}