package com.cookandroid.scholarshiplike

data class Post(val title: String, val category : String, val contents : String)
data class Scholarship(val title: String, val text: String, val date: String, val like: Boolean)
data class Alarm(val category: String, val title: String, val date: String)
data class Search(val title: String, val text: String, val date: String)