package com.cookandroid.scholarshiplike

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil.setContentView
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.cookandroid.scholarshiplike.adapter.HomeCalendarAdapter
import com.cookandroid.scholarshiplike.databinding.FragmentHomeBinding
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.firebase.firestore.FirebaseFirestore


class HomeFragment : Fragment() {

    private var mBinding: FragmentHomeBinding? = null   // 바인딩 객체
    private val binding get() = mBinding!!              // 바인딩 변수 재선언(매번 null 체크x)

    private val db = FirebaseFirestore.getInstance()    // FireStore 인스턴스

    val scholarshiptab = ScholarshipFragment()          // fragment_scholarship 변수
    val UnivWebSite = db.collection("Scholarship")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root

        // 장학금 탭으로 이동
        binding.scholarCnt.setOnClickListener {
            activity?.getSupportFragmentManager()?.beginTransaction()
                ?.replace(R.id.nav, scholarshiptab, "scholarshipTab")
                ?.commit()
        }

        // AdMob
        MobileAds.initialize(requireContext()) {}

        val mAdView = view.findViewById(R.id.adView) as AdView
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

        return view

    }

    // fragment -> activity 화면 이동
    override fun onActivityCreated(savedInstanceState: Bundle?) {

        // 좋아요 게시물 페이지(LikeContentActivity)로 이동
        binding.like.setOnClickListener {
            activity?.let {
                val intent = Intent(context, LikeContentActivity::class.java)
                startActivity(intent)
            }
        }

        // 알림 페이지(AlarmActivity)로 이동
        binding.alarm.setOnClickListener {
            activity?.let {
                val intent = Intent(context, AlarmActivity::class.java)
                startActivity(intent)
            }
        }

        // 검색창(HomeSearchActivity)으로 이동
        binding.searchAll.setOnClickListener {
            activity?.let {
                val intent = Intent(it, HomeSearchActivity::class.java)
                it?.startActivity(intent)
            }
        }

        // 한국장학재단 웹사이트로 이동
        binding.kosafWeb.setOnClickListener {
            val uri = Uri.parse("http://www.kosaf.go.kr")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }

        // 교내 웹사이트로 이동
        binding.univWeb.setOnClickListener {
            UnivWebSite.get()
                .addOnSuccessListener { document ->
                }
            val uri = Uri.parse("http://www.kosaf.go.kr")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }

        // 내조건 수정 페이지로 이동
        binding.profileChange.setOnClickListener {
            activity?.let {
                val intent = Intent(it, ProfileMyConChangeActivity::class.java)
                it?.startActivity(intent)
            }
        }

        super.onActivityCreated(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    fun initView() {
        val homeCalnederAdapter = HomeCalendarAdapter(requireActivity())

        binding.calendarViewPager.adapter = homeCalnederAdapter
        binding.calendarViewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        homeCalnederAdapter.apply {
            binding.calendarViewPager.setCurrentItem(this.firstFragmentPosition, false)
        }
    }

    // 프래그먼트 생성시 툴바 hide
    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
    }

    // 프래그먼트 종료시 툴바 show
    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
    }

    // 프레그먼트 파괴
    override fun onDestroyView() {
        mBinding = null
        super.onDestroyView()
    }
}


