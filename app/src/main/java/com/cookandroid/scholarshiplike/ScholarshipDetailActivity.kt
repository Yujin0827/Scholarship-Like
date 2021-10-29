package com.cookandroid.scholarshiplike

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.cookandroid.scholarshiplike.databinding.ActivityScholarshipDetailBinding
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_scholarship_detail.*
import kotlinx.android.synthetic.main.item_scholarship.*
import java.text.SimpleDateFormat
import java.util.ArrayList

class ScholarshipDetailActivity : AppCompatActivity() {

    val binding by lazy { ActivityScholarshipDetailBinding.inflate(layoutInflater) }

    private var db = Firebase.firestore
    private val user = Firebase.auth.currentUser
    private val user_ref = db.collection("Users")
    private var scholar: detailScholarship ?= null //장학금 정보 저장 변수
    private var scholarList: ArrayList<String> = arrayListOf()
    private var mInterstitialAd: InterstitialAd? = null //Admob

    private val TAG = "ScholarshipDetailActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val title = intent.getStringExtra("title")
        Log.w("세부페이지장학금 title: ",title)

        var ref =  db.collectionGroup("ScholarshipList")

        //데이터 가져오기
        Log.w("장학금세부페이지", "Load Firestore")
        ref.get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    if(document.id == title){
                        scholar = document.toObject()
                        Log.w("세부페이지정보1: ",scholar.toString())

                        val dateFormat = SimpleDateFormat("yyyy.MM.dd")

                        val period = document["period"] as Map<String,Timestamp>
                        val startdate = period.get("startDate")?.toDate()
                        val enddate = period.get("endDate")?.toDate()
                        val startdate2 = period.get("startDate2")?.toDate()
                        val enddate2 = period.get("endDate2")?.toDate()
                        var result = "기간"

                        if((startdate2 == null)&& enddate2 == null){
                            if(startdate == null && enddate == null){
                                result = "자동 선발"
                            }
                            else if(startdate == enddate){
                                result = "추후 공지"
                            }
                            else{
                                result =  dateFormat.format(startdate) + "~" +  dateFormat.format(enddate)
                            }
                        }
                        else{
                            result = "1차: "+dateFormat.format(startdate) + "~" +  dateFormat.format(enddate)+
                                    "\n2차: "+dateFormat.format(startdate2) + "~" +  dateFormat.format(enddate2)
                        }


                        //데이터-뷰 연결
                        binding.title.text = title
                        binding.paymentInstitution.setText(scholar?.paymentInstitution)
                        binding.paymenttype.setText(scholar?.paymentType)
                        binding.period.setText(result)
                        binding.content.setText(scholar?.contents)
                        binding.note.setText(scholar?.note)

                        //조건 연결
                        val condition = document["condition"] as Map<String,Any>
                        val area = condition.get("area")
                        val disabled = condition.get("disabled")
                        val income = condition.get("income")
                        val mom = condition.get("mom")
                        val dad = condition.get("dad")
                        val semester = condition.get("semester")
                        val prescore = condition.get("prescore")
                        val preclass = condition.get("preclass")
                        val nationalmerit = condition.get("nationalmerit")
                        val child = condition.get("child")

                        //레이아웃 연결
                        if (area != null) {
                            binding.area.setText("거주지: "+area.toString())
                        } else {
                            binding.area.visibility = View.GONE
                        }

                        if(scholar?.maxMoney != null) {
                            binding.maxmoney.setText(scholar?.maxMoney.toString()+"원")
                        } else
                            binding.maxmoney.setText("기준에 따라 상이함")

                        if (disabled != null) {
                            if (disabled == true) {
                                binding.disabled.setText("장애 여부 관계 있음")
                            } else {
                                binding.disabled.visibility = View.GONE
                            }
                        } else {
                            binding.disabled.visibility = View.GONE
                        }

                        if (income != null) {
                            binding.income.setText("학자금 지원구간: "+income.toString()+" 이하")
                        } else {
                            binding.income.visibility = View.GONE
                        }

                        if (mom != null && dad != null) {
                            if(mom == false && dad == false) {
                                binding.parent.setText("소년소녀가장")
                            }
                            else if(mom == false || dad == false) {
                                binding.parent.setText("한부모가정")
                            }
                        } else {
                            binding.parent.visibility = View.GONE
                        }

                        if (semester != null) {
                            binding.semester.setText("현재 이수 학기: " +semester.toString()+" 이상")
                        } else {
                            binding.semester.visibility = View.GONE
                        }

                        if (prescore != null) {
                            binding.prescore.setText("직전 학기 성적: "+prescore.toString()+" 이상")
                        } else {
                            binding.prescore.visibility = View.GONE
                        }

                        if (preclass != null) {
                            binding.preclass.setText("직전 학기 이수 학점: "+preclass.toString()+" 이상")
                        } else {
                            binding.preclass.visibility = View.GONE
                        }

                        if (nationalmerit != null) {
                            if(nationalmerit == true) {
                                binding.nationalmerit.setText("보훈보상대상자")
                            } else {
                                binding.nationalmerit.visibility = View.GONE
                            }
                        } else {
                            binding.nationalmerit.visibility = View.GONE
                        }

                        if (child != null) {
                            if(child == true) {
                                binding.child.setText("자녀 수 관계 있음 (세부사항은 홈페이지 참고)")
                            }
                        } else {
                            binding.child.visibility = View.GONE
                        }


                        val URL = scholar!!.URL

                        //홈페이지 버튼 연결
                        binding.movetoweb.setOnClickListener {
                            if (mInterstitialAd != null) {
                                mInterstitialAd?.show(this)
                            } else {
                                Log.d(TAG, "The interstitial ad wasn't ready yet.")
                            }
                            if (URL != null) {
                                val url = Uri.parse(URL)
                                val intent = Intent(Intent.ACTION_VIEW, url)
                                startActivity(intent)
                            }
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

    override fun onStart() {
        super.onStart()
        loadAd()    // admob 광고 초기화
    }

    // Admob 광고 초기화
    private fun loadAd() {
        // 배너광고
        MobileAds.initialize(this) {}
        val mAdView = binding.scholarshipDetailAdView
        var adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

        // 전면광고
        InterstitialAd.load(this,"ca-app-pub-3940256099942544/1033173712", adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.d(TAG, adError?.message)
                mInterstitialAd = null
            }
            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                Log.d(TAG, "Ad was loaded.")
                mInterstitialAd = interstitialAd
            }
        })

        mInterstitialAd?.fullScreenContentCallback = object: FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                Log.d(TAG, "Ad was dismissed.")
                adRequest = AdRequest.Builder().build() // 같은 광고 방지
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError?) {
                Log.d(TAG, "Ad failed to show.")
            }

            override fun onAdShowedFullScreenContent() {
                Log.d(TAG, "Ad showed fullscreen content.")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mInterstitialAd != null) {
            mInterstitialAd?.show(this)
            Log.d(TAG, "show admob before onDestroy")
        } else {
            Log.d(TAG, "The interstitial ad wasn't ready yet.")
        }
        mInterstitialAd = null  // 광고 null
    }
}
