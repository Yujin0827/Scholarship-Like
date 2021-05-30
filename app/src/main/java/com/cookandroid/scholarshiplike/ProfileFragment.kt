package com.cookandroid.scholarshiplike

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.cookandroid.scholarshiplike.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    lateinit var myConChange : LinearLayout
    lateinit var likeContent : LinearLayout
    lateinit var appInfo : LinearLayout
    lateinit var logout : LinearLayout

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreate(savedInstanceState)

        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        myConChange = view.findViewById<LinearLayout>(R.id.myConChange)
        likeContent = view.findViewById<LinearLayout>(R.id.likeContent)
        appInfo = view.findViewById<LinearLayout>(R.id.appInfo)
        logout = view.findViewById<LinearLayout>(R.id.logout)

        return view
    }

    // 프래그먼트 생성시 툴바 hide
    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
    }

    // 프래그먼트 종료시 툴바 show
    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // '내 조건 수정' 클릭 리스너
        myConChange.setOnClickListener {
            activity?.let {
                val intent = Intent(it, ProfileMyConChangeActivity::class.java)
                it?.startActivity(intent)
            }
        }

        // '좋아요 누른 게시물' 클릭 리스너
        likeContent.setOnClickListener {
            activity?.let {
                val intent = Intent(it, LikeContentActivity::class.java)
                it?.startActivity(intent)
            }
        }

        appInfo.setOnClickListener{
            activity?.let {
                val intent = Intent(it, AppInfoActivity::class.java)
                it?.startActivity(intent)
            }
        }

        // '로그아웃' 클릭 리스너
        logout.setOnClickListener {
            val dialog = ProfileLogoutFragment()
            dialog.show(parentFragmentManager, "logoutFragment")
        }
    }
}
