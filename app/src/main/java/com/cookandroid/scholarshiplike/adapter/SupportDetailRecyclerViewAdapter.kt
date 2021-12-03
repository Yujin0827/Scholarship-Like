package com.cookandroid.scholarshiplike.adapter


import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cookandroid.scholarshiplike.DetailSupport
import com.cookandroid.scholarshiplike.R
import com.cookandroid.scholarshiplike.ScholarshipRecyclerViewAdapter
import com.cookandroid.scholarshiplike.Support


class SupportDetailRecyclerViewAdapter (private var list: MutableList<DetailSupport>):
    ListAdapter<DetailSupport, SupportDetailRecyclerViewAdapter.SupportItemViewHolder>(DiffCallbackScholar){



    // 데이터 가져오기
    // inner class로 ViewHolder 정의
    inner class SupportItemViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        var title: TextView = itemView!!.findViewById(R.id.title)
        var contents: TextView = itemView!!.findViewById(R.id.contents)


        // onBindViewHolder의 역할을 대신한다.
        fun bind(data: DetailSupport, position: Int) {
            Log.d("ListAdapter", "===== ===== ===== ===== bind ===== ===== ===== =====")

            title.text = data.title
            contents.text = data.content

        }
    }

    // ViewHolder에게 item을 보여줄 View로 쓰일 item_data_list.xml를 넘기면서 ViewHolder 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SupportItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_support_detail, parent, false)
        return SupportItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    // ViewHolder의 bind 메소드를 호출한다.
        override fun onBindViewHolder(holder: SupportItemViewHolder, position: Int) {
        Log.d("ListAdapter", "===== ===== ===== ===== onBindViewHolder ===== ===== ===== =====")
        holder.bind(list[position], position)
    }


}

object DiffCallbackScholar : DiffUtil.ItemCallback<DetailSupport>() {
    override fun areItemsTheSame(
        oldItem: DetailSupport,
        newItem: DetailSupport
    ): Boolean {
        return oldItem.title == newItem.title
    }

    override fun areContentsTheSame(
        oldItem: DetailSupport,
        newItem: DetailSupport
    ): Boolean {
        return oldItem == newItem
    }
}