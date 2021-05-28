package com.cookandroid.scholarshiplike

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_scholarship_all_scholar.*

class ScholarshipAllscholarFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //setExpandableList()
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_scholarship_all_scholar, container, false)
    }

    //ExpandableListView 설정
    private fun setExpandableList() {
        val parentList = mutableListOf("국가 장학금", "교외 장학금", "교내 장학금")
        val childList = mutableListOf(
            mutableListOf(),
            mutableListOf("서울", "강원"),
            mutableListOf() )

        val expandableAdapter = ScholarshipExpandableListviewAdapter(context, parentList, childList)
        expandableList.setAdapter(expandableAdapter)
        expandableList.setOnGroupClickListener {
                parent, v, groupPosition, id ->
            /* todo : parent 클릭 이벤트 설정 */
            false
        }
        expandableList.setOnChildClickListener {
                parent, v, groupPosition, childPosition, id ->
            /* todo : child 클릭 이벤트 설정 */
            false
        }
    }


}
