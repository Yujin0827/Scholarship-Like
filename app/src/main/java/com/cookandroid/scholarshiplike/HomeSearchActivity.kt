package com.cookandroid.scholarshiplike

import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_home_search.*


class HomeSearchActivity : AppCompatActivity() {

    lateinit var searchBar: EditText            // 검색창
    lateinit var searchGoBtn : ImageView        // 찾기 버튼
    lateinit var resultList : RecyclerView      // 검색 결과
    lateinit var search_tabLayout : TabLayout   // 검색 결과 탭 - 장학금, 매거진

    internal var textlength = 0     // EditText 글자 수
    //var searchList = arrayListOf

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

        // 어댑터 생성, 연결
        viewAdapter = ViewPageAdapter(this)
        viewAdapter.addFragment(HomeSearchScholarshipFragment())
        viewAdapter.addFragment(HomeSearchMagazineFragment())
        search_viewpager.adapter = viewAdapter

        // 탭 레이아웃 이름 연결
        TabLayoutMediator(search_tabLayout, search_viewpager){ tab, position->
            tab.text = tabLayoutTextArray[position]
        }.attach()

        // 탭 생성
        searchBar.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                when(event?.action) {
                    MotionEvent.ACTION_DOWN -> search_tabLayout.visibility = View.VISIBLE
                }

                return v?.onTouchEvent(event) ?: true
            }
        })

        // 검색창 입력 감지
        searchBar.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(edit: Editable?) {

            }

            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
                textlength = searchBar.text.length
            }
        })

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