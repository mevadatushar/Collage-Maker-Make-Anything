package com.collagemaker_makeanything.Adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.collagemaker_makeanything.StoreFragment.FilterFragment
import com.collagemaker_makeanything.StoreFragment.LightFxFragment
import com.collagemaker_makeanything.StoreFragment.StickerFragment
import com.collagemaker_makeanything.StoreFragment.TextFragment
import com.example.photoeditorpolishanything.Fragment.BackgroundFragment


class StoreTabAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
    override fun getCount(): Int {
        return 4
    }

    override fun getItem(position: Int): Fragment {
        if (position == 0)
        {
            return StickerFragment()
        }
        else if (position == 1)
        {
            return BackgroundFragment()
        }
//        else if (position == 2)
//        {
//            return LightFxFragment()
//        }
        else if (position == 2)
        {
            return TextFragment()
        }
        else if (position == 3)
        {
            return FilterFragment()
        }
        return null!!
    }
}