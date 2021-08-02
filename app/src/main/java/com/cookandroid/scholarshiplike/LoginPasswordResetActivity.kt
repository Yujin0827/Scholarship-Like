package com.cookandroid.scholarshiplike

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import kotlinx.android.synthetic.main.activity_login_password_reset.*

class LoginPasswordResetActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_password_reset)

        btnClick()  // 버튼 클릭을 통합 처리
    }

    // 버튼 클릭 통합 처리
    fun btnClick() {
        // X 아이콘 클릭 시
        btn_close_password.setOnClickListener {
            finish()
        }

        // 비밀번호 재설정 버튼 클릭 시
        btn_reset_password.setOnClickListener {
            var userEmail :String = txt_email_password.text.toString()
            if(!userEmail.isEmpty()) {
                // 이메일 보내기
                FirebaseAuth.getInstance().sendPasswordResetEmail(userEmail)
                    .addOnCompleteListener(this){ task ->
                        if(task.isSuccessful) {
                            result_send_mail.visibility = View.VISIBLE  // 전송 결과 출력
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
    }
}