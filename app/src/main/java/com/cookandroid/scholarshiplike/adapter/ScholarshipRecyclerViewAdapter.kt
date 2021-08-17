package com.cookandroid.scholarshiplike

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.solver.GoalRow
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class ScholarshipRecyclerViewAdapter (private var list: MutableList<Scholarship>, val mContext: Context):
    ListAdapter<Scholarship, ScholarshipRecyclerViewAdapter.ScholarItemViewHolder>(DiffCallbackScholar){

    private var mContext1 : Context = mContext

    // 데이터 가져오기
    // inner class로 ViewHolder 정의
    inner class ScholarItemViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        var scholar_paymentType: TextView = itemView!!.findViewById(R.id.scholar_paymentType)
        var scholar_title: TextView = itemView!!.findViewById(R.id.scholar_title)
        var scholar_startDate: TextView = itemView!!.findViewById(R.id.scholar_startDate)
        var scholar_endDate: TextView = itemView!!.findViewById(R.id.scholar_endDate)
        var scholar_startDate2: TextView = itemView!!.findViewById(R.id.scholar_startDate2)
        var scholar_endDate2: TextView = itemView!!.findViewById(R.id.scholar_endDate2)
        var scholar_institution: TextView = itemView!!.findViewById(R.id.scholar_institution)
        var startToend1 : TextView = itemView.findViewById(R.id.startDateToEndDate)
        var startToend2 : TextView = itemView.findViewById(R.id.startDateToEndDate2)
        var scholar_perioid2 : LinearLayout = itemView.findViewById(R.id.scholar_period2)
        var one_period : TextView = itemView.findViewById(R.id.one_period)
        var two_period : TextView = itemView.findViewById(R.id.two_period)

        // onBindViewHolder의 역할을 대신한다.
        fun bind(data: Scholarship, position: Int) {
            Log.d("ListAdapter", "===== ===== ===== ===== bind ===== ===== ===== =====")
            Log.d("ListAdapter", data.title)

            scholar_paymentType.text = data.paymentType
            scholar_title.text = data.title
            scholar_institution.text = data.institution

            if(data.enddate.isNullOrEmpty()){ // 기간이 없을 때 '~' 숨기기
                startToend1.visibility = GONE
            }

            if(data.enddate2.isNullOrEmpty()){
                one_period.visibility = GONE
                scholar_perioid2.visibility = GONE
            }
            else{
                scholar_startDate2.text = data.startdate2
                scholar_endDate2.text = data.enddate2
            }
            scholar_startDate.text = data.startdate
            scholar_endDate.text = data.enddate




            itemView.setOnClickListener {
                val intent = Intent(mContext1, ScholarshipDetailActivity::class.java)
                intent.apply {
                    this.putExtra("title", scholar_title.text.toString())
                }
                mContext1.startActivity(intent)
            }
        }
    }

    // ViewHolder에게 item을 보여줄 View로 쓰일 item_data_list.xml를 넘기면서 ViewHolder 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScholarItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_scholarship, parent, false)
        return ScholarItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    // ViewHolder의 bind 메소드를 호출한다.
    override fun onBindViewHolder(holder: ScholarshipRecyclerViewAdapter.ScholarItemViewHolder, position: Int) {
        Log.d("ListAdapter", "===== ===== ===== ===== onBindViewHolder ===== ===== ===== =====")
        holder.bind(list[position], position)
    }

    override fun getItemViewType (position : Int) : Int{
        return position;
    }


}

object DiffCallbackScholar : DiffUtil.ItemCallback<Scholarship>() {
    override fun areItemsTheSame(
        oldItem: Scholarship,
        newItem: Scholarship
    ): Boolean {
        return oldItem.title == newItem.title
    }

    override fun areContentsTheSame(
        oldItem: Scholarship,
        newItem: Scholarship
    ): Boolean {
        return oldItem == newItem
    }
}