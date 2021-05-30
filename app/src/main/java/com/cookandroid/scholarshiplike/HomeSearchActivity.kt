package com.cookandroid.scholarshiplike

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_home_search.*
import androidx.recyclerview.widget.RecyclerView
import android.app.SearchManager
import android.content.Intent
import android.view.SearchEvent
import android.view.View
import android.widget.EditText
import androidx.core.view.isVisible
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_like_content.*

class HomeSearchActivity : AppCompatActivity() {

    lateinit var goBack : ImageView             // 뒤로가기 버튼
    lateinit var searchBar: EditText            // 검색창
    lateinit var searchGoBtn : ImageView        // 찾기 버튼
    lateinit var resultList : RecyclerView      // 검색 결과
    lateinit var search_tabLayout : TabLayout   // 검색 결과 탭 - 장학금, 매거진

    // 검색결과 탭
    private var tabLayoutTextArray: ArrayList<String> = arrayListOf("장학금", "매거진")
    lateinit var viewAdapter: ViewPageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_search)

        searchBar = findViewById<EditText>(R.id.searchBar)                  // 검색창
        searchGoBtn = findViewById<ImageView>(R.id.searchGoBtn)             // 찾기 버튼
        search_tabLayout = findViewById<TabLayout>(R.id.search_tabLayout)   // 검색 결과 탭
//        resultList = findViewById<RecyclerView>(R.id.toolbar)             // 검색 결과

        search_tabLayout.visibility = View.INVISIBLE

        searchGoBtn.setOnClickListener {
            search_tabLayout.visibility = View.VISIBLE

            // 어댑터 생성, 연결
            viewAdapter = ViewPageAdapter(this)
            viewAdapter.addFragment(HomeSearchScholarshipFragment())
            viewAdapter.addFragment(HomeSearchMagazineFragment())
            search_viewpager.adapter = viewAdapter

            // 탭 레이아웃 이름 연결
            TabLayoutMediator(search_tabLayout, search_viewpager){ tab, position->
                tab.text = tabLayoutTextArray[position]
            }.attach()
        }



        // 쿼리 수신
        if (Intent.ACTION_SEARCH == intent.action) {
            intent.getStringExtra(SearchManager.QUERY)?.also { query ->
                doMySearch(query)
            }
        }
        



    }

    private fun doMySearch(query: String) {

    }

}