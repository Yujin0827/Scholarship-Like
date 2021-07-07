package com.cookandroid.scholarshiplike

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_signup_condition_info.*

class SignupConditionInfoActivity :AppCompatActivity() {
    // 프로필 입력에서 받는 데이터 변수
    lateinit var userNickname :String
    lateinit var userUniversity :String

    // 조건 변수
    var userIncome :String? = null  // 학자금 지원구간
    var userDad :Boolean? = null    // 가족관계 - 부 여부
    var userMom :Boolean? = null    // 가족관계 - 모 여부
    var userChildAll :Int? = null   // 형제 - 전체
    var userChildMe :Int? = null    // 형제 - 자신
    var userSemester :Int? = null   // 이수학기
    var userPreSemGrade :Int? = null    // 직전학기 이수학점
    var userPreSemScore :Double? = null // 직전학기 성적
    var userArea :String? = null    // 거주지
    var userCountry :String? = null // 국적
    var userNationalMerit :Boolean? = null  //보훈 보상 대상자 여부
    var userDisabled :Boolean? = null   //장애 여부

    val db = Firebase.firestore
    val TAG = "SignupConditionInfoActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup_condition_info)

        setSpinner()    // 스피너 초기화, 리스너
        btnClick()  // 버튼 클릭 통합 처리
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
            init_myIncome.adapter = adapter
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
            init_mySemester.adapter = adapter
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
            init_myArea.adapter = adapter
        }


        //'이수학기' 스피너 선택 리스너
        init_mySemester.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                    TODO("Not yet implemented")
                }

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    when(init_mySemester.getItemAtPosition(position).toString()) {
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
        txt_init_myPreSemGrade.alpha = 0.3F
        txt_init_myPreSemScore.alpha = 0.3F
        et_init_myPreSemGrade.isEnabled = false
        et_init_myPreSemScore.isEnabled = false
    }

    //'직전학기' 레이아웃 활성화 함수
    fun abledPreSemester() {
        txt_init_myPreSemGrade.alpha = 1F
        txt_init_myPreSemScore.alpha = 1F
        et_init_myPreSemGrade.isEnabled = true
        et_init_myPreSemScore.isEnabled = true
    }

    // 버튼 클릭 통합 처리
    fun btnClick() {
        // '돌아가기' 버튼 클릭 리스너
        btn_condition_goback.setOnClickListener() {
            finish()    // 현재 액티비티 종료, '초기 프로필 입력' 페이지로 이동
        }

        // '시작하기' 버튼 클릭 리스너
        btn_start.setOnClickListener() {
            setInputData()
            updateUserDB()
        }
    }

    // '초기 프로필 입력 페이지'에서 받은 데이터 저장
    // 입력받은 데이터를 변수에 저장
    fun setInputData() {
        // 닉네임
        userNickname = intent.getStringExtra("nickname")

        // 소속 대학교
        userUniversity = intent.getStringExtra("univ")

        // 학자금 지원 구간
        if (init_myIncome.isSelected) {
            userIncome = init_myIncome.getItemAtPosition(init_myIncome.selectedItemPosition).toString()
        }

        // 가족관계
        if (cb_init_dad.isChecked) {    // 부
            userDad = true
        }
        if (cb_init_mom.isChecked) {    // 모
            userMom = true
        }

        //형제자매
        if ((et_init_childAll.text.toString() != null) && (et_init_childMe.text.toString() != null)) {
            userChildAll = et_init_childAll.text.toString().toInt()
            userChildMe = et_init_childMe.text.toString().toInt()
        }

        // 이수학기
        userSemester = init_mySemester.getItemAtPosition(init_mySemester.selectedItemPosition).toString().toInt()

        // 직전학기 이수학점 & 성적
        if (userSemester!! >= 1) {
            if(et_init_myPreSemGrade.text.toString() != null) {
                userPreSemGrade = et_init_myPreSemGrade.text.toString().toInt()
            }
            if (et_init_myPreSemScore.text.toString() != null) {
                userPreSemScore = et_init_myPreSemScore.text.toString().toDouble()
            }
        }

        // 거주지
        userArea = init_myArea.getItemAtPosition(init_myArea.selectedItemPosition).toString()

        // 국적
        when (rg_init_country.checkedRadioButtonId) {
            R.id.rb_init_country_in -> {
                userCountry = "내국인"
            }
            R.id.rb_init_country_out -> {
                userCountry = "외국인"
            }
        }

        // 보훈 보상 대상자
        if (cb_init_nationalMerit.isChecked) {
            userNationalMerit = true
        }

        // 장애 여부
        if (cb_init_disabled.isChecked) {
            userDisabled = true
        }
    }

    // 유저 DB 업데이트
    fun updateUserDB() {
        val auth = Firebase.auth
        val user = auth.currentUser
        val uId :String?
        user.let {
            uId = user?.uid // user db에서 uid 가져오기
        }

        // 형제자매 set
        val userChildSet = hashMapOf(
            "전체" to userChildAll,
            "자신" to userChildMe
        )

        // 유저 데이터 set
        val userSet = hashMapOf(
            "닉네임" to userNickname,
            "소속 대학교" to userUniversity,
            "부" to userDad,
            "모" to userMom,
            "형제자매" to userChildSet,
            "이수학기" to userSemester,
            "직전학기 이수학점" to userPreSemGrade,
            "직전학기 성적" to userPreSemScore,
            "거주지" to userArea,
            "국적" to userCountry,
            "국가보훈보상대상자" to userNationalMerit,
            "장애여부" to userDisabled
        )

        // 유저 DB에 저장
        db.collection("Users").document(uId!!)
            .set(userSet)
            .addOnSuccessListener {
                Log.d(TAG, "User document successfully written!")
                val iT = Intent(this, MainActivity::class.java) // MainActivity로 이동
                startActivity(iT)
                finish()
            }
            .addOnFailureListener { e -> Log.w(TAG, "Error writing user document", e) }
    }
}