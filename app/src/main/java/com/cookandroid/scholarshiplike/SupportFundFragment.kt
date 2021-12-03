package com.cookandroid.scholarshiplike

import VerticalItemDecorator
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cookandroid.scholarshiplike.adapter.SupportFundExpandableLisviewtAdapter
import com.cookandroid.scholarshiplike.adapter.SupportFundRecyclerViewAdapter
import com.cookandroid.scholarshiplike.databinding.FragmentSupportFundBinding
import com.google.firebase.Timestamp
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import kotlin.concurrent.thread

class SupportFundFragment : Fragment() {

    // binding
    private var _binding: FragmentSupportFundBinding? = null    // 바인딩 객체
    private val binding get() = _binding!!                      // 바인딩 변수 재선언 (매번 null 체크x)

    private val head: MutableList<String> = ArrayList()     // expandableList의 부모 리스트
    private val body: MutableList<MutableList<String>> = ArrayList()    // expandableList의 자식 리스트
    lateinit var areaAdapter: SupportFundExpandableLisviewtAdapter      // expandableList 어댑터 선언
    private lateinit var listAdapter: SupportFundRecyclerViewAdapter    // 리사이클러뷰 어댑터
    private lateinit var slistAdapter: SupportFundRecyclerViewAdapter
    private lateinit var glistAdapter: SupportFundRecyclerViewAdapter


    private var db = Firebase.firestore
    var ref =  db.collectionGroup("Support")
    var dataList: MutableList<Support> = arrayListOf()    // 특정 지원금 리스트
    var slist: MutableList<Support> = arrayListOf()
    var glist: MutableList<Support> = arrayListOf()
    private lateinit var mContext1: Context

    private val nationSupport : MutableList<String> = ArrayList()
    private val areaSupport : MutableList<String> = ArrayList()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext1 = requireActivity()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentSupportFundBinding.inflate(inflater, container, false)
        val view = binding.root

        area()  // 지역 리스트

        head.add("국가 지원금")
        head.add("지역별 지원금")

        body.add(nationSupport)
        body.add(areaSupport)

        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // expandableList Adapter 연결
        areaAdapter = SupportFundExpandableLisviewtAdapter(this, head, body)
        binding.expandableList.setAdapter(areaAdapter)

        // Fragment에서 전달받은 list를 넘기면서 ListAdapter 생성
        listAdapter = SupportFundRecyclerViewAdapter(dataList, mContext1)
        slistAdapter = SupportFundRecyclerViewAdapter(slist, mContext1)
        glistAdapter = SupportFundRecyclerViewAdapter(glist, mContext1)

        binding.supportRecyclerView.addItemDecoration(VerticalItemDecorator(5)) // recyclerview 항목 간격
        binding.supportRecyclerView.addItemDecoration(HorizontalItemDecorator(10))
        binding.supportRecyclerView.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)



        // expandlist 부모 리스트 클릭 시 장학금 데이터 가져오기
        binding.expandableList.setOnGroupClickListener { head, v, groupPosition: Int, parentPosition ->
            when (groupPosition) {
                0 -> {  // 국가 지원금 클릭 시
                    getData("SupportNation") // 데이터 불러옴
                }
                1 -> { // 지역 지원금 클릭 var ref =  db.collectionGroup("Support")시

                }
            }
            false
        }

        // expandlist 자식 리스트 클릭 시 장학금 데이터 가져오기
        binding.expandableList.setOnChildClickListener { head, view, groupPosition, childPosition: Int, l ->
            val areaText: String = areaSupport[childPosition]

            if (areaText == "전체") {
                thread(start = true) {

                    getData("SupportArea")
                }
            }
            else if(areaText == "서울") {
               getAreaData(slistAdapter, slist)
            }
            else if(areaText == "강원"){
                getAreaData(glistAdapter, glist)
            }
            false


        }
    }

    // 지역별 지원금 -> 지역 setting
    private fun area(){
        db.collection("Scholarship").document("OutScholar")
            .get().addOnSuccessListener { document ->
                if (document != null) {
                    val city = document.get("area") as MutableList<String>
                    for(i in 0 until city.size){
                        if(city[i] == "전체"){
                            areaSupport.add(city[i])
                        }
                    }
                    for(i in 0 until city.size){
                        if(city[i] != "전체"){
                            areaSupport.add(city[i])
                        }
                    }
                    Log.w("SupportFundFragment-arealist", areaSupport.toString())
                }
            }
    }

    // 프래그먼트 생성시 툴바 hide
    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()

        // 아래 스와이핑으로 새로고침
        binding.swipeRefreshFragmentSupportFund.setOnRefreshListener {
            Handler().postDelayed({ // 아래로 스와이핑 이후 1초 후에 리플래쉬 아이콘 없애기
                if (binding.swipeRefreshFragmentSupportFund.isRefreshing)
                    binding.swipeRefreshFragmentSupportFund.isRefreshing = false
            }, 1000)
        }

    }

    // 프래그먼트 종료시 툴바 show
    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
    }

    // 프래그먼트 파괴
    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun getData(path : String){ // 데이터 가져오기


            // 작업할 문서
            db.collection("SupportFund").document(path).collection("Support").get()
                .addOnSuccessListener{ result ->
                    listAdapter.notifyDataSetChanged()
                    dataList.clear() // 리스트 재정의

                    for(document in result){
                        if(document != null){
                            val item = Support(document.id)
                            dataList.add(item)

                            Log.w("ScholarshipAllscholarFragment", "Each Scholar Data")
                        }
                    }
                    listAdapter.submitList(dataList)
                    // RecyclerView.adapter에 지정
                    binding.supportRecyclerView.adapter = listAdapter
                }
                .addOnFailureListener { exception ->
                    // 실패할 경우
                    Log.w("ScholarshipAllscholarFragment", "Error getting Each Scholar documents: $exception")
                }

    }

    private fun getAreaData(listAdapter: SupportFundRecyclerViewAdapter, list : MutableList<Support>){ // 데이터 가져오기

            binding.supportRecyclerView.adapter = listAdapter

            listAdapter.notifyDataSetChanged()
            glist.clear()
            slist.clear()

            // 작업할 문서
            db.collection("SupportFund").document("SupportArea").collection("Support").get()
                .addOnSuccessListener{ result ->
                    listAdapter.notifyDataSetChanged()
                    list.clear() // 리스트 재정의


                    for(document in result){
                        if(document != null){
                            val item = Support(document.id)
                            slist.add(item)
                            glist.add(item)

                            Log.w("ScholarshipAllscholarFragment", "Each Scholar Data")
                        }
                    }
                    slist.removeAt(1)
                    Log.w("0000000000000000000000000", slist.toString())
                    slist.removeAt(2)
                    glist.removeAt(0)
                    glist.removeAt(1)
                    listAdapter.submitList(list)
                }
                .addOnFailureListener { exception ->
                    // 실패할 경우
                    Log.w("ScholarshipAllscholarFragment", "Error getting Each Scholar documents: $exception")
                }
        }

}

