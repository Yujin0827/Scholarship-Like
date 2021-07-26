package com.cookandroid.scholarshiplike

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_recycler.*

class LikeContentScholarshipFragment : Fragment() {
    @Suppress("PrivatePropertyName")
    private val TAG = javaClass.simpleName

    private lateinit var listAdapter: ScholarshipRecyclerViewAdapter
    var dataList: MutableList<Scholarship> = arrayListOf()
    private lateinit var mContext1 : Context //프래그먼트의 정보 받아오는 컨텍스트 선언

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext1 = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.w(TAG, "create view")
        return inflater.inflate(R.layout.fragment_recycler, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Fragment에서 전달받은 list를 넘기면서 ListAdapter 생성
        listAdapter = ScholarshipRecyclerViewAdapter(dataList,mContext1)
        listView.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        // RecyclerView.adapter에 지정
        listView.adapter = listAdapter

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.w(TAG, "create")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.w(TAG, "destroy")
    }
}