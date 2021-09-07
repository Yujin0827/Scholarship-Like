package com.cookandroid.scholarshiplike

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.DialogFragment
import com.cookandroid.scholarshiplike.databinding.FragmentLoginTermsBinding
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.fragment_login_terms.*

class LoginTermsFragment : DialogFragment(){
    private var _binding: FragmentLoginTermsBinding? = null
    private val binding get() = _binding!!

    val storage = Firebase.storage

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        isCancelable = false    // 화면 밖 또는 뒤로가기 버튼 클릭시에도 다이얼로그 dismiss 안됨.
        _binding = FragmentLoginTermsBinding.inflate(inflater, container, false)

        val view = binding.root

        getTerms()
        btnClick()

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun btnClick() {
        var termsServiceVisible = false
        var termsPrivacyVisible = false
        var isChecked = false

        // 서비스 약관 보기 클릭 시
        binding.btnShowTermsServcice.setOnClickListener {
            if (termsServiceVisible == false) {
                binding.viewWebTermsService.visibility = View.VISIBLE
                termsServiceVisible = true
            }
            else {
                binding.viewWebTermsService.visibility = View.GONE
                termsServiceVisible = false
            }
        }

        // 개인정보 약관 보기 클릭 시
        binding.btnShowTermsPrivacy.setOnClickListener {
            if (termsPrivacyVisible == false) {
                binding.viewWebTermsPrivacy.visibility = View.VISIBLE
                termsPrivacyVisible = true
            }
            else {
                binding.viewWebTermsPrivacy.visibility = View.GONE
                termsPrivacyVisible = false
            }
        }

        // 확인 버튼 클릭 리스너
        binding.btnLoginTermsOK.setOnClickListener {
            isChecked = cb_terms_service.isChecked && cb_terms_privacy.isChecked

            if(isChecked) { // 모든 약관이 체크되었을 때
                dismiss()   // 팝업창 종료
            }
            else {
                Toast.makeText(context, "모든 약관에 동의가 필요합니다", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Firebase Storage에서 약관 텍스트파일 가져오기
    private fun getTerms() {
        val storageRef = storage.reference

        // 서비스 약관동의
        var pathReference = storageRef.child("terms/terms_service.txt")
        pathReference.downloadUrl
            .addOnSuccessListener {
                binding.webViewInitTermsService.apply {
                    webViewClient = WebViewClient()
                    settings.javaScriptEnabled = true
                }
                binding.webViewInitTermsService.loadUrl(it.toString())
            }

        //개인정보 처리방침
        pathReference = storageRef.child("terms/terms_privacy.txt")
        pathReference.downloadUrl
            .addOnSuccessListener {
                binding.webViewInitTermsPrivacy.apply {
                    webViewClient = WebViewClient()
                    settings.javaScriptEnabled = true
                }
                binding.webViewInitTermsPrivacy.loadUrl(it.toString())
            }
    }
}