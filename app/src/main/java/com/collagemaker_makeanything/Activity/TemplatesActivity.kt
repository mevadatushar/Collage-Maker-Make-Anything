package com.collagemaker_makeanything.Activity

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.collagemaker_makeanything.Adapter.CollageTemplateAdapter
import com.collagemaker_makeanything.Adapter.TabsAdaper
import com.collagemaker_makeanything.R
import com.collagemaker_makeanything.databinding.ActivityTemplatesBinding

import com.google.android.material.tabs.TabLayout


class TemplatesActivity : BaseActivity() {
    lateinit var recyclerView: RecyclerView
    lateinit var adapter: CollageTemplateAdapter
    lateinit var binding: ActivityTemplatesBinding
    private val PICK_IMAGE_REQUEST = 1


    companion object {
        @SuppressLint("StaticFieldLeak")
        var contexts: Context? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTemplatesBinding.inflate(layoutInflater)
        setContentView(binding.root)



        initView()
    }


    private fun initView() {

        binding.imgBack.setOnClickListener { onBackPressed() }

        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(R.string.All))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(R.string.Birthday))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(R.string.Calendar))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(R.string.Christmas))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(R.string.Family))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(R.string.Frame))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(R.string.Graduation))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(R.string.Love))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(R.string.MothersDay))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(R.string.NewYear))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(R.string.Pride))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(R.string.SchoolLife))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(R.string.Summer))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(R.string.Travel))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(R.string.Winter))

        val Myadapter = TabsAdaper(supportFragmentManager)
        binding.viewpager.adapter = Myadapter

        binding.viewpager.addOnPageChangeListener(object :
            TabLayout.TabLayoutOnPageChangeListener(binding.tabLayout) {})

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab != null) {
                    binding.viewpager.currentItem = tab.position
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
        })

        for (i in 0 until binding.tabLayout.tabCount) {
            val tab = (binding.tabLayout.getChildAt(0) as ViewGroup).getChildAt(i) as ViewGroup
            val layoutParams = tab.layoutParams as ViewGroup.MarginLayoutParams
            layoutParams.setMargins(15, 0, 15, 0) // Adjust margins as needed
            tab.requestLayout()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            val selectedImageUri = data.data
            // Use the selectedImageUri to load the image in your ImageView or pass it to another activity
        }
    }
}