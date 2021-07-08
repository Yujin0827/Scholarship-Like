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
import com.cookandroid.scholarshiplike.adapter.ScholarshipRecyclerViewAdapter
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_scholarship_my_scholar.*

class ScholarshipMyscholarFragment : Fragment() {

    private lateinit var listAdapter: ScholarshipRecyclerViewAdapter
    private var db = Firebase.firestore
    var dataList: MutableList<Scholarship> = arrayListOf()
    private lateinit var mContext : Context //프래그먼트의 정보 받아오는 컨텍스트 선언

    private lateinit var myIncome : Spinner
    private lateinit var mySemester : Spinner
    private lateinit var myArea : Spinner
    private lateinit var myArea_datails : Spinner





    override fun onAttach(context: Context) {
        super.onAttach(context)

        mContext = requireActivity()

        val sRef = db.collection("장학금")
            .document("교내").collection("강원")
            .document("강원대").collection("학과")

        sRef // 작업할 문서
            .get()      // 문서 가져오기
            .addOnSuccessListener { result ->
                for (document in result) {  // 가져온 문서들은 result에 들어감
                    val item = Scholarship(document.id, "", "", false)
                    dataList.add(item)
                }
                listAdapter.submitList(dataList)
                Log.w("MainActivity", "Error aaaaaaa: ")

            }
            .addOnFailureListener { exception ->
                // 실패할 경우
                Log.w("MainActivity", "Error getting documents: $exception")
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
        listAdapter =
            ScholarshipRecyclerViewAdapter(
                dataList,
                mContext
            )
        myrecyclerView.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        // RecyclerView.adapter에 지정
        myrecyclerView.adapter = listAdapter

        myIncome = view.findViewById(R.id.incomeSpinner)
        mySemester = view.findViewById(R.id.semesterSpinner)
        myArea = view.findViewById(R.id.areaSpinner)
        myArea_datails = view.findViewById(R.id.inAreaSpinner)

        setSpinner()

    }

    //스피너 초기화 & 리스너
    fun setSpinner() {
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

        //'거주지' 스피너 선택 리스너
        myArea.onItemSelectedListener =
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
                    //서울, 강원만 구현.
                    //나머지는 추후에.
                    setSpinnerMyAreaDatails(areaSpinner.getItemAtPosition(position).toString())
                }

                private fun setSpinnerMyAreaDatails(area : String) {
                    when (area) {
                        "서울" -> {
                            ArrayAdapter.createFromResource(
                                context!!,
                                R.array.local_seoul,
                                android.R.layout.simple_spinner_item
                            ).also { adapter ->
                                // 스피너의 레이아웃 구체화
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                // 스피너에 어뎁터 적용
                                myArea_datails.adapter = adapter
                            }
                        }
                        "강원도" -> {
                            ArrayAdapter.createFromResource(
                                context!!,
                                R.array.local_gangwon,
                                android.R.layout.simple_spinner_item
                            ).also { adapter ->
                                // 스피너의 레이아웃 구체화
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                // 스피너에 어뎁터 적용
                                myArea_datails.adapter = adapter
                            }
                        }
                        else -> {
                            ArrayAdapter.createFromResource(
                                context!!,
                                R.array.local_temp,
                                android.R.layout.simple_spinner_item
                            ).also { adapter ->
                                // 스피너의 레이아웃 구체화
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                // 스피너에 어뎁터 적용
                                myArea_datails.adapter = adapter
                            }
                        }
                    }

                }
            }

    }
}
