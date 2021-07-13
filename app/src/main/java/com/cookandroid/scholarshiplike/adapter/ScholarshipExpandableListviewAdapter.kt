package com.cookandroid.scholarshiplike.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.cookandroid.scholarshiplike.R

class ScholarshipExpandableLisviewtAdapter (
    var fragment : Fragment,
    var head : MutableList<String>,
    var body : MutableList<MutableList<String>>
): BaseExpandableListAdapter() {
    override fun getGroup(groupPosition: Int): String {
        return head[groupPosition]
    }

    override fun isChildSelectable(groupPosition: Int, childPosition : Int): Boolean {
        return true
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun getGroupView(groupPosition: Int, isExpanded : Boolean, convertView: View?, parent : ViewGroup?): View? {
        var convertView = convertView
        if(convertView == null){
            val inflater = fragment.activity?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = inflater.inflate(R.layout.scholarship_expandlist_parent, null)
        }
        val title = convertView?.findViewById<TextView>(R.id.expandlist_parent)
        title?.text = getGroup(groupPosition)
        return convertView
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return body[groupPosition].size
    }

    override fun getChild(groupPosition: Int, childPosition: Int): String {
        return body[groupPosition][childPosition]
    }

    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild : Boolean, convertView: View?, parent: ViewGroup?): View?{
        var convertView = convertView
        if(convertView == null){
            val inflater = fragment.activity?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = inflater.inflate(R.layout.scholarship_expandlist_child, null)

        }
        val title = convertView?.findViewById<TextView>(R.id.expandlist_child)
        title?.text = getChild(groupPosition, childPosition)


        return convertView
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    override fun getGroupCount(): Int {
        return head.size
    }

}