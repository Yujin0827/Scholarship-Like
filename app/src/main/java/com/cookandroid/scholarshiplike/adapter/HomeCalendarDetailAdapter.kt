package com.cookandroid.scholarshiplike

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.cookandroid.scholarshiplike.databinding.FragmentHomeCalendarItemListBinding
import java.util.*
import kotlin.collections.ArrayList

class HomeCalendarDetailAdapter(val fragment: Fragment, val context: Context, val date: Date, val pageindex: Int, val scholar:ArrayList<tmpScholarship>) :
    RecyclerView.Adapter<HomeCalendarDetailAdapter.CalendarItemHolder>() {

    private val TAG = javaClass.simpleName
    private var dataList: ArrayList<Int> = arrayListOf() //날짜 데이터 리스트
    private var dataList_2D: ArrayList<MutableList<Int>> = arrayListOf() //날짜 데이터 리스트 2D
    private var dateIntList: ArrayList<MutableList<Int>> = arrayListOf() //날짜 int형 리스트

    private lateinit var binding: FragmentHomeCalendarItemListBinding

    // HomeCalendarDateCalculate을 이용하여 날짜 리스트 세팅
    var calculatedDate: HomeCalendarDateCalculate = HomeCalendarDateCalculate(date)

    init {
        calculatedDate.initBaseCalendar()
        dataList = calculatedDate.dateList
        dataList_2D = calculatedDate.dateList_2D
    }

    override fun onBindViewHolder(holder: CalendarItemHolder, position: Int) {
        holder?.bind(dataList_2D[position], position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarItemHolder {
        binding = FragmentHomeCalendarItemListBinding.inflate(
            LayoutInflater.from(context), parent, false)

        return CalendarItemHolder(binding)
    }

    override fun getItemCount(): Int = dataList_2D.size

    inner class CalendarItemHolder(val binding : FragmentHomeCalendarItemListBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(data: MutableList<Int>, position: Int) {
            binding.date1.text = data[0].toString()
            binding.date2.text = data[1].toString()
            binding.date3.text = data[2].toString()
            binding.date4.text = data[3].toString()
            binding.date5.text = data[4].toString()
            binding.date6.text = data[5].toString()
            binding.date7.text = data[6].toString()

            //스케줄 추가(임시)
            if( pageindex == 0) {
                if(position==1) {
                    binding.contents.addView(createscheduelView("국가장학금 Ⅰ 유형(학생직접지원형)", 0, 0, 5, "#f2d7d7"))
                    binding.contents.addView(createscheduelView("한림 리더십 장학금", 2, 0, 5, "#c9ddf2"))
                }

                if(position==2) {
                    binding.contents.addView(createscheduelView("교외 장학금", 1, 0, 3, "#caf1c8"))
                }
            }
        }
    }

    fun createscheduelView(text: CharSequence, leftmargin: Int, rightmargin: Int, width: Int, color: String): TextView {

        val length = 145
        val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        layoutParams.setMargins(length*leftmargin, 0, length*rightmargin, 5)

        val textView = TextView(context)
        textView.text = text
        textView.setBackgroundColor(Color.parseColor(color))
        textView.gravity
        textView.width = length*width
        textView.setPaddingRelative(10,20,10,20)
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
}