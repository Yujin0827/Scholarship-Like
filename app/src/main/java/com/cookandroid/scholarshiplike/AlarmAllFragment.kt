package com.cookandroid.scholarshiplike

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cookandroid.scholarshiplike.adapter.AlarmRecyclerViewAdapter
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_recycler.*
import java.text.SimpleDateFormat

class AlarmAllFragment: Fragment() {
    private lateinit var listAdapter: AlarmRecyclerViewAdapter
    private var db = Firebase.firestore
    var dataList: MutableList<Alarm> = arrayListOf()
    private lateinit var text: TextView

    override fun onAttach(context: Context) {
        super.onAttach(context)
        getAlarmData()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_recycler, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Fragment에서 전달받은 list를 넘기면서 ListAdapter 생성
        listAdapter =
            AlarmRecyclerViewAdapter(
                dataList
            )

        // item 클릭시 새 activity 호
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

    // DB에서 알람 데이터 가져오기
   fun getAlarmData() {
        val sRef = db.collection("Alarm")
            .orderBy("pushTime", Query.Direction.DESCENDING)
        val sdf = SimpleDateFormat("yyyy년 MM월 dd일")

        sRef // 작업할 문서
            .get()      // 문서 가져오기
            .addOnSuccessListener { result ->
                for (document in result) {  // 가져온 문서들은 result에 들어감
                    val date = (document["pushTime"] as Timestamp).toDate()
                    val formattedDate = sdf.format(date).toString() // 날짜 형식 변환
                    val item = Alarm("1", document["content"].toString(), formattedDate)
                    dataList.add(item)
                }
                listAdapter.submitList(dataList)
                Log.w("MainActivity", "Error aaaaaaa: ")

            }
            .addOnFailureListener { exception ->
                // 실패할 경우
                Log.w("MainActivity", "Error getting documents: $exception")
            }
    }

}

