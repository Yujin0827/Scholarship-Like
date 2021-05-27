package com.cookandroid.scholarshiplike

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_magazine_detail.*

class MagazineDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_magazine_detail)

        //프래그먼트에서 정보전달 및 텍스트뷰에 저장
        val titlename = intent.getStringExtra("title")
        titleText.text = titlename
    }
}