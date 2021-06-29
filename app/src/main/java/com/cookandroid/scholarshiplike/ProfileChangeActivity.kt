package com.cookandroid.scholarshiplike




import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_profile_change.*


class ProfileChangeActivity : AppCompatActivity() {




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_change)




        //비밀번호 일치 확인
        checkPwInput.addTextChangedListener(object : TextWatcher {
            // EditText에 문자 입력 전
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            // EditText에 변화가 있을 경우
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            // EditText 입력이 끝난 후
            override fun afterTextChanged(p0: Editable?) {
                if(newPwInput.getText().toString().equals(checkPwInput.getText().toString())){
                    newPwCheckMessage.setText("비밀번호가 일치합니다.")
                }
                else
                    newPwCheckMessage.setText("비밀번호가 일치하지 않습니다.")
            }
        })

    }



}
