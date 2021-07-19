package com.cookandroid.scholarshiplike

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.cookandroid.scholarshiplike.databinding.FragmentProfileEtcBinding

class ProfileEtcFragment : Fragment() {

    private var _binding: FragmentProfileEtcBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        _binding = FragmentProfileEtcBinding.inflate(inflater, container, false)

        btnClick()

        return binding.root
    }

    // 버튼 클릭 통합 처리
    private fun btnClick() {
        // '앱 정보' 클릭 리스너
        binding.etcAppInfo.setOnClickListener {
            activity?.let {
                val intent = Intent(it, AppInfoActivity::class.java)
                it?.startActivity(intent)
            }
        }

        // '로그아웃' 클릭 리스너
        binding.etcLogout.setOnClickListener {
            val dialog = ProfileLogoutFragment()
            dialog.show(parentFragmentManager, "logoutFragment")
        }
    }
}