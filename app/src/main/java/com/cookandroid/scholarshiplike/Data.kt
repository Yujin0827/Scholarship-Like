package com.cookandroid.scholarshiplike

import java.util.*

data class Post (val title: String = "", val category : String = "", val contents : String = "")
data class Scholarship(val title: String, val text: String, val startdate: String, val enddate: String, val like: Boolean)
data class tmpScholarship(val title: String, val text: String, val startdate: Date?, val enddate: Date?)
data class Alarm(val category: String, val title: String, val date: String)
data class Search(val title: String, val text: String, val date: String)
data class Scheduel(val name: String, val start:String, val end:String)