package com.cookandroid.scholarshiplike

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.cookandroid.scholarshiplike.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.getField
import com.google.firebase.ktx.Firebase

// Fragment 변수 생성
private const val ProfileTab = "Profile_Fragment"
private const val ProfileTabEtc = "Profile_Etc_Fragment"

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    val TAG = "ProfileFragment"

    // Firebase
    lateinit var auth: FirebaseAuth
    lateinit var db: FirebaseFirestore

//    lateinit var fm: FragmentManager
//    lateinit var transaction: FragmentTransaction

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreate(savedInstanceState)

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val view = binding.root

        // Firebase
        auth = Firebase.auth
        db = Firebase.firestore

        setUserNickname()

        // '기타' 클릭 리스너
        binding.profileEtc.setOnClickListener {
            val fm = parentFragmentManager
            val transaction = fm.beginTransaction()
            if (fm.findFragmentByTag(ProfileTabEtc) == null) {
                transaction.add(R.id.nav, ProfileEtcFragment(), ProfileTabEtc)
            }
            val profiletab = fm.findFragmentByTag(ProfileTab)
            val profiletabEtc = fm.findFragmentByTag(ProfileTabEtc)
            profiletab?.let { it ->  transaction.hide(it)}
            profiletabEtc?.let { it -> transaction.show(it) }
            transaction.addToBackStack("ProfileTab")
            transaction.setReorderingAllowed(true)
            transaction.commitAllowingStateLoss()
        }

        return view
    }

    // 프래그먼트 생성시 툴바 hide
    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()

        // 아래 스와이핑으로 새로고침
        binding.swipeRefreshFragmentProfile.setOnRefreshListener {
            Handler().postDelayed({ // 아래로 스와이핑 이후 1초 후에 리플래쉬 아이콘 없애기
                if (binding.swipeRefreshFragmentProfile.isRefreshing)
                    binding.swipeRefreshFragmentProfile.isRefreshing = false
            }, 1000)
            setUserNickname()
        }
    }

    // 프래그먼트 종료시 툴바 show
    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // '내 조건 수정' 클릭 리스너
        binding.myConChange.setOnClickListener {
            activity?.let {
                val intent = Intent(it, ProfileMyConChangeActivity::class.java)
                it?.startActivity(intent)
            }
        }

        // '좋아요 누른 게시물' 클릭 리스너
        binding.likeContent.setOnClickListener {
            activity?.let {
                val intent = Intent(it, LikeContentActivity::class.java)
                it?.startActivity(intent)
            }
        }

        // '프로필 수정' 클릭 리스너
        binding.profileTitleIconLayout.setOnClickListener {
            activity?.let {
                val intent = Intent(it, ProfileChangeActivity::class.java)
                it?.startActivity(intent)
            }
        }

        // '닉네임' 클릭 리스너
        binding.btnProfileUserName.setOnClickListener {
            activity?.let {
                val intent = Intent(it, ProfileChangeActivity::class.java)
                it?.startActivity(intent)
            }
        }

    }

    // 유저 닉네임 가져오기
    private fun setUserNickname() {
        val user = auth.currentUser

        if (user != null) {
            db.collection("Users")
                .document(user.uid)
                .get()
                .addOnSuccessListener { result ->
                    binding.btnProfileUserName.text = result.getField<String>("nickname")
                }
                .addOnFailureListener() { exception ->
                    Log.e(TAG, "Fail to get user nickname from DB!", exception)
                }
        }
    }

    // 프래그먼트 파괴
    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

}
