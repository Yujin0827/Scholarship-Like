package com.cookandroid.scholarshiplike

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.cookandroid.scholarshiplike.adapter.HomeCalendarPopupAdapter
import com.cookandroid.scholarshiplike.adapter.MagazineRecyclerViewAdapter
import com.cookandroid.scholarshiplike.databinding.FragmentHomeCalendarPopupBinding
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat

class HomeCalendarPopupFragment(val scholar:ArrayList<tmpScholarship>, val mContext: Context, val Month:Int, val Date:Int, val position:Int, val fisrt:Int, val last:Int) : DialogFragment() {

    private lateinit var binding: FragmentHomeCalendarPopupBinding

    private lateinit var listAdapter: HomeCalendarPopupAdapter //리사이클러뷰 변수 생성
    private var visibleList: ArrayList<tmpScholarship> = arrayListOf()

    val format = SimpleDateFormat("MMdd")
    val selectedDate = Month*100+Date

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //false로 설정해 주면 화면밖 혹은 뒤로가기 버튼시 다이얼로그라 dismiss 되지 않는다.
        isCancelable = true

        for(item in scholar) {
            val start = format.format(item.startdate).toInt()
            val end = format.format(item.enddate).toInt()

            if(selectedDate in start..end && position >= fisrt && position <= last) visibleList.add(item)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeCalendarPopupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.textView.setText(Date.toString()+"일")

        listAdapter = HomeCalendarPopupAdapter(visibleList, mContext)
        binding.popupRecyclerView.layoutManager = LinearLayoutManager(mContext)
        binding.popupRecyclerView.setHasFixedSize(true) //리사이클러뷰 성능 개선 방안
        binding.popupRecyclerView.adapter = listAdapter //어댑터 연결

    }
}