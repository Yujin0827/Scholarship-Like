package com.cookandroid.scholarshiplike

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_home_search.*


class HomeSearchActivity : AppCompatActivity() {

    lateinit var searchBar: EditText            // 검색창
    lateinit var searchGoBtn : ImageView        // 찾기 버튼
    lateinit var resultList : RecyclerView      // 검색 결과
    lateinit var search_tabLayout : TabLayout   // 검색 결과 탭 - 장학금, 매거진
    lateinit var searchMagazinFrag: HomeSearchMagazineFragment
    lateinit var searchScholarFrag: HomeSearchScholarshipFragment


    // 검색결과 탭
    private var tabLayoutTextArray: ArrayList<String> = arrayListOf("장학금", "매거진")
    lateinit var viewAdapter: ViewPageAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_search)

        searchBar = findViewById<EditText>(R.id.searchBar)                  // 검색창
        searchGoBtn = findViewById<ImageView>(R.id.searchGoBtn)             // 찾기 버튼
        search_tabLayout = findViewById<TabLayout>(R.id.search_tabLayout)   // 검색 결과 탭
        //resultList = findViewById<RecyclerView>(R.id.toolbar)             // 검색 결과
        searchMagazinFrag = HomeSearchMagazineFragment()
        searchScholarFrag = HomeSearchScholarshipFragment()

        // 어댑터 생성, 연결
        viewAdapter = ViewPageAdapter(this)
        viewAdapter.addFragment(HomeSearchScholarshipFragment())
        viewAdapter.addFragment(HomeSearchMagazineFragment())
        search_viewpager.adapter = viewAdapter

        search_viewpager.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                Log.e("page num", "page ${position+1}")
            }
        })


        // 탭 레이아웃 이름 연결
        TabLayoutMediator(search_tabLayout, search_viewpager) { tab, position ->
            tab.text = tabLayoutTextArray[position]
        }.attach()

        searchBar.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                Log.w("TextWatcher", s.toString())
                searchMagazinFrag.update(s.toString())
                searchScholarFrag.update(s.toString())

                viewAdapter.notifyDataSetChanged()
            }
        })
    }

}


