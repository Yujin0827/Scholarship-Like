package com.cookandroid.scholarshiplike





import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login_password_reset.*
import kotlinx.android.synthetic.main.activity_profile_change.*


class ProfileChangeActivity : AppCompatActivity() {

    private var db = Firebase.firestore
    private val univList: ArrayList<String> = arrayListOf() // 대학교 이름 리스트
    lateinit var univName: String
    lateinit var emailText: TextView
    lateinit var userEmail: String
    lateinit var userUid: String



    var user = Firebase.auth.currentUser // 사용자 가져오기


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_change)


        emailText = findViewById(R.id.emailInput)



        getUserInfo() // User 정보 가져오기
        univSearch() // 대학교 검색 자동완성


        // 이메일 보내기 버튼 클릭 시
        reSetPwBt.setOnClickListener {
            if(!userEmail.isEmpty()) {
                // 이메일 보내기
                FirebaseAuth.getInstance().sendPasswordResetEmail(userEmail)
                    .addOnCompleteListener(this){ task ->
                        if(task.isSuccessful) {
                            sendMailResultText.visibility = View.VISIBLE  // 전송 결과 출력
                        }
                        else {
                            Toast.makeText(this, task.exception.toString(), Toast.LENGTH_LONG).show()
                        }
                    }
            }
        }
    }


    //UserNikname 가져오기
    fun getUserInfo(){
        //User Email
        user?.let {
            userEmail = user!!.email.toString()
            userUid = user!!.uid
        }
        emailText.text = userEmail

        //User 필드 값
        db.collection("Users")
            .document(userUid)
            .get()
            .addOnSuccessListener{ document ->
                if (document != null){
                    if(document.getString("nickname") != null){
                        nickNameInput.hint = document.getString("nickname")!!
                    }
                    if(document.getString("univ") != null){
                        univeInput.hint = document.getString("univ")


                    }
                }

            }
    }

    fun univSearch(){ // 대학교 자동완성
        // 대학교 리스트 가져오기
        val sRef = db.collection("교내")


        sRef.get().addOnSuccessListener { snapshots ->
            val num: Int = snapshots?.documents!!.size
            for (i in 0 until num) {
                univName = snapshots.documents[i]?.id.toString()
                univList.add(univName)
            }
        }
        // 자동완성 리스트
        var edit = findViewById<View>(R.id.univeInput) as AutoCompleteTextView

        edit.setAdapter<ArrayAdapter<String>>(
            ArrayAdapter<String>(
                this,
                R.layout.dropdown_size,R.id.dropdown_size, univList
            )
        )

    }

}







