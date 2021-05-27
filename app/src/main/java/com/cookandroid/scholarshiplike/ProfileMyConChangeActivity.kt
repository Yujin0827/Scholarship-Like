package com.cookandroid.scholarshiplike

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Spinner

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
                        //구현해야함
                        println("-----------<Test>----------------")
                        println("'직전 학기' Layout 비활성화")
                    }
                    else -> {   //이수학기 1 이상일 때 : '직전 학기' Layout 활성화
                        //구현해야함
                        println("------------<Test>----------------")
                        println("'직전 학기' Layout 활성화")
                    }
                }
            }
        }
    }
}
