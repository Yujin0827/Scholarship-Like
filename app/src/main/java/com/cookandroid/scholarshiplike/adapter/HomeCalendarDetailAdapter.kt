package com.cookandroid.scholarshiplike

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.cookandroid.scholarshiplike.databinding.FragmentHomeCalendarItemListBinding
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class HomeCalendarDetailAdapter(val fragment: Fragment, val context: Context, val date: Date, val pageindex: Int, val scholar:ArrayList<tmpScholarship>) :
    RecyclerView.Adapter<HomeCalendarDetailAdapter.CalendarItemHolder>() {

    private lateinit var binding: FragmentHomeCalendarItemListBinding

    private var dateList_2D: ArrayList<List<Int>> = arrayListOf()
    private var intList_2D: ArrayList<List<Int>> = arrayListOf()
    private var currentMonth: Int =0

    // HomeCalendarDateCalculate을 이용하여 날짜 리스트 세팅
    var calculatedDate: HomeCalendarDateCalculate = HomeCalendarDateCalculate(date, pageindex)

    init {
        calculatedDate.initBaseCalendar()
        dateList_2D = calculatedDate.dateList_2D
        intList_2D = calculatedDate.intList_2D
        currentMonth = calculatedDate.currentMonth
        Log.e("scholar:     ","$scholar")
    }

    override fun onBindViewHolder(holder: CalendarItemHolder, position: Int) {
        holder?.bind(dateList_2D[position], intList_2D[position], position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarItemHolder {
        binding = FragmentHomeCalendarItemListBinding.inflate(
            LayoutInflater.from(context), parent, false)

        return CalendarItemHolder(binding)
    }

    override fun getItemCount(): Int = dateList_2D.size

    inner class CalendarItemHolder(val binding : FragmentHomeCalendarItemListBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(data: List<Int>, intList: List<Int>,position: Int) {

            //View text, id 설정
            binding.date1.text = data[0].toString()
            binding.date1.id = intList[0]

            binding.date2.text = data[1].toString()
            binding.date2.id = intList[1]

            binding.date3.text = data[2].toString()
            binding.date3.id = intList[2]

            binding.date4.text = data[3].toString()
            binding.date4.id = intList[3]

            binding.date5.text = data[4].toString()
            binding.date5.id = intList[4]

            binding.date6.text = data[5].toString()
            binding.date6.id = intList[5]

            binding.date7.text = data[6].toString()
            binding.date7.id = intList[6]

            //스케줄 추가
            val dateFormat = SimpleDateFormat("MMdd")

            for(item in scholar) {
                val start = dateFormat.format(item.startdate).toInt()
                val end = dateFormat.format(item.enddate).toInt()
                var width = 0
                var leftmargin = 0
                var rightmargin = 0

                if(binding.date1.id in start..end)  width++ else { if (binding.date1.id<start) leftmargin++ else rightmargin++ }
//                var data = binding.date1.id
//                Log.w("start, end, data, leftmargin, rightmargin, width, position","$start, $end, $data, $leftmargin, $rightmargin, $width, $position")
                if(binding.date2.id in start..end)  width++ else { if (binding.date2.id<start) leftmargin++ else rightmargin++ }
                if(binding.date3.id in start..end)  width++ else { if (binding.date3.id<start) leftmargin++ else rightmargin++ }
                if(binding.date4.id in start..end)  width++ else { if (binding.date4.id<start) leftmargin++ else rightmargin++ }
                if(binding.date5.id in start..end)  width++ else { if (binding.date5.id<start) leftmargin++ else rightmargin++ }
                if(binding.date6.id in start..end)  width++ else { if (binding.date6.id<start) leftmargin++ else rightmargin++ }
                if(binding.date7.id in start..end)  width++ else { if (binding.date7.id<start) leftmargin++ else rightmargin++ }
                if(width !=0) binding.contents.addView(createscheduelView(item.title, leftmargin, rightmargin, width, "#f2d7d7"))
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