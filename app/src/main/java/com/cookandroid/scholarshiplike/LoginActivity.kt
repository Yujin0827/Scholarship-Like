package com.cookandroid.scholarshiplike

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cookandroid.scholarshiplike.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class LoginActivity :AppCompatActivity(){
    @Suppress("PrivatePropertyName")
    private val TAG = javaClass.simpleName

    private var mBinding: ActivityLoginBinding? = null
    private val binding get() = mBinding!!

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var db :FirebaseFirestore
    private val RC_SIGN_IN = 99

    var imm: InputMethodManager? = null // 키보드

    // onBackPressed 메소드 변수
    var backPressedTime : Long = 0
    val FINISH_INTERVAL_TIME = 2000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager  // 키보드

        firebaseAuth = FirebaseAuth.getInstance()   // firebase auth 객체
        db = Firebase.firestore

        // 버튼 클릭을 통합 처리
        btnClick()

        // 약관 동의 팝업 띄우기
        showTerms()
    }

    // 약관 동의 팝업 띄우기
    fun showTerms() {
        val dialog = LoginTermsFragment()
        dialog.show(supportFragmentManager, "LoginTermsFragment")
    }

    // 버튼 클릭 통합 처리
    private fun btnClick() {
        // 로그인 버튼 클릭 시
        binding.btnLogin.setOnClickListener() {
            val txtEmail : String = binding.loginTxtEmail.text.toString()
            val txtPassword : String = binding.loginTxtPassword.text.toString()

            if (txtEmail.isNotEmpty() && txtPassword.isNotEmpty()) {
                FirebaseAuth.getInstance().signInWithEmailAndPassword(txtEmail, txtPassword)
                    .addOnCompleteListener(this) { task ->
                        if(task.isSuccessful) {
                            Toast.makeText(this, "로그인 성공", Toast.LENGTH_LONG).show()
                            val iT = Intent(this, MainActivity::class.java)
                            startActivity(iT)
                            finish()
                        }
                    }
                    .addOnFailureListener {
                        // 예외 토스트 메시지
                        val errorCode = (it as FirebaseAuthException).errorCode
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
            else if (txtEmail.isEmpty()){
                Toast.makeText(this, "이메일을 입력하세요", Toast.LENGTH_SHORT).show()
            }
            else if (txtPassword.isEmpty()) {
                Toast.makeText(this, "비밀번호를 입력하세요", Toast.LENGTH_SHORT).show()
            }
        }

        // 비밀번호 찾기 클릭 시
        binding.gotoFindPw.setOnClickListener {
            val iT = Intent(this, LoginPasswordResetActivity::class.java)
            startActivity(iT)
        }

        // 회원가입 클릭 시
        binding.gotoSignup.setOnClickListener {
            val iT = Intent(this, SignupActivity::class.java)
            startActivity(iT)
        }

        //구글 로그인
        binding.loginGoogle.setOnClickListener {
            // Google 로그인 옵션 구성 (requestIdToken, Email 요청)
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

            googleSignInClient = GoogleSignIn.getClient(this, gso)

            signIn()    // 구글 로그인
        }

        //엔터 입력 시 키보드 내리기
        binding.loginTxtPassword.setOnKeyListener { _, keyCode, event ->
            //Enter key Action
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                imm?.hideSoftInputFromWindow(binding.loginTxtPassword.windowToken, 0) //hide keyboard
                true
            } else false
        }

        // 배경 클릭시 키보드 내리기
        binding.rootViewActivityLogin.setOnClickListener {
            imm?.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }

    // Google 로그인
    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }
    // MainActivity로 이동
    private fun toMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    // SignupProfileInfoActivity로 이동
    private fun toSignupProfileInfoActivity() {
        startActivity(Intent(this, SignupProfileInfoActivity::class.java))
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        Log.d("LoginActivity", "firebaseAuthWithGoogle:" + acct.id!!)

        //Google SignInAccount 객체에서 ID 토큰을 가져와서 Firebase Auth로 교환하고 Firebase에 인증
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.w(TAG, "firebaseAuthWithGoogle 성공", task.exception)
                    confirmUserDB()
                } else {
                    Log.w(TAG, "firebaseAuthWithGoogle 실패", task.exception)
                    Toast.makeText(this, "로그인에 실패하였습니다.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    // 유저 DB 존재 유무 확인
    private fun confirmUserDB() {
        var user = Firebase.auth.currentUser
        val dd = db.collection("Users").document(user!!.uid)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val document = task.result
                    if (!document.exists()) {   // 유저 DB가 존재하지 않을 때
                        Log.d(TAG, "User's db doesn't exist!")
                        toSignupProfileInfoActivity()
                    }
                    else {  // 유저 DB가 존재할 때
                        toMainActivity()
                    }
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

    override fun onDestroy() {
        mBinding = null
        super.onDestroy()
    }
}