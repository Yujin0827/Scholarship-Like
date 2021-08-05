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
import com.cookandroid.scholarshiplike.adapter.ScholarshipExpandableLisviewtAdapter
import com.cookandroid.scholarshiplike.databinding.FragmentHomeBinding
import com.cookandroid.scholarshiplike.databinding.FragmentScholarshipAllScholarBinding
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_scholarship_detail.*
import kotlinx.android.synthetic.main.fragment_scholarship_all_scholar.*
import kotlinx.coroutines.*
import java.text.SimpleDateFormat


class ScholarshipAllscholarFragment : Fragment() {

    private var abinding: FragmentScholarshipAllScholarBinding? = null   // 바인딩 객체
    private val binding get() = abinding!!              // 바인딩 변수 재선언 (매번 null 체크x)

    private val head: MutableList<String> = ArrayList() // expandableList의 부모 리스트
    private val body: MutableList<MutableList<String>> = ArrayList() // expandableList의 자식 리스트

    lateinit var listAdapter: ScholarshipExpandableLisviewtAdapter // expandableList 어댑터 선언
    private lateinit var RlistAdapter: ScholarshipRecyclerViewAdapter // 리사이클러뷰 어댑터
    private var db = Firebase.firestore
    var dataList: MutableList<Scholarship> = arrayListOf() // 특정 장학금 리스트
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

        abinding = FragmentScholarshipAllScholarBinding.inflate(inflater, container, false)
        val view = binding.root

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

        //User Email
        user?.let {
            userUid  = user!!.uid
        }

        //User's Univ
        db.collection("Users")
            .document(userUid)
            .get()
            .addOnSuccessListener{ document ->
                if (document != null){
                    if(document.getString("univ") != null){
                        userUniv = document.getString("univ")!!
                    }
                }
            }

        // expandableList Adapter 연결
        listAdapter = ScholarshipExpandableLisviewtAdapter(this, head, body)
        expandableList.setAdapter(listAdapter)


        // Fragment에서 전달받은 list를 넘기면서 ListAdapter 생성
        RlistAdapter = ScholarshipRecyclerViewAdapter(dataList, mContext1)
        allrecyclerView.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        // RecyclerView.adapter에 지정
        allrecyclerView.adapter = RlistAdapter



        expandableList.setOnGroupClickListener { // expandlist 부모 리스트 클릭 시 장학금 데이터 가져오기
                head, v, groupPosition :Int, parentPosition ->

            when (groupPosition) {
                0 -> {  // 국가 장학금 클릭 시
                    getData("Nation", "paymentInstitution", "한국장학재단") // 데이터 불러옴
                }
                2 -> { // 교내 장학금 클릭 시
                    getData("UnivScholar", "univ", userUniv)
                }
            }
            false
        }

        expandableList.setOnChildClickListener {  // expandlist 자식 리스트 클릭 시 장학금 데이터 가져오기
                head, view, groupPosition, childPosition : Int, l ->
            val areaText : String = outScholar[childPosition]

            getData("OutScholar", "place", areaText)

            false
        }
    }


    private fun area(){ // 지역 리스트 가져오기
        db.collection("Scholarship").document("OutScholar")
            .get().addOnSuccessListener { document ->
                if (document != null) {
                    val city = document.get("area") as MutableList<String>
                    for(i in 0 until city.size){
                        if(city[i] == "전국"){
                            outScholar.add(city[i])
                        }
                    }
                    for(i in 0 until city.size){
                        if(city[i] != "전국"){
                            outScholar.add(city[i])
                        }
                    }
                    Log.w("ScholarshipAllscholarFragment - arealist", outScholar.toString())
                }
            }
    }

   private fun getData( kind : String, field_key : String, field_value : String){ // 데이터 가져오기

        // 작업할 문서
        db.collection("Scholarship")
            .document(kind)
            .collection("ScholarshipList")
            .whereEqualTo(field_key, field_value)
            .get()
            .addOnSuccessListener{ result ->

                RlistAdapter.notifyDataSetChanged()
                dataList.clear() // 리스트 재정의

                for(document in result){
                    if(document != null){
                        if(document["period2"] != null){
                            //  필드값 가져오기
                            val paymentType = document["paymentType"].toString()
                            val period = document["period"] as Map<String, Timestamp>
                            val startdate = period.get("startDate")?.toDate()
                            val enddate = period.get("endDate")?.toDate()
                            val period2 = document["period2"] as Map<String, Timestamp>
                            val startdate2 = period2.get("startDate2")?.toDate()
                            val enddate2 = period2.get("endDate2")?.toDate()

                            val institution = document["paymentInstitution"].toString()

                            val date = SimpleDateFormat("yyyy-MM-dd") // 날짜 형식으로 변환

                            val item = Scholarship(paymentType, document.id, date.format(startdate!!), date.format(enddate!!), date.format(startdate2!!), date.format(enddate2!!), institution)
                            dataList.add(item)

                            RlistAdapter.submitList(dataList)
                            Log.w("ScholarshipAllscholarFragment-allData",  document.id)


                        }
                        else{
                            //  필드값 가져오기
                            val paymentType = document["paymentType"].toString()
                            val period = document["period"] as Map<String, Timestamp>
                            val startdate = period.get("startDate")?.toDate()
                            val enddate = period.get("endDate")?.toDate()

                            val institution = document["paymentInstitution"].toString()

                            val date = SimpleDateFormat("yyyy-MM-dd") // 날짜 형식으로 변환


                            if(startdate == null && enddate == null){
                                val item = Scholarship(paymentType, document.id, "자동 신청", "","","", institution)
                                dataList.add(item)
                            }
                            else if(startdate == enddate){
                                val item = Scholarship(paymentType, document.id, " 추후 기간 공지", "","", "", institution)
                                dataList.add(item)
                            }
                            else{
                                val item = Scholarship(paymentType, document.id, date.format(startdate!!), date.format(enddate!!), "", "", institution)
                                dataList.add(item)
                            }

                            RlistAdapter.submitList(dataList)
                            Log.w("ScholarshipAllscholarFragment-allData",  document.id)
                        }
                    }
                }
            }
            .addOnFailureListener { exception ->
                // 실패할 경우
                Log.w("ScholarshipAllscholarFragment", "Error getting UnivScholar documents: $exception")
            }

    }

    private fun allData(kind : String){ // 데이터 가져오기
            // 작업할 문서
            db.collection("Scholarship")
                .document(kind)
                .collection("ScholarshipList")
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        if (document != null) {
                            val period2 = document["period2"]
                            if(period2 == null){
                                //  필드값 가져오기
                                val paymentType = document["paymentType"].toString()
                                val period = document["period"] as Map<String, Timestamp>
                                val startdate = period.get("startDate")?.toDate()
                                val enddate = period.get("endDate")?.toDate()

                                val institution = document["paymentInstitution"].toString()

                                val date = SimpleDateFormat("yyyy-MM-dd") // 날짜 형식으로 변환




                                if(startdate == null && enddate == null){
                                    val item = Scholarship(paymentType, document.id, "자동 신청", "","","", institution)
                                    dataList.add(item)
                                }
                                else if(startdate == enddate){
                                    val item = Scholarship(paymentType, document.id, "추후 공지", "","", "", institution)
                                    dataList.add(item)
                                }
                                else{
                                    val item = Scholarship(paymentType, document.id, date.format(startdate!!), date.format(enddate!!), "", "", institution)
                                    dataList.add(item)
                                }

                                RlistAdapter.submitList(dataList)
                                Log.w("ScholarshipAllscholarFragment-allData",  document.id)

                            }
                            else{
                                //  필드값 가져오기
                                val paymentType = document["paymentType"].toString()
                                val period = document["period"] as Map<String, Timestamp>
                                val startdate = period.get("startDate")?.toDate()
                                val enddate = period.get("endDate")?.toDate()

                                val period2 = document["period2"] as Map<String, Timestamp>
                                val startdate2 = period2.get("startDate2")?.toDate()
                                val enddate2 = period2.get("endDate2")?.toDate()

                                val institution = document["paymentInstitution"].toString()

                                val date = SimpleDateFormat("yyyy-MM-dd") // 날짜 형식으로 변환

                                val item = Scholarship(paymentType, document.id, date.format(startdate!!), date.format(enddate!!), date.format(startdate2!!), date.format(enddate2!!), institution)
                                dataList.add(item)

                                RlistAdapter.submitList(dataList)
                                Log.w("ScholarshipAllscholarFragment-allData",  document.id)

                            }
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    // 실패할 경우
                    Log.w(
                        "ScholarshipAllscholarFragment",
                        "Error getting UnivScholar documents: $exception"
                    )
                }

    }

    override fun onResume() {
        super.onResume()

        // 모든 장학금 불러오기
        allData("Nation")
        allData("OutScholar")
        allData("UnivScholar")


    }
    // 프래그먼트 파괴
    override fun onDestroyView() {
        super.onDestroyView()
        abinding = null
    }



}