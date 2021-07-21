package com.cookandroid.scholarshiplike

import java.util.*

data class Post (val title: String = "", val category : String = "", val contents : String = "")
data class tmpScholarship(val title: String, val text: String, val startdate: Date?, val enddate: Date?)
data class Alarm(val category: String, val title: String, val date: String)
data class Schedule(val name: String, val start:String, val end:String)

// Search Activity - Scholarship
data class SearchScholarship(val title: String, val period_start: String, val period_end : String,  val institution: String)


data class Scholarship(val title: String, val startdate: String, val enddate: String, val institution: String)

