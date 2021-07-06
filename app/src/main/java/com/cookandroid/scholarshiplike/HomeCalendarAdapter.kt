package com.cookandroid.scholarshiplike

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class HomeCalendarAdapter (fragmentActivity: FragmentActivity)
    : FragmentStateAdapter(fragmentActivity) {

    private val pageCount = Int.MAX_VALUE
    val firstFragmentPosition = Int.MAX_VALUE / 2

    override fun getItemCount(): Int = Int.MAX_VALUE

    override fun createFragment(position: Int): Fragment {
        val homeCalendarDetailFragment = HomeCalendarDetailFragment()
        homeCalendarDetailFragment.pageIndex = position
        return homeCalendarDetailFragment
    }
}