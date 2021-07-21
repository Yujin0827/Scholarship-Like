package com.cookandroid.scholarshiplike

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cookandroid.scholarshiplike.databinding.ActivitySignupBinding
import com.cookandroid.scholarshiplike.databinding.FragmentLoginTermsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_signup.*
import kotlinx.android.synthetic.main.fragment_profile_logout.*

class SignupActivity :AppCompatActivity() {
    @Suppress("PrivatePropertyName")
    private val TAG = javaClass.simpleName

    private var _binding: ActivitySignupBinding? = null
    private val binding get() = _binding!!
    val auth = Firebase.auth
    val db = Firebase.firestore

    lateinit var txtEmail:String
    lateinit var txtPassword:String
    lateinit var txtPasswordConfirm:String
    lateinit var txtNickname:String
    lateinit var txtUniv:String



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //버튼 클릭을 통합 처리
        btnClick()
    }

    // 버튼 클릭 통합 처리
    fun btnClick(){
        var isExistBlank = false
        var isPWSame = false

        //회원가입 버튼을 클릭하였을 때
        btn_signup.setOnClickListener() {
            txtEmail = binding.txtEmail.text.toString()
            txtPassword = binding.txtPassword.text.toString()
            txtPasswordConfirm = binding.txtPasswordConfirm.text.toString()
            txtNickname = binding.txtNickname.text.toString()
            txtUniv = binding.txtUniv.text.toString()

            isExistBlank = txtEmail.isEmpty() || txtPassword.isEmpty() || txtPasswordConfirm.isEmpty() || txtNickname.isEmpty() || txtUniv.isEmpty()
            isPWSame = txtPassword == txtPasswordConfirm

            if (isExistBlank) {
                Toast.makeText(this, "빈 항목이 있습니다", Toast.LENGTH_SHORT).show()
            }
            else if (!isPWSame) {
                Toast.makeText(this, "비밀번호가 일치하지 않습니다", Toast.LENGTH_SHORT).show()
            }

            // 조건 만족시, 아이디 생성 & 유저 DB 업데이트
            if(!isExistBlank && isPWSame) {
                createEmailId(txtEmail, txtPassword)    // 유저 계정 만들기
            }
        }

        // 돌아가기 버튼 클릭 시
        btn_goto_back.setOnClickListener() {
            finish()    // 현재 액티비티 제거
        }
    }

    // 아이디 생성
    private fun createEmailId(email : String, pw : String) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, pw)
            .addOnCompleteListener(this) { task ->
                if(task.isSuccessful) {
                    Toast.makeText(this, "회원가입 성공", Toast.LENGTH_LONG).show()
                    updateUserDB()  // 유저 DB 저장
                }
                else {
                    Toast.makeText(this, task.exception.toString(), Toast.LENGTH_LONG).show()
                }
            }
    }

    // 유저 DB 저장
    fun updateUserDB() {
        val uId = auth.currentUser?.uid
        var userProfileSet :HashMap<String, Any> = hashMapOf(
            "nickname" to txtNickname,
            "univ" to txtUniv
        )

        val userLikeContentSet = hashMapOf(
            "scholarship" to arrayListOf<String>(),
            "magazine" to arrayListOf<String>()
        )

        userProfileSet["likeContent"] = userLikeContentSet

        db.collection("Users").document(uId!!)
            .set(userProfileSet)
            .addOnSuccessListener { // 성공 시 MainActivity로 이동
                Log.d(TAG, "User profile DB successfully written!")
                val iT = Intent(this, MainActivity::class.java)
                startActivity(iT)
                finish()
            }
            .addOnFailureListener {e ->
                Log.w(TAG, "Error writting user DB!", e)
            }
    }
}