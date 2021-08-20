package com.cookandroid.scholarshiplike


import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import java.text.SimpleDateFormat
import kotlin.concurrent.thread
import kotlin.properties.Delegates

class ScholarshipMyscholarFragment : Fragment() {

    private var mbinding: FragmentScholarshipMyScholarBinding? = null   // 바인딩 객체
    private val binding get() = mbinding!!              // 바인딩 변수 재선언 (매번 null 체크x)

    private lateinit var listAdapter: ScholarshipRecyclerViewAdapter
    private lateinit var listAdapterA: ScholarshipRecyclerViewAdapter
    private lateinit var auth: FirebaseAuth
    private var  db = Firebase.firestore
    var dataList: MutableList<Scholarship> = arrayListOf()
    var alist: MutableList<Scholarship> = arrayListOf()
    var incomelist: MutableList<Scholarship> = arrayListOf()
    var blist: MutableList<String> = arrayListOf()
    private var asize by Delegates.notNull<Int>()
    private var bsize by Delegates.notNull<Int>()
    private lateinit var mContext : Context //프래그먼트의 정보 받아오는 컨텍스트 선언

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

    var isSpinnerSelected : Boolean = false

    var incomen : Long? = null // 사용자가 입력한 incomeSpinner
    var semestern : Long? = null // 사용자가 입력한 semesterSpinner
    var check : Boolean = false // 조건 선택 안됨


    private lateinit var userUniv: String

    lateinit var listSize : String

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = requireActivity()


        // 초기 화면 장학금 데이터 가져오기
        user(object  : ThridCallback{
            override fun tCallback() {
                userScholar(object : SecondCallback{
                    override fun sCallback(){
                        allData(object : MyCallback{
                            override fun onCallback(value: MutableList<Scholarship>) {
                                listSize = dataList.size.toString()
                                binding.scholarCount.text = listSize

                            } })
                    }
                })
            }
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment.
        mbinding = FragmentScholarshipMyScholarBinding.inflate(inflater, container, false)
        val view = binding.root

        initSetCondition() // 초기 조건 데이터 set

        spinnerEvent() // 스피너 이벤트

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) { // 어뎁터 연결
        super.onViewCreated(view, savedInstanceState)

        // Fragment에서 전달받은 list를 넘기면서 ListAdapter 생성
        listAdapter = ScholarshipRecyclerViewAdapter(dataList,mContext) // 전체 장학금
        listAdapterA = ScholarshipRecyclerViewAdapter(incomelist,mContext) // 전체 장학금
        myrecyclerView.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        // RecyclerView.adapter에 지정
        myrecyclerView.adapter = listAdapter


    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        reset_bt.setOnClickListener { initSetCondition() } //초기화 버튼



    }

    override fun onStart() { // 사용자에게 보여지기 전 호출되는 함수
        super.onStart()





    }

    override fun onResume() {
        super.onResume()



        // 아래로 스와이프 새로고침
        binding.swipeRefreshFragmentScholarship.setOnRefreshListener {
            Handler().postDelayed({ // 아래로 스와이핑 이후 1초 후에 리플래쉬 아이콘 없애기
                if (binding.swipeRefreshFragmentScholarship.isRefreshing)
                    binding.swipeRefreshFragmentScholarship.isRefreshing = false
            }, 1000)

            myrecyclerView.adapter = listAdapter
            listAdapter.notifyDataSetChanged()
            dataList.clear() // 리스트 재정의

            // 초기 화면 장학금 데이터 가져오기
            user(object  : ThridCallback{
                override fun tCallback() {
                    userScholar(object : SecondCallback{
                        override fun sCallback(){
                            allData(object : MyCallback{
                                override fun onCallback(value: MutableList<Scholarship>) {
                                    listSize = dataList.size.toString()
                                    binding.scholarCount.text = listSize
                                } })
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
        val pref : SharedPreferences = requireActivity().getSharedPreferences("SharedData", Context.MODE_PRIVATE)

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
            requireContext(),
            array.incomeList,
            android.R.layout.simple_spinner_dropdown_item
        ).also { adapter ->
            // 스피너에 어뎁터 적용
            binding.incomeSpinner.adapter = adapter

            // 스피너의 레이아웃 구체화
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)


            // 변수에 저장된 데이터가 있으면 초기값으로 설정
            if (userIncome != null) {
                binding.incomeSpinner.setSelection(adapter.getPosition(userIncome))
            }
        }

        //'이수 학기' 스피너 설정
        ArrayAdapter.createFromResource(
            requireContext(),
            array.semester,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // 스피너의 레이아웃 구체화
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // 스피너에 어뎁터 적용
            binding.semesterSpinner.adapter = adapter

            // 변수에 저장된 데이터가 있으면 초기값으로 설정
            if (userSemester!! >= 0) {
                binding.semesterSpinner.setSelection(userSemester!!)
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
            array.local,
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
        binding.nationalMerit.isChecked = userNationalMerit!!
        binding.disabled.isChecked = userDisabled!!
    }

    //'직전학기' 레이아웃 비활성화 함수
    fun disabledPreSemester() {
        binding.myPreGradeText.alpha = 0.3F
        binding.myPreScoreText.alpha = 0.3F
        binding.myPreGrade.isEnabled = false
        binding.myPreScore.isEnabled = false

        binding.myPreGrade.setText("")
        binding.myPreScore.setText("")
    }

    //'직전학기' 레이아웃 활성화 함수
    fun abledPreSemester() {
        binding.myPreGradeText.alpha = 1F
        binding.myPreScoreText.alpha = 1F
        binding.myPreGrade.isEnabled = true
        binding.myPreScore.isEnabled = true

        if (userPreSemClass == -5) {
            binding.myPreGrade.setText("")
        }
        else {
            binding.myPreGrade.setText(userPreSemClass.toString())
        }

        if (userPreSemScore == -5.0f) {
            binding.myPreScore.setText("")
        }
        else {
            binding.myPreScore.setText(userPreSemScore.toString())
        }
    }

    // 유저 조건 데이터 변수 저장
    private fun setUserConditioinData() {


        // 학자금 지원 구간
        userIncome = binding.incomeSpinner.getItemAtPosition(binding.incomeSpinner.selectedItemPosition).toString()

        // 이수학기
        userSemester = binding.semesterSpinner.getItemAtPosition(binding.semesterSpinner.selectedItemPosition).toString().toInt()

        // 직전학기 이수학점 & 성적
        if (userSemester!! >= 1) {
            val stringClass = binding.myPreGrade.text.toString()
            val stringScore = binding.myPreScore.text.toString()

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

        // 보훈 보상 대상자
        userNationalMerit = binding.nationalMerit.isChecked

        // 장애 여부
        userDisabled = binding.disabled.isChecked
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



    private fun spinnerEvent(){

        // 학자금 지원구간 스피너 이벤트
        binding.incomeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if(isSpinnerSelected){
                    // 학자금 지원 구간
                    incomen = binding.incomeSpinner.getItemIdAtPosition(position) - 1
                    Log.w("선택한 학자금 지원구간", incomen.toString())
                    check = true
                    income(incomen!!)
                }
                isSpinnerSelected = true


            }
        }

        //이수학기 스피너 이벤트
        binding.semesterSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // 학자금 지원 구간
                semestern = binding.semesterSpinner.getItemIdAtPosition(position)
                Log.w("선택한 이수학기", semestern.toString())
                semester(semestern!!)

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
        myrecyclerView.adapter = listAdapterA
        listAdapterA.submitList(list)
        scholar_count.text = list.size.toString()

        Log.w("ScholarshipMyscholarFragment", "all Data")


    }

    var ref =  db.collectionGroup("ScholarshipList")

    private fun income( incomen : Long) {
        incomelist.clear()

        thread(start = true){

            if (check == true){ // 조건이 선택되었을 때


                ref.whereGreaterThanOrEqualTo("condition.income", incomen )
                    .get()
                    .addOnSuccessListener { document ->
                        for (snap in document) {

                            setDataShape(incomelist, snap)

                        }
                        if (incomelist.isEmpty()){
                            dataList.clear()
                            listAdapterA.notifyDataSetChanged()
                            listAdapter.notifyDataSetChanged()
                            scholar_count.text = incomelist.size.toString()
                        }
                        Log.w("dataList ", incomelist.toString())

                    }
                    .addOnFailureListener { exception ->
                        // 실패할 경우
                        Log.w("ScholarshipMyscholarFragment - incomeScholar", "Error getting data: $exception")
                    }

            }
            if(check == false){ // 모든 데이터 추가

                user(object  : ThridCallback{
                    override fun tCallback() {
                        userScholar(object : SecondCallback{
                            override fun sCallback(){
                                allData(object : MyCallback{
                                    override fun onCallback(value: MutableList<Scholarship>) {
//                                    listSize = dataList.size.toString()
//                                    binding.scholarCount.text = listSize
                                    } })
                            }
                        })
                    }
                })
            }
        }

    }

    private fun semester(semestern : Long) {

        blist.clear()

        ref.whereLessThanOrEqualTo("condition.semester", semestern)
            .get()
            .addOnSuccessListener { document ->

                if (alist.size == 0){
                    Log.w("alist size1", alist.size.toString())
                    for (snap in document) {
                        var item = snap.id
                        blist.add(item)
                    }

                }
                else{
                    for (snap in document){
                        Log.w("alist size2", alist.size.toString())
                        var item = snap.id
//                        for (i in 0 until asize) {
//                            if (alist[i] == item) { blist.add(item) }
//                        }
                    }
                }

                bsize = blist.size
                Log.w("blist ", blist.toString())
            }
            .addOnFailureListener { exception ->
                // 실패할 경우
                Log.w("ScholarshipMyscholarFragment - semesterScholar", "Error getting data: $exception")
            }
    }

    private fun score(scoren: Long) {

        blist.clear()

        ref.whereLessThanOrEqualTo("condition.prescore", scoren)
            .get()
            .addOnSuccessListener { document ->
                if (alist.isEmpty()){
                    for (snap in document) {
                        var item = snap.id
                        blist.add(item)
                    }

                }

                for (snap in document){
                    var item = snap.id
//                    for (i in 0 until asize) {
//                        if (alist[i] == item) { blist.add(item) }
//                    }
                }

                bsize = blist.size
                Log.w("ㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁ", blist.toString())
            }
            .addOnFailureListener { exception ->
                // 실패할 경우
                Log.w("ScholarshipMyscholarFragment - semesterScholar", "Error getting data: $exception")
            }
    }




}


