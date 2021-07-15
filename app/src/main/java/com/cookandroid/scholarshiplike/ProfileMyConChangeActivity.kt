package com.cookandroid.scholarshiplike

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*

class ProfileMyConChangeActivity : AppCompatActivity() {

    // xml 뷰 변수
    lateinit var spinnerIncome :Spinner
    lateinit var cbDad :CheckBox
    lateinit var cbMom :CheckBox
    lateinit var edittxtChildAll :EditText
    lateinit var edittxtChildMe :EditText
    lateinit var spinnerSemester :Spinner
    lateinit var txtPreSemClass :TextView
    lateinit var txtPreSemScore :TextView
    lateinit var edittxtPreSemClass :EditText
    lateinit var edittxtPreSemScore :EditText
    lateinit var spinnerArea :Spinner
    lateinit var rgCountry :RadioGroup
    lateinit var rbtnCountryIn :RadioButton
    lateinit var rbtnCountryOut :RadioButton
    lateinit var cbNationalMerit :CheckBox
    lateinit var cbDisabled :CheckBox
    lateinit var btnSave :Button

    // 조건 저장 변수
    var userIncome :String? = null  // 학자금 지원구간
    var userDad :Boolean? = false    // 가족관계 - 부 여부
    var userMom :Boolean? = false    // 가족관계 - 모 여부
    var userChildAll :Int? = -5   // 형제 - 전체
    var userChildMe :Int? = -5    // 형제 - 자신
    var userSemester :Int? = -5   // 이수학기
    var userPreSemClass :Int? = -5    // 직전학기 이수학점
    var userPreSemScore : Float? = -5.0f // 직전학기 성적
    var userArea :String? = null    // 거주지
    var userCountry :String? = null // 국적
    var userNationalMerit :Boolean? = false  //보훈 보상 대상자 여부
    var userDisabled :Boolean? = false   //장애 여부

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_my_con_change)

        // xml 뷰 변수 초기화
        setXmlView()

        //화면 전환 방지 (세로로 고정)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        initSetCondition()
        btnClick()
    }

    // xml 뷰 변수 초기화
    private fun setXmlView() {
        spinnerIncome = findViewById(R.id.myIncome)
        cbDad = findViewById(R.id.myDad)
        cbMom = findViewById(R.id.myMom)
        edittxtChildAll = findViewById(R.id.myChildAll)
        edittxtChildMe = findViewById(R.id.myChildMe)
        spinnerSemester = findViewById(R.id.mySemester)
        txtPreSemClass = findViewById(R.id.txt_myPreSemClass)
        txtPreSemScore = findViewById(R.id.txt_myPreSemScore)
        edittxtPreSemClass = findViewById(R.id.myPreSemClass)
        edittxtPreSemScore = findViewById(R.id.myPreSemScore)
        spinnerArea = findViewById(R.id.myArea)
        rgCountry = findViewById(R.id.rg_myCountry)
        rbtnCountryIn = findViewById(R.id.myCountry_in)
        rbtnCountryOut = findViewById(R.id.myCountry_out)
        cbNationalMerit = findViewById(R.id.myNationalMerit)
        cbDisabled = findViewById(R.id.myDisabled)
        btnSave = findViewById(R.id.btn_save)
    }

    // 초기 조건 데이터 set
    private fun initSetCondition() {
        loadAndSetData()
        setInitView()
    }

    // 데이터 파일에서 가져온 데이터를 변수에 저장
    private fun loadAndSetData() {
        val pref= this.getPreferences(0)

        // key에 해당하는 value 가져오기
        userIncome = pref.getString("KEY_USER_INCOME", null)
        userDad = pref.getBoolean("KEY_USER_DAD", false)
        userMom = pref.getBoolean("KEY_USER_MOM", false)
        userChildAll = pref.getInt("KEY_USER_CHILD_ALL", -5)
        userChildMe = pref.getInt("KEY_USER_CHILD_ME", -5)
        userSemester = pref.getInt("KEY_USER_SEMESTER", -5)
        userPreSemClass = pref.getInt("KEY_USER_PRE_SEM_CLASS", -5)
        userPreSemScore = pref.getFloat("KEY_USER_PRE_SEM_SCORE", -5.0f)
        userArea = pref.getString("KEY_USER_AREA", null)
        userCountry = pref.getString("KEY_USER_COUNTRY", null)
        userNationalMerit = pref.getBoolean("KEY_USER_NATIONAL_MERIT", false)
        userDisabled = pref.getBoolean("KEY_USER_DISABLED", false)
    }
    
    // 변수에 저장된 값으로 초기 view 설정
    private fun setInitView() {
        //'학자금 지원구간' 스피너 설정
        ArrayAdapter.createFromResource(
            this,
            R.array.incomeList,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // 스피너의 레이아웃 구체화
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // 스피너에 어뎁터 적용
            spinnerIncome.adapter = adapter

            // 변수에 저장된 데이터가 있으면 초기값으로 설정
            if (userIncome != null) {
                spinnerIncome.setSelection(adapter.getPosition(userIncome))
            }
        }

        // '가족 관계' 설정
        cbDad.isChecked = userDad!!
        cbMom.isChecked = userMom!!

        if (userChildAll == -5 && userChildMe == -5) {
            edittxtChildAll.setText("")
            edittxtChildMe.setText("")
        }
        else {
            edittxtChildAll.setText(userChildAll!!.toString())
            edittxtChildMe.setText(userChildMe!!.toString())
        }

        //'이수 학기' 스피너 설정
        ArrayAdapter.createFromResource(
            this,
            R.array.semester,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // 스피너의 레이아웃 구체화
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // 스피너에 어뎁터 적용
            spinnerSemester.adapter = adapter

            // 변수에 저장된 데이터가 있으면 초기값으로 설정
            if (userSemester!! >= 0) {
                spinnerSemester.setSelection(userSemester!!)
            }
        }

        //'이수학기' 스피너 선택 리스너
        spinnerSemester.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                    TODO("Not yet implemented")
                }

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    when(spinnerSemester.getItemAtPosition(position).toString()) {
                        "0" -> {  //이수학기 0일 때 : '직전 학기' Layout 비활성화
                            disabledPreSemester()
                        }
                        else -> {   //이수학기 1 이상일 때 : '직전 학기' Layout 활성화
                            abledPreSemester()
                        }
                    }
                }
            }

        //'거주지' 스피너 설정
        ArrayAdapter.createFromResource(
            this,
            R.array.local,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // 스피너의 레이아웃 구체화
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // 스피너에 어뎁터 적용
            spinnerArea.adapter = adapter

            // 변수에 저장된 데이터가 있으면 초기값으로 설정
            if (userArea != null) {
                spinnerArea.setSelection(adapter.getPosition(userArea))
            }
        }

        // '국적' 라디오버튼 설정
        if (userCountry.equals("내국인")) {
            rbtnCountryIn.isChecked = true
        }
        else if (userCountry.equals("외국인")) {
            rbtnCountryOut.isChecked = true
        }

        // '추가사항' 설정
        cbNationalMerit.isChecked = userNationalMerit!!
        cbDisabled.isChecked = userDisabled!!
    }

    //'직전학기' 레이아웃 비활성화 함수
    fun disabledPreSemester() {
        txtPreSemClass.alpha = 0.3F
        txtPreSemScore.alpha = 0.3F
        edittxtPreSemClass.isEnabled = false
        edittxtPreSemScore.isEnabled = false

        edittxtPreSemClass.setText("")
        edittxtPreSemScore.setText("")
    }

    //'직전학기' 레이아웃 활성화 함수
    fun abledPreSemester() {
        txtPreSemClass.alpha = 1F
        txtPreSemScore.alpha = 1F
        edittxtPreSemClass.isEnabled = true
        edittxtPreSemScore.isEnabled = true

        if (userPreSemClass == -5) {
            edittxtPreSemClass.setText("")
        }
        else {
            edittxtPreSemClass.setText(userPreSemClass.toString())
        }

        if (userPreSemScore == -5.0f) {
            edittxtPreSemScore.setText("")
        }
        else {
            edittxtPreSemScore.setText(userPreSemScore.toString())
        }
    }

    // 버튼 클릭 통합 처리
    fun btnClick() {
        // '시작하기' 버튼 클릭 리스너
        btnSave.setOnClickListener() {
            setUserConditioinData()
            saveUserConditionData()
            finish()
        }
    }

    // 유저 조건 데이터 변수 저장
    private fun setUserConditioinData() {
        // 학자금 지원 구간
        userIncome = spinnerIncome.getItemAtPosition(spinnerIncome.selectedItemPosition).toString()

        // 가족관계
        userDad = cbDad.isChecked   // 부
        userMom = cbMom.isChecked   // 모

        //형제자매
        if (edittxtChildAll.text.toString() != "" && edittxtChildMe.text.toString() != "") {
            val intChildAll = edittxtChildAll.text.toString().toInt()
            val intChildMe = edittxtChildMe.text.toString().toInt()

            if ((intChildAll > 0) && (intChildMe > 0 && intChildMe <= intChildAll)) {
                userChildAll = intChildAll
                userChildMe = intChildMe
            }
            else {
                userChildAll = -5
                userChildMe = -5
            }
        }
        else {
            userChildAll = -5
            userChildMe = -5
        }

        // 이수학기
        userSemester = spinnerSemester.getItemAtPosition(spinnerSemester.selectedItemPosition).toString().toInt()

        // 직전학기 이수학점 & 성적
        if (userSemester!! >= 1) {
            val stringClass = edittxtPreSemClass.text.toString()
            val stringScore = edittxtPreSemScore.text.toString()

            if (stringClass != "" && stringClass.toInt() > 0) {
                userPreSemClass = stringClass.toInt()
            }
            else {
                userPreSemClass = -5
            }

            if (stringScore != "" && stringScore.toFloat() >= 0 && stringScore.toFloat() <= 4.5) {
                userPreSemScore = stringScore.toFloat()
            }
            else {
                userPreSemScore = (-5.0).toFloat()
            }
        }
        else {
            userPreSemClass = -5
            userPreSemScore = -5.0f

        }

        // 거주지
        userArea = spinnerArea.getItemAtPosition(spinnerArea.selectedItemPosition).toString()

        // 국적
        when (rgCountry.checkedRadioButtonId) {
            R.id.myCountry_in -> {
                userCountry = "내국인"
            }
            R.id.myCountry_out -> {
                userCountry = "외국인"
            }
        }

        // 보훈 보상 대상자
        userNationalMerit = cbNationalMerit.isChecked

        // 장애 여부
        userDisabled = cbDisabled.isChecked
    }

    // 데이터 파일에 유저 조건 데이터 저장
    private fun saveUserConditionData() {
        val pref = this.getPreferences(0)
        val editor=pref.edit()

        // 키와 밸류를 쌍으로 저장 & apply
        editor.putString("KEY_USER_INCOME", userIncome)
        userDad?.let { editor.putBoolean("KEY_USER_DAD", it) }
        userMom?.let { editor.putBoolean("KEY_USER_MOM", it) }
        userChildAll?.let { editor.putInt("KEY_USER_CHILD_ALL", it) }
        userChildMe?.let { editor.putInt("KEY_USER_CHILD_ME", it) }
        userSemester?.let { editor.putInt("KEY_USER_SEMESTER", it) }
        userPreSemClass?.let { editor.putInt("KEY_USER_PRE_SEM_CLASS", it) }
        userPreSemScore?.let { editor.putFloat("KEY_USER_PRE_SEM_SCORE", it) }
        editor.putString("KEY_USER_AREA", userArea)
        editor.putString("KEY_USER_COUNTRY", userCountry)
        userNationalMerit?.let { editor.putBoolean("KEY_USER_NATIONAL_MERIT", it) }
        userDisabled?.let { editor.putBoolean("KEY_USER_DISABLED", it) }

        editor.apply()
    }
}
