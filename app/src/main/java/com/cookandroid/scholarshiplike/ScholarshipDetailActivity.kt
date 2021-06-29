package com.cookandroid.scholarshiplike

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class ScholarshipDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 레아이웃 설정
        setContentView(R.layout.activity_scholarship_detail)

        //좋아요 버튼 클릭 유지
        var likeButton : Button = findViewById(R.id.like)
        likeButton.setOnClickListener{
            likeButton.isSelected = likeButton.isSelected != true
        }
    }
}