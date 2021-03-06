package com.cookandroid.scholarshiplike

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.cookandroid.scholarshiplike.adapter.ScholarshipViewPageAdapter
import com.cookandroid.scholarshiplike.databinding.FragmentScholarshipBinding
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.fragment_scholarship.*


class ScholarshipFragment : Fragment() {

    private var _binding: FragmentScholarshipBinding? = null    // 바인딩 객체
    private val binding get() = _binding!!                      // 바인딩 변수 재선언(매번 null 체크x)

    lateinit var like : ImageView       // 좋아요 페이지로 이동 버튼
    lateinit var alarm : ImageView      // 알람 페이지로 이동 버튼
    lateinit var viewPagers : ViewPager // 뷰 페이저 선언
    lateinit var tabLayouts : TabLayout // 탭 레이아웃 선언

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_scholarship, container, false)

        like = view.findViewById<ImageView>(R.id.like)              // hometab의 좋아요 버튼 변수 생성
        alarm = view.findViewById<ImageView>(R.id.alarm)            // hometab의 알람 버튼 변수 생성

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setUpViewPager()

        tabLayouts.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabSelected(tab: TabLayout.Tab?) {

            }
        })

        like.setOnClickListener {
            activity?.let {
                val intent = Intent(it, LikeContentActivity::class.java)
                it?.startActivity(intent)
            }
        }

        alarm.setOnClickListener{
            activity?.let{
                val intent = Intent(it, AlarmActivity::class.java)
                it?.startActivity(intent)
            }
        }



    }

    // 뷰페이저 레이아웃 연결 , 생성
    private fun setUpViewPager(){
        viewPagers = scholar_viewpager
        tabLayouts = scholar_tabLayout

        var adapter =
            ScholarshipViewPageAdapter(
                childFragmentManager
            )
        adapter.addFragment(ScholarshipMyscholarFragment(), "내 장학금")
        adapter.addFragment(ScholarshipAllscholarFragment(), "전체 장학금")

        viewPagers!!.adapter = adapter
        tabLayouts!!.setupWithViewPager(viewPagers)
    }



    // 프래그먼트 생성시 툴바 hide
    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
    }
    companion object{

    }

    // 프래그먼트 종료시 툴바 show
    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
    }


    // 프래그먼트 파괴
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}


