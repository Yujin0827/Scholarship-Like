package com.cookandroid.scholarshiplike

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_scholarship_all_scholar.*
import androidx.fragment.app.Fragment

class ScholarshipAllscholarFragment : Fragment() {

    val head: MutableList<String> = ArrayList() // expandableList의 부모 리스트
    val body: MutableList<MutableList<String>> = ArrayList() // expandableList의 자식 리스트

    lateinit var listAdapter: ScholarshipExpandableLisviewtAdapter // expandableList 어댑터 선언
    private lateinit var RlistAdapter: ScholarshipRecyclerViewAdapter // 리사이클러뷰 어댑터
    private var db = Firebase.firestore
    var dataList: MutableList<Scholarship> = arrayListOf()
    private lateinit var mContext1: Context //프래그먼트의 정보 받아오는 컨텍스트 선언




    override fun onAttach(context: Context) {
        super.onAttach(context)

        mContext1 = requireActivity()

        val sRef = db.collection("장학금")
            .document("교내").collection("강원")
            .document("강원대").collection("학과")

        sRef // 작업할 문서
            .get()      // 문서 가져오기
            .addOnSuccessListener { result ->
                for (document in result) {  // 가져온 문서들은 result에 들어감
                    val item = Scholarship(document.id, "", "", false)
                    dataList.add(item)
                }
                RlistAdapter.submitList(dataList)
                Log.w("MainActivity", "Error aaaaaaa: ")

            }
            .addOnFailureListener { exception ->
                // 실패할 경우
                Log.w("MainActivity", "Error getting documents: $exception")
            }



    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_scholarship_all_scholar, container, false)


        val koreaScholar: MutableList<String> = ArrayList()
        val univScholar: MutableList<String> = ArrayList()

        head.add("서울")
        head.add("강원")

        body.add(koreaScholar)

        body.add(univScholar)

        val outScholarBt: Button = view.findViewById(R.id.outScholarBt) // 교외 장학금 버튼


        outScholarBt.setOnClickListener { // 교외 장학금 클릭 시 레이아웃 표현
            if (expand_layout.visibility == View.GONE) {
                expand_layout.visibility = View.VISIBLE
            } else {
                expand_layout.visibility = View.GONE
            }


        }


        // Inflate the layout for this fragment
        return view


    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // expandableList 어탭터 연결
        listAdapter = ScholarshipExpandableLisviewtAdapter(this, head, body)
        expandableList.setAdapter(listAdapter)

        // Fragment에서 전달받은 list를 넘기면서 ListAdapter 생성
        RlistAdapter = ScholarshipRecyclerViewAdapter(dataList, mContext1)
        allrecyclerView.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        // RecyclerView.adapter에 지정
        allrecyclerView.adapter = RlistAdapter
    }





}