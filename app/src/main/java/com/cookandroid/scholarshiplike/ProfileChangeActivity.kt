package com.cookandroid.scholarshiplike





import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.KeyEvent.KEYCODE_ENTER
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_profile_change.*


class ProfileChangeActivity : AppCompatActivity() {

    private var db = Firebase.firestore
    private val univArray: ArrayList<String> = arrayListOf() // 대학교 이름 리스트
    lateinit var univName : String
    lateinit var currentPw : EditText
    lateinit var newPw : EditText
    lateinit var checkPw : EditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_change)

        currentPw = findViewById(R.id.checkPwInput)
        newPw = findViewById(R.id.newPwInput)
        checkPw = findViewById(R.id.checkPwInput)

        // 대학교 이름 리스트 가져오기
        val sRef = db.collection("장학금")
            .document("교내").collection("강원")

        sRef.get().addOnSuccessListener { snapshots ->
            val num: Int = snapshots?.documents!!.size
            for (i in 0 until num) {
                univName = snapshots?.documents?.get(i)?.id.toString()
                univArray.add(univName)
            }
        }

        // 대학교 자동완성
        var edit = findViewById<View>(R.id.univeInput) as AutoCompleteTextView

        edit.setAdapter<ArrayAdapter<String>>(
            ArrayAdapter<String>(
                this,
                android.R.layout.simple_dropdown_item_1line, univArray
            )
        )


        //비밀번호 일치 확인
        checkPwInput.addTextChangedListener(object : TextWatcher {
            // EditText에 문자 입력 전
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            // EditText에 변화가 있을 경우
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            // EditText 입력이 끝난 후
            override fun afterTextChanged(p0: Editable?) {
                if (newPwInput.getText().toString().equals(checkPwInput.getText().toString())) {
                    newPwCheckMessage.setText("비밀번호가 일치합니다.")
                } else
                    newPwCheckMessage.setText("비밀번호가 일치하지 않습니다.")
            }
        })








    }







}


