package com.cookandroid.scholarshiplike

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView


class MagazineRecyclerViewAdapter(val postlist: ArrayList<Post>, val mContext: Context) : ListAdapter<Post, MagazineRecyclerViewAdapter.MagazineViewHolder>(DiffCallbackMagazine) {

    private var mContext1 : Context = mContext

    //뷰홀더 생성 때 호출
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MagazineViewHolder {
        //연결 레이아웃 설정
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_magazine, parent, false)

        return MagazineViewHolder(view)
    }

    override fun getItemCount(): Int = postlist.size

    override fun onBindViewHolder(holder: MagazineRecyclerViewAdapter.MagazineViewHolder, position: Int) {
        holder.bind(postlist[position])
    }

    inner class MagazineViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title = itemView.findViewById<TextView>(R.id.titleText) //타이틀

        fun bind(item: Post) {
            title.text = item.title

            itemView.setOnClickListener {
                val intent = Intent(mContext1, MagazineDetailActivity::class.java)
                intent.apply {
                    this.putExtra("title",title.text.toString())
                }
                mContext1.startActivity(intent)
            }

        }
    }
}

object DiffCallbackMagazine : DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(
        oldItem: Post,
        newItem: Post
    ): Boolean {
        return oldItem.title == newItem.title
    }

    override fun areContentsTheSame(
        oldItem: Post,
        newItem: Post
    ): Boolean {
        return oldItem == newItem
    }

}