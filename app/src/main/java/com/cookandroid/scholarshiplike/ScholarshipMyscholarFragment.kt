package com.cookandroid.scholarshiplike


import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cookandroid.scholarshiplike.R.*
import com.cookandroid.scholarshiplike.databinding.FragmentScholarshipAllScholarBinding
import com.cookandroid.scholarshiplike.databinding.FragmentScholarshipMyScholarBinding
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.model.Document
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_scholarship_my_scholar.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import kotlin.concurrent.thread
import kotlin.properties.Delegates

class ScholarshipMyscholarFragment : Fragment() {

    private var mbinding: FragmentScholarshipMyScholarBinding? = null   // 바인딩 객체
    private val binding get() = mbinding!!              // 바인딩 변수 재선언 (매번 null 체크x)

    private lateinit var listAdapter: ScholarshipRecyclerViewAdapter
    private lateinit var incomeListAdapter: ScholarshipRecyclerViewAdapter
    private lateinit var semesterListAdapter: ScholarshipRecyclerViewAdapter
    private lateinit var preClassListAdapter: ScholarshipRecyclerViewAdapter
    private lateinit var preScoreListAdapter: ScholarshipRecyclerViewAdapter

    private lateinit var auth: FirebaseAuth
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
    var blist: MutableList<Scholarship> = arrayListOf()
    var clist: MutableList<Scholarship> = arrayListOf()

    private var bigSize by Delegates.notNull<Int>()
    private var smallSize by Delegates.notNull<Int>()


    private lateinit var mContext: Context //프래그먼트의 정보 받아오는 컨텍스트 선언

    // 조건 저장 변수
    var userIncome: Long = -10  // 학자금 지원구간
    var userDad: Boolean = false    // 가족관계 - 부 여부
    var userMom: Boolean = false    // 가족관계 - 모 여부
    var userChildAll: Long = 0   // 형제 - 전체
    var userChildMe: Long = 0    // 형제 - 자신
    var userSemester: Long = 30   // 이수학기
    var userPreSemClass: Long = 30    // 직전학기 이수학점
    var userPreSemScore: Float = 30f // 직전학기 성적
    var userArea: String? = null    // 거주지
    var userCountry: String? = null // 국적
    var userNationalMerit: Boolean = false  //보훈 보상 대상자 여부
    var userDisabled: Boolean = false   //장애 여부


    var isIncomeSpinnerSelected: Boolean = false // 초기 스피너 이벤트 자동 실행 방지
    var isSemesterSpinnerSelected: Boolean = false // 초기 스피너 이벤트 자동 실행 방지
    var isAreaSpinnerSelected: Boolean = false // 초기 스피너 이벤트 자동 실행 방지

    var changeIncome: Long = -10 // 사용자가 입력한 incomeSpinner
    var changeDad: Boolean = false
    var changeMom: Boolean = false
    var changeChildAll: Long? = null
    var changeChildMe: Long? = null
    var changeSemester: Long = 30 // 사용자가 입력한 semesterSpinner
    var changePreclass: Long = 30 // 사용자가 입력한 semesterSpinner
    var changePreScore: Double = 30.0 // 사용자가 입력한 semesterSpinner
    var changeArea: String? = null // 사용자가 입력한 areaSpinner
    var changeCountry: String? = null // 사용자가 입력한 areaSpinner
    var changeNationMerit: Boolean? = false // 사용자가 입력한 areaSpinner
    var changeDisabled: Boolean? = false // 사용자가 입력한 areaSpinner
    var change: Long? = null // 사용자가 입력한 areaSpinner


    private lateinit var userUniv: String

    lateinit var listSize: String

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

        binding.myPreClass.imeOptions = EditorInfo.IME_ACTION_DONE
        binding.myPreScore.imeOptions = EditorInfo.IME_ACTION_DONE


        initSetCondition() // 초기 조건 데이터 set

        spinnerEvent() // 스피너 이벤트

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

        binding.myPreScore.setOnEditorActionListener { textView, action, event ->
            var handled = false
            if (action == EditorInfo.IME_ACTION_DONE) {
                // 키보드 숨기기
                var imm: InputMethodManager =
                    activity?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)

                var txt = binding.myPreScore.getText().toString()
                changePreScore = txt.toDouble()
                Log.w("에딧 텍스트 값", changePreScore.toString())
                conditionSearch()
                handled = true
            }
            handled
        }


        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) { // 어뎁터 연결
        super.onViewCreated(view, savedInstanceState)

        // Fragment에서 전달받은 list를 넘기면서 ListAdapter 생성
        listAdapter = ScholarshipRecyclerViewAdapter(dataList, mContext)
        incomeListAdapter = ScholarshipRecyclerViewAdapter(incomelist, mContext)
        semesterListAdapter = ScholarshipRecyclerViewAdapter(semesterlist, mContext)
        preClassListAdapter = ScholarshipRecyclerViewAdapter(preclasslist, mContext)
        preScoreListAdapter = ScholarshipRecyclerViewAdapter(prescorelist, mContext)

        myrecyclerView.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        // RecyclerView.adapter에 지정


    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        reset_bt.setOnClickListener {
            initSetCondition()

            //초기화 버튼
            listAdapter.notifyDataSetChanged()

            dataList.clear() // 리스트 재정의

            // 초기 화면 장학금 데이터 가져오기
            user(object : ThridCallback {
                override fun tCallback() {
                    userScholar(object : SecondCallback {
                        override fun sCallback() {
                            allData(object : MyCallback {
                                override fun onCallback(value: MutableList<Scholarship>) {
                                    listSize = dataList.size.toString()
                                    binding.scholarCount.text = listSize
                                }
                            })
                        }
                    })
                }
            })
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

            // 변수값도 모두 초기화
            changeIncome = -10
            changeSemester = 30
            changePreclass = 30
            changePreScore = 30.0

            myrecyclerView.adapter = listAdapter
            listAdapter.notifyDataSetChanged()
            dataList.clear() // 리스트 재정의

            // 초기 화면 장학금 데이터 가져오기
            user(object : ThridCallback {
                override fun tCallback() {
                    userScholar(object : SecondCallback {
                        override fun sCallback() {
                            allData(object : MyCallback {
                                override fun onCallback(value: MutableList<Scholarship>) {
                                    listSize = dataList.size.toString()
                                    binding.scholarCount.text = listSize
                                }
                            })
                        }
                    })
                }
            })

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
        userIncome = pref.getLong("KEY_USER_INCOME", -10)
        userDad = pref.getBoolean("KEY_USER_DAD", false)
        userMom = pref.getBoolean("KEY_USER_MOM", false)
        userChildAll = pref.getLong("KEY_USER_CHILD_ALL", 0)
        userChildMe = pref.getLong("KEY_USER_CHILD_ME", 0)
        userSemester = pref.getLong("KEY_USER_SEMESTER", 30)
        userPreSemClass = pref.getLong("KEY_USER_PRE_SEM_CLASS", 30)
        userPreSemScore = pref.getFloat("KEY_USER_PRE_SEM_SCORE", 30f)
        userArea = pref.getString("KEY_USER_AREA", null)
        userCountry = pref.getString("KEY_USER_COUNTRY", null)
        userNationalMerit = pref.getBoolean("KEY_USER_NATIONAL_MERIT", false)
        userDisabled = pref.getBoolean("KEY_USER_DISABLED", false)
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
            if (userIncome != -10L) {
                binding.incomeSpinner.setSelection(userIncome.toInt() + 1)
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
            if (userSemester >= 0 && userSemester != 30L) {
                binding.semesterSpinner.setSelection(userSemester.toInt())
            }
        }

        //'이수학기' 스피너 선택 리스너
        binding.semesterSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                    TODO("Not yet implemented")
                }

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    when(binding.semesterSpinner.getItemAtPosition(position).toString()) {
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
            requireContext(),
            R.array.local,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // 스피너의 레이아웃 구체화
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // 스피너에 어뎁터 적용
            binding.areaSpinner.adapter = adapter

            // 변수에 저장된 데이터가 있으면 초기값으로 설정
            if (userArea != null) {
                binding.areaSpinner.setSelection(adapter.getPosition(userArea))
            }
        }


        // '추가사항' 설정
        binding.nationalMerit.isChecked = userNationalMerit
        binding.disabled.isChecked = userDisabled
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

            if (userPreSemClass == 30L) {
                binding.myPreClass.setText("")
            } else {
                binding.myPreClass.setText(userPreSemClass.toString())
            }

            if (userPreSemScore == 30f) {
                binding.myPreScore.setText("")
            } else {
                binding.myPreScore.setText(userPreSemScore.toString())

            }
        }



    //allDate에서 list 크기 콜백
    interface MyCallback {
        fun onCallback(value : MutableList<Scholarship>)
    }

    // userScholar에서 all Data 콜백
    interface SecondCallback{
        fun sCallback()
    }

    // user에서 userScholar 콜백
    interface ThridCallback{
        fun tCallback()
    }


    // 유저 대학 장학금 가져오기
    private fun userScholar(secondCallback: SecondCallback){
        thread(start = true){
            // 작업할 문서
            db.collection("Scholarship")
                .document("UnivScholar")
                .collection("ScholarshipList")
                .whereEqualTo("univ", userUniv)
                .get()
                .addOnSuccessListener{ result ->
                    for(document in result){
                        if(document != null){
                            //  필드값 가져오기
                            val paymentType = document["paymentType"].toString()
                            val period = document["period"] as Map<String, Timestamp>
                            val startdate = period.get("startDate")?.toDate()
                            val enddate = period.get("endDate")?.toDate()
                            val startdate2 = period.get("startDate2")?.toDate()
                            val enddate2 = period.get("endDate2")?.toDate()
                            val institution = document["paymentInstitution"].toString()

                            val date = SimpleDateFormat("yyyy-MM-dd") // 날짜 형식으로 변환

                            if((startdate2 == null)&& enddate2 == null){
                                if(startdate == null && enddate == null){
                                    val item = Scholarship(paymentType, document.id, "자동 신청", "", "", "", institution)
                                    dataList.add(item)
                                }
                                else if(startdate == enddate){
                                    val item = Scholarship(paymentType, document.id, "추후 공지", "", "", "", institution)
                                    dataList.add(item)
                                }
                                else{
                                    val item = Scholarship(paymentType, document.id, date.format(startdate!!), date.format(enddate!!), "", "", institution)
                                    dataList.add(item)
                                }
                            }
                            else{
                                val item = Scholarship(paymentType, document.id, date.format(startdate!!), date.format(enddate!!), date.format(startdate2!!), date.format(enddate2!!), institution)
                                dataList.add(item)
                            }

                            listAdapter.submitList(dataList)
                            Log.w("ScholarshipMyscholarFragment", "UnivScholar Data")
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    // 실패할 경우
                    Log.w("ScholarshipMyscholarFragment", "Error getting UnivScholar documents: $exception")
                }
            secondCallback.sCallback()

        }
    }


    // 국가, 교외 장학금 가져오기
    private fun allData(myCallback: MyCallback) {
        thread(start = true){
            // 작업할 문서
            val some = listOf("Nation", "OutScholar")
            for (i in some) {
                db.collection("Scholarship")
                    .document(i)
                    .collection("ScholarshipList")
                    .get()
                    .addOnSuccessListener { result ->
                        for (document in result) {
                            if (document != null) {
                                //  필드값 가져오기
                                val paymentType = document["paymentType"].toString()
                                val period = document["period"] as Map<String, Timestamp>
                                val startdate = period.get("startDate")?.toDate()
                                val enddate = period.get("endDate")?.toDate()
                                val startdate2 = period.get("startDate2")?.toDate()
                                val enddate2 = period.get("endDate2")?.toDate()
                                val institution = document["paymentInstitution"].toString()

                                val date = SimpleDateFormat("yyyy-MM-dd") // 날짜 형식으로 변환

                                if((startdate2 == null)&& enddate2 == null){
                                    if(startdate == null && enddate == null){
                                        val item = Scholarship(paymentType, document.id, "자동 신청", "", "", "", institution)
                                        dataList.add(item)
                                    }
                                    else if(startdate == enddate){
                                        val item = Scholarship(paymentType, document.id, "추후 공지", "", "", "", institution)
                                        dataList.add(item)
                                    }
                                    else{
                                        val item = Scholarship(paymentType, document.id, date.format(startdate!!), date.format(enddate!!), "", "", institution)
                                        dataList.add(item)
                                    }
                                }
                                else{
                                    val item = Scholarship(paymentType, document.id, date.format(startdate!!), date.format(enddate!!), date.format(startdate2!!), date.format(enddate2!!), institution)
                                    dataList.add(item)
                                }
                                myrecyclerView.adapter = listAdapter
                                listAdapter.submitList(dataList)
                                myCallback.onCallback(dataList)
                                Log.w("ScholarshipMyscholarFragment", "all Data")
                            }
                        }
                    }
                    .addOnFailureListener { exception ->
                        // 실패할 경우
                        Log.w("ScholarshipMyscholarFragment", "Error getting UnivScholar documents: $exception")
                    }
            }
        }
    }

    // 유저 정보
    private fun user(thridCallback : ThridCallback){
        // Firebase
        auth = Firebase.auth

        val user = auth.currentUser

        //User's Univ
        if (user != null) {
            thread(start = true){
                db.collection("Users").document(user.uid).get()
                    .addOnSuccessListener{ document ->
                        if (document != null){
                            if(document.getString("univ") != null){
                                userUniv = document.getString("univ")!!
                            }
                        }
                        thridCallback.tCallback()
                    }
            }

        }
    }

    // --------------------------------------------- 조건 ------------------------------------------


    var ref =  db.collectionGroup("ScholarshipList")


    private fun spinnerEvent(){

        // 학자금 지원구간 스피너 이벤트
        binding.incomeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onNothingSelected(p0: AdapterView<*>?) { // 스피너 선택 안했을 때
//                thread (start = true){
//                    ref.get().addOnSuccessListener { document ->
//                        for (snap in document) {
//
//                            setDataShape(incomelist, snap)
//                            Log.w("incomelist", incomelist.toString())
//
//                        }
//                    }
//                }
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
                if (userSemester != 0L && userSemester != 30L){
                    abledPreSemester()
                }



                if(isSemesterSpinnerSelected){

                    when(position) {
                        0 -> {  //이수학기 0일 때 : '직전 학기' Layout 비활성화
                            disabledPreSemester()
                        }
                        else -> {   //이수학기 1 이상일 때 : '직전 학기' Layout 활성화
                            abledPreSemester()
                        }
                    }

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
//                thread (start = true){
//                    ref.get().addOnSuccessListener { document ->
//                        for (snap in document) {
//                            setDataShape(arealist, snap)
//                            Log.w("semesterlist", semesterlist.toString())
//
//                        }
//                    }
//                }
            }


            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (isAreaSpinnerSelected){
                    // 이수학기
                    changeArea = binding.semesterSpinner.getItemIdAtPosition(position).toString()
                    Log.w("선택한 거주지", changeArea.toString())
                }
                isAreaSpinnerSelected = true

            }
        }
    }

    private fun conditionSearch(){
        thread(start = true) {

            incomelist.clear()
            semesterlist.clear()
            preclasslist.clear()
            prescorelist.clear()
            alist.clear()


            //학자금 지원구간
            Log.w("db 가져오기 직전에 유저 학자금지원구간 값", changeSemester.toString())
            ref.whereGreaterThanOrEqualTo("condition.income", changeIncome)
                .get()
                .addOnSuccessListener { document ->
                    for (snap in document) {
                        setDataShape(incomelist, snap)
                    }

                    Log.w("incomelist ", incomelist.toString())

                    //리사이클러뷰 갱신
                    myrecyclerView.adapter = incomeListAdapter
                    incomeListAdapter.notifyDataSetChanged()
                    incomeListAdapter.submitList(incomelist)
                    scholar_count.text = incomelist.size.toString()

                    if (incomelist.isNotEmpty()){
                        // 이수학기
                        Log.w("db 가져오기 직전에 유저 이수학기 값", changeSemester.toString())
                        ref.whereLessThanOrEqualTo("condition.semester", changeSemester)
                            .get()
                            .addOnSuccessListener { document ->
                                for (snap in document){
                                    setDataShape(alist, snap)
                                    Log.w("alist(이수학기)", alist.toString())

                                }

                                Log.w("incomelist 크기", incomelist.size.toString())
                                Log.w("alist 크기", alist.size.toString())

                                for (i in 0 until incomelist.size){
                                    for (j in 0 until alist.size){
                                        if (incomelist[i] == alist[j]){
                                            semesterlist.add(alist[j])
                                        }
                                        Log.w("MyScholar semsterlist", semesterlist.toString())
                                    }
                                }

                                myrecyclerView.adapter = semesterListAdapter
                                semesterListAdapter.notifyDataSetChanged()
                                semesterListAdapter.submitList(semesterlist)
                                scholar_count.text = semesterlist.size.toString()

                                if (semesterlist.isNotEmpty()){
                                    // 직전학기 이수학점

                                    alist.clear()
                                    Log.w("db 가져오기 직전에 유저 직전학기 이수학점 값", changePreclass.toString())
                                    ref.whereLessThanOrEqualTo("condition.preclass", changePreclass)
                                        .get()
                                        .addOnSuccessListener { document ->
                                            for (snap in document){
                                                setDataShape(alist, snap)
                                                Log.w("alist(직전학기 학점)", alist.toString())

                                            }

                                            for (i in 0 until semesterlist.size){
                                                for (j in 0 until alist.size){
                                                    if (semesterlist[i] == alist[j]){
                                                        preclasslist.add(alist[j])
                                                    }
                                                    Log.w("MyScholar preclasslist", preclasslist.toString())
                                                }
                                            }

                                            //리사이클러뷰 갱신
                                            myrecyclerView.adapter = preClassListAdapter
                                            preClassListAdapter.notifyDataSetChanged()
                                            preClassListAdapter.submitList(preclasslist)
                                            scholar_count.text = preclasslist.size.toString()

                                            if(preclasslist.isNotEmpty()){
                                                // 직전학기 성적
                                                alist.clear()
                                                Log.w("db 가져오기 직전에 유저 직전학기 성적 값", changePreScore.toString())

                                                ref.whereLessThanOrEqualTo("condition.prescore", changePreScore)
                                                    .get()
                                                    .addOnSuccessListener { document ->
                                                        for (snap in document){
                                                            setDataShape(alist, snap)
                                                            Log.w("alist(직전학기 성적)", alist.toString())
                                                        }

                                                        for (i in 0 until preclasslist.size){
                                                            for (j in 0 until alist.size){
                                                                if (preclasslist[i] == alist[j]){
                                                                    prescorelist.add(alist[j])
                                                                }
                                                                Log.w("MyScholar prescorelist", prescorelist.toString())
                                                            }
                                                        }

                                                        //리사이클러뷰 갱신
                                                        myrecyclerView.adapter = preScoreListAdapter
                                                        preScoreListAdapter.notifyDataSetChanged()
                                                        preScoreListAdapter.submitList(preclasslist)
                                                        scholar_count.text = prescorelist.size.toString()

                                                        if (prescorelist.isNotEmpty()){

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
                            .addOnFailureListener { exception ->
                                Log.w("ScholarshipMyscholarFragment - semesterScholar", "Error getting data: $exception")

                            }

                    }


                }
                .addOnFailureListener { exception ->
                    // 실패할 경우
                    Log.w("ScholarshipMyscholarFragment - incomeScholar", "Error getting data: $exception")
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

        Log.w("ScholarshipMyscholarFragment", "all Data")
    }

    // 학자금 지원구간
    private fun income( incomen : Long) {
        alist.clear()

        thread(start = true) {



                ref.whereGreaterThanOrEqualTo("condition.income", incomen)
                    .get()
                    .addOnSuccessListener { document ->
                        for (snap in document) {

                            setDataShape(alist, snap)

                        }

                        Log.w("alist ", alist.toString())

                    }
                    .addOnFailureListener { exception ->
                        // 실패할 경우
                        Log.w("ScholarshipMyscholarFragment - incomeScholar", "Error getting data: $exception")
                    }

        }
    }

    // 이수 학기
    private fun semester(semestern : Long) {

        blist.clear()

        thread (start = true){
            if(alist.isNullOrEmpty() && incomelist.isNotEmpty()){ // 학자금 선택 안했을 때

               Log.w("semesterlist", semesterlist.toString())


            }
        }
    }

    // 직전학기 이수학점
    private fun preclass(){
        thread(start = true){
            var preclass = binding.myPreClass.getText().toString()

            if (preclass.isNotEmpty()){
                ref.whereLessThanOrEqualTo("condition.preclass", preclass)
                    .get()
                    .addOnSuccessListener { document ->
                        for (snap in document){

                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.w("ScholarshipMyscholarFragment - preClassScholar", "Error getting data: $exception")

                    }
            }
        }


    }

    // 직전학기 성적
    private fun score() {
        thread(start = true){
            var score = binding.myPreScore.getText().toString()

            if (score.isNotEmpty()){
                ref.whereLessThanOrEqualTo("condition.prescore", score)
                    .get()
                    .addOnSuccessListener { document ->

                    }
                    .addOnFailureListener { exception ->
                        Log.w("ScholarshipMyscholarFragment - scoreScholar", "Error getting data: $exception")

                    }
            }
        }
    }

    // 거주지
    private fun area(){
        thread(start = true){
    // 거주지 배열로 가져오기

        }


    }

    private fun nationMerit(){

        thread (start = true){
            if(binding.nationalMerit.isChecked) {    //체크 박스가 체크 된 경우

                ref.whereEqualTo("condition.merit", true)
                    .get()
                    .addOnSuccessListener { document ->
                        for(snap in document){
                            setDataShape(nationalMeritlist, snap)

                        }
                    }
            }

            else{

            }


        }
    }

    private fun disabled(){
        thread(start = true) {
            if(binding.disabled.isChecked) {    //체크 박스가 체크 된 경우

                ref.whereEqualTo("condition.disabled", true)
                    .get()
                    .addOnSuccessListener { document ->
                        for(snap in document){
                            setDataShape(disabledlist, snap)

                        }
                    }
            }

            else{

            }

        }
    }






}


