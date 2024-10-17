package com.collagemaker_makeanything.Adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.collagemaker_makeanything.TemplatesFragment.AllFragment
import com.collagemaker_makeanything.TemplatesFragment.BirthdayFragment
import com.collagemaker_makeanything.TemplatesFragment.CalenderFragment
import com.collagemaker_makeanything.TemplatesFragment.ChristmasFragment
import com.collagemaker_makeanything.TemplatesFragment.FamilyFragment
import com.collagemaker_makeanything.TemplatesFragment.FrameFragment
import com.collagemaker_makeanything.TemplatesFragment.GraduationFragment
import com.collagemaker_makeanything.TemplatesFragment.LoveFragment
import com.collagemaker_makeanything.TemplatesFragment.MotherDayFragment
import com.collagemaker_makeanything.TemplatesFragment.NewyearFragment
import com.collagemaker_makeanything.TemplatesFragment.PrideFragment
import com.collagemaker_makeanything.TemplatesFragment.SchoolLifeFragment
import com.collagemaker_makeanything.TemplatesFragment.SummerFragment
import com.collagemaker_makeanything.TemplatesFragment.TravelFragment
import com.collagemaker_makeanything.TemplatesFragment.WinterFragment



class TabsAdaper(fm: FragmentManager) : FragmentPagerAdapter(fm)
{
    override fun getCount(): Int {
        return 15
    }

    override fun getItem(position: Int): Fragment
    {
        if (position == 0)
        {
            return AllFragment()
        }
        else if (position == 1)
        {
            return BirthdayFragment()
        }
        else if (position == 2)
        {
            return CalenderFragment()
        }
        else if (position == 3)
        {
            return ChristmasFragment()
        }
        else if (position == 4)
        {
            return FamilyFragment()
        }
        else if (position == 5)
        {
            return FrameFragment()
        }
        else if (position == 6)
        {
            return GraduationFragment()
        }
        else if (position == 7)
        {
            return LoveFragment()
        }
        else if (position == 8)
        {
            return MotherDayFragment()
        }
        else if (position == 9)
        {
            return NewyearFragment()
        }
        else if (position == 10)
        {
            return PrideFragment()
        }
        else if (position == 11)
        {
            return SchoolLifeFragment()
        }
        else if (position == 12)
        {
            return SummerFragment()
        }
        else if (position == 13)
        {
            return TravelFragment()
        }
        else if (position == 14)
        {
            return WinterFragment()
        }
        return null!!
    }
}
