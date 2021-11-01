package com.cookandroid.scholarshiplike

import android.util.Log
import java.time.Month
import java.util.*
import kotlin.collections.ArrayList

class HomeCalendarDateCalculate(date: Date, pageindex: Int) {

    companion object {
        const val DAYS_OF_WEEK = 7
        const val LOW_OF_CALENDAR = 6
    }

    val calendar = Calendar.getInstance()

    var plusvalue = 1
    var currentMonth = pageindex+calendar.get(Calendar.MONTH)+plusvalue
    var prevTail = 0
    var nextHead = 0
    var currentMaxDate = 0

    var dateList = arrayListOf<Int>()
    var intList = arrayListOf<Int>() //달+날짜 int형

    var dateList_2D: ArrayList<List<Int>> = arrayListOf()
    var intList_2D: ArrayList<List<Int>> = arrayListOf()

    var flag:Boolean = false

    init {
        calendar.time = date
        if (currentMonth==0) {
            currentMonth=12
            plusvalue += 2
        }
    }

    fun initBaseCalendar() {
        makeMonthDate()
    }

    private fun makeMonthDate() {

        dateList.clear()

        calendar.set(Calendar.DATE, 1)

        currentMaxDate = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

        prevTail = calendar.get(Calendar.DAY_OF_WEEK) - 1

        makePrevTail(calendar.clone() as Calendar)
        makeCurrentMonth(calendar)

        nextHead = LOW_OF_CALENDAR * DAYS_OF_WEEK - (prevTail + currentMaxDate)
        makeNextHead()
    }

    private fun makePrevTail(calendar: Calendar) {
        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1)
        val maxDate = calendar.getActualMaximum(Calendar.DATE)
        var maxOffsetDate = maxDate - prevTail

        for (i in 1..prevTail) dateList.add(++maxOffsetDate)
    }

    private fun makeCurrentMonth(calendar: Calendar) {
        for (i in 1..calendar.getActualMaximum(Calendar.DATE))
            dateList.add(i)
    }

    private fun makeNextHead() {
        var date = 1

        for (i in 1..nextHead) dateList.add(date++)
        toIntList()
    }

    private fun toIntList() {
        for (i in 0..41) {
            if (i == prevTail) flag = true

            if (flag) intList.add(dateList[i] + 100 * currentMonth)
            else if (i < prevTail) {
                if (currentMonth == 1) intList.add(dateList[i] + 1200)
                else intList.add(dateList[i] + 100 * (currentMonth - 1))
            } else {
                if (currentMonth == 12) intList.add(dateList[i] + 100)
                else intList.add(dateList[i] + 100 * (currentMonth + 1))
            }

            if (flag && dateList[i] == currentMaxDate) flag = false
        }

        dateList_2D = to2D(dateList)
        intList_2D = to2D(intList)
        Log.e("intList", "$intList")
    }

    private fun to2D(convertlist: MutableList<Int>): ArrayList<List<Int>> {
        var list: MutableList<Int> = arrayListOf()
        var list_2D: ArrayList<List<Int>> = arrayListOf()

        var i=0
        for(item in convertlist) {
            if (i%7==0 && i!=0) {
                list_2D.add(list.toList())
                list.clear()
            }
            list.add(item)
            i++
        }
        list_2D.add(list.toList())

        return list_2D
    }
}