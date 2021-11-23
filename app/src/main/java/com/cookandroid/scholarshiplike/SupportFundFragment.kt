package com.cookandroid.scholarshiplike

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.cookandroid.scholarshiplike.adapter.SupportFundExpandableLisviewtAdapter
import com.cookandroid.scholarshiplike.databinding.FragmentSupportFundBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SupportFundFragment : Fragment() {

    // binding
    private var _binding: FragmentSupportFundBinding? = null    // 바인딩 객체
    private val binding get() = _binding!!                      // 바인딩 변수 재선언 (매번 null 체크x)

    private val head: MutableList<String> = ArrayList()     // expandableList의 부모 리스트
    private val body: MutableList<MutableList<String>> = ArrayList()    // expandableList의 자식 리스트
    lateinit var areaAdapter: SupportFundExpandableLisviewtAdapter      // expandableList 어댑터 선언
//    private lateinit var listAdapter: SupportFundRecyclerViewAdapter    // 리사이클러뷰 어댑터

    private var db = Firebase.firestore
//    var dataList: MutableList<SupportFund> = arrayListOf()    // 특정 지원금 리스트
    private lateinit var mContext1: Context

    private val nationSupport : MutableList<String> = ArrayList()
    private val areaSupport : MutableList<String> = ArrayList()

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
}