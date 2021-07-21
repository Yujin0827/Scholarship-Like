package com.cookandroid.scholarshiplike

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.cookandroid.scholarshiplike.adapter.ViewPageAdapter
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_like_content.*

class LikeContentActivity : AppCompatActivity() {
    @Suppress("PrivatePropertyName")
    private val TAG = javaClass.simpleName

    private var tabLayoutTextArray: ArrayList<String> = arrayListOf("장학금", "매거진")
    private lateinit var viewAdapter: ViewPageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_like_content)

        // 어댑터 생성, 연결
        viewAdapter = ViewPageAdapter(this)
        viewAdapter.addFragment(LikeContentScholarshipFragment())
        viewAdapter.addFragment(LikeContentMagazineFragment())
        like_viewpager.adapter = viewAdapter

        // 탭 레이아웃 이름 연결
        TabLayoutMediator(like_tabLayout, like_viewpager){ tab, position->
            tab.text = tabLayoutTextArray[position]
        }.attach()

        // 서비스 시작
        Intent(this, DataService::class.java).also { intent ->
            startService(intent)
            Log.w(TAG, "startService()")
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        // 서비스 종료
        Intent(this, DataService::class.java).also { intent ->
            stopService(intent)
            Log.w(TAG, "stopService()")
        }
    }

}
