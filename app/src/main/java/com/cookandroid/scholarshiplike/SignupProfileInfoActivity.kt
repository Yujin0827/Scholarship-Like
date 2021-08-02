package com.cookandroid.scholarshiplike

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_signup_profile_info.*

class SignupProfileInfoActivity :AppCompatActivity() {
    val auth = Firebase.auth
    val db = Firebase.firestore

    lateinit var txtNickname: String
    lateinit var txtUniv: String
    lateinit var univList: ArrayList<String>

    @Suppress("PrivatePropertyName")
    private val TAG = javaClass.simpleName

    // onBackPressed 메소드 변수
    var backPressedTime : Long = 0
    val FINISH_INTERVAL_TIME = 2000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup_profile_info)

        btnClick()
    }

    // 버튼 클릭 통합 처리
    fun btnClick() {
        // '간편로그인' 버튼 클릭 시
        btn_easy_login.setOnClickListener {
            txtNickname = txt_nickname1.text.toString()
            txtUniv = txt_univ1.text.toString()

            var isExistBlank: Boolean = txtNickname!!.isEmpty() || txtUniv!!.isEmpty()

            if (isExistBlank) {
                Toast.makeText(this, "빈 항목이 있습니다", Toast.LENGTH_SHORT).show()
            }

            // 조건 만족시, 다음으로 넘어감
            if(!isExistBlank) {
                updateUserDB()
            }
        }

    }

    // 유저 DB 업데이트
    private fun updateUserDB() {
        val user = auth.currentUser
        var userProfileSet: HashMap<String, Any> = hashMapOf(
            "nickname" to txtNickname,
            "univ" to txtUniv
        )

        val userLikeContentSet = hashMapOf(
            "scholarship" to arrayListOf<String>(),
            "magazine" to arrayListOf<String>()
        )

        userProfileSet["likeContent"] = userLikeContentSet

        if(user != null) {
            db.collection("Users").document(user.uid)
                .set(userProfileSet)
                .addOnSuccessListener {
                    Log.d(TAG, "Success update user profile db!")
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
                .addOnFailureListener {
                    Log.d(TAG, "Fail update user profile db!")
                }
        }
    }

    // back 버튼 클릭 리스너 재정의
    override fun onBackPressed() {
        if(supportFragmentManager.backStackEntryCount == 0) {
            val tempTime = System.currentTimeMillis()
            val intervalTime = tempTime - backPressedTime

            if (!(0 > intervalTime || FINISH_INTERVAL_TIME < intervalTime)) {
                finishAffinity()
                System.runFinalization()
                System.exit(0)
            } else {
                backPressedTime = tempTime
                Toast.makeText(this, "'뒤로' 버튼을 한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
                return
            }
        }
        super.onBackPressed()
    }
}