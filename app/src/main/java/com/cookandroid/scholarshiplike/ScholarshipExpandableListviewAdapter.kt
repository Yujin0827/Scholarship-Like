package com.cookandroid.scholarshiplike

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import kotlinx.android.synthetic.main.scholarship_expandlist_child.view.*
import kotlinx.android.synthetic.main.scholarship_expandlist_parent.view.*


class ScholarshipExpandableListviewAdapter(
    private val context: Context?,
    private val parents: MutableList<String>,
    private val childList: MutableList<MutableList<String>>) : BaseExpandableListAdapter(), Parcelable {


    constructor(parcel: Parcel) : this(
        TODO("context"),
        TODO("parents"),
        TODO("childList")
    ) {
    }

    override fun getGroupCount() = parents.size

    override fun getChildrenCount (parent : Int) = childList[parent].size

    override fun getGroup(parent: Int) = parents[parent]

    override fun getChild(parent: Int, child: Int): String = childList[parent][child]

    override fun getGroupId(parent: Int) = parent.toLong()

    override fun getChildId(parent: Int, child: Int) = child.toLong()

    override fun hasStableIds() = false

    override fun isChildSelectable(groupPosition: Int, childPosition: Int) = true

   /* 부모 계층 레이아웃 설정 */

    override fun getGroupView(
        parent: Int, isExpanded: Boolean, convertView: View?, parentview: ViewGroup
    ): View {
        val inflater = context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val parentView = inflater.inflate(R.layout.scholarship_expandlist_parent, parentview, false)

        parentView.expandlist_parent.text = parents[parent]


        setArrow(parent, parentView, isExpanded)
        return parentView
    }

    /* 자식 계층 레이아웃 설정 */
    override fun getChildView( parent: Int, child: Int, isLastChild: Boolean, convertView: View?, parentview: ViewGroup
    ): View {
        val inflater = context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val childView = inflater.inflate(R.layout.scholarship_expandlist_child, parentview, false)

        childView.expandlist_child.text = getChild(parent, child)
        return childView
    }

    /* 닫힘, 열림 표시해주는 화살표 설정 */
    private fun setArrow(parentPosition: Int, parentView: View, isExpanded: Boolean) {
        /* 0번째 부모는 자식이 없으므로 화살표 설정해주지 않음 */
        if (parentPosition != 0) {
            if (isExpanded) parentView.scholar_image.setImageResource(R.drawable.img_scholarshiplist)
            else parentView.scholar_image.setImageResource(R.drawable.img_scholarshiplist)
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ScholarshipExpandableListviewAdapter> {
        override fun createFromParcel(parcel: Parcel): ScholarshipExpandableListviewAdapter {
            return ScholarshipExpandableListviewAdapter(parcel)
        }

        override fun newArray(size: Int): Array<ScholarshipExpandableListviewAdapter?> {
            return arrayOfNulls(size)
        }
    }


}

