package com.cookandroid.scholarshiplike

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.cookandroid.scholarshiplike.adapter.ViewPageAdapter
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_like_content.*
import kotlinx.coroutines.coroutineScope

class LikeContentActivity : AppCompatActivity() {
    @Suppress("PrivatePropertyName")
    private val TAG = javaClass.simpleName

    private var tabLayoutTextArray: ArrayList<String> = arrayListOf("장학금", "매거진")
    private lateinit var viewAdapter: ViewPageAdapter

    // 서비스 연결
    lateinit var mService: DataService
    var mBound: Boolean = false

    /** Defines callbacks for service binding, passed to bindService()  */
    private val connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            val binder = service as DataService.DataLocalBinder
            mService = binder.getService()
            Log.w(TAG, "Service connected")
            mBound = true
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            Log.w(TAG, "Service disconnected")
            mBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_like_content)
        Log.w(TAG, "onCreate()")
        // Bind to LocalService
        Intent(this, DataService::class.java).also { intent ->
            this.bindService(intent, connection, Context.BIND_AUTO_CREATE)
            Log.w(TAG, "Service active")
        }

        // 어댑터 생성, 연결
            viewAdapter = ViewPageAdapter(this)
            viewAdapter.addFragment(LikeContentScholarshipFragment())
            viewAdapter.addFragment(LikeContentMagazineFragment())
            like_viewpager.adapter = viewAdapter

            // 탭 레이아웃 이름 연결
            TabLayoutMediator(like_tabLayout, like_viewpager) { tab, position ->
                tab.text = tabLayoutTextArray[position]
            }.attach()

        Log.w(TAG, "onCreate() end")
    }

    override fun onDestroy() {
        super.onDestroy()

        unbindService(connection)
        Log.w(TAG, "Service deactivate")
        mBound = false
    }

}
