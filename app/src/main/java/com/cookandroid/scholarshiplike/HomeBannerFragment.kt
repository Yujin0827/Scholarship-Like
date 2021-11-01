//package com.cookandroid.scholarshiplike
//
//import android.os.Bundle
//import android.view.View
//import androidx.fragment.app.Fragment
//import com.cookandroid.scholarshiplike.databinding.FragmentHomeBannerBinding
//
//class HomeBannerFragment(val image : Int) : Fragment() {
//
//    // binding
//    private var _binding: FragmentHomeBannerBinding? = null   // 바인딩 객체
//    private val binding get() = _binding!!              // 바인딩 변수 재선언 (매번 null 체크x)
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        binding.bannerImage.setImageResource(image)
//    }
//}