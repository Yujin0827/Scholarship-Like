package com.cookandroid.scholarshiplike

import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.cookandroid.scholarshiplike.adapter.HomeCalendarAdapter
import com.cookandroid.scholarshiplike.databinding.FragmentHomeBinding
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.getField
import com.google.firebase.ktx.Firebase


class HomeFragment : Fragment() {

    // binding
    private var _binding: FragmentHomeBinding? = null   // 바인딩 객체
    private val binding get() = _binding!!              // 바인딩 변수 재선언 (매번 null 체크x)

    // Firebase
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var userUid: String                            // user id
    private lateinit var userUniv: String                           // user 대학교
    private lateinit var univWebSite: String                        // user 대학교 사이트
    private var banner_list: ArrayList<SlideModel> = arrayListOf()  // banner list

    private val scholarshipTab = ScholarshipFragment()              // fragment_scholarship 변수
    lateinit var tabNav: BottomNavigationView                       // 하단바 (MainActivity)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root

        // Firebase
        auth = Firebase.auth
        db = Firebase.firestore

        // Banner
        db.collection("Banner")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    if (document.getString("URL") != null) {
                        banner_list.add(SlideModel(document.getString("URL")))
                    }
                }
                binding.bannerSlider.setImageList(banner_list, ScaleTypes.FIT)
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }

        // 장학금 최대 건수 ( scholarship 탭으로 이동 )
        setUserName()
        binding.scholarCnt.setOnClickListener {
            tabNav = (activity as MainActivity).findViewById<BottomNavigationView>(R.id.tabNav)
            tabNav.menu.findItem(R.id.scholarshipTab).isChecked = true

            activity?.getSupportFragmentManager()?.beginTransaction()
                ?.replace(R.id.nav, scholarshipTab, "scholarshipTab")
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
                var intent = Intent(context, LikeContentActivity::class.java)
                startActivity(intent)
            }
        }

        // 알림 페이지(AlarmActivity)로 이동
        binding.alarm.setOnClickListener {
            activity?.let {
                var intent = Intent(context, AlarmActivity::class.java)
                startActivity(intent)
            }
        }

        // 검색창(HomeSearchActivity)으로 이동
        binding.searchAll.setOnClickListener {
            activity?.let {
                var intent = Intent(it, HomeSearchActivity::class.java)
                it?.startActivity(intent)
            }
        }

        // 한국장학재단 웹사이트로 이동
        binding.kosafWeb.setOnClickListener {
            var uri = Uri.parse("http://www.kosaf.go.kr")
            var intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }

        // 교내 웹사이트로 이동
        binding.univWeb.setOnClickListener {
            val user = auth.currentUser

            user?.let {
                userUid = user.uid
            }

            db.collection("Users")
                .document(userUid)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        if (document.getString("univ") != null) {
                            userUniv = document.getString("univ")!!
                        }
                    }

                    db.collection("Scholarship")
                        .document("UnivScholar")
                        .get()
                        .addOnSuccessListener { document ->
                            if (document != null) {
                                if (document.getString(userUniv) != null) {
                                    univWebSite = document.getString(userUniv)!!

                                    var uri = Uri.parse(univWebSite)
                                    var intent = Intent(Intent.ACTION_VIEW, uri)
                                    startActivity(intent)
                                }
                            }
                        }
                }
        }

        // 내 조건 수정 페이지로 이동
        binding.profileChange.setOnClickListener {
            activity?.let {
                var intent = Intent(it, ProfileMyConChangeActivity::class.java)
                it?.startActivity(intent)
            }
        }

        super.onActivityCreated(savedInstanceState)
    }

    // 유저 닉네임 가져오기
    private fun setUserName() {
        val user = auth.currentUser

        if (user != null) {
            db.collection("Users")
                .document(user.uid)
                .get()
                .addOnSuccessListener { result ->
                    binding.scholarName.text = result.getField<String>("nickname")
                }
                .addOnFailureListener() { exception ->
                    binding.scholarName.text = " "
                    Log.e(TAG, "Fail to get user nickname from DB!", exception)
                }
        }
    }

    // calendar
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    // calendar
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

    // 프래그먼트 파괴
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}