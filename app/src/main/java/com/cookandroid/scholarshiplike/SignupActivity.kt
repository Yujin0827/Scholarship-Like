package com.cookandroid.scholarshiplike

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cookandroid.scholarshiplike.databinding.ActivitySignupBinding
import com.cookandroid.scholarshiplike.databinding.FragmentLoginTermsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_signup.*
import kotlinx.android.synthetic.main.fragment_profile_logout.*

class SignupActivity :AppCompatActivity() {
    @Suppress("PrivatePropertyName")
    private val TAG = javaClass.simpleName

    private lateinit var binding: ActivitySignupBinding
    val auth = Firebase.auth
    val db = Firebase.firestore

    lateinit var txtEmail:String
    lateinit var txtPassword:String
    lateinit var txtPasswordConfirm:String
    lateinit var txtNickname:String
    lateinit var txtUniv:String
    var univList: ArrayList<String> = arrayListOf()    // 대학교 자동완성을 위한 리스트


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUnivList()
        //버튼 클릭을 통합 처리
        btnClick()
    }

    // DB에서 대학교 이름 가져오기
    private fun setUnivList() {
        // 대학교 리스트 가져오기
        val sRef = db.collection("Scholarship").document("UnivScholar")
        sRef.get()
            .addOnSuccessListener { document ->
            if (document != null) {
                Log.d(TAG, "DocumentSnapshot data: ${document.data}")
                val json = document.data
                json?.keys?.let { univList.addAll(it) }
            } else {
                Log.d(TAG, "No such document")
            }
        }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }

        binding.txtUniv.setAdapter<ArrayAdapter<String>>(
            ArrayAdapter<String>(
                this,
                R.layout.dropdown_size, R.id.dropdown_size, univList
            )
        )
    }


    // 버튼 클릭 통합 처리
    fun btnClick(){
        //회원가입 버튼을 클릭하였을 때
        binding.btnSignup.setOnClickListener() {
            txtEmail = binding.txtEmail.text.toString()
            txtPassword = binding.txtPassword.text.toString()
            txtPasswordConfirm = binding.txtPasswordConfirm.text.toString()
            txtNickname = binding.txtNickname.text.toString()
            txtUniv = binding.txtUniv.text.toString()

            // 입력데이터 확인 후, 아이디 생성 & 유저 DB 업데이트
            if (checkInputData()) {
                createEmailId(txtEmail, txtPassword)
            }
        }

        // 돌아가기 버튼 클릭 시
        binding.btnGotoBack.setOnClickListener() {
            finish()    // 현재 액티비티 제거
        }
    }

    private fun checkInputData(): Boolean {
        var result = false
        var isExistBlank = false
        var isPWSame = false

        isExistBlank = txtEmail.isEmpty() || txtPassword.isEmpty() || txtPasswordConfirm.isEmpty() || txtNickname.isEmpty() || txtUniv.isEmpty()
        isPWSame = txtPassword == txtPasswordConfirm

        if (isExistBlank) {
            Toast.makeText(this, "빈 항목이 있습니다", Toast.LENGTH_SHORT).show()
        }
        else if (!isPWSame) {
            Toast.makeText(this, "비밀번호가 일치하지 않습니다", Toast.LENGTH_SHORT).show()
        }
        else if (!univList.contains(txtUniv)) {
            Toast.makeText(this, "대학교를 선택해주세요", Toast.LENGTH_SHORT).show()
        } else {
            result = true
        }

        return result
    }

    // 아이디 생성
    private fun createEmailId(email : String, pw : String) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, pw)
            .addOnCompleteListener(this) { task ->
                if(task.isSuccessful) {
                    Toast.makeText(this, "회원가입 성공", Toast.LENGTH_LONG).show()
                    updateUserDB()  // 유저 DB 저장
                }
            }
            .addOnFailureListener {
                // 예외 토스트 메시지
                val errorCode = (it as FirebaseAuthException).errorCode
                when (errorCode) {
                    "ERROR_INVALID_EMAIL" -> {
                        Toast.makeText(this, "올바른 이메일 주소의 형식을 입력하세요", Toast.LENGTH_SHORT).show()
                    }
                    "ERROR_EMAIL_ALREADY_IN_USE" -> {
                        Toast.makeText(this, "이미 사용중인 이메일 입니다", Toast.LENGTH_SHORT).show()
                    }
                    "ERROR_WEAK_PASSWORD" -> {
                        Toast.makeText(this, "안정성이 낮은 비밀번호 입니다", Toast.LENGTH_SHORT).show()
                    }
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
            "scholarship" to arrayListOf<String?>(null),
            "magazine" to arrayListOf<String?>(null)
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