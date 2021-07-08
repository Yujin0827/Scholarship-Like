package com.cookandroid.scholarshiplike

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.cookandroid.scholarshiplike.databinding.FragmentProfileBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_profile_change.*
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment : Fragment() {

    lateinit var myConChange : LinearLayout
    lateinit var likeContent : LinearLayout
    lateinit var appInfo : LinearLayout
    lateinit var logout : LinearLayout
    lateinit var profileChange : LinearLayout
    lateinit var allAlarmSwitch : Switch
    lateinit var userUid: String

    var userName = Firebase.auth.currentUser // 사용자 닉네임 가져오기
    private var db = Firebase.firestore

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreate(savedInstanceState)

        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        myConChange = view.findViewById<LinearLayout>(R.id.myConChange)
        likeContent = view.findViewById<LinearLayout>(R.id.likeContent)
        appInfo = view.findViewById<LinearLayout>(R.id.appInfo)
        logout = view.findViewById<LinearLayout>(R.id.logout)
        profileChange = view.findViewById<LinearLayout>(R.id.profileTitleIconLayout)

        getUserInfo() // 사용자 이름

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

        profileChange.setOnClickListener{
            activity?.let {
                val intent = Intent(it, ProfileChangeActivity::class.java)
                it?.startActivity(intent)
            }
        }
    }

    //사용자 이름 가져오기
    fun getUserInfo(){
        //User Email
        userName?.let {
            userUid = userName!!.uid
        }

        //User 필드 값
        db.collection("Users")
            .document(userUid)
            .get()
            .addOnSuccessListener{ document ->
                if (document != null){
                    if(document.getString("nickname") != null){
                        btnProfileUserName.text = document.getString("nickname")
                    }
                }
            }
    }
}
