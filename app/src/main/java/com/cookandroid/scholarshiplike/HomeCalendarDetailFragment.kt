package com.cookandroid.scholarshiplike

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.cookandroid.scholarshiplike.databinding.FragmentHomeCalendarDetailBinding
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

class HomeCalendarDetailFragment : Fragment() {
    @Suppress("PrivatePropertyName")
    private val TAG = javaClass.simpleName
    private lateinit var binding: FragmentHomeCalendarDetailBinding

    lateinit var mContext: Context

    var pageIndex = 0
    lateinit var currentDate: Date

    private var db = Firebase.firestore
    private val user = Firebase.auth.currentUser

    lateinit var calendar_year_month_text: TextView

    private var scholarList: ArrayList<String> = arrayListOf()
    private var scholar: ArrayList<tmpScholarship> = arrayListOf()
    var dataList: ArrayList<Int> = arrayListOf()

    companion object {
        var instance: HomeCalendarDetailFragment? = null
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MainActivity) {
            mContext = context
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        instance = this
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeCalendarDetailBinding.inflate(inflater, container, false)
        initView(binding)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //유저가 좋아요한 목록 가져오기
        var user_ref = db.collection("Users")
        var scholar_ref =  db.collectionGroup("ScholarshipList")

        Log.w(TAG, "Load Firestore")
        if (user != null) {
            user_ref.document(user.uid).get().
            addOnSuccessListener { document ->
                if (document.data != null){
                    if (document.data!!.get("likeScholarship") != null) {
                        scholarList = document["likeScholarship"] as ArrayList<String> //장학금 이름을 리스트에 넣음
                    }

                    Log.w(TAG, scholarList.toString())

                    scholar_ref.get()
                        .addOnSuccessListener { result ->
                            for (document in result) {
                                Log.w(TAG, document.id)
                                for(title in scholarList){
                                    if(document.id == title){
                                        //날짜가져오기
                                        var period = document["period"] as Map<String,Timestamp>
                                        var startdate : Timestamp? = period.get("startDate")
                                        var enddate : Timestamp? = period.get("endDate")

                                        scholar.add(tmpScholarship( //장학금 이름으로 장학금 정보 빼와서 scholar에 정보 포함 리스트 넘겨줌
                                            document.id, "", startdate?.toDate(), enddate?.toDate(), document["category"] as String?
                                        ))
                                    }
                                }
                            }

                            binding.calendarView.layoutManager = LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)
                            binding.calendarView.adapter = HomeCalendarDetailAdapter(this,mContext,currentDate,pageIndex,scholar)

                        }.addOnFailureListener { exception ->
                            Log.w(TAG, "Error getting documents: $exception")
                        }

//                    //구경로
//                    db.collection("Scholarship").document("Nation").collection("ScholarshipList").get()
                }
            }.addOnFailureListener { exception ->
                // 실패할 경우
                Log.w(TAG, "Error getting documents: $exception")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        instance = null
    }

    fun initView(binding: FragmentHomeCalendarDetailBinding) {
        //캘린더 월 설정
        pageIndex -= (Int.MAX_VALUE / 2)
        Log.e(TAG, "Calender Index: $pageIndex")
        calendar_year_month_text = binding.calendarYearMonthText

        // 날짜 적용
        val date = Calendar.getInstance().run {
            add(Calendar.MONTH, pageIndex)
            time
        }
        currentDate = date
        Log.e(TAG, "$date")

        // 포맷 적용
        var datetime: String = SimpleDateFormat(mContext.getString(R.string.calendar_year_month_format), Locale.KOREA).format(date.time)
        calendar_year_month_text.setText(datetime)

    }
}