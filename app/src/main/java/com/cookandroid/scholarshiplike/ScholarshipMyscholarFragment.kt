package com.cookandroid.scholarshiplike

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_profile_my_con_change.*
import kotlinx.android.synthetic.main.fragment_scholarship_my_scholar.*
import java.text.SimpleDateFormat

class ScholarshipMyscholarFragment : Fragment() {
    private val TAG = javaClass.simpleName

    private lateinit var listAdapter: ScholarshipRecyclerViewAdapter
    private var db = Firebase.firestore
    var dataList: MutableList<Scholarship> = arrayListOf()
    private lateinit var mContext : Context //프래그먼트의 정보 받아오는 컨텍스트 선언

    private lateinit var myIncome : Spinner
    private lateinit var mySemester : Spinner
    private lateinit var myArea : Spinner


    override fun onAttach(context: Context) {
        super.onAttach(context)

        mContext = requireActivity()

        // 작업할 문서
        db.collection("Scholarship")
            .document("Nation")
            .collection("소득연계형")
            .get()      // 문서 가져오기
            .addOnSuccessListener { result ->

                for (document in result) {  // 가져온 문서들은 result에 들어감

                    //필드값 가져오기
                    val paymentType = document["paymentType"].toString()
                    val period = document["period"] as Map<String, Timestamp>
                    val startdate = period.get("startDate")?.toDate()
                    val enddate = period.get("endDate")?.toDate()
                    val institution = document["paymentInstitution"].toString()

                    val date = SimpleDateFormat("yyyy-MM-dd") // 날짜 형식으로 변환

                    if(startdate == null && enddate == null){
                        val item = Scholarship(paymentType, document.id, date.format(startdate!!), date.format(enddate!!), institution)
                        dataList.add(item)
                    }
                    else{
                        val item = Scholarship(paymentType, document.id, date.format(startdate!!), date.format(enddate!!), institution)
                        dataList.add(item)
                    }
                }
                listAdapter.submitList(dataList)
                Log.w("ScholarshipAllscholarFragment", "all Data")
            }
            .addOnFailureListener { exception ->
                // 실패할 경우
                Log.w("ScholarshipAllscholarFragment", "Error getting UnivScholar documents: $exception")
            }
        db.collection("Scholarship")
            .document("UnivScholar")
            .collection("한림대학교")
            .get()      // 문서 가져오기
            .addOnSuccessListener { result ->

                for (document in result) {  // 가져온 문서들은 result에 들어감

                    //필드값 가져오기
                    val paymentType = document["paymentType"].toString()
                    val period = document["period"] as Map<String, Timestamp>
                    val startdate = period.get("startDate")?.toDate()
                    val enddate = period.get("endDate")?.toDate()
                    val institution = document["paymentInstitution"] as String

                    val date = SimpleDateFormat("yyyy-MM-dd") // 날짜 형식으로 변환

                    if(startdate == null && enddate == null){
                        val item = Scholarship(paymentType, document.id, date.format(startdate!!), date.format(enddate!!), institution)
                        dataList.add(item)
                    }
                    else{
                        val item = Scholarship(paymentType, document.id, date.format(startdate!!), date.format(enddate!!), institution)
                        dataList.add(item)
                    }
                }
                listAdapter.submitList(dataList)
                Log.w("ScholarshipAllscholarFragment", "all Data")
            }
            .addOnFailureListener { exception ->
                // 실패할 경우
                Log.w("ScholarshipAllscholarFragment", "Error getting UnivScholar documents: $exception")
            }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_scholarship_my_scholar, container, false)


        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Fragment에서 전달받은 list를 넘기면서 ListAdapter 생성
        listAdapter = ScholarshipRecyclerViewAdapter(dataList,mContext)
        myrecyclerView.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        // RecyclerView.adapter에 지정
        myrecyclerView.adapter = listAdapter

        myIncome = view.findViewById(R.id.incomeSpinner)
        mySemester = view.findViewById(R.id.semesterSpinner)
        myArea = view.findViewById(R.id.areaSpinner)


        setSpinner()

    }

    //스피너 초기화 & 리스너
    private fun setSpinner() {
        //'학자금 지원구간' 스피너의 ArrayAdapter
        ArrayAdapter.createFromResource(
            context!!,
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
            context!!,
            R.array.semester,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // 스피너의 레이아웃 구체화
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // 스피너에 어뎁터 적용
            semesterSpinner.adapter = adapter
        }

        //'거주지' 스피너의 ArrayAdapter
        ArrayAdapter.createFromResource(
            context!!,
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

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    when (mySemester.getItemAtPosition(position).toString()) {
                        "0" -> {  //이수학기 0일 때 : '직전 학기' Layout 비활성화
                            disabledPreSemester()
                        }
                        else -> {   //이수학기 1 이상일 때 : '직전 학기' Layout 활성화
                            abledPreSemester()
                        }
                    }
                }

                private fun abledPreSemester() {
                    view?.findViewById<TextView>(R.id.myPreText)?.alpha = 1F
                    view?.findViewById<TextView>(R.id.myPreGradeText)?.alpha = 1F
                    view?.findViewById<TextView>(R.id.myPreScoreText)?.alpha = 1F
                    view?.findViewById<EditText>(R.id.myPreGrade)?.isEnabled = true
                    view?.findViewById<EditText>(R.id.myPreScore)?.isEnabled = true
                }

                private fun disabledPreSemester() {
                    view?.findViewById<TextView>(R.id.myPreText)?.alpha = 0.3F
                    view?.findViewById<TextView>(R.id.myPreGradeText)?.alpha = 0.3F
                    view?.findViewById<TextView>(R.id.myPreScoreText)?.alpha = 0.3F
                    view?.findViewById<EditText>(R.id.myPreGrade)?.isEnabled = false
                    view?.findViewById<EditText>(R.id.myPreScore)?.isEnabled = false
                }
            }



    }
}
