package com.cookandroid.scholarshiplike

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_magazine_detail.*
import java.util.ArrayList

class MagazineDetailActivity : AppCompatActivity() {

    private var db = Firebase.firestore
    private val user = Firebase.auth.currentUser
    private val user_ref = db.collection("Users")
    private var magazineList: ArrayList<String> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 레이아웃 설정
        setContentView(R.layout.activity_magazine_detail)

        // 매거진 프래그먼트에서 정보 전달 받아 텍스트뷰에 저장
        val titlename = intent.getStringExtra("title")
        val contents = intent.getStringExtra("contents")
        titleText.text = titlename
        contentsText.text = contents

        //좋아요 버튼
        var likeButton : Button = findViewById(R.id.like)

        //유저가 좋아요한 목록 가져와 매거진 이름이랑 비교
        if (user != null) {
            user_ref.document(user.uid).get().addOnSuccessListener { document ->
                if (document.data != null) {
                    if (document.data!!.get("likeMagazine") != null) {
                        magazineList = document["likeMagazine"] as ArrayList<String>
                        for (item in magazineList) {
                            if(item==titlename) likeButton.isSelected = true //좋아요 클릭 유지
                        }
                    }
                }
            }
        }

        likeButton.setOnClickListener{
            likeButton.isSelected = likeButton.isSelected != true

            if(likeButton.isSelected) { //눌리면 유저 좋아요 목록에 추가하기
                Log.w("isSelected: ",  "Enter")

                if (user != null) {
                    user_ref.document(user.uid).update("likeMagazine", FieldValue.arrayUnion(titlename))
                }
            } else { //해제되면 유저 좋아요 목록에서 삭제하기
                if (user != null) {
                    user_ref.document(user.uid).update("likeMagazine", FieldValue.arrayRemove(titlename))
                }
            }
        }
    }
}