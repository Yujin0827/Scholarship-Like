package com.cookandroid.scholarshiplike

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView

class HomeSearchActivity : AppCompatActivity() {

    lateinit var goBack : ImageView         //뒤로가기 버튼
    lateinit var searchfield : EditText     //검색창
    lateinit var searchButton : ImageButton //찾기 버튼
    lateinit var resultList : RecyclerView  //검색 결과

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_search)

//        searchfield = findViewById<EditText>(R.id.toolbar)      //검색창
//        searchButton = findViewById<ImageButton>(R.id.toolbar)  //찾기 버튼
//        resultList = findViewById<RecyclerView>(R.id.toolbar)   //검색 결과


    }
}