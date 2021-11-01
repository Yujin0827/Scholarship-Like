//package com.cookandroid.scholarshiplike.adapter
//
//import android.view.LayoutInflater
//import android.view.ViewGroup
//import androidx.fragment.app.Fragment
//import androidx.fragment.app.FragmentActivity
//import androidx.recyclerview.widget.RecyclerView
//import androidx.viewpager2.adapter.FragmentStateAdapter
//import com.cookandroid.scholarshiplike.HomeCalendarDetailFragment
//import com.cookandroid.scholarshiplike.R
//import com.denzcoskun.imageslider.adapters.ViewPagerAdapter
//
//class HomeBannerAdapter (bannerList: ArrayList<Int>) : RecyclerView.Adapter<ViewPagerAdapter.PagerViewHolder>() {
//    var item = bannerList
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = PagerViewHolder((parent))
//
//    override fun getItemCount(): Int = Int.MAX_VALUE   // 아이템의 갯수를 임의로 확 늘린다.
//
//    override fun onBindViewHolder(holder: PagerViewHolder, position: Int) {
//        holder.banner.setImageResource(item[position%3])   // position에 3을 나눈 나머지 값을 사용한다.
//    }
//
//    inner class PagerViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder
//        (LayoutInflater.from(parent.context).inflate(R.layout.banner_list_item, parent, false)){
//
//        val banner = itemView.imageView_banner!!
//    }
//
//}