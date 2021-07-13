package com.cookandroid.scholarshiplike.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cookandroid.scholarshiplike.R
import com.cookandroid.scholarshiplike.Scheduel
import com.cookandroid.scholarshiplike.ScholarshipDetailActivity

class HomeCalendarPopupAdapter(val arraylist: ArrayList<Scheduel>, val mContext: Context) : RecyclerView.Adapter<HomeCalendarPopupAdapter.mViewholder>() {

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

        fun bind(item: Scheduel, context: Context) {
            popup_name.text = item.name
            start.text = item.start
            end.text = item.end

            itemView.setOnClickListener {
                val intent = Intent(context, ScholarshipDetailActivity::class.java)
                context.startActivity(intent)
            }
        }

    }
}