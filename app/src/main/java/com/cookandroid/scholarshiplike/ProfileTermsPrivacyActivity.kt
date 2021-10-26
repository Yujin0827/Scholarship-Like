package com.cookandroid.scholarshiplike

import android.os.Bundle
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.cookandroid.scholarshiplike.databinding.ActivityProfileTermsPrivacyBinding
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class ProfileTermsPrivacyActivity : AppCompatActivity(){
    private var mBinding: ActivityProfileTermsPrivacyBinding? = null
    private val binding get() = mBinding!!

    val storage = Firebase.storage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityProfileTermsPrivacyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getTerms()
    }

    // Firebase Storage에서 약관 텍스트파일 가져오기
    private fun getTerms() {
        val storageRef = storage.reference
        val pathReference = storageRef.child("terms/terms_privacy.txt")

        pathReference.downloadUrl
            .addOnSuccessListener {
                binding.webviewTermsPrivacy.apply {
                    webViewClient = WebViewClient()
                    settings.javaScriptEnabled = true
                }
                binding.webviewTermsPrivacy.loadUrl(it.toString())
            }
    }
}