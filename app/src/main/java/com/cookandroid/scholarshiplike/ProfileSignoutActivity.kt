package com.cookandroid.scholarshiplike

import android.content.Context
import android.content.Intent
import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cookandroid.scholarshiplike.databinding.ActivityProfileSignoutBinding
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ProfileSignoutActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileSignoutBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private val TAG = "ProfileSignoutActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileSignoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Firebase
        auth = Firebase.auth
        db = Firebase.firestore

        btnClick()
    }

    // 버튼 클릭 통합 처리
    private fun btnClick() {
        // '탈퇴하기' 클릭 리스너
        binding.btnSignout.setOnClickListener {
            if (confirmCheckbox()) {    // 이유를 체크했을 경우
                userReauth() // 사용자 재인증
            }
        }

        // 뒤로가기 클릭 리스너
        binding.btnSignoutBack.setOnClickListener {
            finish()
        }

        // 기타 클릭 리스너
        binding.cbRsnEtc.setOnClickListener {
            if (binding.cbRsnEtc.isChecked) {
                binding.edittxtSignoutEtcAdd.visibility = View.VISIBLE
            }
            else {
                binding.edittxtSignoutEtcAdd.visibility = View.GONE
            }
        }
    }

    // 탈퇴 이유 체크 여부 확인
    private fun confirmCheckbox(): Boolean {
        var isChecked = false

        isChecked = binding.cbRsnInfo.isChecked
                || binding.cbRsnUncom.isChecked
                || binding.cbRsnBetter.isChecked
                || binding.cbRsnUseless.isChecked
                || binding.cbRsnEtc.isChecked
        if (binding.cbRsnEtc.isChecked && binding.edittxtSignoutEtcAdd.text.toString() == "") {
            isChecked = false
            Toast.makeText(this, "탈퇴 이유를 작성 해 주세요", Toast.LENGTH_SHORT).show()

            binding.edittxtSignoutEtcAdd.requestFocus() // 기타 이유 입력 포커스 주기
            // 키보드 보이기
            val imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.toggleSoftInput(
                InputMethodManager.SHOW_FORCED,
                InputMethodManager.HIDE_IMPLICIT_ONLY
            )
        }
        else if (!isChecked) {
            Toast.makeText(this, "탈퇴 이유를 선택해주세요", Toast.LENGTH_SHORT).show()
        }

        return isChecked
    }

    // 사용자 재인증
    private fun userReauth() {
        val user = auth.currentUser

        if (user != null) {
            val userEmail: String? = user?.email
            val userPW = binding.edittxtSignoutPw.text.toString()

            if (userEmail != null && userPW != null) {
                val credential = EmailAuthProvider
                    .getCredential(userEmail!!, userPW)

                user?.reauthenticate(credential)    // 사용자 재인증
                    ?.addOnSuccessListener {
                        Log.d(TAG, "User re-authenticated.")
                        deleteUserDB(user)  // 유저 DB 삭제
                    }
                    ?.addOnFailureListener { it ->
                        Log.w(TAG, "Fail to user re-authenticate", it)
                        Toast.makeText(this, "비밀번호가 일치하지 않습니다", Toast.LENGTH_SHORT).show()
                        binding.edittxtSignoutPw.requestFocus() // 비밀번호 입력 포커스 주기
                        // 키보드 보이기
                        val imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.toggleSoftInput(
                            InputMethodManager.SHOW_FORCED,
                            InputMethodManager.HIDE_IMPLICIT_ONLY
                        )
                    }
            }
            else if (userPW == null) {
                Toast.makeText(this, "비밀번호를 입력하세요", Toast.LENGTH_SHORT).show()
                binding.edittxtSignoutPw.requestFocus() // 비밀번호 입력 포커스 주기
                // 키보드 보이기
                val imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.toggleSoftInput(
                    InputMethodManager.SHOW_FORCED,
                    InputMethodManager.HIDE_IMPLICIT_ONLY
                )
            }
        }
    }

    // 현재 사용자 DB 삭제
    private fun deleteUserDB(user: FirebaseUser) {
        db.collection("Users").document(user!!.uid)
            .delete()
            .addOnSuccessListener {
                Log.d(TAG, "Delete current user DB!")
                deletUserAuth() // 유저 계정 삭제
            }
            .addOnFailureListener { it ->
                Log.d(TAG, it.toString())
            }
    }

    // 유저 계정 삭제
    private fun deletUserAuth() {
        val user = auth.currentUser

        user?.delete()
            ?.addOnSuccessListener {
                Log.d(TAG, "Success signout!")
                Toast.makeText(this, "탈퇴가 완료되었습니다", Toast.LENGTH_LONG).show()

                // 앱 종료 & 재시작
                finishAffinity()
                val iT = Intent(this, LoginActivity::class.java)
                startActivity(iT)
            }
            ?.addOnFailureListener { it ->
                Log.d(TAG, it.toString())
                Toast.makeText(this, "탈퇴 실패", Toast.LENGTH_LONG).show()
            }
    }
}