package com.cookandroid.scholarshiplike

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cookandroid.scholarshiplike.adapter.ViewPageAdapter
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_alarm.*


class AlarmActivity : AppCompatActivity() {
    private var tabLayoutTextArray: ArrayList<String> = arrayListOf("전체", "장학금")
    private lateinit var viewAdapter: ViewPageAdapter

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm)

        // 화면 전환 방지 (세로로 고정)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        //어댑터 생성, 연결
        viewAdapter = ViewPageAdapter(this)
        viewAdapter.addFragment(AlarmAllFragment())
        viewAdapter.addFragment(AlarmScholarshipFragment())
        alarm_viewpager.adapter = viewAdapter

        // 탭 레이아웃 이름 연결
        TabLayoutMediator(alarm_tabLayout, alarm_viewpager){ tab, position->
            tab.text = tabLayoutTextArray[position]
        }.attach()

    }
}
