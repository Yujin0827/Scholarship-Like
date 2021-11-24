package com.cookandroid.scholarshiplike

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.cookandroid.scholarshiplike.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.getField
import com.google.firebase.ktx.Firebase
import kotlin.concurrent.thread

// Fragment 변수 생성
private const val ProfileTab = "Profile_Fragment"
private const val ProfileTabEtc = "Profile_Etc_Fragment"

@Suppress("UNREACHABLE_CODE")
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    val TAG = "ProfileFragment"

    // Firebase
    lateinit var auth: FirebaseAuth
    lateinit var db: FirebaseFirestore

    // SharedPreferences
    lateinit var pref : SharedPreferences
    lateinit var editor : SharedPreferences.Editor

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreate(savedInstanceState)

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val view = binding.root

        // Firebase
        auth = Firebase.auth
        db = Firebase.firestore

        pref= activity!!.getSharedPreferences("SharedData", Context.MODE_PRIVATE)
        editor = pref.edit()

        setUserNickname()
        setAlarm()
        alarmStateChange()

        // '기타' 클릭 리스너
        binding.profileEtc.setOnClickListener {
            val fm = parentFragmentManager
            val transaction = fm.beginTransaction()
            if (fm.findFragmentByTag(ProfileTabEtc) == null) {
                transaction.add(R.id.nav, ProfileEtcFragment(), ProfileTabEtc)
            }
            val profiletab = fm.findFragmentByTag(ProfileTab)
            val profiletabEtc = fm.findFragmentByTag(ProfileTabEtc)
            profiletab?.let { it -> transaction.hide(it) }
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

        // 사용자 정보 레이아웃 클릭 리스너
        binding.profileUserInfoLayout.setOnClickListener {
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
            thread(start = true) {
                db.collection("Users")
                    .document(user.uid)
                    .get()
                    .addOnSuccessListener { result ->
                        activity?.runOnUiThread {
                            binding.profileUserName.text = result.getField<String>("nickname")
                            binding.txtUserEmail.text = user.email.toString()
                        }
                    }
                    .addOnFailureListener() { exception ->
                        Log.e(TAG, "Fail to get user nickname from DB!", exception)
                    }
            }
        }
    }

    // 프래그먼트 파괴
    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    fun setAlarm() {
        val al_all = pref.getBoolean("KEY_ALARM_ALL", false)
        val al_scholarship = pref.getBoolean("KEY_ALARM_SCHOLARSHIP", false)
        val al_magazine = pref.getBoolean("KEY_ALARM_MAGAZINE", false)

        binding.setAlarmAll.setChecked(al_all)
        binding.setAlarmScholarship.setChecked(al_scholarship)
        binding.setAlarmMagazine.setChecked(al_magazine)
    }

    // 알람 설정 상태 변경 리스너 함수
    fun alarmStateChange() {
        // 전체 알림 상태 변경시
        binding.setAlarmAll.setOnCheckedChangeListener { _, isChecked ->
            val al_scholarship = binding.setAlarmScholarship.isChecked
            val al_magazine = binding.setAlarmMagazine.isChecked

            editor.putBoolean("KEY_ALARM_ALL", isChecked)

            if (isChecked || (al_scholarship && al_magazine)) {
                editor.putBoolean("KEY_ALARM_SCHOLARSHIP", isChecked)
                editor.putBoolean("KEY_ALARM_MAGAZINE", isChecked)

                binding.setAlarmScholarship.setChecked(isChecked)
                binding.setAlarmMagazine.setChecked(isChecked)
            }

            editor.apply()
        }

        // 장학금 알림 상태 변경시
        binding.setAlarmScholarship.setOnCheckedChangeListener { _, isChecked ->
            editor.putBoolean("KEY_ALARM_SCHOLARSHIP", isChecked)
            editor.apply()

            val al_schorship = binding.setAlarmScholarship.isChecked
            val al_magazine = binding.setAlarmMagazine.isChecked

            checkAlarmAll()
        }

        // 매거진 알림 상태 변경시
        binding.setAlarmMagazine.setOnCheckedChangeListener { _, isChecked ->
            editor.putBoolean("KEY_ALARM_MAGAZINE", isChecked)
            editor.apply()

            val al_schorship = binding.setAlarmScholarship.isChecked
            val al_magazine = binding.setAlarmMagazine.isChecked

            checkAlarmAll()
        }
    }

    // 장학금, 매거진 알림 상태가 같을경우, 전체 알림도 같게 설정
    fun checkAlarmAll() {
        val al_scholarship = binding.setAlarmScholarship.isChecked
        val al_magazine = binding.setAlarmMagazine.isChecked

        if (al_scholarship && al_magazine) {
            binding.setAlarmAll.setChecked(true)
            editor.putBoolean("KEY_ALARM_ALL", true)
            editor.apply()
        }
        else {
            binding.setAlarmAll.setChecked(false)
            editor.putBoolean("KEY_ALARM_ALL", false)
            editor.apply()
        }
    }
}
