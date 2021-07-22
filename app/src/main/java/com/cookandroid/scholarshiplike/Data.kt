package com.cookandroid.scholarshiplike

import com.google.firebase.Timestamp
import java.util.*

data class Post (val title: String = "", val category : String = "", val contents : String = "")
data class tmpScholarship(val title: String, val text: String, val startdate: Date?, val enddate: Date?) //캘린더에 사용
data class detailScholarship(val URL:String?=null, val contents:String?=null, val note:String?=null, val paymentInstitution:String?=null, val paymentType:String?=null
                             , val maxMoney:Int?=null, val period:Map<String,Timestamp>? = null, val usercondition:String ?= null, val title:String ?= null)
data class Alarm(val category: String, val title: String, val date: String)

// Search Activity - Scholarship
data class SearchScholarship(val title: String, val period_start: String, val period_end : String,  val institution: String)

data class Scholarship(val title: String, val startdate: String, val enddate: String, val institution: String)

