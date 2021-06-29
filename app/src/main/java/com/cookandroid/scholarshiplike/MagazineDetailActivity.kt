package com.cookandroid.scholarshiplike

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_magazine_detail.*

class MagazineDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 레이아웃 설정
        setContentView(R.layout.activity_magazine_detail)

        // 매거진 프래그먼트에서 정보 전달 받아 텍스트뷰에 저장
        val titlename = intent.getStringExtra("title")
        val contents = intent.getStringExtra("contents")
        titleText.text = titlename
        contentsText.text = contents

        //좋아요 버튼 클릭 유지
        var likeButton : Button = findViewById(R.id.like)
        likeButton.setOnClickListener{
            likeButton.isSelected = likeButton.isSelected != true
        }
    }
}