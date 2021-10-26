package com.cookandroid.scholarshiplike

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.cookandroid.scholarshiplike.databinding.ActivityMagazineDetailBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.firebase.storage.ktx.storage
import java.util.ArrayList

class MagazineDetailActivity : AppCompatActivity() {

    val binding by lazy { ActivityMagazineDetailBinding.inflate(layoutInflater) }

    private var db = Firebase.firestore
    private val user = Firebase.auth.currentUser
    private val user_ref = db.collection("Users")
    private var magazineList: ArrayList<String> = arrayListOf()
    private var mInterstitialAd: InterstitialAd? = null
    private val TAG = "MagazineDetailActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // 매거진 프래그먼트에서 정보 전달 받아 텍스트뷰에 저장
        val titlename = intent.getStringExtra("title")
        val contents = intent.getStringExtra("contents")
        val imageURL = intent.getStringExtra("imageURL")
        val imageView = findViewById<ImageView>(R.id.imageView)

        val storageRef = Firebase.storage

        if(imageURL != null) {
            storageRef.getReferenceFromUrl(imageURL).downloadUrl.addOnSuccessListener {
                Glide.with(this)
                    .load(it)
                    .into(imageView)
            }.addOnFailureListener {
                // Handle any errors
            }
        }

        binding.titleText.text = titlename
        binding.contentsText.text = contents
        binding.magazinename.text = titlename

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

    override fun onStart() {
        super.onStart()
        loadAd()    // Admob 광고 초기화
    }

    // Admob 광고 초기화
    private fun loadAd() {
        // 배너광고
        MobileAds.initialize(this) {}
        val mAdView = binding.magazineDetailAdView
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

    // 상세페이지 종료시 전면광고 보이기
    override fun onDestroy() {
        super.onDestroy()
        if (mInterstitialAd != null) {
            mInterstitialAd?.show(this)
        } else {
            Log.d(TAG, "The interstitial ad wasn't ready yet.")
        }
        mInterstitialAd = null  // 광고 null
    }
}