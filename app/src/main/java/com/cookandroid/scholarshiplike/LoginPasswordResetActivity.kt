package com.cookandroid.scholarshiplike

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cookandroid.scholarshiplike.databinding.ActivityLoginPasswordResetBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException

class LoginPasswordResetActivity : AppCompatActivity(){
    private var mBinding: ActivityLoginPasswordResetBinding? = null
    private val binding get() = mBinding!!
    var imm: InputMethodManager? = null // 키보드

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityLoginPasswordResetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager  // 키보드

        btnClick()  // 버튼 클릭을 통합 처리
    }

    // 버튼 클릭 통합 처리
    fun btnClick() {
        // X 아이콘 클릭 시
        binding.btnClosePassword.setOnClickListener {
            finish()
        }

        // 비밀번호 재설정 버튼 클릭 시
        binding.btnResetPassword.setOnClickListener {
            var userEmail :String = binding.txtEmailPassword.text.toString()
            if(!userEmail.isEmpty()) {
                // 이메일 보내기
                FirebaseAuth.getInstance().sendPasswordResetEmail(userEmail)
                    .addOnCompleteListener(this){ task ->
                        if(task.isSuccessful) {
                            binding.resultSendMail.visibility = View.VISIBLE  // 전송 결과 출력
                        }
                        else {
                            // 예외 토스트 메시지
                            val errorCode =  (task.exception as FirebaseAuthException).errorCode
                            when(errorCode) {
                                "ERROR_INVALID_EMAIL" -> {
                                    Toast.makeText(this, "올바른 이메일 주소의 형식을 입력하세요", Toast.LENGTH_SHORT).show()
                                }
                                "ERROR_USER_NOT_FOUND" -> {
                                    Toast.makeText(this, "가입되어 있지 않은 이메일입니다", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
            }
        }

        // 배경 클릭시 키보드 내리기
        binding.rootViewActivityLoginPasswordReset.setOnClickListener {
            imm?.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }

    override fun onDestroy() {
        mBinding = null
        super.onDestroy()
    }
}