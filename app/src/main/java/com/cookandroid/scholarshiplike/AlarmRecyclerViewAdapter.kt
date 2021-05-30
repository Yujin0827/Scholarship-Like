package com.cookandroid.scholarshiplike

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class AlarmRecyclerViewAdapter (private var list: MutableList<Alarm>): ListAdapter<Alarm, AlarmRecyclerViewAdapter.AlarmItemViewHolder>(DiffCallbackAlarm){
    interface OnItemClickListener{
        fun onItemClick(v:View, data: Alarm, pos : Int)
    }
    private var listener : OnItemClickListener? = null
    fun setOnItemClickListener(listener : OnItemClickListener) {
        this.listener = listener
    }


    // inner class로 ViewHolder 정의
    inner class AlarmItemViewHolder(itemView: View?): RecyclerView.ViewHolder(itemView!!) {

        var category: TextView = itemView!!.findViewById(R.id.alarm_category)
        var title: TextView = itemView!!.findViewById(R.id.alarm_title)
        var date: TextView = itemView!!.findViewById(R.id.alarm_date)

        // onBindViewHolder의 역할을 대신한다.
        fun bind(data: Alarm, position: Int) {
            Log.d("ListAdapter", "===== ===== ===== ===== bind ===== ===== ===== =====")
            Log.d("ListAdapter", data.title+" "+data.date)
            category.text = data.category
            title.text = data.title
            date.text = data.date

            val pos = bindingAdapterPosition
            if(pos!= RecyclerView.NO_POSITION)
            {
                itemView.setOnClickListener {
                    listener?.onItemClick(itemView,data,pos)
                }
            }
        }
    }

    // ViewHolder에게 item을 보여줄 View로 쓰일 item_data_list.xml를 넘기면서 ViewHolder 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_alarm, parent, false)
        return AlarmItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.count()
    }

    // ViewHolder의 bind 메소드를 호출한다.
    override fun onBindViewHolder(holder: AlarmRecyclerViewAdapter.AlarmItemViewHolder, position: Int) {
        Log.d("ListAdapter", "===== ===== ===== ===== onBindViewHolder ===== ===== ===== =====")
        holder.bind(list[position], position)
    }
}

//데이터 갱신
object DiffCallbackAlarm : DiffUtil.ItemCallback<Alarm>() {
    override fun areItemsTheSame(
        oldItem: Alarm,
        newItem: Alarm
    ): Boolean {
        return oldItem.title == newItem.title
    }

    override fun areContentsTheSame(
        oldItem: Alarm,
        newItem: Alarm
    ): Boolean {
        return oldItem == newItem
    }
}