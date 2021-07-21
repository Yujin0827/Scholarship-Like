package com.cookandroid.scholarshiplike

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.ArrayList

class ScholarshipDetailActivity : AppCompatActivity() {

    private var db = Firebase.firestore
    private lateinit var scholar: tmpScholarship //장학금 정보 저장 변수

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 레아이웃 설정
        setContentView(R.layout.activity_scholarship_detail)

//        val title = intent.getStringExtra("title")
//
//        var ref = db.collection("Users")
//
//        Log.w("장학금세부페이지", "Load Firestore")
//        db.collection("국가").get()
//            .addOnSuccessListener { result ->
//                for (document in result) {
//                    if(document.id == title){
//
//                        //정보가져오기
//                        val period = document["period"] as Map<String, Timestamp>
//                        val startdate : Timestamp? = period.get("startdate")
//                        val enddate : Timestamp? = period.get("enddate")
//
//                    }
//                }
//            }.addOnFailureListener { exception ->
//                Log.w("장학금세부페이지", "Error getting documents: $exception")
//            }

        //좋아요 버튼 클릭 유지
        var likeButton : Button = findViewById(R.id.like)
        likeButton.setOnClickListener{
            likeButton.isSelected = likeButton.isSelected != true
        }
    }
}