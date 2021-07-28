package com.cookandroid.scholarshiplike

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_home_calendar_item_list.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

// 높이를 구하는데 필요한 LinearLayout과 HomeCalendarDateCalculate를 사용할 때 필요한 date를 받는다.
class HomeCalendarDetailAdapter(val fragment: Fragment, val context: Context, val calendarLayout: LinearLayout, val date: Date, val pageindex: Int, scholar:ArrayList<tmpScholarship>) :
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
            val dateString: String = SimpleDateFormat("dd", Locale.KOREA).format(date)
            val dateInt = dateString.toInt()

            //현재 달
            val monthFormat = SimpleDateFormat("MM")
            val cur = System.currentTimeMillis()
            val curMonth = pageindex+monthFormat.format(cur).toInt()
            var givemonth = curMonth

            // 날짜 표시
            itemCalendarDateText.setText(data.toString())

            //오늘 날짜 강조
            if (dataList[position] == dateInt && pageindex == 0) {
                //검정색,굵게
                itemCalendarDateText.setTypeface(itemCalendarDateText.typeface, Typeface.BOLD)
                itemCalendarDateText.setTextColor(Color.parseColor("#000000"))

            }

            // 현재 월의 1일 이전, 현재 월의 마지막일 이후 값 처리
            if (position < firstDateIndex || position > lastDateIndex) {
                itemCalendarDateText.setTextColor(Color.parseColor("#DEDEDE"))
            }

            //일정 추가
            for (item in scholarList) {
                addScheduel("#AFEEEE",item, position, firstDateIndex, lastDateIndex, itemCalendarContents)
            }

            if(position < firstDateIndex) givemonth = curMonth-1
            else if (position > lastDateIndex) givemonth = curMonth+1

            //클릭 시 팝업창
            itemCalendar.setOnClickListener {
                HomeCalendarPopupFragment(scholarList, context, givemonth, dataList[position], position, firstDateIndex, lastDateIndex).show(fragment.parentFragmentManager, "HomeCalendarPopupFragmentDialog")
            }
        }
    }


    // <뷰 생성 함수>
    fun createView(text: CharSequence, bgcol: String, textcol: String): TextView{
        val textView = TextView(context)
        textView.text = text

        //동적 뷰 생성(일정추가)
        textView.setBackgroundColor(Color.parseColor(bgcol))
        textView.gravity
        textView.setTextColor(Color.parseColor(textcol))
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 8F)

        return textView
    }

    // <일정 추가 함수>
    fun addScheduel(backgroundcolor: String ,item: tmpScholarship, position: Int, firstDateIndex: Int, lastDateIndex: Int, itemCalendarContents: LinearLayout) {

        val monthFormat = SimpleDateFormat("MM")
        val dateFormat = SimpleDateFormat("dd")

        //현재 달
        val cur = System.currentTimeMillis()
        val curMonth = monthFormat.format(cur).toInt()

        val tmpstartdate = dateFormat.format(item.startdate).toInt()
        val tmpstartmonth = monthFormat.format(item.startdate).toInt() - curMonth
        val tmpenddate = dateFormat.format(item.enddate).toInt()
        val tmpendmonth = monthFormat.format(item.enddate).toInt() - curMonth
        val tmptext = item.title
        val tmpbgcol = backgroundcolor

        if (tmpstartmonth == tmpendmonth) { //시작-끝 월 같을 때

            if(pageindex == tmpstartmonth) { //해당 달이면
                if(position in firstDateIndex..lastDateIndex) {
                    if (dataList[position] in tmpstartdate..tmpenddate) {
                        if (dataList[position] != tmpstartdate) itemCalendarContents.addView(createView(tmptext, tmpbgcol, "#00000000"))
                        else itemCalendarContents.addView(createView(tmptext, tmpbgcol, "#000000"))
                    }
                }
            }

        } else { //시작-끝 월 다를 때
            //1. 시작 달 달력
            if (pageindex == tmpstartmonth) {
                if (position in firstDateIndex..lastDateIndex) {
                    if (dataList[position] >= tmpstartdate) {
                        if (dataList[position] != tmpstartdate) itemCalendarContents.addView(createView(tmptext, tmpbgcol, "#00000000"))
                        else itemCalendarContents.addView(createView(tmptext, tmpbgcol, "#000000"))

                    }
                }
            }

            // 2. 시작-끝 사이 달력
            if (pageindex in (tmpstartmonth + 1) until tmpendmonth) {
                if(position in firstDateIndex..lastDateIndex) itemCalendarContents.addView(createView(tmptext, tmpbgcol, "#000000"))
            }

            //3. 끝 달 달력
            if (pageindex == tmpendmonth) {
                if(position in firstDateIndex..lastDateIndex) {
                    if (dataList[position] <= tmpenddate) {
                        itemCalendarContents.addView(createView(tmptext, tmpbgcol, "#00000000"))
                    }
                }
            }

            //4. 해당 달 앞
            if(pageindex-1 == tmpstartmonth) {
                if(position < firstDateIndex) {
                    if(dataList[position] >= tmpstartdate) {
                        if (dataList[position] != tmpstartdate) itemCalendarContents.addView(createView(tmptext, tmpbgcol, "#00000000"))
                        else itemCalendarContents.addView(createView(tmptext, tmpbgcol, "#000000"))
                    }
                }
            }

            //5. 해당 달 뒤
            if(pageindex+1 == tmpendmonth) {
                if(position > lastDateIndex) {
                    if(dataList[position] <= tmpenddate) {
                        if (dataList[position] != tmpstartdate) itemCalendarContents.addView(createView(tmptext, tmpbgcol, "#00000000"))
                        else itemCalendarContents.addView(createView(tmptext, tmpbgcol, "#000000"))
                    }
                }
            }

        }
    }
}

//            //단일 일정
//            //임시날짜(계산필요)
//            val date = 10;
//            val month = 1;
//
//            if (dataList[position] == date && pageindex == month && position > firstDateIndex && position < lastDateIndex) {
//                itemCalendarContents.addView(createView(tmptext, tmpbgcol, "#000000"))
//            }