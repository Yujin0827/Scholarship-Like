package com.cookandroid.scholarshiplike

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
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
import kotlinx.android.synthetic.main.item_calendar_popup.*
import java.text.SimpleDateFormat
import java.util.*

class HomeCalendarDetailFragment : Fragment() {
    @Suppress("PrivatePropertyName")
    private val TAG = javaClass.simpleName
    lateinit var mContext: Context

    var pageIndex = 0

    private var db = Firebase.firestore
    private val user = Firebase.auth.currentUser

    lateinit var calendar_year_month_text: TextView

    private var scholarList: ArrayList<String> = arrayListOf()
    private var scholar: ArrayList<tmpScholarship> = arrayListOf()
    var dataList: ArrayList<Int> = arrayListOf()
    var dataList_2D: ArrayList<MutableList<Int>> = arrayListOf()
    var dateList: ArrayList<MutableList<Int>> = arrayListOf()

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

//                                calendar_view.layoutManager = gridLayoutManager
//                                calendar_view.adapter = HomeCalendarDetailAdapter(this,mContext,calendar_layout,currentDate,pageIndex,scholar)

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
        //캘린더 월 설정
        pageIndex -= (Int.MAX_VALUE / 2)
        Log.e(TAG, "Calender Index: $pageIndex")
        calendar_year_month_text = view.calendar_year_month_text

        // 월 적용
        val date = Calendar.getInstance().run { add(Calendar.MONTH, pageIndex)
            time
        }

        // 포맷 적용
        var datetime: String = SimpleDateFormat(mContext.getString(R.string.calendar_year_month_format), Locale.KOREA).format(date.time)

        // FurangCalendar을 이용하여 날짜 리스트 세팅
        var homeCalendarDateCalculate: HomeCalendarDateCalculate = HomeCalendarDateCalculate(date)
        homeCalendarDateCalculate.initBaseCalendar()
        dataList = homeCalendarDateCalculate.dateList
        dataList_2D = homeCalendarDateCalculate.dateList_2D
        dateList = dataList_2D.clone() as ArrayList<MutableList<Int>>

        //뷰에 날짜 세팅
        for (i in 0..6) {
            view.week1.addView(createdateView(dataList_2D[0][i].toString()))
            view.week2.addView(createdateView(dataList_2D[1][i].toString()))
            view.week3.addView(createdateView(dataList_2D[2][i].toString()))
            view.week4.addView(createdateView(dataList_2D[3][i].toString()))
            view.week5.addView(createdateView(dataList_2D[4][i].toString()))
            view.week6.addView(createdateView(dataList_2D[5][i].toString()))
        }

        Log.w(TAG,"$dataList_2D")
        Log.w(TAG,"$dateList")

        calendar_year_month_text.setText(datetime)
        Log.e("date", "$date")

        //스케줄 추가(임시)
        if(pageIndex == 0) {
            view.scheduel1.addView(createscheduelView("국가장학금 Ⅰ 유형(학생직접지원형)", 0, 0, 5, "#f2d7d7"))
            view.scheduel1.addView(createscheduelView("국가장학금 Ⅰ 유형(학생직접지원형) - 기초, 차상위", 3, 0, 4, "#f2d7d7"))
            view.scheduel2.addView(createscheduelView("국가장학금 Ⅰ 유형(학생직접지원형) - 기초, 차상위", 0, 0, 2, "#f2d7d7"))
            view.scheduel4.addView(createscheduelView("한림 리더십 장학금", 2, 0, 5, "#c9ddf2"))
            view.scheduel3.addView(createscheduelView("교외 장학금", 3, 0, 3, "#caf1c8"))
        }
    }

    fun createdateView(text: CharSequence): TextView{
        val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        layoutParams.setMargins(changeDP(0), 0, changeDP(5), 0)

        val textView = TextView(context)
        textView.text = text
        textView.gravity
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15F)
        textView.layoutParams = layoutParams
        textView.width = changeDP(47)
        //텍스트뷰 크기 45, 마진 7,5해서 60  > 158dp
        return textView
    }

    fun createscheduelView(text: CharSequence, leftmargin: Int, rightmargin: Int, width: Int, color: String): TextView {
        val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        layoutParams.setMargins(changeDP(52*leftmargin), 0, changeDP(52*rightmargin), 5)

        val textView = TextView(context)
        textView.text = text
        textView.setBackgroundColor(Color.parseColor(color))
        textView.gravity
        textView.width = changeDP(52*width)
        textView.setPaddingRelative(changeDP(5),changeDP(8),changeDP(5),changeDP(8))
        textView.setTextColor(Color.parseColor("#000000"))
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10F)
        textView.layoutParams = layoutParams
        textView.isSingleLine = true

        textView.setOnClickListener {
            val intent = Intent(context, ScholarshipDetailActivity::class.java)
            intent.apply {
                this.putExtra("title",text.toString())
            }
            context?.startActivity(intent)
        }

        return textView
    }

    private fun changeDP(value : Int) : Int{
        var displayMetrics = resources.displayMetrics
        var dp = Math.round(value * displayMetrics.density)
        return dp
    }

    override fun onDestroy() {
        super.onDestroy()
        instance = null
    }

}