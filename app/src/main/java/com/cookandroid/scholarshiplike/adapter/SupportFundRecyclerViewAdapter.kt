package com.cookandroid.scholarshiplike.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cookandroid.scholarshiplike.R
import com.cookandroid.scholarshiplike.Support
import com.cookandroid.scholarshiplike.SupportFundDetailActivity


class SupportFundRecyclerViewAdapter (private var list: MutableList<Support>, val mContext: Context):
    ListAdapter<Support, SupportFundRecyclerViewAdapter.SupportItemViewHolder>(DiffCallbackSupport){

    private var mContext1 : Context = mContext

    // 데이터 가져오기
    // inner class로 ViewHolder 정의
    inner class SupportItemViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        var title: TextView = itemView!!.findViewById(R.id.support_title)


        // onBindViewHolder의 역할을 대신한다.
        fun bind(data: Support, position: Int) {
            Log.d("ListAdapter", "===== ===== ===== ===== bind ===== ===== ===== =====")
            Log.d("ListAdapter", data.title)

            title.text = data.title



            itemView.setOnClickListener {
                val intent = Intent(mContext1, SupportFundDetailActivity::class.java)
                intent.apply {
                    this.putExtra("title", title.text.toString())
                }
                mContext1.startActivity(intent)
            }
        }
    }

    // ViewHolder에게 item을 보여줄 View로 쓰일 item_data_list.xml를 넘기면서 ViewHolder 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SupportItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_support, parent, false)
        return SupportItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    // ViewHolder의 bind 메소드를 호출한다.
    override fun onBindViewHolder(holder: SupportFundRecyclerViewAdapter.SupportItemViewHolder, position: Int) {
        Log.d("ListAdapter", "===== ===== ===== ===== onBindViewHolder ===== ===== ===== =====")
        holder.bind(list[position], position)
    }



}

object DiffCallbackSupport : DiffUtil.ItemCallback<Support>() {
    override fun areItemsTheSame(
        oldItem: Support,
        newItem: Support
    ): Boolean {
        return oldItem.title == newItem.title
    }

    override fun areContentsTheSame(
        oldItem: Support,
        newItem: Support
    ): Boolean {
        return oldItem == newItem
    }
}