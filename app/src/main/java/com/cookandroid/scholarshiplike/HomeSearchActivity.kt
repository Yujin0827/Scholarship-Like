package com.cookandroid.scholarshiplike

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.KeyEvent.KEYCODE_ENTER
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class HomeSearchActivity : AppCompatActivity() {
    @Suppress("PrivatePropertyName")
    private val TAG = javaClass.simpleName

    lateinit var searchField: EditText            // 검색창
    lateinit var searchBtn : ImageView        // 찾기 버튼
    lateinit var search_result_field_s : LinearLayout   // 검색 결과필드 - 장학금
    lateinit var search_result_field_m : LinearLayout   // 검색 결과필드 - 매거진
    lateinit var search_result_s : RecyclerView         // 검색 결과 - 장학금
    lateinit var search_result_m : RecyclerView         // 검색 결과 - 매거진

    internal var textlength = 0     // EditText 글자 수
    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_search)
        Log.d(TAG, "onCreate: started")

        val sRef = db.collection("장학금")
            .document("교내").collection("강원")
            .document("강원대").collection("학과")

        sRef // 작업할 문서
            .get()      // 문서 가져오기
            .addOnSuccessListener { result ->
                for (document in result) {  // 가져온 문서들은 result에 들어감
                    val item = Alarm("1", document.id, "asdfadf")
                }
                Log.w(TAG, "Error aaaaaaa: ")

            }
            .addOnFailureListener { exception ->
                // 실패할 경우
                Log.w(TAG, "Error getting documents: $exception")
            }

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
        searchField.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent): Boolean {
                if (event.action == KeyEvent.ACTION_DOWN && keyCode == KEYCODE_ENTER) {
                    click()

                    return true
                }
                return false
            }
        })


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


}