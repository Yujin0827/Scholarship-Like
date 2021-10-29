package com.cookandroid.scholarshiplike

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cookandroid.scholarshiplike.adapter.AlarmRecyclerViewAdapter
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_recycler.*

class AlarmScholarshipFragment: Fragment() {
    @Suppress("PrivatePropertyName")
    private val TAG = javaClass.simpleName
    private var db = Firebase.firestore
    private lateinit var listAdapter: AlarmRecyclerViewAdapter
    var dataList: ArrayList<Alarm> = arrayListOf()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val sRef = db.collection("장학금")
            .document("교내").collection("강원")
            .document("강원대").collection("학과")

        sRef // 작업할 문서
            .get()      // 문서 가져오기
            .addOnSuccessListener { result ->
                for (document in result) {  // 가져온 문서들은 result에 들어감
                    val item = Alarm(document.id, "asdasdasd", "asdfadf")
                    dataList.add(item)
                }
                listAdapter.submitList(dataList)
                Log.w(TAG, "Error aaaaaaa: ")
            }
            .addOnFailureListener { exception ->
                // 실패할 경우
                Log.w(TAG, "Error getting documents: $exception")
            }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Fragment에서 전달받은 list를 넘기면서 ListAdapter 생성
        return inflater.inflate(R.layout.fragment_recycler, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Fragment에서 전달받은 list를 넘기면서 ListAdapter 생성
        listAdapter =
            AlarmRecyclerViewAdapter(
                dataList
            )


        listView.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        // RecyclerView.adapter에 지정
        listView.adapter = listAdapter

//        listAdapter.setOnItemClickListener(object : AlarmRecyclerViewAdapter.OnItemClickListener{
//            override fun onItemClick(v: View, data: Alarm, pos : Int) {
//                Intent(requireActivity(), AlarmDetailActivity::class.java).apply {
//                    //putExtra("data", data)
//                    //addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                }.run { startActivity(this) }
//            }
//
//        })
        listView.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        // RecyclerView.adapter에 지정
        listView.adapter = listAdapter
    }

}

