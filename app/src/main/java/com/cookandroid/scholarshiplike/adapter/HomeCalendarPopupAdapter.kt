package com.cookandroid.scholarshiplike.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cookandroid.scholarshiplike.R
import com.cookandroid.scholarshiplike.ScholarshipDetailActivity
import com.cookandroid.scholarshiplike.tmpScholarship
import java.text.SimpleDateFormat

class HomeCalendarPopupAdapter(val arraylist: ArrayList<tmpScholarship>, val mContext: Context) : RecyclerView.Adapter<HomeCalendarPopupAdapter.mViewholder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): mViewholder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_calendar_popup, parent, false)
        return mViewholder(
            view
        )
    }

    override fun getItemCount(): Int {
        return arraylist.size
    }

    override fun onBindViewHolder(holder: mViewholder, position: Int) {
        holder.bind(arraylist[position],mContext)

    }

    class mViewholder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val popup_name = itemView.findViewById<TextView>(R.id.popup_name)
        val start = itemView.findViewById<TextView>(R.id.start)
        val end = itemView.findViewById<TextView>(R.id.end)

        val format = SimpleDateFormat("yyyy.MM.dd")

        fun bind(item: tmpScholarship, context: Context) {
            popup_name.text = item.title
            start.text = format.format(item.startDate).toString()
            end.text = format.format(item.endDate).toString()

            itemView.setOnClickListener {
                val intent = Intent(context, ScholarshipDetailActivity::class.java)
                intent.apply {
                    this.putExtra("title",popup_name.text.toString())
                }
                context.startActivity(intent)
            }
        }

    }
}