package com.cookandroid.scholarshiplike

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.cookandroid.scholarshiplike.adapter.ViewPageAdapter
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_home_search_result.*


class HomeSearchResultActivity : AppCompatActivity() {

    // 검색결과 탭
    private var tabLayoutTextArray: ArrayList<String> = arrayListOf("장학금", "매거진")
    private lateinit var viewAdapter: ViewPageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_search_result)
        Log.d("tag", "onCreate: started")

        // 어댑터 생성, 연결
        viewAdapter = ViewPageAdapter(this)
        viewAdapter.addFragment(HomeSearchScholarshipFragment())
        viewAdapter.addFragment(HomeSearchMagazineFragment())
        search_viewpager.adapter = viewAdapter

        // 탭 레이아웃 이름 연결
        TabLayoutMediator(search_tabLayout, search_viewpager) { tab, position ->
            tab.text = tabLayoutTextArray[position]
        }.attach()



    }

    // 검색어 수신
    private fun getExtra() {

    }

}