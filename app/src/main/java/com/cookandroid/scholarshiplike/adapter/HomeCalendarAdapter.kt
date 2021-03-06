package com.cookandroid.scholarshiplike.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.cookandroid.scholarshiplike.HomeCalendarDetailFragment

class HomeCalendarAdapter (fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    val firstFragmentPosition = Int.MAX_VALUE / 2

    override fun getItemCount(): Int = Int.MAX_VALUE

    override fun createFragment(position: Int): Fragment {
        val homeCalendarDetailFragment = HomeCalendarDetailFragment()
        homeCalendarDetailFragment.pageIndex = position
        return homeCalendarDetailFragment
    }
}
