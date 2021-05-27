package com.cookandroid.scholarshiplike
/*
import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter

class ScholarshipExpandableListAdapter(
    private val context: Context,
    private val parents: MutableList<String>,
    private val childList: MutableList<String>) : BaseExpandableListAdapter(), Parcelable {



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
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val parentView = inflater.inflate(R.layout.menu_parent, parentview, false)

        parentView.tv_list_title.text = parents[parent]


        setArrow(parent, parentView, isExpanded)
        return parentView
    }

    /* 자식 계층 레이아웃 설정 */
    override fun getChildView( parent: Int, child: Int, isLastChild: Boolean, convertView: View?, parentview: ViewGroup
    ): View {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val childView = inflater.inflate(R.layout.menu_child, parentview, false)

        childView.tv_child_title.text = getChild(parent, child)
        return childView
    }

    /* 닫힘, 열림 표시해주는 화살표 설정 */
    private fun setArrow(parentPosition: Int, parentView: View, isExpanded: Boolean) {
        /* 0번째 부모는 자식이 없으므로 화살표 설정해주지 않음 */
        if (parentPosition != 0) {
            if (isExpanded) parentView.iv_arrow_drop.setImageResource(R.drawable.ic_arrow_drop_up)
            else parentView.iv_arrow_drop.setImageResource(R.drawable.ic_arrow_drop_down)
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ScholarshipViewPageAdapter> {
        override fun createFromParcel(parcel: Parcel): ScholarshipViewPageAdapter {
            return ScholarshipViewPageAdapter(parcel)
        }

        override fun newArray(size: Int): Array<ScholarshipViewPageAdapter?> {
            return arrayOfNulls(size)
        }
    }


}

 */