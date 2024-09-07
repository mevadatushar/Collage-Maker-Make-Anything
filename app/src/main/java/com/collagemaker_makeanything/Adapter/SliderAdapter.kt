package com.collagemaker_makeanything.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import com.collagemaker_makeanything.R

class SliderAdapter(private val imageSlider: List<Int>, private val context: Context) : PagerAdapter() {

    override fun getCount(): Int = imageSlider.size

    override fun isViewFromObject(view: View, obj: Any): Boolean = view == obj

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = LayoutInflater.from(context).inflate(R.layout.slider_item_layout, container, false)
        val imageView: ImageView = view.findViewById(R.id.iv_auto_image_slider)
        imageView.setImageResource(imageSlider[position])
        container.addView(view)
        return view

    }

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        container.removeView(obj as View)
    }
}
