package com.cookandroid.scholarshiplike

import android.os.Bundle
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ExpandableListAdapter
import android.widget.ExpandableListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_scholarship_all_scholar.*

class ScholarshipAllscholarFragment : Fragment() {

    val head : MutableList<String> = ArrayList() // expandableList의 부모 리스트
    val body : MutableList<MutableList<String>> = ArrayList() // expandableList의 자식 리스트

    lateinit var listAdapter: ScholarshipExpandableListAdapter // expandableList 어댑터 선언

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_scholarship_all_scholar, container, false)

        val koreaScholar : MutableList<String> = ArrayList()

        val outScholar : MutableList<String> = ArrayList()
        outScholar.add("춘천시")


        val univScholar : MutableList<String> = ArrayList()

        head.add("서울")
        head.add("강원")


        body.add(koreaScholar)
        body.add(outScholar)
        body.add(univScholar)

        val outScholarBt : Button = view.findViewById(R.id.outScholarBt) // 교외 장학금 버튼


        outScholarBt.setOnClickListener{ // 교외 장학금 클릭 시 레이아웃 표현
            if(expand_layout.visibility == View.GONE){
                expand_layout.visibility = View.VISIBLE
            }
            else{
                expand_layout.visibility = View.GONE
            }


        }



    // Inflate the layout for this fragment
        return view


    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // expandableList 어탭터 연결
        listAdapter = ScholarshipExpandableListAdapter(this, head, body)
        expandableList.setAdapter(listAdapter)
    }






}
