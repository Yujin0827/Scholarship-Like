package com.cookandroid.scholarshiplike

import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ExpandableListView
import android.widget.SimpleExpandableListAdapter
import androidx.fragment.app.Fragment


class ScholarshipMyscholarFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {





        // Inflate the layout for this fragment
        return inflater.inflate(com.cookandroid.scholarshiplike.R.layout.fragment_scholarship_my_scholar, container, false)
    }

}
