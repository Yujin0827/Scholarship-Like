package com.cookandroid.scholarshiplike

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging


// Fragment 변수 생성
private const val HomeTab = "Home_fragment"
private const val ScholarshipTab = "Scholarship_Fragment"
private const val MagazineTab = "Magazine_Fragment"
private const val ProfileTab = "Profile_Fragment"

open class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener  {
    @Suppress("PrivatePropertyName")
    private val TAG = javaClass.simpleName

    // firebase
    var authStateListener: FirebaseAuth.AuthStateListener? = null
    val db = Firebase.firestore

    // 하단바
    lateinit var tabNav : BottomNavigationView

    // onBackPressed 메소드 변수
    private var backPressedTime : Long = 0
    private val FINISH_INTERVAL_TIME = 2000

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 현재 유저 확인
        confirmUser()
        
        // 하단바 설정
        tabNav = findViewById<BottomNavigationView>(R.id.tabNav)

        // 툴바 설정
        var toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // 처음 화면 - 홈탭
        setFragment(HomeTab, HomeFragment())

        // 하단바 연결
        tabNav.setOnNavigationItemSelectedListener(this)

        // 화면 전환 방지 (세로로 고정)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        // DB에 FCM 토큰 추가
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("FCM Test", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            Log.d("FCM Test", token)

            val db = Firebase.firestore.collection("Users").document("TOKEN")
            db.update("FCM", FieldValue.arrayUnion(token))
        })

    }

    // 현재 유저 확인
    fun confirmUser() {
        Log.e("LLPP", "로그인")
        var user = Firebase.auth.currentUser
        if (user != null) { // 유저가 존재하면
            Log.d(TAG, "Current user exist!")
            val dd = db.collection("Users").document(user.uid)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val document = task.result
                        if (!document.exists()) {   // 유저 DB가 존재하지 않으면
                            Log.d(TAG, "User's db doesn't exist!")
                            var iT = Intent(this, SignupProfileInfoActivity::class.java)
                            startActivity(iT)
                            finish()
                        }
                    }
                }
        }
        else {  // 유저가 존재하지 않으면
            Log.d(TAG, "Current user doesn't exist!")
            var iT = Intent(this, LoginActivity::class.java)
            startActivity(iT)
            finish()    // 현재 activity 제거
        }
    }

    // 하단바 누르면 탭 화면 전환 & BackStack 생성 및 제거
    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
        when(p0.itemId){
            R.id.homeTab -> {
                setFragment(HomeTab, HomeFragment())
            }
            R.id.scholarshipTab -> {
                setFragment(ScholarshipTab, ScholarshipFragment())
            }
            R.id.magazineTab -> {
                setFragment(MagazineTab, MagazineFragment())
            }
            R.id.profileTab -> {
                setFragment(ProfileTab, ProfileFragment())
            }
        }

        return true
    }

    // Tab Fragment show & hide
    private fun setFragment(tag: String, fragment: Fragment) {
        val fm = supportFragmentManager
        val transaction: FragmentTransaction = fm.beginTransaction()

        if (fm.findFragmentByTag(tag) == null) {
            transaction.add(R.id.nav, fragment, tag)
        }

        val hometab = fm.findFragmentByTag(HomeTab)
        val scholarshiptab = fm.findFragmentByTag(ScholarshipTab)
        val magazinetab = fm.findFragmentByTag(MagazineTab)
        val profiletab = fm.findFragmentByTag(ProfileTab)

        // Hide all Fragment
        if (hometab != null) {
            transaction.hide(hometab)
        }
        if (scholarshiptab != null) {
            transaction.hide(scholarshiptab)
        }
        if (magazinetab != null) {
            transaction.hide(magazinetab)
        }
        if (profiletab != null) {
            transaction.hide(profiletab)
        }

        // Show  current Fragment
        if (tag == HomeTab) {
            if (hometab != null) {
                transaction.show(hometab)
            }
        }
        if (tag == ScholarshipTab) {
            if (scholarshiptab != null) {
                transaction.show(scholarshiptab)
            }
        }
        if (tag == MagazineTab) {
            if (magazinetab != null) {
                transaction.show(magazinetab)
            }
        }
        if (tag == ProfileTab) {
            if (profiletab != null) {
                transaction.show(profiletab)
            }
        }

        transaction.commitAllowingStateLoss()
    }

    // fragment 클릭했을 때 자동적으로 하단바 아이콘 변경 ( 뒤로가기 눌렀을 때 호출 )
    fun updateBottomMenu(navigation: BottomNavigationView) {
        val homeTab: Fragment? = supportFragmentManager.findFragmentByTag("homeTab")
        val scholarshipTab: Fragment? = supportFragmentManager.findFragmentByTag("scholarshipTab")
        val magazineTab: Fragment? = supportFragmentManager.findFragmentByTag("magazineTab")
        val profileTab: Fragment? = supportFragmentManager.findFragmentByTag("profileTab")

        if(homeTab != null && homeTab.isVisible) {navigation.menu.findItem(R.id.homeTab).isChecked = true }
        if(scholarshipTab != null && scholarshipTab.isVisible) {navigation.menu.findItem(R.id.scholarshipTab).isChecked = true }
        if(magazineTab != null && magazineTab.isVisible) {navigation.menu.findItem(R.id.magazineTab).isChecked = true }
        if(profileTab != null && profileTab.isVisible) {navigation.menu.findItem(R.id.profileTab).isChecked = true }
    }

    // back 버튼 클릭 리스너 재정의
    override fun onBackPressed() {
        if(supportFragmentManager.backStackEntryCount == 0) {
            val tempTime = System.currentTimeMillis()
            val intervalTime = tempTime - backPressedTime

            if (!(0 > intervalTime || FINISH_INTERVAL_TIME < intervalTime)) {
                finishAffinity()
                System.runFinalization()
                System.exit(0)
            } else {
                backPressedTime = tempTime
                Toast.makeText(this, "'뒤로' 버튼을 한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
                return
            }
        }
        super.onBackPressed()
        updateBottomMenu(tabNav)
    }

}
