package com.cookandroid.scholarshiplike

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
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
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*

class ProfileSignoutActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileSignoutBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private var reasonList = mutableListOf<String>()

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
            } else {
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
            val imm: InputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.toggleSoftInput(
                InputMethodManager.SHOW_FORCED,
                InputMethodManager.HIDE_IMPLICIT_ONLY
            )
        } else if (!isChecked) {
            Toast.makeText(this, "탈퇴 이유를 선택해주세요", Toast.LENGTH_SHORT).show()
        } else if (isChecked) {   // 탈퇴 사유 저장
            if (binding.cbRsnInfo.isChecked) {
                reasonList.add("lackOfInfo")
            }
            if (binding.cbRsnUncom.isChecked) {
                reasonList.add("uncomfortable")
            }
            if (binding.cbRsnBetter.isChecked) {
                reasonList.add("betterExist")
            }
            if (binding.cbRsnUseless.isChecked) {
                reasonList.add("useless")
            }
            if (binding.cbRsnEtc.isChecked) {
                reasonList.add("etc")
            }
        }

        return isChecked
    }

    // 사용자 재인증
    private fun userReauth() {
        val user = auth.currentUser

        if (user != null) {
            val userEmail: String? = user?.email
            val userPW = binding.edittxtSignoutPw.text.toString()

            if (userEmail != null && userPW != "") {
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
                        val imm: InputMethodManager =
                            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.toggleSoftInput(
                            InputMethodManager.SHOW_FORCED,
                            InputMethodManager.HIDE_IMPLICIT_ONLY
                        )
                    }
            } else if (userPW == "") {
                Toast.makeText(this, "비밀번호를 입력하세요", Toast.LENGTH_SHORT).show()
                binding.edittxtSignoutPw.requestFocus() // 비밀번호 입력 포커스 주기
                // 키보드 보이기
                val imm: InputMethodManager =
                    getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
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
                Log.d(TAG, "[Success] Delete current user DB!")
                GlobalScope.launch {
                    if (sendFeedback()) {   // 탈퇴 사유 전송 성공시
                        Log.d(TAG, "-------- resceive true from sendFeedback()")
                        deletUserAuth(user) // 유저 계정 삭제
                    }
                }
//                if (rec) {   // 탈퇴 사유 전송 성공시
//                    Log.d(TAG, "-------- resceive true from sendFeedback()")
//                    deletUserAuth(user) // 유저 계정 삭제
//                }
            }
            .addOnFailureListener { it ->
                Log.d(TAG, it.toString())
            }
    }

    // 유저 계정 삭제
    fun deletUserAuth(user: FirebaseUser) {
        Log.d(TAG, "-------- come in deleteUserAuth()")
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

    // 탈퇴 사유 DB로 전송
    private suspend fun sendFeedback(): Boolean {
        var isSuccess = false
//        CoroutineScope(Dispatchers.Main).launch {
////            var isSuccess = false
//            val temp = CoroutineScope(Dispatchers.IO).async {
//                for (rsn in reasonList) {
//                    if (rsn == "etc") { // 기타 사유 저장
//                        val rsnEtc = binding.edittxtSignoutEtcAdd.text.toString()
//                        db.collection("Feedback").document("SignoutReason")
//                            .update(rsn, FieldValue.arrayUnion(rsnEtc))
//                            .addOnSuccessListener {
//                                isSuccess = true
//                                Log.d(TAG, "etc success isSuccess : " + isSuccess.toString())
//                            }
//                            .addOnFailureListener { it ->
//                                Log.e(TAG, "[Fail] update signout etc reason of 'Feedback' DB!", it)
//                                isSuccess = false
//                            }
//                    }
//                    else {  // 그 외 사유 저장
//                        db.collection("Feedback").document("SignoutReason")
//                            .update(rsn, FieldValue.increment(1))
//                            .addOnSuccessListener {
//                                isSuccess = true
//                                Log.d(TAG, "update db success isSuccess : " + isSuccess.toString())
//                            }
//                            .addOnFailureListener { it ->
//                                Log.e(TAG, "[Fail] update signout reason of 'Feedback' DB!", it)
//                                isSuccess = false
//                            }
//                    }
//                }
//            }.await()
//
//            if (isSuccess) {
//                Log.d(TAG, "[Success] update signout reason of 'Feedback' DB!")
//            }
//
//            Log.d(TAG, "---------isSucess : " + isSuccess.toString())
//
//            return@sendFeedback isSuccess
//        }

//        for (rsn in reasonList) {
//            if (rsn == "etc") { // 기타 사유 저장
//                val rsnEtc = binding.edittxtSignoutEtcAdd.text.toString()
//                db.collection("Feedback").document("SignoutReason")
//                    .update(rsn, FieldValue.arrayUnion(rsnEtc))
//                    .addOnSuccessListener {
//                        isSuccess = true
//                        Log.d(TAG, "etc success isSuccess : " + isSuccess.toString())
//                    }
//                    .addOnFailureListener { it ->
//                        Log.e(TAG, "[Fail] update signout etc reason of 'Feedback' DB!", it)
//                        isSuccess = false
//                    }
//            } else {  // 그 외 사유 저장
//                db.collection("Feedback").document("SignoutReason")
//                    .update(rsn, FieldValue.increment(1))
//                    .addOnSuccessListener {
//                        isSuccess = true
//                        Log.d(TAG, "update db success isSuccess : " + isSuccess.toString())
//                    }
//                    .addOnFailureListener { it ->
//                        Log.e(TAG, "[Fail] update signout reason of 'Feedback' DB!", it)
//                        isSuccess = false
//                    }
//            }
//        }

//        for (rsn in reasonList) {
//            if (rsn == "etc") { // 기타 사유 저장
//                val rsnEtc = binding.edittxtSignoutEtcAdd.text.toString()
//                db.collection("Feedback").document("SignoutReason")
//                    .update(rsn, FieldValue.arrayUnion(rsnEtc))
//                    .addOnSuccessListener {
//                        isSuccess = true
//                        Log.d(TAG, "etc success isSuccess : " + isSuccess.toString())
//                    }
//                    .addOnFailureListener { it ->
//                        Log.e(TAG, "[Fail] update signout etc reason of 'Feedback' DB!", it)
//                        isSuccess = false
//                    }
//            } else {  // 그 외 사유 저장
//                db.collection("Feedback").document("SignoutReason")
//                    .update(rsn, FieldValue.increment(1))
//                    .addOnSuccessListener {
//                        isSuccess = true
//                        Log.d(TAG, "update db success isSuccess : " + isSuccess.toString())
//                    }
//                    .addOnFailureListener { it ->
//                        Log.e(TAG, "[Fail] update signout reason of 'Feedback' DB!", it)
//                        isSuccess = false
//                    }
//            }
//        }

        val bool: Boolean = GlobalScope.async(Dispatchers.IO) {
            for (rsn in reasonList) {
                if (rsn == "etc") { // 기타 사유 저장
                    val rsnEtc = binding.edittxtSignoutEtcAdd.text.toString()
                    db.collection("Feedback").document("SignoutReason")
                        .update(rsn, FieldValue.arrayUnion(rsnEtc))
                        .addOnSuccessListener {
                            isSuccess = true
                            Log.d(TAG, "etc success isSuccess : " + isSuccess.toString())
                        }
                        .addOnFailureListener { it ->
                            Log.e(TAG, "[Fail] update signout etc reason of 'Feedback' DB!", it)
                            isSuccess = false
                        }
                } else {  // 그 외 사유 저장
                    db.collection("Feedback").document("SignoutReason")
                        .update(rsn, FieldValue.increment(1))
                        .addOnSuccessListener {
                            isSuccess = true
                            Log.d(TAG, "update db success isSuccess : " + isSuccess.toString())
                        }
                        .addOnFailureListener { it ->
                            Log.e(TAG, "[Fail] update signout reason of 'Feedback' DB!", it)
                            isSuccess = false
                        }
                }
            }
            isSuccess
        }.await()

        if (bool) {
            Log.d(TAG, "[Success] update signout reason of 'Feedback' DB!")
        }

        Log.d(TAG, "---------bool : " + bool.toString())

        return bool


//        if (isSuccess) {
//            Log.d(TAG, "[Success] update signout reason of 'Feedback' DB!")
//        }
//
//        Log.d(TAG, "---------isSucess : " + isSuccess.toString())


//        return

    }
}