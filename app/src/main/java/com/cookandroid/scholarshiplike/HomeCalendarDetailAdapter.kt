package com.cookandroid.scholarshiplike

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_home_calendar_item_list.view.*
import kotlinx.android.synthetic.main.fragment_home_calendar_popup.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

// 높이를 구하는데 필요한 LinearLayout과 HomeCalendarDateCalculate를 사용할 때 필요한 date를 받는다.
class HomeCalendarDetailAdapter(val context: Context, val calendarLayout: LinearLayout, val date: Date, val pageindex: Int, scholar:ArrayList<tmpScholarship>) :
    RecyclerView.Adapter<HomeCalendarDetailAdapter.CalendarItemHolder>() {

    private val TAG = javaClass.simpleName
    private var dataList: ArrayList<Int> = arrayListOf() //날짜 데이터 리스트
    private var scholarList: ArrayList<tmpScholarship> = scholar //장학금 리스트

    // HomeCalendarDateCalculate을 이용하여 날짜 리스트 세팅
    var calculatedDate: HomeCalendarDateCalculate = HomeCalendarDateCalculate(date)

    init {
        calculatedDate.initBaseCalendar()
        dataList = calculatedDate.dateList
    }

    interface ItemClick {
        fun onClick(view: View, position: Int)
    }

    var itemClick: ItemClick? = null

    override fun onBindViewHolder(holder: CalendarItemHolder, position: Int) {

        // list_item_calendar 높이 지정
        val h = calendarLayout.height / 6
        holder.itemView.layoutParams.height = h

        holder?.bind(dataList[position], position, context)
        if (itemClick != null) {
            holder?.itemView?.setOnClickListener { v ->
                itemClick?.onClick(v, position)

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarItemHolder {
        val view =
            LayoutInflater.from(context)
                .inflate(R.layout.fragment_home_calendar_item_list, parent, false)

        return CalendarItemHolder(view)
    }

    override fun getItemCount(): Int = dataList.size

    inner class CalendarItemHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {

        var itemCalendar: LinearLayout = itemView!!.item_calendar //캘린더 세부요소 구성 뷰
        var itemCalendarDateText: TextView = itemView!!.item_calendar_date_text //해당 날짜 텍스트뷰
        private val itemCalendarContents: LinearLayout =
            itemView!!.item_calendar_contents //해당 날짜 밑 리니어 레이아웃

        fun bind(data: Int, position: Int, context: Context) {
            val firstDateIndex = calculatedDate.prevTail
            val lastDateIndex = dataList.size - calculatedDate.nextHead - 1

            // 날짜 표시
            itemCalendarDateText.setText(data.toString())

            var dateString: String = SimpleDateFormat("dd", Locale.KOREA).format(date)
            var dateInt = dateString.toInt()

            //오늘 날짜
            if (dataList[position] == dateInt && pageindex == 0) {
                //검정색,굵게
                itemCalendarDateText.setTypeface(itemCalendarDateText.typeface, Typeface.BOLD)
                itemCalendarDateText.setTextColor(Color.parseColor("#000000"))

            }

            // 현재 월의 1일 이전, 현재 월의 마지막일 이후 값 처리(마무리)
            if (position < firstDateIndex || position > lastDateIndex) {
                itemCalendarDateText.setTextColor(Color.parseColor("#DEDEDE"))
            }


            // <일정 추가>
            fun addView(text: CharSequence, bgcol: String, textcol: String) {
                val textView = TextView(context)
                textView.text = text

                //동적 뷰 생성(일정추가)
                textView.setBackgroundColor(Color.parseColor(bgcol))
                textView.gravity
                textView.setTextColor(Color.parseColor(textcol))
                textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 8F)

                //동적 뷰 추가 코드
                itemCalendarContents.addView(textView)
            }


            var monthFormat = SimpleDateFormat("MM")
            var dateFormat = SimpleDateFormat("dd")

            //현재 달
            var cur = System.currentTimeMillis()
            var curMonth = monthFormat.format(cur).toInt()


            Log.e(TAG, "장학금 리스트: $scholarList")
            for (item in scholarList) {

                var tmpstartdate = dateFormat.format(item.startdate).toInt()
                var tmpstartmonth = monthFormat.format(item.startdate).toInt() - curMonth
                var tmpenddate = dateFormat.format(item.enddate).toInt()
                var tmpendmonth = monthFormat.format(item.enddate).toInt() - curMonth
                var tmptext = item.title
                var tmpbgcol = "#DEDEDE"

                Log.e(TAG, "$tmptext: $tmpstartmonth/$tmpstartdate ~ $tmpendmonth/$tmpenddate")

                Log.e(TAG, "pageindex: $pageindex")

                Log.e(TAG, "addView 위")

                if (tmpstartmonth == tmpendmonth) {

                    //동적 뷰 선언
                    val textView2 = TextView(context)
                    textView2.text = tmptext

                    for (i in tmpstartdate..tmpenddate) {
                        if (dataList[position] == i && pageindex == tmpstartmonth && position > firstDateIndex && position < lastDateIndex) {

                            //일정 이름 처음 한 칸에만 표시
                            if (i != tmpstartdate) addView(tmptext, tmpbgcol, "#00000000"
                            ) else addView(tmptext, tmpbgcol, "#000000")
                        }

                    }


                    //2-2. 시작, 마지막 월 다를 때
                } else {
                    if (pageindex == tmpstartmonth) { //해당 일정의 시작 달의 달력일때
                        for (i in tmpstartdate..42) { //달의 시작부터 일정의 끝까지 반복

                            if (dataList[position] == i && position > firstDateIndex) { //해당날짜와 같고 첫번쨰날의 인덱스보다 크면
                                if (i != tmpstartdate) addView(tmptext, tmpbgcol, "#00000000"
                                ) else addView(tmptext, tmpbgcol, "#000000")
                            }
                        }
                    }

                    if (tmpstartmonth < pageindex && pageindex < tmpendmonth && position > firstDateIndex && position <= lastDateIndex) { //시작달과 끝달 사이일때

                        addView(tmptext, tmpbgcol, "#00000000")
                    }

                    if (pageindex == tmpendmonth) { //해당 일정의 종료 달의 달력일때
                        for (i in 1..tmpenddate) { //달의 시작부터 일정의 끝까지 반복

                            if (dataList[position] == i && position < lastDateIndex) {

                                if (i != tmpstartdate) addView(tmptext, tmpbgcol, "#00000000"
                                ) else addView(tmptext, tmpbgcol, "#000000")
                            }
                        }
                    }
                }
            }

            //1.단일 일정
            //임시날짜(계산필요)
            var date = 10;
            var month = 1;

            if (dataList[position] == date && pageindex == month && position > firstDateIndex && position < lastDateIndex) {
                addView("민지생일♥","#ffc0cc","#000000")
            }

            fun showPopup(date: CharSequence) {
                val view =
                    LayoutInflater.from(context)
                        .inflate(R.layout.fragment_home_calendar_popup, null, false)

                val alertDialog = AlertDialog.Builder(context)
                    .setTitle(date.toString() + "일").create()

                val List = arrayListOf(
                    Scheduel("국가장학금 1차", "2020.04.6", "2020.05.12")
                    , Scheduel("국가장학금 2차", "2020.08.10", "2020.09.27")
                )

                view.popupRecyclerView.layoutManager =
                    LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                view.popupRecyclerView.setHasFixedSize(true)
                view.popupRecyclerView.adapter = HomeCalendarPopupAdapter(List, context)

                alertDialog.setView(view)
                alertDialog.show()
                alertDialog.window?.setLayout(900, 1300)

            }

            itemCalendar.setOnClickListener {
                showPopup(itemCalendarDateText.text)
            }
        }
    }
}


//            캘린더 일정추가 관련 코드
//
//
//            //2.연속 일정
//            //임시날짜(계산필요)
//            var tmpstartdate = 27;
//            var tmpstartmonth = 0;
//            var tmpenddate = 3;
//            var tmpendmonth = 1;
//            var tmptext = "방학"
//            var tmpbgcol= "#DEDEDE"
//
//
//
//            //2-1. 시작, 마지막 월 똑같을때
//            if (tmpstartmonth == tmpendmonth) {
//
//                //동적 뷰 선언
//                val textView2 = TextView(context)
//                textView2.text = tmptext
//
//                for (i in tmpstartdate..tmpenddate) {
//                    if (dataList[position] == i && pageindex == tmpstartmonth && position > firstDateIndex && position < lastDateIndex) {
//
//                        //일정 이름 처음 한 칸에만 표시
//                        if (i != tmpstartdate) addView(tmptext,tmpbgcol,"#00000000") else addView(tmptext,tmpbgcol,"#000000")
//                    }
//
//                }
//
//
//                //2-2. 시작, 마지막 월 다를 때
//            } else {
//                if(pageindex==tmpstartmonth ) { //해당 일정의 시작 달의 달력일때
//                    for (i in tmpstartdate..42) { //달의 시작부터 일정의 끝까지 반복
//
//                        if (dataList[position] == i && position > firstDateIndex) { //해당날짜와 같고 첫번쨰날의 인덱스보다 크면
//                            if (i != tmpstartdate) addView(tmptext,tmpbgcol,"#00000000") else addView(tmptext,tmpbgcol,"#000000")
//                        }
//                    }
//                }
//
//                if(tmpstartmonth < pageindex && pageindex < tmpendmonth && position > firstDateIndex && position <= lastDateIndex) { //시작달과 끝달 사이일때
//
//                    addView(tmptext,tmpbgcol,"#00000000")
//                }
//
//                if(pageindex==tmpendmonth) { //해당 일정의 종료 달의 달력일때
//                    for (i in 1..tmpenddate) { //달의 시작부터 일정의 끝까지 반복
//
//                        if (dataList[position] == i && position<lastDateIndex) {
//
//                            if (i != tmpstartdate) addView(tmptext,tmpbgcol,"#00000000") else addView(tmptext,tmpbgcol,"#000000")
//                        }
//                    }
//                }
//            }
