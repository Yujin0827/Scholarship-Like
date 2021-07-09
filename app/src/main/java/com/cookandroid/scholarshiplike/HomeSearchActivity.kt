package com.cookandroid.scholarshiplike

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.KeyEvent.KEYCODE_ENTER
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.cookandroid.scholarshiplike.databinding.ActivityHomeSearchBinding
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_home_search.*

class HomeSearchActivity : AppCompatActivity() {

    private var mBinding: ActivityHomeSearchBinding? = null    // 바인딩 객체
    private val binding get() = mBinding!!                      // 바인딩 변수 재선언(매번 null 체크x)
    private val db = FirebaseFirestore.getInstance()                // FireStore 인스턴스
    private val scholarshipList = arrayListOf<SearchScholarship>()  // 리스트 아이템 배열

    private lateinit var search_word: String    // 검색어 변수

   override fun onCreate(savedInstanceState: Bundle?) {
       super.onCreate(savedInstanceState)
       mBinding = ActivityHomeSearchBinding.inflate(layoutInflater)  // 액티비티에서 사용할 바인딩 클래스의 인스턴스 생성
       setContentView(binding.root)

       // 검색 버튼 클릭 (아이콘)
       binding.searchBtn.setOnClickListener {
           click()
       }

       // 검색 버튼 클릭 (키보드)
       binding.searchField.setOnKeyListener(object : View.OnKeyListener {
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
        val intent = Intent(this, HomeSearchResultActivity::class.java)
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager      // 키보드 제어 변수

        search_word = binding.searchField.text.toString()

        if(search_word.equals(null) || search_word.equals("")) {
            var null_message = Toast.makeText(this, "검색어를 입력하세요.", Toast.LENGTH_SHORT)
            null_message.show()

            imm.showSoftInput(binding.searchField, 0)       // 키보드 제어 - show
        }
        else {
            intent.putExtra("search_word", binding.searchField.text.toString())

            imm.hideSoftInputFromWindow(binding.searchField.windowToken, 0)     // 키보드 제어 - hide
            startActivity(intent)       // HomeSearchResultActivity 이동
        }
    }

    // 액티비티 파괴
    override fun onDestroy() {
        // onDestroy 에서 binding class 인스턴스 참조를 정리해주어야 한다.
        mBinding = null
        super.onDestroy()
    }

}