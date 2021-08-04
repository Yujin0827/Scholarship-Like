package com.cookandroid.scholarshiplike

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cookandroid.scholarshiplike.databinding.ActivitySignupProfileInfoBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SignupProfileInfoActivity :AppCompatActivity() {
    private lateinit var binding: ActivitySignupProfileInfoBinding
    val auth = Firebase.auth
    val db = Firebase.firestore

    lateinit var txtNickname: String
    lateinit var txtUniv: String
    private var univList: ArrayList<String> = arrayListOf()

    @Suppress("PrivatePropertyName")
    private val TAG = javaClass.simpleName

    // onBackPressed 메소드 변수
    var backPressedTime : Long = 0
    val FINISH_INTERVAL_TIME = 2000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =  ActivitySignupProfileInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUnivInput()  // 대학교 입력 자동완성

        btnClick()
    }

    // 대학교 입력시 자동완성
    private fun setUnivInput() {
        // 대학교 리스트 가져오기
        val sRef = db.collection("Scholarship").document("UnivScholar")

        sRef.get().addOnSuccessListener { doc ->
            val json = doc.data
            Log.d(TAG, "[Test] Object.keys(doc) : " + json?.keys)
            json?.keys?.let { univList.addAll(it) }
        }

        binding.txtSignProUniv.setAdapter<ArrayAdapter<String>>(
            ArrayAdapter<String>(
                this,
                R.layout.dropdown_size, R.id.dropdown_size, univList
            )
        )
    }

    // 버튼 클릭 통합 처리
    fun btnClick() {
        // '시작하기' 버튼 클릭 시
        binding.btnSignProStart.setOnClickListener {
            txtNickname = binding.txtSignProNickname.text.toString()
            txtUniv = binding.txtSignProUniv.text.toString()

            if(checkInputData()) {  // 사용자의 입력 데이터 확인
                updateUserDB()  // 유저 DB 업데이트
            }
        }
    }

    // 사용자의 입력 데이터 확인
    private fun checkInputData(): Boolean {
        var result = false

        // 빈칸 확인
        if (txtNickname!!.isEmpty() || txtUniv!!.isEmpty()) {
            Toast.makeText(this, "빈 항목이 존재합니다", Toast.LENGTH_SHORT).show()
        }
        else if (!univList.contains(txtUniv)) {
            Toast.makeText(this, "대학교를 선택해주세요", Toast.LENGTH_SHORT).show()
        }
        else {
            result = true
        }

        return result
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