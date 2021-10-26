package com.cookandroid.scholarshiplike.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cookandroid.scholarshiplike.MagazineDetailActivity
import com.cookandroid.scholarshiplike.Post
import com.cookandroid.scholarshiplike.R
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage


class MagazineRecyclerViewAdapter(val postlist: ArrayList<Post>, val mContext: Context) : ListAdapter<Post, MagazineRecyclerViewAdapter.MagazineViewHolder>(
    DiffCallbackMagazine
) {

    //뷰홀더 생성 때 호출
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MagazineViewHolder {
        //연결 레이아웃 설정
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_magazine, parent, false)

        return MagazineViewHolder(view)
    }

    override fun getItemCount(): Int = postlist.size

    override fun onBindViewHolder(holder: MagazineViewHolder, position: Int) {
        holder.bind(postlist[position])
    }

    inner class MagazineViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val title = itemView.findViewById<TextView>(R.id.titleText) //타이틀
        val imageView = itemView.findViewById<ImageView>(R.id.imageView) //이미지뷰
        var contents = ""
        var imageURL = ""
        val storageRef = Firebase.storage

        fun bind(item: Post) {
            title.text = item.title
            contents = item.contents
            imageURL = item.imageURL

            if(imageURL != null) {
                storageRef.getReferenceFromUrl(imageURL).downloadUrl.addOnSuccessListener {
                    Glide.with(mContext)
                        .load(it)
                        .into(imageView)
                }.addOnFailureListener {
                    // Handle any errors
                }
            }

            itemView.setOnClickListener {
                val intent = Intent(mContext, MagazineDetailActivity::class.java)
                intent.apply {
                    this.putExtra("title",title.text.toString())
                    this.putExtra("contents",contents)
                    this.putExtra("imageURL",imageURL)
                }
                mContext.startActivity(intent)
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