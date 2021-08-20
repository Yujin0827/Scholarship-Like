package com.cookandroid.scholarshiplike

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.cookandroid.scholarshiplike.databinding.ActivityScholarshipDetailBinding
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.ArrayList

class ScholarshipDetailActivity : AppCompatActivity() {

    val binding by lazy { ActivityScholarshipDetailBinding.inflate(layoutInflater) }

    private var db = Firebase.firestore
    private var scholar: detailScholarship ?= null //장학금 정보 저장 변수
    private var mInterstitialAd: InterstitialAd? = null //Admob

    private val TAG = "ScholarshipDetailActivity"

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
                            if (mInterstitialAd != null) {
                                mInterstitialAd?.show(this)
                            } else {
                                Log.d(TAG, "The interstitial ad wasn't ready yet.")
                            }

                            val uri = Uri.parse(URL)
                            val intent = Intent(Intent.ACTION_VIEW, uri)
                            startActivity(intent)
                        }
                    }
                }
            }.addOnFailureListener { exception ->
                Log.w("세부페이지", "Error getting documents: $exception")
            }

        //좋아요 버튼 클릭 유지
        var likeButton : Button = findViewById(R.id.like)
        likeButton.setOnClickListener{
            likeButton.isSelected = likeButton.isSelected != true
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