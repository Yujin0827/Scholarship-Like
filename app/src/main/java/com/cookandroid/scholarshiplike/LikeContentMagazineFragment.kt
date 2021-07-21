package com.cookandroid.scholarshiplike
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.cookandroid.scholarshiplike.adapter.MagazineRecyclerViewAdapter
import kotlinx.android.synthetic.main.fragment_recycler.*


class LikeContentMagazineFragment : Fragment() {
    @Suppress("PrivatePropertyName")
    private val TAG = javaClass.simpleName

    private lateinit var listAdapter: MagazineRecyclerViewAdapter
    private lateinit var mContext : Context
    private lateinit var magazine: ArrayList<Post>

    // 서비스 연결
    private lateinit var mService: DataService
    private var mBound: Boolean = false

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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Bind to LocalService
        Intent(mContext, DataService::class.java).also { intent ->
            mContext.bindService(intent, connection, Context.BIND_AUTO_CREATE)
            Log.w(TAG, "Service active")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_recycler, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 2초 딜레이 후 코드 시작
        Handler().postDelayed({
            // 서비스의 데이터와 연결
            magazine = mService.magazine

            // Fragment 에서 전달받은 list 를 넘기면서 ListAdapter 생성
            listAdapter = MagazineRecyclerViewAdapter(magazine, mContext)
            listView.layoutManager = GridLayoutManager(activity, 2) //그리드 레아이웃 지정
            listView.adapter = listAdapter //어댑터 연결
        }, 2000) // <-- ms로 조절
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.w(TAG, "destroy")

        mContext.unbindService(connection)
        Log.w(TAG, "Service deactivate")
        mBound = false
    }
}