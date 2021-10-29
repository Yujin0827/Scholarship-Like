package com.cookandroid.scholarshiplike

import VerticalItemDecorator
import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cookandroid.scholarshiplike.databinding.FragmentScholarshipMyScholarBinding
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_scholarship.*
import kotlinx.android.synthetic.main.fragment_scholarship_my_scholar.*
import java.text.SimpleDateFormat
import kotlin.concurrent.thread

class ScholarshipMyscholarFragment : Fragment() {

    private var mbinding: FragmentScholarshipMyScholarBinding? = null   // 바인딩 객체
    private val binding get() = mbinding!!              // 바인딩 변수 재선언 (매번 null 체크x)

    private lateinit var listAdapter: ScholarshipRecyclerViewAdapter
    private lateinit var resultListAdapter: ScholarshipRecyclerViewAdapter
    private lateinit var semesterListAdapter: ScholarshipRecyclerViewAdapter
    private lateinit var preClassListAdapter: ScholarshipRecyclerViewAdapter
    private lateinit var preScoreListAdapter: ScholarshipRecyclerViewAdapter
    private lateinit var areaListAdapter: ScholarshipRecyclerViewAdapter
    private lateinit var nationMeritListAdapter: ScholarshipRecyclerViewAdapter
    private lateinit var disabledListAdapter: ScholarshipRecyclerViewAdapter

    var user = Firebase.auth.currentUser // 사용자 가져오기
    private var db = Firebase.firestore

    var dataList: MutableList<Scholarship> = arrayListOf()
    var alist: MutableList<Scholarship> = arrayListOf()
    var incomelist: MutableList<Scholarship> = arrayListOf()
    var semesterlist: MutableList<Scholarship> = arrayListOf()
    var preclasslist: MutableList<Scholarship> = arrayListOf()
    var prescorelist: MutableList<Scholarship> = arrayListOf()
    var arealist: MutableList<Scholarship> = arrayListOf()
    var nationalMeritlist: MutableList<Scholarship> = arrayListOf()
    var disabledlist: MutableList<Scholarship> = arrayListOf()
    var resultlist: MutableList<Scholarship> = arrayListOf()



    private lateinit var mContext: Context //프래그먼트의 정보 받아오는 컨텍스트 선언

    var isIncomeSpinnerSelected: Boolean = false // 초기 스피너 이벤트 자동 실행 방지
    var isSemesterSpinnerSelected: Boolean = false // 초기 스피너 이벤트 자동 실행 방지
    var isAreaSpinnerSelected: Boolean = false // 초기 스피너 이벤트 자동 실행 방지

    var changeUniv : String? = null
    var changeIncome: Long = -10 // 사용자가 입력한 incomeSpinner
    var changeDad: Boolean = false
    var changeMom: Boolean = false
    var changeChildAll: Long = 0
    var changeChildMe: Long = 0
    var changeSemester: Long = 30 // 사용자가 입력한 semesterSpinner
    var changePreclass: Long = 30 // 사용자가 입력한 semesterSpinner
    var changePreScore: Float = 30.0F // 사용자가 입력한 semesterSpinner
    var changeArea: String? = null // 사용자가 입력한 areaSpinner
    var changeCountry: String? = null // 사용자가 입력한 areaSpinner
    var changeNationMerit: Boolean = false // 사용자가 입력한 areaSpinner
    var changeDisabled: Boolean = false // 사용자가 입력한 areaSpinner

    private lateinit var userUid : String
    private lateinit var userUniv : String

    private lateinit var scholar_count : TextView



    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = requireActivity()



    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment.
        mbinding = FragmentScholarshipMyScholarBinding.inflate(inflater, container, false)
        val view = binding.root

        scholar_count = view.findViewById(R.id.scholar_count)

        binding.myPreClass.imeOptions = EditorInfo.IME_ACTION_DONE
        binding.myPreScore.imeOptions = EditorInfo.IME_ACTION_DONE

        //화면 전환 방지 (세로로 고정) <- 이거 구현 해야됨



        initSetCondition() // 초기 조건 데이터 set
        spinnerEvent() // 스피너 이벤트

        // 이수 학점
        binding.myPreClass.setOnEditorActionListener { textView, action, event ->
            var handled = false
            if (action == EditorInfo.IME_ACTION_DONE) {
                // 키보드 숨기기
                var imm: InputMethodManager =
                    activity?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)

                var txt = binding.myPreClass.getText().toString()
                changePreclass = txt.toLong()
                Log.w("에딧 텍스트 값", changePreclass.toString())
                conditionSearch()
                handled = true
            }
            handled
        }

        // 성적
        binding.myPreScore.setOnEditorActionListener { textView, action, event ->
            var handled = false
            if (action == EditorInfo.IME_ACTION_DONE) {
                // 키보드 숨기기
                var imm: InputMethodManager =
                    activity?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)

                var txt = binding.myPreScore.getText().toString()
                changePreScore = txt.toFloat()
                Log.w("에딧 텍스트 값", changePreScore.toString())
                conditionSearch()
                handled = true
            }
            handled
        }

        // 보훈대상자 체크
        binding.nationalMerit.setOnCheckedChangeListener { button, isChecked ->
            if(isChecked){
                changeNationMerit = true
                conditionSearch()
            }
            else{
                changeNationMerit = false
                conditionSearch()

            }

        }

        // 장애 체크
        binding.disabled.setOnCheckedChangeListener { button, isChecked ->
            if (isChecked){
                changeDisabled = true
                conditionSearch()
            }
            else {
                changeDisabled = false
                conditionSearch()
            }

        }

        conditionSearch()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) { // 어뎁터 연결
        super.onViewCreated(view, savedInstanceState)

        // Fragment에서 전달받은 list를 넘기면서 ListAdapter 생성
        listAdapter = ScholarshipRecyclerViewAdapter(dataList, mContext)
        resultListAdapter = ScholarshipRecyclerViewAdapter(resultlist, mContext)
//        semesterListAdapter = ScholarshipRecyclerViewAdapter(semesterlist, mContext)
//        preClassListAdapter = ScholarshipRecyclerViewAdapter(preclasslist, mContext)
//        preScoreListAdapter = ScholarshipRecyclerViewAdapter(prescorelist, mContext)
//        areaListAdapter = ScholarshipRecyclerViewAdapter(arealist, mContext)
//        nationMeritListAdapter = ScholarshipRecyclerViewAdapter(nationalMeritlist, mContext)
        disabledListAdapter = ScholarshipRecyclerViewAdapter(disabledlist, mContext)

        binding.myrecyclerView.addItemDecoration(VerticalItemDecorator(17)) // recyclerview 항목 간격
        binding.myrecyclerView.addItemDecoration(HorizontalItemDecorator(10))
        binding.myrecyclerView.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        // RecyclerView.adapter에 지정

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        reset_bt.setOnClickListener { //초기화 버튼
            initSetCondition()

            listAdapter.notifyDataSetChanged()
            dataList.clear() // 리스트 재정의

           conditionSearch() // 초기 화면 장학금 데이터 가져오기
        }

    }

    override fun onStart() { // 사용자에게 보여지기 전 호출되는 함수
        super.onStart()


    }

    override fun onResume() {
        super.onResume()

        // 아래로 스와이프 새로고침
        binding.swipeRefreshFragmentScholarship.setOnRefreshListener {
            Handler().postDelayed({
                // 아래로 스와이핑 이후 1초 후에 리플래쉬 아이콘 없애기
                if (binding.swipeRefreshFragmentScholarship.isRefreshing)
                    binding.swipeRefreshFragmentScholarship.isRefreshing = false
            }, 1000)

            setInitView() // 유저 조건 초기화

            binding.myrecyclerView.adapter = listAdapter
            listAdapter.notifyDataSetChanged()
            dataList.clear() // 리스트 재정의

            conditionSearch() // 장학금 데이터 가져오기

        }

    }


    // 프래그먼트 파괴
    override fun onDestroyView() {
        super.onDestroyView()
        mbinding = null
    }

    // --------------------------------- function  ------------------------------------------

    // 초기 조건 데이터 set
    private fun initSetCondition() {
        loadAndSetData()
        setInitView()
    }

    // 데이터 파일에서 가져온 데이터를 변수에 저장
    private fun loadAndSetData() {
        val pref: SharedPreferences =
            requireActivity().getSharedPreferences("SharedData", Context.MODE_PRIVATE)

        // key에 해당하는 value 가져오기
        changeIncome = pref.getLong("KEY_USER_INCOME", -10)
        changeDad = pref.getBoolean("KEY_USER_DAD", false)
        changeMom = pref.getBoolean("KEY_USER_MOM", false)
        changeChildAll = pref.getLong("KEY_USER_CHILD_ALL", 0)
        changeChildMe = pref.getLong("KEY_USER_CHILD_ME", 0)
        changeSemester = pref.getLong("KEY_USER_SEMESTER", 30)
        changePreclass = pref.getLong("KEY_USER_PRE_SEM_CLASS", 30)
        changePreScore = pref.getFloat("KEY_USER_PRE_SEM_SCORE", 30f)
        changeArea = pref.getString("KEY_USER_AREA", null)
        changeCountry = pref.getString("KEY_USER_COUNTRY", null)
        changeNationMerit = pref.getBoolean("KEY_USER_NATIONAL_MERIT", false)
        changeDisabled = pref.getBoolean("KEY_USER_DISABLED", false)
    }


    // 변수에 저장된 값으로 초기 view 설정
    private fun setInitView() {

        isIncomeSpinnerSelected = false
        isSemesterSpinnerSelected = false
        isAreaSpinnerSelected = false


        //'학자금 지원구간' 스피너 설정
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.incomeList,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // 스피너의 레이아웃 구체화
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // 스피너에 어뎁터 적용
            binding.incomeSpinner.adapter = adapter

            // 변수에 저장된 데이터가 있으면 초기값으로 설정
            if (changeIncome != -10L) {
                binding.incomeSpinner.setSelection(changeIncome.toInt() + 1)
            }
        }


        //'이수 학기' 스피너 설정
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.semester,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // 스피너의 레이아웃 구체화
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // 스피너에 어뎁터 적용
            binding.semesterSpinner.adapter = adapter

            // 변수에 저장된 데이터가 있으면 초기값으로 설정
            if (changeSemester >= 0 && changeSemester != 30L) {
                binding.semesterSpinner.setSelection(changeSemester.toInt())
            }
        }


        //'거주지' 스피너 설정
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.local,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // 스피너의 레이아웃 구체화
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // 스피너에 어뎁터 적용
            binding.areaSpinner.adapter = adapter

            // 변수에 저장된 데이터가 있으면 초기값으로 설정
            if (changeArea != null) {
                binding.areaSpinner.setSelection(adapter.getPosition(changeArea))
            }
        }


        // '추가사항' 설정
        binding.nationalMerit.isChecked = changeNationMerit
        binding.disabled.isChecked = changeDisabled
    }


        //'직전학기' 레이아웃 비활성화 함수
    private fun disabledPreSemester() {
            binding.myPreText.alpha = 0.3F
            binding.myPreClassText.alpha = 0.3F
            binding.myPreScoreText.alpha = 0.3F
            binding.myPreClass.isEnabled = false
            binding.myPreScore.isEnabled = false

            binding.myPreClass.setText("")
            binding.myPreScore.setText("")
        }

        //'직전학기' 레이아웃 활성화 함수
    private fun abledPreSemester() {
            binding.myPreText.alpha = 1F
            binding.myPreClassText.alpha = 1F
            binding.myPreScoreText.alpha = 1F
            binding.myPreClass.isEnabled = true
            binding.myPreScore.isEnabled = true

            if (changeSemester == 30L) {
                binding.myPreClass.setText("")
            } else {
                binding.myPreClass.setText(changePreclass.toString())
            }

            if (changePreScore == 30f) {
                binding.myPreScore.setText("")
            } else {
                binding.myPreScore.setText(changePreScore.toString())

            }
        }


    // --------------------------------------------- 조건 ------------------------------------------


    var ref =  db.collectionGroup("ScholarshipList")


    private fun spinnerEvent(){

        // 학자금 지원구간 스피너 이벤트
        binding.incomeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onNothingSelected(p0: AdapterView<*>?) { // 스피너 선택 안했을 때

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) { // 스피너 선택 했을 때
                if(isIncomeSpinnerSelected){
                    // 학자금 지원 구간
                    changeIncome = binding.incomeSpinner.getItemIdAtPosition(position) - 1
                    Log.w("선택한 학자금 지원구간", changeIncome.toString())

                    conditionSearch()
                }
                isIncomeSpinnerSelected = true

            }
        }

        //이수학기 스피너 이벤트
        binding.semesterSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onNothingSelected(p0: AdapterView<*>?) {

        }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position == 0){
                    disabledPreSemester()
                }
                else{
                    abledPreSemester()
                }


                if(isSemesterSpinnerSelected){

                    // 이수학기
                    changeSemester = binding.semesterSpinner.getItemIdAtPosition(position)
                    Log.w("선택한 이수학기", changeSemester.toString())

                    conditionSearch()
                }
                isSemesterSpinnerSelected = true


            }
        }

        //거주지 스피너 이벤트
        binding.areaSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }


            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (isAreaSpinnerSelected){
                    // 이수학기
                    changeArea = binding.areaSpinner.getItemAtPosition(position).toString()
                    Log.w("선택한 거주지", changeArea)

                    conditionSearch()
                }
                isAreaSpinnerSelected = true

            }
        }
    }

    private fun conditionSearch(){
        thread(start = true) {

            //모든 리스트 초기화
            incomelist.clear()
            semesterlist.clear()
            preclasslist.clear()
            prescorelist.clear()
            arealist.clear()
            nationalMeritlist.clear()
            disabledlist.clear()
            alist.clear()


            //학자금 지원구간
            Log.w("db 가져오기 직전에 유저 학자금지원구간 값", changeSemester.toString())
            ref.whereGreaterThanOrEqualTo("condition.income", changeIncome)
                .get()
                .addOnSuccessListener { document ->
                    for (snap in document) {
                        setDataShape(incomelist, snap)
                        Log.w("incomelist ", snap.id)
                    }

                    Log.w("MyScholar incomelist", incomelist.toString())
                    if (incomelist.isNotEmpty()){
                        // 이수학기
                        Log.w("db 가져오기 직전에 유저 이수학기 값", changeSemester.toString())

                        if (changeSemester == 0L){ //신입생

                            Log.w("신입생", changeSemester.toString())
                            ref.whereEqualTo("condition.fresher", true)
                                .get()
                                .addOnSuccessListener{ document ->
                                    for (snap in document){
                                        setDataShape(alist, snap)
                                        Log.w("alist(이수학기-신입생)", snap.id)

                                    }
                                    remainConditionSearch()

                                }
                                .addOnFailureListener { exception ->
                                    Log.w("ScholarshipMyscholarFragment - semesterScholar(fresher)", "Error getting data: $exception")

                                }
                        }

                        else{
                            Log.w("신입생 말고", changeSemester.toString())
                            ref.whereLessThanOrEqualTo("condition.semester", changeSemester)
                                .get()
                                .addOnSuccessListener { document ->
                                    for (snap in document){
                                        setDataShape(alist, snap)
                                        Log.w("alist(이수학기(신입생 말고))", snap.id)
                                    }

                                    remainConditionSearch()
                                }
                                .addOnFailureListener { exception ->
                                    Log.w("ScholarshipMyscholarFragment - semesterScholar", "Error getting data: $exception")

                                }
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    // 실패할 경우
                    Log.w("ScholarshipMyscholarFragment - incomeScholar", "Error getting data: $exception")
                }
        }
    }

    private fun remainConditionSearch(){



        if (alist.isEmpty()){
            for (i in 0 until incomelist.size){
                semesterlist.add(incomelist[i])
            }
        }

        else{
            for (i in 0 until incomelist.size){
                for (j in 0 until alist.size){
                    if (incomelist[i] == alist[j]){
                        semesterlist.add(alist[j])
                    }

                }
            }
        }


        Log.w("MyScholar semsterlist", semesterlist.toString())
        if (semesterlist.isNotEmpty()){
            // 직전학기 이수학점

            alist.clear()
            Log.w("db 가져오기 직전에 유저 직전학기 이수학점 값", changePreclass.toString())
            ref.whereLessThanOrEqualTo("condition.preclass", changePreclass)
                .get()
                .addOnSuccessListener { document ->
                    for (snap in document){
                        setDataShape(alist, snap)
                       Log.w("alist(직전학기 학점)", snap.id)

                    }

                    if (alist.isEmpty()){
                        for (i in 0 until semesterlist.size){
                            preclasslist.add(semesterlist[i])
                        }
                    }
                    else{
                        for (i in 0 until semesterlist.size){
                            for (j in 0 until alist.size){
                                if (semesterlist[i] == alist[j]){
                                    preclasslist.add(alist[j])
                                }

                            }
                        }
                    }

                    Log.w("MyScholar preclasslist", preclasslist.toString())
                    if(preclasslist.isNotEmpty()){
                        // 직전학기 성적
                        alist.clear()
                        Log.w("db 가져오기 직전에 유저 직전학기 성적 값", changePreScore.toString())

                        ref.whereLessThanOrEqualTo("condition.prescore", changePreScore)
                            .get()
                            .addOnSuccessListener { document ->
                                for (snap in document){
                                    setDataShape(alist, snap)
                                    Log.w("alist(직전학기 성적)", snap.id)
                                }

                                if (alist.isEmpty()){
                                    for (i in 0 until preclasslist.size){
                                        prescorelist.add(preclasslist[i])
                                    }
                                }

                                else{
                                    for (i in 0 until preclasslist.size){
                                        for (j in 0 until alist.size){
                                            if (preclasslist[i] == alist[j]){
                                                prescorelist.add(alist[j])
                                            }

                                        }
                                    }
                                }

                                Log.w("MyScholar prescorelist", prescorelist.toString())
                                if (prescorelist.isNotEmpty()){

                                    // 거주지
                                    alist.clear()
                                    Log.w("db 가져오기 직전에 유저 거주지", changeArea)

                                    ref.whereEqualTo("condition.area", changeArea)
                                        .get()
                                        .addOnSuccessListener { document ->
                                            Log.w("거주지", "유저")
                                            for(snap in document){
                                                setDataShape(alist, snap)
                                               Log.w("alist(거주지-유저)", snap.id)
                                            }

                                            ref.whereEqualTo("condition.area", "전체")
                                                .get().addOnSuccessListener { document ->
                                                    Log.w("거주지", "전체")
                                                    for (snap in document){
                                                        setDataShape(alist, snap)
                                                       Log.w("alist(거주지-전체)", snap.id)
                                                    }

                                                    if (alist.isEmpty()){
                                                        for(i in 0 until prescorelist.size){
                                                            arealist.add(prescorelist[i])

                                                        }
                                                    }
                                                    else{
                                                        for (i in 0 until prescorelist.size){
                                                            for(j in 0 until alist.size){
                                                                if (prescorelist[i] == alist[j]){
                                                                    arealist.add(alist[j])
                                                                }

                                                            }
                                                        }
                                                    }
                                                    Log.w("MyScholar arealist", arealist.toString())

                                                    if (arealist.isNotEmpty()){
                                                        alist.clear()

                                                        Log.w("db 가져오기 직전에 유저 보훈보상대상자", changeNationMerit.toString())

                                                        if(changeNationMerit){
                                                            ref.whereEqualTo("condition.nationalmerit", true)
                                                                .get().addOnSuccessListener { document ->
                                                                    for(snap in document){
                                                                        setDataShape(alist, snap)
                                                                        Log.w("alist(보훈 true)", snap.id)
                                                                    }
                                                                    if(alist.isEmpty()){
                                                                        for(i in 0 until arealist.size){
                                                                            nationalMeritlist.add(arealist[i])
                                                                        }
                                                                    }
                                                                    else{
                                                                        for (i in 0 until arealist.size){
                                                                            for(j in 0 until alist.size){
                                                                                if (arealist[i] == alist[j]){
                                                                                    nationalMeritlist.add(alist[j])
                                                                                }

                                                                            }
                                                                        }
                                                                    }
                                                                    Log.w("MyScholar nationalMerit(true)", nationalMeritlist.toString())

                                                                    if(nationalMeritlist.isNotEmpty()){
                                                                        alist.clear()
                                                                        if (changeDisabled){
                                                                            ref.whereEqualTo("condition.disabled", true)
                                                                                .get().addOnSuccessListener { document ->
                                                                                    for(snap in document){
                                                                                        setDataShape(alist, snap)
                                                                                        Log.w("alist(보훈 true - 장애 true)", snap.id)
                                                                                    }
                                                                                    if(alist.isEmpty()){
                                                                                        for(i in 0 until nationalMeritlist.size){
                                                                                            disabledlist.add(nationalMeritlist[i])
                                                                                        }
                                                                                    }
                                                                                    else{
                                                                                        for (i in 0 until nationalMeritlist.size){
                                                                                            for(j in 0 until alist.size){
                                                                                                if (nationalMeritlist[i] == alist[j]){
                                                                                                    disabledlist.add(alist[j])
                                                                                                }

                                                                                            }
                                                                                        }
                                                                                    }
                                                                                    Log.w("MyScholar - 보훈 true 장애 true disalbedList", disabledlist.toString())
                                                                                    resultDate()

                                                                                }
                                                                                .addOnFailureListener { exception ->
                                                                                    Log.w("ScholarshipMyscholarFragment - disabled", "Error getting data: $exception")

                                                                                }
                                                                        }

                                                                        else{
                                                                            alist.clear()
                                                                            ref.whereEqualTo("condition.disabled", false)
                                                                                .get().addOnSuccessListener { document ->
                                                                                    for(snap in document){
                                                                                        setDataShape(alist, snap)
                                                                                        Log.w("alist(보훈 true - 장애 false)", snap.id)
                                                                                    }
                                                                                    if(alist.isEmpty()){
                                                                                        for(i in 0 until nationalMeritlist.size){
                                                                                            disabledlist.add(nationalMeritlist[i])
                                                                                        }
                                                                                    }
                                                                                    else{
                                                                                        for (i in 0 until nationalMeritlist.size){
                                                                                            for(j in 0 until alist.size){
                                                                                                if (nationalMeritlist[i] == alist[j]){
                                                                                                    disabledlist.add(alist[j])
                                                                                                }

                                                                                            }
                                                                                        }
                                                                                    }
                                                                                    Log.w("MyScholar 보훈 true 장애 false", disabledlist.toString())
                                                                                    resultDate()

                                                                                }
                                                                                .addOnFailureListener { exception ->
                                                                                    Log.w("ScholarshipMyscholarFragment - disabled(false)", "Error getting data: $exception")

                                                                                }

                                                                        }

                                                                    }
                                                                }
                                                                .addOnFailureListener { exception ->
                                                                    Log.w("ScholarshipMyscholarFragment - nationalmerit", "Error getting data: $exception")

                                                                }
                                                        }
                                                        else{
                                                            alist.clear()
                                                            ref.whereEqualTo("condition.nationalmerit", false)
                                                                .get().addOnSuccessListener { document ->
                                                                    for(snap in document){
                                                                        setDataShape(alist, snap)
                                                                        Log.w("alist(보훈 false)", snap.id)
                                                                    }
                                                                    if(alist.isEmpty()){
                                                                        for(i in 0 until arealist.size){
                                                                            nationalMeritlist.add(arealist[i])
                                                                        }
                                                                    }
                                                                    else{
                                                                        for (i in 0 until arealist.size){
                                                                            for(j in 0 until alist.size){
                                                                                if (arealist[i] == alist[j]){
                                                                                    nationalMeritlist.add(alist[j])
                                                                                }

                                                                            }
                                                                        }
                                                                    }
                                                                    Log.w("MyScholar nationalMerit(보훈 false)", nationalMeritlist.toString())
                                                                    if(nationalMeritlist.isNotEmpty()) {
                                                                        alist.clear()

                                                                        if (changeDisabled) {
                                                                            ref.whereEqualTo("condition.disabled", true)
                                                                                .get()
                                                                                .addOnSuccessListener { document ->
                                                                                    for (snap in document) {
                                                                                        setDataShape(alist, snap)
                                                                                        Log.w("alist(보훈 false - 장애 true)", snap.id)
                                                                                    }
                                                                                    if (alist.isEmpty()) {
                                                                                        for (i in 0 until nationalMeritlist.size) {
                                                                                            disabledlist.add(nationalMeritlist[i])
                                                                                        }
                                                                                    }
                                                                                    else {
                                                                                        for (i in 0 until nationalMeritlist.size) {
                                                                                            for (j in 0 until alist.size) {
                                                                                                if (nationalMeritlist[i] == alist[j]) {
                                                                                                    disabledlist.add(alist[j])
                                                                                                }

                                                                                            }
                                                                                        }
                                                                                    }
                                                                                    Log.w("MyScholar 보훈 false 장애 true  disablelist", disabledlist.toString())
                                                                                    resultDate()

                                                                                }
                                                                                .addOnFailureListener { exception ->
                                                                                    Log.w("ScholarshipMyscholarFragment - disabled", "Error getting data: $exception")

                                                                                }
                                                                        }
                                                                        else {
                                                                            alist.clear()
                                                                            ref.whereEqualTo("condition.disabled", false)
                                                                                .get()
                                                                                .addOnSuccessListener { document ->
                                                                                    for (snap in document) {
                                                                                        setDataShape(alist, snap)
                                                                                        Log.w("alist(보훈 false - 장애 false)", snap.id)
                                                                                    }
                                                                                    if (alist.isEmpty()) {
                                                                                        for (i in 0 until nationalMeritlist.size) {
                                                                                            disabledlist.add(nationalMeritlist[i])
                                                                                        }
                                                                                    }
                                                                                    else {
                                                                                        for (i in 0 until nationalMeritlist.size) {
                                                                                            for (j in 0 until alist.size) {
                                                                                                if (nationalMeritlist[i] == alist[j]) {
                                                                                                    disabledlist.add(alist[j])
                                                                                                }
                                                                                            }
                                                                                        }
                                                                                    }
                                                                                    Log.w("MyScholar 보훈 false 장애 false  disableist", disabledlist.toString())
                                                                                    resultDate()

                                                                                }
                                                                                .addOnFailureListener { exception ->
                                                                                    Log.w("ScholarshipMyscholarFragment - disabled", "Error getting data: $exception")

                                                                                }
                                                                        }

                                                                    }
                                                                }
                                                                .addOnFailureListener { exception ->
                                                                    Log.w("ScholarshipMyscholarFragment - nationalmerit", "Error getting data: $exception")

                                                                }
                                                        }
                                                    }

                                                }
                                                .addOnFailureListener { exception ->
                                                    Log.w("ScholarshipMyscholarFragment - areaScholar(전체)", "Error getting data: $exception")

                                                }
                                        }
                                        .addOnFailureListener { exception ->
                                            Log.w("ScholarshipMyscholarFragment - areaScholar", "Error getting data: $exception")

                                        }
                                }

                            }
                            .addOnFailureListener { exception ->
                                Log.w("ScholarshipMyscholarFragment - prescoreScholar", "Error getting data: $exception")

                            }
                    }

                }
                .addOnFailureListener { exception ->
                    Log.w("ScholarshipMyscholarFragment - preClassScholar", "Error getting data: $exception")

                }
        }

    }



    private fun setDataShape(list : MutableList<Scholarship>, snap: DocumentSnapshot){

        //  필드값 가져오기
        val paymentType = snap["paymentType"].toString()
        val period = snap["period"] as Map<String, Timestamp>
        val startdate = period.get("startDate")?.toDate()
        val enddate = period.get("endDate")?.toDate()
        val startdate2 = period.get("startDate2")?.toDate()
        val enddate2 = period.get("endDate2")?.toDate()
        val institution = snap["paymentInstitution"].toString()

        val date = SimpleDateFormat("yyyy-MM-dd") // 날짜 형식으로 변환

        if((startdate2 == null)&& enddate2 == null){
            if(startdate == null && enddate == null){
                val item = Scholarship(paymentType, snap.id, "자동 신청", "", "", "", institution)
                list.add(item)
            }
            else if(startdate == enddate){
                val item = Scholarship(paymentType, snap.id, "추후 공지", "", "", "", institution)
                list.add(item)
            }
            else{
                val item = Scholarship(paymentType, snap.id, date.format(startdate!!), date.format(enddate!!), "", "", institution)
                list.add(item)
            }
        }
        else{
            val item = Scholarship(paymentType, snap.id, date.format(startdate!!), date.format(enddate!!), date.format(startdate2!!), date.format(enddate2!!), institution)
            list.add(item)
        }


    }

    private fun resultDate(){
        alist.clear()
        db.collection("Scholarship").document("Nation").collection("ScholarshipList")
            .get().addOnSuccessListener { document ->
                for(snap in document){
                    setDataShape(alist, snap)
                    Log.w("for univlist - Nation", snap.id)
                }
                if(alist.isNotEmpty()){
                    db.collection("Scholarship").document("OutScholar").collection("ScholarshipList")
                        .get().addOnSuccessListener { document ->
                            for(snap in document){
                                setDataShape(alist, snap)
                                Log.w("for univlist - OutScholar", snap.id)
                            }

                            //User Email
                            user?.let {
                                userUid  = user!!.uid

                            }

                            //User's Univ
                            db.collection("Users").document(userUid)
                                .get().addOnSuccessListener{ document ->
                                    if (document != null){
                                        if(document.getString("univ") != null){
                                            userUniv = document.getString("univ")!!
                                            Log.w("유저 대학 정보", userUniv)
                                        }
                                    }

                                   ref.whereEqualTo("univ", userUniv)
                                       .get().addOnSuccessListener { document ->
                                           for(snap in document){
                                               setDataShape(alist, snap)
                                               Log.w("for univlist - univ", snap.id)
                                           }

                                           if (alist.isEmpty()){
                                               binding.myrecyclerView.adapter = disabledListAdapter
                                               disabledListAdapter.notifyDataSetChanged()
                                               disabledListAdapter.submitList(disabledlist)
                                               scholar_count.text = disabledlist.size.toString()
                                           }
                                           else {
                                               resultlist.clear()
                                               for (i in 0 until alist.size) {
                                                   for (j in 0 until disabledlist.size) {
                                                       if (alist[i] == disabledlist[j]) {
                                                           resultlist.add(disabledlist[j])

                                                       }
                                                   }
                                               }
                                               binding.myrecyclerView.adapter = resultListAdapter
                                               resultListAdapter.notifyDataSetChanged()
                                               resultListAdapter.submitList(resultlist)
                                               scholar_count.text = resultlist.size.toString()
                                           }


                                           Log.w("최종  list", resultlist.toString())
                                       }
                                       .addOnFailureListener { exception ->
                                           Log.w("ScholarshipMyscholarFragment -for univlist - univ", "Error getting data: $exception")

                                       }

                                }


                    }
                        .addOnFailureListener { exception ->
                            Log.w("ScholarshipMyscholarFragment -for univlist - OutScholar", "Error getting data: $exception")

                        }
                }


            }
            .addOnFailureListener { exception ->
                Log.w("ScholarshipMyscholarFragment -for univlist - Nation", "Error getting data: $exception")

            }
    }


}


