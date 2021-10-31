package com.cookandroid.scholarshiplike

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import com.cookandroid.scholarshiplike.adapter.ViewPageAdapter
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_like_content.*

class LikeContentActivity : AppCompatActivity() {
    @Suppress("PrivatePropertyName")
    private val TAG = javaClass.simpleName

    private var tabLayoutTextArray: ArrayList<String> = arrayListOf("장학금", "매거진")
    private lateinit var viewAdapter: ViewPageAdapter

    // 서비스 연결
    var mService: DataService = DataService()
    var mBound: Boolean = false

    /** Defines callbacks for service binding, passed to bindService()  */
    private val connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            val binder = service as DataService.DataLocalBinder
            mService = binder.getService()
            Log.w(TAG, "Service connected")
            mBound = true

            if(mBound) {
                setAdpater()
            }
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            Log.w(TAG, "Service disconnected")
            mBound = false
        }
    }

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_like_content)
        Log.w(TAG, "onCreate()")
        // Bind to LocalService
        Intent(this, DataService::class.java).also { intent ->
            this.bindService(intent, connection, Context.BIND_AUTO_CREATE)
            Log.w(TAG, "Service active")
        }

        // 화면 전환 방지 (세로로 고정)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

//        // 어댑터 생성, 연결
//            viewAdapter = ViewPageAdapter(this)
//            viewAdapter.addFragment(LikeContentScholarshipFragment())
//            viewAdapter.addFragment(LikeContentMagazineFragment())
//            like_viewpager.adapter = viewAdapter
//
//            // 탭 레이아웃 이름 연결
//            TabLayoutMediator(like_tabLayout, like_viewpager) { tab, position ->
//                tab.text = tabLayoutTextArray[position]
//            }.attach()

        Log.w(TAG, "onCreate() end")
    }

    override fun onDestroy() {
        super.onDestroy()

        unbindService(connection)
        Log.w(TAG, "Service deactivate")
        mBound = false
    }

    fun setAdpater() {
        // 어댑터 생성, 연결
        viewAdapter = ViewPageAdapter(this)
        viewAdapter.addFragment(LikeContentScholarshipFragment())
        viewAdapter.addFragment(LikeContentMagazineFragment())
        like_viewpager.adapter = viewAdapter

        // 탭 레이아웃 이름 연결
        TabLayoutMediator(like_tabLayout, like_viewpager) { tab, position ->
            tab.text = tabLayoutTextArray[position]
        }.attach()
    }

}
