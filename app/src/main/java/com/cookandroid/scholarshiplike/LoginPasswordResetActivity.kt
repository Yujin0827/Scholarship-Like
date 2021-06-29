package com.cookandroid.scholarshiplike

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login_password_reset.*

class LoginPasswordResetActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_password_reset)

        btnClick()
    }

    // 버킅 클릭 통합 처리
    fun btnClick() {
        // X 아이콘 클릭 시
        btn_close_password.setOnClickListener {
            var iT = Intent(this, LoginActivity::class.java)
            iT.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) // 로그인 화면이 스택에 중복으로 쌓이는 것 방지
            startActivity(iT)
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
                            Toast.makeText(this, task.exception.toString(), Toast.LENGTH_LONG).show()
                        }
                    }
            }
        }
    }
}