package com.cookandroid.scholarshiplike.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cookandroid.scholarshiplike.R
import com.cookandroid.scholarshiplike.SearchScholarship

class HomeSearchScholarshipRecyclerViewAdapter(val scholarshipList: ArrayList<SearchScholarship>) : RecyclerView.Adapter<HomeSearchScholarshipRecyclerViewAdapter.SearchItemViewHolder>() {

    // ViewHolder에게 item을 보여줄 View로 쓰일 item_scholarship.xml를 넘기면서 ViewHolder 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_scholarship, parent, false)
        return SearchItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return scholarshipList.size
    }

    // ViewHolder의 bind 메소드를 호출
    override fun onBindViewHolder(holder: SearchItemViewHolder, position: Int) {
        holder.title.text = scholarshipList[position].title
        holder.period_start.text = scholarshipList[position].period_start
        holder.period_end.text = scholarshipList[position].period_end
        holder.institution.text = scholarshipList[position].institution
    }

    // ViewHolder 정의
    class SearchItemViewHolder(scholarshipView: View): RecyclerView.ViewHolder(scholarshipView) {
        var title: TextView = scholarshipView.findViewById(R.id.scholar_title)
        var period_start: TextView = scholarshipView.findViewById(R.id.scholar_startDate)
        var period_end: TextView = scholarshipView.findViewById(R.id.scholar_endDate)
        var institution: TextView = scholarshipView.findViewById(R.id.scholar_institution)
    }

}
