package com.cookandroid.scholarshiplike

import android.annotation.SuppressLint
import android.app.backup.SharedPreferencesBackupHelper
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.cookandroid.scholarshiplike.databinding.ActivityProfileMyConChangeBinding

class ProfileMyConChangeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileMyConChangeBinding

    var imm: InputMethodManager? = null // 키보드

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
        binding = ActivityProfileMyConChangeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //화면 전환 방지 (세로로 고정)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        
        imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager  // 키보드

        initSetCondition()
        btnClick()
    }

    // 초기 조건 데이터 set
    private fun initSetCondition() {
        loadAndSetData()
        setInitView()
    }

    // 데이터 파일에서 가져온 데이터를 변수에 저장
    private fun loadAndSetData() {
        val pref : SharedPreferences = getSharedPreferences("SharedData", Context.MODE_PRIVATE)

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
            binding.myIncome.adapter = adapter

            // 변수에 저장된 데이터가 있으면 초기값으로 설정
            if (userIncome != null) {
                binding.myIncome.setSelection(adapter.getPosition(userIncome))
            }
        }

        // '가족 관계' 설정
        binding.myDad.isChecked = userDad!!
        binding.myMom.isChecked = userMom!!

        if (userChildAll == -5 && userChildMe == -5) {
            binding.myChildAll.setText("")
            binding.myChildMe.setText("")
        }
        else {
            binding.myChildAll.setText(userChildAll!!.toString())
            binding.myChildMe.setText(userChildMe!!.toString())
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
            binding.mySemester.adapter = adapter

            // 변수에 저장된 데이터가 있으면 초기값으로 설정
            if (userSemester!! >= 0) {
                binding.mySemester.setSelection(userSemester!!)
            }
        }

        //'이수학기' 스피너 선택 리스너
        binding.mySemester.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                    TODO("Not yet implemented")
                }

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    when(binding.mySemester.getItemAtPosition(position).toString()) {
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
            binding.myArea.adapter = adapter

            // 변수에 저장된 데이터가 있으면 초기값으로 설정
            if (userArea != null) {
                binding.myArea.setSelection(adapter.getPosition(userArea))
            }
        }

        // '국적' 라디오버튼 설정
        if (userCountry.equals("내국인")) {
            binding.myCountryIn.isChecked = true
        }
        else if (userCountry.equals("외국인")) {
            binding.myCountryOut.isChecked = true
        }

        // '추가사항' 설정
        binding.myNationalMerit.isChecked = userNationalMerit!!
        binding.myDisabled.isChecked = userDisabled!!
    }

    //'직전학기' 레이아웃 비활성화 함수
    fun disabledPreSemester() {
        binding.txtMyPreSemClass.alpha = 0.3F
        binding.txtMyPreSemScore.alpha = 0.3F
        binding.myPreSemClass.isEnabled = false
        binding.myPreSemScore.isEnabled = false

        binding.myPreSemClass.setText("")
        binding.myPreSemScore.setText("")
    }

    //'직전학기' 레이아웃 활성화 함수
    fun abledPreSemester() {
        binding.txtMyPreSemClass.alpha = 1F
        binding.txtMyPreSemScore.alpha = 1F
        binding.myPreSemClass.isEnabled = true
        binding.myPreSemScore.isEnabled = true

        if (userPreSemClass == -5) {
            binding.myPreSemClass.setText("")
        }
        else {
            binding.myPreSemClass.setText(userPreSemClass.toString())
        }

        if (userPreSemScore == -5.0f) {
            binding.myPreSemScore.setText("")
        }
        else {
            binding.myPreSemScore.setText(userPreSemScore.toString())
        }
    }

    // 버튼 클릭 통합 처리
    fun btnClick() {
        // '저장' 버튼 클릭 리스너
        binding.btnSave.setOnClickListener() {
            setUserConditioinData()
            saveUserConditionData()
            finish()
        }
        
        // 배경 클릭시 키보드 내리기
        binding.rootViewActivityProfileMyConChange.setOnClickListener {
            imm?.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }

    // 유저 조건 데이터 변수 저장
    private fun setUserConditioinData() {
        // 학자금 지원 구간
        userIncome = binding.myIncome.getItemAtPosition(binding.myIncome.selectedItemPosition).toString()

        // 가족관계
        userDad = binding.myDad.isChecked   // 부
        userMom = binding.myMom.isChecked   // 모

        //형제자매
        if (binding.myChildAll.text.toString() != "" && binding.myChildMe.text.toString() != "") {
            val intChildAll = binding.myChildAll.text.toString().toInt()
            val intChildMe = binding.myChildMe.text.toString().toInt()

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
        userSemester = binding.mySemester.getItemAtPosition(binding.mySemester.selectedItemPosition).toString().toInt()

        // 직전학기 이수학점 & 성적
        if (userSemester!! >= 1) {
            val stringClass = binding.myPreSemClass.text.toString()
            val stringScore = binding.myPreSemScore.text.toString()

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
        userArea = binding.myArea.getItemAtPosition(binding.myArea.selectedItemPosition).toString()

        // 국적
        when (binding.rgMyCountry.checkedRadioButtonId) {
            R.id.myCountry_in -> {
                userCountry = "내국인"
            }
            R.id.myCountry_out -> {
                userCountry = "외국인"
            }
        }

        // 보훈 보상 대상자
        userNationalMerit = binding.myNationalMerit.isChecked

        // 장애 여부
        userDisabled = binding.myDisabled.isChecked
    }

    // 데이터 파일에 유저 조건 데이터 저장
    private fun saveUserConditionData() {
        val pref : SharedPreferences = getSharedPreferences("SharedData", Context.MODE_PRIVATE)
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
