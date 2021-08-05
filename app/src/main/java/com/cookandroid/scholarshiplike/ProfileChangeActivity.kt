package com.cookandroid.scholarshiplike

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.cookandroid.scholarshiplike.databinding.ActivityProfileChangeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlin.collections.ArrayList


class ProfileChangeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileChangeBinding
    private var db = Firebase.firestore
    private var univList: ArrayList<String> = arrayListOf() // 대학교 이름 리스트
    lateinit var userEmail: String  // 사용자 이메일
    lateinit var userUid: String    // 사용자 uid
    private var userNickname: String? = null
    private var userUniv: String? = null


    private val TAG = javaClass.simpleName

    var user = Firebase.auth.currentUser // 사용자 가져오기

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileChangeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getUserInfo()   // User 정보 가져오기
        btnClick()  // 클릭 리스너 정의 메소드
    }

    // User Nickname 가져오기
    fun getUserInfo() {
        //User Email
        user?.let {
            userEmail = user!!.email.toString()
            userUid = user!!.uid
        }
        binding.emailInput.text = userEmail

        //User Nickname & Univ
        db.collection("Users")
            .document(userUid)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    userNickname = document.getString("nickname")
                    userUniv = document.getString("univ")
                    if (userNickname != null) {
                        binding.nickNameInput.setText(userNickname)
                    }
                    univSearch()    // 대학교 입력 자동완성 & 초기화
                }
            }
    }

    // 대학교 자동완성 & 초기화
    fun univSearch() {
        // 대학교 리스트 가져오기
        val sRef = db.collection("Scholarship").document("UnivScholar")

        sRef.get().addOnSuccessListener { doc ->
            val json = doc.data
            Log.d(TAG, "[Test] Object.keys(doc) : " + json?.keys)
            json?.keys?.let { univList.addAll(it) }
        }

        binding.univeInput.setAdapter<ArrayAdapter<String>>(
            ArrayAdapter<String>(
                this,
                R.layout.dropdown_size, R.id.dropdown_size, univList
            )
        )

        // 사용자 대학교로 초기화
        if (userUniv != null) {
            binding.univeInput.setText(userUniv, false)
        }

    }

    // 버튼 클릭 통합 메소드
    private fun btnClick() {
        // 이메일 보내기 버튼 클릭 시
        binding.reSetPwBt.setOnClickListener {
            if (!userEmail.isEmpty()) {
                // 이메일 보내기
                FirebaseAuth.getInstance().sendPasswordResetEmail(userEmail)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            binding.sendMailResultText.visibility = View.VISIBLE  // 전송 결과 출력
                        } else {
                            Toast.makeText(this, task.exception.toString(), Toast.LENGTH_LONG)
                                .show()
                        }
                    }
            }
        }

        // 저장 버튼 클릭 리스너
        binding.save.setOnClickListener {
            userNickname = binding.nickNameInput.text.toString()
            userUniv = binding.univeInput.text.toString()

            Log.d(TAG, "[test] userNickname" + userNickname)
            Log.d(TAG, "[test] userUniv" + userUniv)
            
            if (checkInputData()) { // 사용자의 입력 데이터 확인
                updateUserDB()  // 사용자 DB 업데이트
            }
        }
    }

    // 사용자의 입력 데이터 확인
    private fun checkInputData(): Boolean {
        var result = false
        
        // 빈칸 확인
        if ((userNickname == "") || (userUniv == "")) {
            Toast.makeText(this, "빈 항목이 존재합니다", Toast.LENGTH_SHORT).show()
        }
        else if (!univList.contains(userUniv)) {
            Toast.makeText(this, "대학교를 선택해주세요", Toast.LENGTH_SHORT).show()
        }
        else {
            result = true
        }
        
        return result
    }
    
    // 사용자 DB 업데이트
    private fun updateUserDB() {
        db.collection("Users")
            .document(userUid)
            .update(
                mapOf(
                    "nickname" to userNickname,
                    "univ" to userUniv
                )
            )
            .addOnSuccessListener {
                Log.d(TAG, "[Success] update user nickname!")
//                HomeFragment().setUserInfo()
//                (supportFragmentManager.findFragmentByTag("Home_Fragment") as HomeFragment).setUserInfo()
//                val cont = this
//                ((this as MainActivity).supportFragmentManager.findFragmentByTag("Home_Fragment") as HomeFragment).setUserInfo()
//                val manager = (MainActivity().context_main as MainActivity).supportFragmentManager
//                (manager.findFragmentByTag("Home_Fragment") as HomeFragment).setUserInfo()

                finish()
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "[Fail] update user nickname!", e)
            }
    }

}

