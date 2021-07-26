package com.cookandroid.scholarshiplike

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_scholarship_all_scholar.*
import androidx.fragment.app.Fragment
import com.cookandroid.scholarshiplike.adapter.ScholarshipExpandableLisviewtAdapter
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.android.synthetic.main.item_calendar_popup.*
import kotlinx.android.synthetic.main.item_scholarship.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class ScholarshipAllscholarFragment : Fragment() {

    private val head: MutableList<String> = ArrayList() // expandableList의 부모 리스트
    private val body: MutableList<MutableList<String>> = ArrayList() // expandableList의 자식 리스트

    lateinit var listAdapter: ScholarshipExpandableLisviewtAdapter // expandableList 어댑터 선언
    private lateinit var RlistAdapter: ScholarshipRecyclerViewAdapter // 리사이클러뷰 어댑터
    private var db = Firebase.firestore
    var dataList: MutableList<Scholarship> = arrayListOf() // 특정 장학금 리스트
    var allDataList: MutableList<Scholarship> = arrayListOf() // 전체 장학금 리스트
    private lateinit var mContext1: Context //프래그먼트의 정보 받아오는 컨텍스트 선언

    private val koreaScholar : MutableList<String> = ArrayList()
    private val outScholar : MutableList<String> = ArrayList()
    private val univScholar : MutableList<String> = ArrayList()

    var user = Firebase.auth.currentUser // 사용자 가져오기
    private lateinit var userUid : String
    private lateinit var userUniv : String




    override fun onAttach(context: Context) {
        super.onAttach(context)

        mContext1 = requireActivity()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_scholarship_all_scholar, container, false)

       area() // 지역 리스트 가져오기

        head.add("국가 장학금")
        head.add("교외 장학금")
        head.add("교내 장학금")

        body.add(koreaScholar)
        body.add(outScholar)
        body.add(univScholar)

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




        expandableList.setOnGroupClickListener {
                head, v, groupPosition :Int, parentPosition ->

            when (groupPosition) {
                0 -> {  // 국가 장학금 클릭 시
                    getData("Nation", "소득연계형") // 데이터 불러옴
                }
                2 -> { // 교내 장학금 클릭 시
                    //User Email
                    user?.let {
                        userUid  = user!!.uid
                    }

                    //User 필드 값
                    db.collection("Users")
                        .document(userUid)
                        .get()
                        .addOnSuccessListener{ document ->
                            if (document != null){
                                if(document.getString("univ") != null){
                                    userUniv = document.getString("univ")!!
                                } }
                    getData("UnivScholar",userUniv) // 데이터 불러옴
                }}
            }
            false
        }

        expandableList.setOnChildClickListener {  // childList 클릭 시 장학금 가져오기
                head, view, groupPosition, childPosition : Int, l ->
            val areaText = outScholar[childPosition]

            getData("OutScholar",areaText)
            false
        }
    }


    private fun area(){ // 지역 리스트 가져오기
        db.collection("Scholarship").document("OutScholar")
            .get().addOnSuccessListener { document ->
                if (document != null) {
                    val city = document.get("area") as MutableList<String>
                    for(i in 0 until city.size-1){
                        if(city[i] == "전국"){
                            outScholar.add(city[i])
                        }
                    }
                    for(i in 0 until city.size-1){
                        if(city[i] != "전국"){
                            outScholar.add(city[i])
                        }
                    }
                    Log.w("ScholarshipAllscholarFragment", outScholar.toString())
                }
            }
    }

    private fun getData( kind : String, etc : String){
        // 작업할 문서
        db.collection("Scholarship")
            .document(kind)
            .collection(etc)
            .get()      // 문서 가져오기
            .addOnSuccessListener { result ->

                RlistAdapter.notifyDataSetChanged()
                dataList.clear()

                for (document in result) {  // 가져온 문서들은 result에 들어감

                    //  필드값 가져오기
                    val period = document["period"] as Map<String, Timestamp>
                    val startdate = period.get("startDate")?.toDate()
                    val enddate = period.get("endDate")?.toDate()
                    val institution = document["paymentInstitution"] as String

                    val date = SimpleDateFormat("yyyy-MM-dd") // 날짜 형식으로 변환

                    if(startdate == null && enddate == null){
                        val item = Scholarship(document.id, "자동 신청", "", institution)
                        dataList.add(item)
                    }
                    else{
                        val item = Scholarship(document.id, date.format(startdate!!), date.format(enddate!!), institution)
                        dataList.add(item)
                    }
                }
                RlistAdapter.submitList(dataList)
                Log.w("ScholarshipAllscholarFragment", "UnivScholar Data")

            }
            .addOnFailureListener { exception ->
                // 실패할 경우
                Log.w("ScholarshipAllscholarFragment", "Error getting UnivScholar documents: $exception")
            }

    }


}