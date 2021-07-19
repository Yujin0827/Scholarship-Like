package com.cookandroid.scholarshiplike

import java.util.*

data class Post (val title: String = "", val category : String = "", val contents : String = "")
data class Scholarship(val title: String, val text: String, val startdate: String, val enddate: String, val like: Boolean)
data class tmpScholarship(val title: String, val text: String, val startdate: Date?, val enddate: Date?)
data class Alarm(val category: String, val title: String, val date: String)

//// Search Activity - Scholarship
data class SearchScholarship(val name: String, val period: String, val institution: String)