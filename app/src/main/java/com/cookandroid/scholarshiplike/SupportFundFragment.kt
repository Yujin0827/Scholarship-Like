package com.cookandroid.scholarshiplike

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.cookandroid.scholarshiplike.databinding.FragmentSupportFundBinding

class SupportFundFragment : Fragment() {

    // binding
    private var _binding: FragmentSupportFundBinding? = null   // 바인딩 객체
    private val binding get() = _binding!!              // 바인딩 변수 재선언 (매번 null 체크x)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentSupportFundBinding.inflate(inflater, container, false)
        val view = binding.root

        return view
    }
}