package com.collagemaker_makeanything.Activity

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager.widget.ViewPager
import com.collagemaker_makeanything.Adapter.SliderAdapter
import com.collagemaker_makeanything.R
import com.collagemaker_makeanything.databinding.ActivityDashboardBinding

class DashboardActivity : BaseActivity() {
    private lateinit var binding: ActivityDashboardBinding


    private val imageSlider = arrayListOf(
        R.drawable.featuredimage,
        R.drawable.grid_eid,
        R.drawable.grid_congrutulation,
        R.drawable.grid_love,
        R.drawable.grid_summer,
        R.drawable.grid_ai,
        R.drawable.grid_removeobject,
        R.drawable.grid_collage,
        R.drawable.grid_calender,
        R.drawable.grid_remove_bg,
        R.drawable.grid_freestyle
    )

    private lateinit var viewPager: ViewPager
    private lateinit var dotsLayout: LinearLayout
    private lateinit var dots: Array<ImageView?>
    private val NUM_PAGES = imageSlider.size

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)



        initView()

        binding.navSettings.elevation = 4f
    }

    private fun initView() {
        viewPager = findViewById(R.id.viewpager)
        dotsLayout = findViewById(R.id.dotsLayout)

        val sliderAdapter = SliderAdapter(imageSlider, this)
        viewPager.adapter = sliderAdapter

        addDotsIndicator()
        viewPager.addOnPageChangeListener(viewListener)

        binding.Collage.setOnClickListener {
            val intent = Intent(this@DashboardActivity, ImagePickerActivity::class.java)
            intent.putExtra("imagePickLimit", 100)
            intent.putExtra("modeType", "collage")  // Pass mode type as "collage"
            startActivity(intent)
        }

        binding.multiFit.setOnClickListener {
            val intent = Intent(this@DashboardActivity, ImagePickerActivity::class.java)
            intent.putExtra("imagePickLimit", 10)
            intent.putExtra("modeType", "multifit")  // Pass mode type as "multifit"
            startActivity(intent)
        }

        binding.imgPremium.setOnClickListener {
            val intent = Intent(this@DashboardActivity, PremiumActivity::class.java)
            startActivity(intent)

        }

        binding.Templates.setOnClickListener {
            val intent = Intent(this@DashboardActivity, TemplatesActivity::class.java)
            startActivity(intent)

        }

      binding.imgShop.setOnClickListener {
            val intent = Intent(this@DashboardActivity, StoreActivity::class.java)
            startActivity(intent)

        }

       /* binding.Freestyle.setOnClickListener {
            val intent = Intent(this@DashboardActivity, ImagePickerActivity::class.java)
            startActivity(intent)

        } */

        binding.navSettings.setOnClickListener {
            val intent = Intent(this@DashboardActivity, SettingsActivity::class.java)
            startActivity(intent)
        }

        binding.edit.setOnClickListener {
            val intent = Intent(this@DashboardActivity, EditImagePickerActivity::class.java)
            startActivity(intent)

        }


//        binding.imgPremium.setOnClickListener {
//            val intent = Intent(this@DashboardActivity, PremiumActivity::class.java)
//            startActivity(intent)
//
//        }

    }

    private fun addDotsIndicator() {
        dots = arrayOfNulls(NUM_PAGES)
        dotsLayout.removeAllViews()

        for (i in 0 until NUM_PAGES) {
            dots[i] = ImageView(this)
            dots[i]?.setImageResource(R.drawable.inactive_dot)
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(8, 15, 8, 15)
            dotsLayout.addView(dots[i], params)
        }

        if (dots.isNotEmpty()) {
            dots[0]?.setImageResource(R.drawable.active_dot)
        }

    }

    private val viewListener = object : ViewPager.OnPageChangeListener {
        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {
        }

        override fun onPageSelected(position: Int) {
            for (i in dots.indices) {
                dots[i]?.setImageResource(R.drawable.inactive_dot)
            }
            dots[position]?.setImageResource(R.drawable.active_dot)
        }

        override fun onPageScrollStateChanged(state: Int) {}
    }
}