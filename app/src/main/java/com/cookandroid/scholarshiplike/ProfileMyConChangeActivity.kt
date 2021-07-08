package com.cookandroid.scholarshiplike

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import kotlinx.android.synthetic.main.activity_profile_my_con_change.*
import kotlinx.android.synthetic.main.activity_signup_condition_info.*

class ProfileMyConChangeActivity : AppCompatActivity() {
    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_my_con_change)

        //화면 전환 방지 (세로로 고정)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        setSpinner()
        btnClick()
    }

    //스피너 초기화 & 리스너
    fun setSpinner() {
        //'학자금 지원구간' 스피너의 ArrayAdapter
        ArrayAdapter.createFromResource(
            this,
            R.array.incomeList,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // 스피너의 레이아웃 구체화
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // 스피너에 어뎁터 적용
            myIncome.adapter = adapter
        }

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

        //'거주지' 스피너의 ArrayAdapter
        ArrayAdapter.createFromResource(
            this,
            R.array.local,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // 스피너의 레이아웃 구체화
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // 스피너에 어뎁터 적용
            myArea.adapter = adapter
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
        txt_myPreSemGrade.alpha = 0.3F
        txt_myPreSemScore.alpha = 0.3F
        myPreSemGrade.isEnabled = false
        myPreSemScore.isEnabled = false
    }

    //'직전학기' 레이아웃 활성화 함수
    fun abledPreSemester() {
        txt_myPreSemGrade.alpha = 1F
        txt_myPreSemScore.alpha = 1F
        myPreSemGrade.isEnabled = true
        myPreSemScore.isEnabled = true
    }

    // 버튼 클릭 통합 처리
    fun btnClick() {
        // '시작하기' 버튼 클릭 리스너
        btn_save.setOnClickListener() {
//            updateUserConditionData()
        }
    }

//    // 유저 조건 데이터 업데이트
//    private fun updateUserConditionData() {
//        val pref = this.getPreferences(0)
//        val editor=pref.edit()
//
//        // 키와 밸류를 쌍으로 저장하고 apply한다
//        editor.putInt("KEY_HEIGHT",height)
//            .putInt("KEY_WEIGHT",weight)
//            .apply()
//    }
}
