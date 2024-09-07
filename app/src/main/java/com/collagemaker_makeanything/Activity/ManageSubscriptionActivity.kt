package com.collagemaker_makeanything.Activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.collagemaker_makeanything.R
import com.collagemaker_makeanything.databinding.ActivityManageSubscriptionBinding

class ManageSubscriptionActivity : BaseActivity() {
    lateinit var binding: ActivityManageSubscriptionBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManageSubscriptionBinding.inflate(layoutInflater)
        setContentView(binding.root)
            initView()
    }

    private fun initView() {
        binding.imgBack.setOnClickListener {
            onBackPressed()
        }
    }
}