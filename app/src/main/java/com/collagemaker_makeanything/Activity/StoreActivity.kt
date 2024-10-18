package com.collagemaker_makeanything.Activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.collagemaker_makeanything.Adapter.StoreTabAdapter
import com.collagemaker_makeanything.R
import com.collagemaker_makeanything.databinding.ActivityStoreBinding
import com.google.android.material.tabs.TabLayout

class StoreActivity : BaseActivity() {

    lateinit var binding : ActivityStoreBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoreBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initview()
    }

    private fun initview() {

        binding.imgback.setOnClickListener { onBackPressed()}

        binding.tabLayout.addTab(binding.tabLayout.newTab().setIcon(R.drawable.sticker))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setIcon(R.drawable.backgrouond))
      //  binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Light FX"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setIcon(R.drawable.aa))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setIcon(R.drawable.store_filter))


        val Myadapter = StoreTabAdapter(supportFragmentManager)
        binding.viewpager.adapter = Myadapter

        binding.viewpager.addOnPageChangeListener(object : TabLayout.TabLayoutOnPageChangeListener(binding.tabLayout){})

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{

            override fun onTabSelected(tab: TabLayout.Tab?)
            {
                if(tab != null)
                {
                    binding.viewpager.currentItem = tab.position
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?)
            {

            }

            override fun onTabReselected(tab: TabLayout.Tab?)
            {

            }

        })
    }
}