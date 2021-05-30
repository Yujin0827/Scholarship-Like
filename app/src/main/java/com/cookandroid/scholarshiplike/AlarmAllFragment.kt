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
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_recycler.*

class AlarmAllFragment: Fragment() {
    private lateinit var listAdapter: AlarmRecyclerViewAdapter
    private var db = Firebase.firestore
    var dataList: MutableList<Alarm> = arrayListOf()
    private lateinit var text: TextView

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val sRef = db.collection("장학금")
            .document("교내").collection("강원")
            .document("강원대").collection("학과")

        sRef // 작업할 문서
            .get()      // 문서 가져오기
            .addOnSuccessListener { result ->
                for (document in result) {  // 가져온 문서들은 result에 들어감
                    val item = Alarm("1", document.id, "asdfadf")
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_recycler, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Fragment에서 전달받은 list를 넘기면서 ListAdapter 생성
        listAdapter = AlarmRecyclerViewAdapter(dataList)

        // item 클릭시 새 activity 호출
        listAdapter.setOnItemClickListener(object : AlarmRecyclerViewAdapter.OnItemClickListener{
            override fun onItemClick(v: View, data: Alarm, pos : Int) {
                Intent(requireActivity(), AlarmDetailActivity::class.java).apply {
                    //putExtra("data", data)
                    //addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }.run { startActivity(this) }
            }

        })

        listView.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        // RecyclerView.adapter에 지정
        listView.adapter = listAdapter

    }

}

