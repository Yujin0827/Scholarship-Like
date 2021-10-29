package com.cookandroid.scholarshiplike

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cookandroid.scholarshiplike.databinding.ActivityProfileQuestionsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ProfileQuestionsActivity: AppCompatActivity() {
    private var mBinding: ActivityProfileQuestionsBinding? = null
    private val binding get() = mBinding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var content: String    // 입력받는 문의 내용

    var imm: InputMethodManager? = null // 키보드

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityProfileQuestionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager  // 키보드

        btn_Click()
    }

    private fun btn_Click() {
        // 메일 보내기 클릭 리스너
        binding.btnSendMail.setOnClickListener {
            if (isFilled()) {   // 문의 내용 유무 확인
                val scholarEmail = arrayOf("scholarshiplike@naver.com")
                val email = Intent(Intent.ACTION_SEND)
                email.setType("plain/text")
                email.putExtra(Intent.EXTRA_EMAIL, scholarEmail)
                email.putExtra(Intent.EXTRA_SUBJECT, "[장학라이크] 사용자 문의")
                email.putExtra(Intent.EXTRA_TEXT, content)
                startActivity(email)
            }
        }

        // 배경 클릭시 키보드 내리기
        binding.rootViewActivityProfileQuestions.setOnClickListener {
            imm?.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }

    // 문의 내용이 작성되었는지 확인
    private fun isFilled(): Boolean {
        var fill = false
        content = binding.edittxtQuestionsContent.text.toString()

        if (content != "") {
            fill = true
        }
        else {
            Toast.makeText(this, "문의 내용을 작성 해 주세요", Toast.LENGTH_SHORT).show()

            binding.edittxtQuestionsContent.requestFocus() // 문의 내용 입력 포커스 주기
            // 키보드 보이기
            val imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.toggleSoftInput(
                InputMethodManager.SHOW_FORCED,
                InputMethodManager.HIDE_IMPLICIT_ONLY)
        }

        return fill
    }

    override fun onDestroy() {
        mBinding = null
        super.onDestroy()
    }
}