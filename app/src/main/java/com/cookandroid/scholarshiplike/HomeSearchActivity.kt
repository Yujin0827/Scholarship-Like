package com.cookandroid.scholarshiplike

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.KeyEvent
import android.view.KeyEvent.KEYCODE_ENTER
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cookandroid.scholarshiplike.databinding.ActivityHomeSearchBinding


class HomeSearchActivity : AppCompatActivity() {

    private var mBinding: ActivityHomeSearchBinding? = null         // 바인딩 객체
    private val binding get() = mBinding!!                          // 바인딩 변수 재선언(매번 null 체크x)

    private lateinit var search_word: String        // 검색어 변수
    private var imm: InputMethodManager?= null      // 키보드 변수 선언

   @SuppressLint("SourceLockedOrientationActivity")
   override fun onCreate(savedInstanceState: Bundle?) {
       super.onCreate(savedInstanceState)
       mBinding = ActivityHomeSearchBinding.inflate(layoutInflater)  // 액티비티에서 사용할 바인딩 클래스의 인스턴스 생성
       setContentView(binding.root)

       // 화면 전환 방지 (세로로 고정)
       requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

       // 키보드 세팅
       imm = getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as InputMethodManager?

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

        search_word = binding.searchField.text.toString()

        if(search_word.isEmpty()) {
            var null_message = Toast.makeText(this, "검색어를 입력하세요.", Toast.LENGTH_SHORT)
            null_message.show()
        }
        else {
            intent.putExtra("search_word", binding.searchField.text.toString())
            startActivity(intent)       // HomeSearchResultActivity 이동
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