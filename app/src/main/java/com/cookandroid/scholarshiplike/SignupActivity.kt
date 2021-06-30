package com.cookandroid.scholarshiplike

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_signup.*
import kotlinx.android.synthetic.main.fragment_profile_logout.*

class SignupActivity :AppCompatActivity() {
    var authStateListener: FirebaseAuth.AuthStateListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        //로그인 세션을 체크하는 부분
        authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            Log.e("LLPP", "로그인")
            var user = firebaseAuth.currentUser
            if (user != null) {
                var iT = Intent(this, MainActivity::class.java)
                startActivity(iT)
            }
        }

        //버튼 클릭을 통합 처리
        btnClick()
    }

    // 버튼 클릭 통합 처리
    fun btnClick(){
        var isPWSame = false
        var isExistBlank = false
        var isCheckedAccept = false

        //회원가입 버튼을 클릭하였을 때
        btn_signup.setOnClickListener() {
            val txtEmail = txt_email.text.toString()
            val txtPassword = txt_password.text.toString()
            val txtPasswordConfirm = txt_password_confirm.text.toString()
            val txtName = txt_name.text.toString()

            isExistBlank = txtEmail.isEmpty() || txtPassword.isEmpty() || txtPasswordConfirm.isEmpty() || txtName.isEmpty()
            isPWSame = txtPassword == txtPasswordConfirm
            isCheckedAccept = cb_accept_service.isChecked && cb_accept_privacy.isChecked

            if (isExistBlank) {
                Toast.makeText(this, "빈 항목이 있습니다", Toast.LENGTH_SHORT).show()
            }
            else if (!isPWSame) {
                Toast.makeText(this, "비밀번호가 일치하지 않습니다", Toast.LENGTH_SHORT).show()
            }
            else if (!isCheckedAccept){
                Toast.makeText(this, "모든 약관에 동의가 필요합니다", Toast.LENGTH_SHORT).show()
            }

            // 조건 만족시, 아이디 생성
            if(!isExistBlank && isPWSame && isCheckedAccept) {
                createEmailId(txtEmail, txtPassword, txtName)
            }
        }

        // 돌아가기 버튼 클릭 시
        btn_goto_back.setOnClickListener() {
            finish()    // 현재 액티비티 제거
        }
    }

    // 아이디 생성
    private fun createEmailId(email : String, pw : String, name : String) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, pw)
            .addOnCompleteListener(this) { task ->
                if(task.isSuccessful) {
                    Toast.makeText(this, "회원가입 성공", Toast.LENGTH_LONG).show()
                    var iT = Intent(this, MainActivity::class.java)
                    startActivity(iT)
                    finish()
                }
                else {
                    Toast.makeText(this, task.exception.toString(), Toast.LENGTH_LONG).show()
                }
        }
    }

    override fun onResume() {
        super.onResume()
        FirebaseAuth.getInstance().addAuthStateListener(authStateListener!!)
    }

    override fun onStop() {
        super.onStop()
        FirebaseAuth.getInstance().removeAuthStateListener(authStateListener!!)
    }
}