package com.cookandroid.scholarshiplike

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*

class ProfileMyConChangeActivity : AppCompatActivity() {
    private lateinit var mySemester : Spinner
    private lateinit var myPreSemesterLayout : LinearLayout

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_my_con_change)

        mySemester = findViewById(R.id.mySemester)
        myPreSemesterLayout = findViewById(R.id.myPreSemesterLayout)

        //화면 전환 방지 (세로로 고정)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        //'이수 학기' 스피너의 ArrayAdapter
        ArrayAdapter.createFromResource(
            this,
            R.array.semester,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // 스피너의 레이아웃 구체화
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // 스피너에 어뎁터 적용
            mySemester.adapter = adapter
        }

        //'이수학기' 스피너 선택 리스너
        mySemester.onItemSelectedListener =
        object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when(mySemester.getItemAtPosition(position).toString()) {
                    "0" -> {  //이수학기 0일 때 : '직전 학기' Layout 비활성화
                        disabledPreSemester()
                    }
                    else -> {   //이수학기 1 이상일 때 : '직전 학기' Layout 활성화
                        abledPreSemester()
                    }
                }
            }
        }
    }

    //'직전학기' 레이아웃 비활성화 함수
    fun disabledPreSemester() {
        findViewById<TextView>(R.id.txtMyPreSemester).alpha = 0.5F
        findViewById<TextView>(R.id.txtMyPreSemGrade).alpha = 0.3F
        findViewById<TextView>(R.id.txtMyPreSemScore).alpha = 0.3F
        findViewById<EditText>(R.id.myPreGrade).isEnabled = false
        findViewById<EditText>(R.id.myScore).isEnabled = false
    }

    //'직전학기' 레이아웃 활성화 함수
    fun abledPreSemester() {
        findViewById<TextView>(R.id.txtMyPreSemester).alpha = 1F
        findViewById<TextView>(R.id.txtMyPreSemGrade).alpha = 1F
        findViewById<TextView>(R.id.txtMyPreSemScore).alpha = 1F
        findViewById<EditText>(R.id.myPreGrade).isEnabled = true
        findViewById<EditText>(R.id.myScore).isEnabled = true
    }
}
