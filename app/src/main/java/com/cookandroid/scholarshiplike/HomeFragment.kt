package com.cookandroid.scholarshiplike

import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.cookandroid.scholarshiplike.adapter.HomeCalendarAdapter
import com.cookandroid.scholarshiplike.databinding.FragmentHomeBinding
import com.denzcoskun.imageslider.interfaces.ItemClickListener
import com.denzcoskun.imageslider.models.SlideModel
import com.google.ads.AdSize
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

//    // banner
//    private var numBanner = 3
//    private var currentPosition = Int.MAX_VALUE / 2

    // Firebase
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var userUid: String                            // user id
    private lateinit var userUniv: String                           // user 대학교
    private lateinit var univWebSite: String                        // user 대학교 사이트
//    private var banner_list: ArrayList<SlideModel> = arrayListOf()  // banner list

    private val HomeTab = "Home_Fragment"                           // fragment_home 변수
    private val ScholarshipTab = "Scholarship_Fragment"             // fragment_scholarship 변수
    lateinit var tabNav: BottomNavigationView                       // 하단바 (MainActivity)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root

        // Firebase
        auth = Firebase.auth
        db = Firebase.firestore

        // Banner
        setBanner()

        // user 정보 세팅 - 이름, 대학교
        setUserInfo()

        // 장학금 최대 건수 ( scholarship 탭으로 이동 )
        binding.scholarCnt.setOnClickListener {
            tabNav = (activity as MainActivity).findViewById<BottomNavigationView>(R.id.tabNav)
            tabNav.menu.findItem(R.id.scholarshipTab).isChecked = true

            val transaction = parentFragmentManager.beginTransaction()
            var scholarshiptab = parentFragmentManager.findFragmentByTag(ScholarshipTab)

            if (scholarshiptab != null) {
                transaction.remove(scholarshiptab)
            }

            transaction.add(R.id.nav, ScholarshipFragment(), ScholarshipTab)

            var hometab = parentFragmentManager.findFragmentByTag(HomeTab)

            hometab?.let { it ->  transaction.hide(it)}
            scholarshiptab?.let { it -> transaction.show(it) }
            transaction.commit()
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

//        // 검색창(HomeSearchActivity)으로 이동
//        binding.searchAll.setOnClickListener {
//            activity?.let {
//                var intent = Intent(it, HomeSearchActivity::class.java)
//                it?.startActivity(intent)
//            }
//        }

        // banner click
        binding.banner.setItemClickListener(object : ItemClickListener {
            override fun onItemSelected(position: Int) {
                binding.kosafWeb.setOnClickListener {
                    var uri = Uri.parse("https://www.kosaf.go.kr/ko/scholar.do?pg=scholarship_main")
                    var intent = Intent(Intent.ACTION_VIEW, uri)
                    startActivity(intent)
                }
            }
        })

        // 한국장학재단 웹사이트로 이동
        binding.kosafWeb.setOnClickListener {
            var uri = Uri.parse("http://www.kosaf.go.kr")
            var intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }

        // 교내 웹사이트로 이동
        binding.univWeb.setOnClickListener {
            userUniv = binding.univText.text.toString()

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

        // 내 조건 수정 페이지로 이동
        binding.profileChange.setOnClickListener {
            activity?.let {
                var intent = Intent(it, ProfileMyConChangeActivity::class.java)
                it?.startActivity(intent)
            }
        }

        super.onActivityCreated(savedInstanceState)
    }

    // 유저 정보 가져오기 - name, univ
    fun setUserInfo() {
        val user = auth.currentUser

        user?.let {
            userUid = user.uid
        }

        if (user != null) {
            db.collection("Users")
                .document(userUid)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        binding.userName.text = document.getField<String>("nickname")   // user name
                        binding.univText.text = document.getString("univ")!!            // user univ
                    }
                }
                .addOnFailureListener() { exception ->
                    binding.userName.text = " "
                    Log.e(TAG, "Fail to get user nickname from DB!", exception)
                }
        }
    }

    // banner
    fun setBanner() {
        val imageList = ArrayList<SlideModel>() // Create image list

        db.collection("Banner")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    if (document.getString("URL") != null) {
                        imageList.add(SlideModel(document.getString("URL")))
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }

        binding.banner.setImageList(imageList)
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

        // 아래 스와이핑으로 새로고침
        binding.swipeRefreshFragmentHome.setOnRefreshListener {
            Handler().postDelayed({ // 아래로 스와이핑 이후 1초 후에 리플래쉬 아이콘 없애기
                if (binding.swipeRefreshFragmentHome.isRefreshing)
                    binding.swipeRefreshFragmentHome.isRefreshing = false
            }, 1000)

            // !새로고침에 적용할 메소드 추가하기!
            setUserInfo()
            initView()
        }
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