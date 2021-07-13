package com.cookandroid.scholarshiplike

data class Post (val title: String = "", val category : String = "", val contents : String = "")
data class Scholarship(val title: String, val text: String, val date: String, val like: Boolean)
data class Alarm(val category: String, val title: String, val date: String)
data class Scheduel(val name: String, val start:String, val end:String)

// Search Activity - Scholarship
data class SearchScholarship(val title: String, val period_start: String, val period_end: String, val institution: String)