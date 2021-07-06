package com.cookandroid.scholarshiplike

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.KeyEvent.KEYCODE_ENTER
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_home_search.*


class HomeSearchActivity : AppCompatActivity() {

    lateinit var searchField: EditText            // 검색창
    lateinit var searchBtn : ImageView        // 찾기 버튼
    lateinit var search_result_field_s : LinearLayout   // 검색 결과필드 - 장학금
    lateinit var search_result_field_m : LinearLayout   // 검색 결과필드 - 매거진
    lateinit var search_result_s : RecyclerView         // 검색 결과 - 장학금
    lateinit var search_result_m : RecyclerView         // 검색 결과 - 매거진

    internal var textlength = 0     // EditText 글자 수

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_search)
        Log.d("tag", "onCreate: started")


        searchField = findViewById<EditText>(R.id.searchField)          // 검색창
        searchBtn = findViewById<ImageView>(R.id.searchBtn)     // 찾기 버튼
        search_result_field_s = findViewById<LinearLayout>(R.id.search_result_field_s)      // 검색 결과필드 - 장학금
        search_result_field_m = findViewById<LinearLayout>(R.id.search_result_field_m)      // 검색 결과필드 - 매거진
        search_result_s = findViewById<RecyclerView>(R.id.search_result_s)      // 검색 결과 - 장학금
        search_result_m = findViewById<RecyclerView>(R.id.search_result_m)      // 검색 결과 - 매거진


        search_result_field_s.visibility = View.INVISIBLE
        search_result_field_m.visibility = View.INVISIBLE

        // 검색 버튼 클릭 (돋보기)
        searchBtn.setOnClickListener {
            click()
        }

        // 검색 버튼 클릭 (키보드)
        searchField.setOnKeyListener { v, keyCode, event ->
            if(event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KEYCODE_ENTER) {
                click()
            }
            true
        }


    }

    // 검색 버튼 클릭했을 때 동작
    private fun click() {
        search_result_field_s.visibility = View.VISIBLE
        search_result_field_m.visibility = View.VISIBLE

        hideKeyboard()
    }

    // 키보드 제어
    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(searchField.windowToken, 0)
    }

    private fun firebaseSearch() {

    }

}