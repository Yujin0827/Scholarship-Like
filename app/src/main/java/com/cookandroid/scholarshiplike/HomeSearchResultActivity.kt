package com.cookandroid.scholarshiplike

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cookandroid.scholarshiplike.adapter.ViewPageAdapter
import com.cookandroid.scholarshiplike.databinding.ActivityHomeSearchResultBinding
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_home_search.*
import kotlinx.android.synthetic.main.activity_home_search_result.*


class HomeSearchResultActivity : AppCompatActivity() {

    // 검색결과 탭
    private var tabLayoutTextArray: ArrayList<String> = arrayListOf("장학금", "매거진")
    private lateinit var viewAdapter: ViewPageAdapter

    private var mBinding: ActivityHomeSearchResultBinding? = null         // 바인딩 객체
    private val binding get() = mBinding!!                          // 바인딩 변수 재선언(매번 null 체크x)

    private lateinit var search_word: String     // 검색어 변수
    private var imm: InputMethodManager?= null      // 키보드 변수 선언

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityHomeSearchResultBinding.inflate(layoutInflater)  // 액티비티에서 사용할 바인딩 클래스의 인스턴스 생성
        setContentView(binding.root)

        getExtra()      // 검색어 수신

        // 어댑터 생성, 연결
        viewAdapter = ViewPageAdapter(this)
        viewAdapter.addFragment(HomeSearchScholarshipFragment())
        viewAdapter.addFragment(HomeSearchMagazineFragment())
        search_viewpager.adapter = viewAdapter

        // 탭 레이아웃 이름 연결
        TabLayoutMediator(search_tabLayout, search_viewpager) { tab, position ->
            tab.text = tabLayoutTextArray[position]
        }.attach()

        // 키보드 세팅
        imm = getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as InputMethodManager?

        // 검색 버튼 클릭 (아이콘)
        binding.searchBtn.setOnClickListener {
            click()
        }

        // 검색 버튼 클릭 (키보드)
        binding.searchField.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent): Boolean {
                if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    click()
                    return true
                }
                return false
            }
        })

    }

    // 검색어 수신
    private fun getExtra() {
        if (intent.hasExtra("search_word")) {
            search_word = intent.getStringExtra("search_word")
            binding.searchField.setText(search_word)
        }
        else {
            Toast.makeText(this, "검색어를 입력하세요.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun firebaseSearch() {

    }

    // 검색 버튼 클릭했을 때 동작
    private fun click() {
        if(search_word.length == 0) {
            var null_message = Toast.makeText(this, "검색어를 입력하세요.", Toast.LENGTH_SHORT)
            null_message.show()
        }
        else {

        }
    }

    // 키보드 제어 - hide
    fun hideKeyboard(view: View) {
        if (view != null) {
            imm?.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    // 키보드 제어 - show
    fun showKeyboard(view: View) {
        imm?.showSoftInput(binding.searchField, 0)
    }

    // 액티비티 파괴
    override fun onDestroy() {
        mBinding = null
        super.onDestroy()
    }

}