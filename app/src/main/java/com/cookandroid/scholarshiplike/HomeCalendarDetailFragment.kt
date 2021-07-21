package com.cookandroid.scholarshiplike

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_home_calendar_detail.view.*
import java.text.SimpleDateFormat
import java.util.*

class HomeCalendarDetailFragment : Fragment() {
    @Suppress("PrivatePropertyName")
    private val TAG = javaClass.simpleName
    lateinit var mContext: Context

    var pageIndex = 0
    lateinit var currentDate: Date

    lateinit var calendar_year_month_text: TextView
    lateinit var calendar_layout: LinearLayout
    lateinit var calendar_view: RecyclerView

    private lateinit var listAdapter: ScholarshipRecyclerViewAdapter
    private var db = Firebase.firestore
    private var scholarList: ArrayList<String> = arrayListOf()
    private var scholar: ArrayList<tmpScholarship> = arrayListOf()
    private val user = Firebase.auth.currentUser

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
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home_calendar_detail, container, false)
        initView(view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val gridLayoutManager = GridLayoutManager(mContext,7)

        //유저가 좋아요한 목록 가져오기
        var ref = db.collection("Users")

        Log.w(TAG, "Load Firestore")
        if (user != null) {
            ref.document(user.uid).get().
            addOnSuccessListener { document ->
                if (document.data != null){
                    if (document.data!!.get("likeContent") != null) {
                        val data = document.data!!["likeContent"] as Map<String, String>
                        scholarList = data["scholarship"] as ArrayList<String> //장학금 이름을 리스트에 넣음
                    }

                    Log.w(TAG, scholarList.toString())

                    db.collection("국가").get()
                        .addOnSuccessListener { result ->
                            for (document in result) {
                                Log.w(TAG, document.id)
                                for(title in scholarList){
                                    if(document.id == title){
                                        //날짜가져오기

                                        var period = document["period"] as Map<String,Timestamp>
                                        var startdate : Timestamp? = period.get("startdate")
                                        var enddate : Timestamp? = period.get("enddate")

                                        scholar.add(tmpScholarship( //장학금 이름으로 장학금 정보 빼와서 scholar에 정보 포함 리스트 넘겨줌
                                            document.id, "", startdate?.toDate(), enddate?.toDate()))
                                    }
                                }

                                Log.w(TAG, scholar.toString())
                                calendar_view.layoutManager = gridLayoutManager
                                calendar_view.adapter = HomeCalendarDetailAdapter(mContext,calendar_layout,currentDate,pageIndex,scholar)

                            }
                        }.addOnFailureListener { exception ->
                            Log.w(TAG, "Error getting documents: $exception")
                        }
                }
            }.addOnFailureListener { exception ->
                // 실패할 경우
                Log.w(TAG, "Error getting documents: $exception")
            }
        }
    }

    fun initView(view: View) {
        pageIndex -= (Int.MAX_VALUE / 2)
        Log.e(TAG, "Calender Index: $pageIndex")
        calendar_year_month_text = view.calendar_year_month_text
        calendar_layout = view.calendar_layout
        calendar_view = view.calendar_view

        // 날짜 적용
        val date = Calendar.getInstance().run {
            add(Calendar.MONTH, pageIndex)
            time
        }
        currentDate = date
        Log.e(TAG, "$date")
        // 포맷 적용
        var datetime: String = SimpleDateFormat(
            mContext.getString(R.string.calendar_year_month_format),
            Locale.KOREA
        ).format(date.time)
        calendar_year_month_text.setText(datetime)
        Log.e("date", "$date")
    }

    override fun onDestroy() {
        super.onDestroy()
        instance = null
    }
}