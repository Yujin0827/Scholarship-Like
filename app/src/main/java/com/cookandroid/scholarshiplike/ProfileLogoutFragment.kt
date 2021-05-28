package com.cookandroid.scholarshiplike

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.cookandroid.scholarshiplike.databinding.ProfileLogoutBinding
import java.security.AccessControlContext

class ProfileLogoutFragment : DialogFragment(), View.OnClickListener {
    private var _binding: ProfileLogoutBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        isCancelable = false    // 화면 밖 또는 뒤로가기 버튼 클릭시에도 다이얼로그 dismiss 안됨.
        _binding = ProfileLogoutBinding.inflate(inflater, container, false)

//        // 로그아웃 팝업 창 모서리를 둥글게 할 경우 사용
//        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)

        val view = binding.root

        // 로그아웃 버튼 클릭 리스너
        binding.btnLogout.setOnClickListener {
            dismiss()   // 임시 - 대화상자 닫기
        }
        // 취소 버튼 클릭 리스너
        binding.btnCancel.setOnClickListener {
            dismiss()   // 대화상자 닫기
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClick(v: View?) {
        dismiss()
    }
}