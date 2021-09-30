package com.cookandroid.scholarshiplike

import android.os.Bundle
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.cookandroid.scholarshiplike.databinding.ActivityProfileTermsServiceBinding
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class ProfileTermsServiceActivity : AppCompatActivity() {
    private var mBinding: ActivityProfileTermsServiceBinding? = null
    private val binding get() = mBinding!!

    val storage = Firebase.storage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityProfileTermsServiceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getTerms()
    }

    // Firebase Storage에서 약관 텍스트파일 가져오기
    private fun getTerms() {
        val storageRef = storage.reference
        val pathReference = storageRef.child("terms/terms_service.txt")

        pathReference.downloadUrl
            .addOnSuccessListener {
                binding.webviewTermsService.apply {
                    webViewClient = WebViewClient()
                    settings.javaScriptEnabled = true
                }
                binding.webviewTermsService.loadUrl(it.toString())
            }
    }
}