package com.cookandroid.scholarshiplike

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.cookandroid.scholarshiplike.databinding.ActivityScholarshipDetailBinding
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.ArrayList

class ScholarshipDetailActivity : AppCompatActivity() {

    val binding by lazy { ActivityScholarshipDetailBinding.inflate(layoutInflater) }

    private var db = Firebase.firestore
    private val user = Firebase.auth.currentUser
    private val user_ref = db.collection("Users")
    private var scholar: detailScholarship ?= null //장학금 정보 저장 변수
    private var scholarList: ArrayList<String> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val title = intent.getStringExtra("title")
        Log.w("세부페이지장학금 title: ",title)

        Log.w("장학금세부페이지", "Load Firestore")
        db.collection("국가").get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    if(document.id == title){
                        scholar = document.toObject()
                        Log.w("세부페이지정보1: ",scholar.toString())

                        val dateFormat = SimpleDateFormat("yyyy.MM.dd")

                        var period = document["period"] as Map<String,Timestamp>
                        var startdate : Timestamp? = period.get("startdate")
                        var enddate : Timestamp? = period.get("enddate")
                        var result = dateFormat.format(startdate?.toDate())+" ~ "+dateFormat.format(enddate?.toDate())

                        val URL = scholar!!.URL

                        //데이터-뷰 연결
                        binding.title.text = title
                        binding.paymentInstitution.setText(scholar?.paymentInstitution)
                        binding.paymenttype.setText(scholar?.paymentType)
                        binding.maxmoney.setText(scholar?.maxMoney.toString()+"원")
                        binding.period.setText(result)
                        binding.condition.setText(scholar?.usercondition)
                        binding.content.setText(scholar?.contents)
                        binding.note.setText(scholar?.note)

                        //홈페이지 버튼 연결
                        binding.movetoweb.setOnClickListener {
                            val uri = Uri.parse(URL)
                            val intent = Intent(Intent.ACTION_VIEW, uri)
                            startActivity(intent)
                        }
                    }
                }
            }.addOnFailureListener { exception ->
                Log.w("세부페이지", "Error getting documents: $exception")
            }

        //좋아요 버튼
        var likeButton : Button = findViewById(R.id.like)

        //유저가 좋아요한 목록 가져와 장학금 이름이랑 비교
        if (user != null) {
            user_ref.document(user.uid).get().addOnSuccessListener { document ->
                if (document.data != null) {
                    if (document.data!!.get("likeScholarship") != null) {
                         scholarList = document["likeScholarship"] as ArrayList<String>
                        for (item in scholarList) {
                            if(item==title) likeButton.isSelected = true //좋아요 클릭 유지
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
                    user_ref.document(user.uid).update("likeScholarship", FieldValue.arrayUnion(title))
                }
            } else { //해제되면 유저 좋아요 목록에서 삭제하기
                if (user != null) {
                    user_ref.document(user.uid).update("likeScholarship", FieldValue.arrayRemove(title))
                }
            }
        }
    }
}