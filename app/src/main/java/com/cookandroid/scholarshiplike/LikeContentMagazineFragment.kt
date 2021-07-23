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
import kotlin.properties.Delegates


class LikeContentMagazineFragment : Fragment() {
    @Suppress("PrivatePropertyName")
    private val TAG = javaClass.simpleName

    private lateinit var listAdapter: MagazineRecyclerViewAdapter
    private lateinit var mContext : Context
    private lateinit var magazine: ArrayList<Post>



    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_recycler, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.w(TAG, "onViewCreated()")

        // 서비스의 데이터와 연결
            magazine = (activity as LikeContentActivity).mService.mMagazine
            Log.w(TAG, "$magazine : MAGAZINE")
            // Fragment 에서 전달받은 list 를 넘기면서 ListAdapter 생성
            listAdapter = MagazineRecyclerViewAdapter(magazine, mContext)

            listView.layoutManager = GridLayoutManager(activity, 2) //그리드 레아이웃 지정
            listView.adapter = listAdapter //어댑터 연결
    }


    override fun onDestroy() {
        super.onDestroy()
        Log.w(TAG, "destroy")
    }
}