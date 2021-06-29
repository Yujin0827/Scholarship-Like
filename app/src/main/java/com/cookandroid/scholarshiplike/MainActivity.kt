package com.cookandroid.scholarshiplike

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import kotlinx.android.synthetic.main.activity_signup.*

open class MainActivity : AppCompatActivity(),
    BottomNavigationView.OnNavigationItemSelectedListener  {

    private val hometab = HomeFragment()

    lateinit var tabNav : BottomNavigationView

    // onBackPressed 메소드 변수
    var backPressedTime : Long = 0
    val FINISH_INTERVAL_TIME = 2000

    // firebase auth 리스너 (로그인, 로그아웃 처리)
    var authStateListener: FirebaseAuth.AuthStateListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 현재 유저
        var user = FirebaseAuth.getInstance().currentUser
        
        // 유저 확인 후, 로그인 창으로 이동
        if (user == null) {
            var iT = Intent(this, LoginActivity::class.java)
            startActivity(iT)
            finish()    //현재 activity 제거
        }

//        authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
//            var user = FirebaseAuth.getInstance().currentUser
//            if (user == null) {
//                startActivity(iT)
//            }
//        }

        // 하단바 변수 생성
        tabNav = findViewById<BottomNavigationView>(R.id.tabNav)

        // 툴바 설정
        var toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.nav, HomeFragment(), "homeTab")
            .commit()

        // 하단바 연결
        tabNav.setOnNavigationItemSelectedListener(this)
    }

    // 하단바 누르면 탭 화면 전환 & BackStack 생성 및 제거
    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
        val fm = supportFragmentManager
        val transaction: FragmentTransaction = fm.beginTransaction()

        when(p0.itemId){
            R.id.homeTab -> {
                fm.popBackStack("homeTab", FragmentManager.POP_BACK_STACK_INCLUSIVE)    // BackStack에서 해당 fragment 제거
                val hometab = HomeFragment()                            // fragment 변수 생성
                transaction.replace(R.id.nav, hometab, "homeTab")     // fragment 화면 전환
                transaction.addToBackStack("homeTab")               // fragment 생성하면서 BackStack 생성
            }
            R.id.scholarshipTab -> {
                fm.popBackStack("scholarshipTab", FragmentManager.POP_BACK_STACK_INCLUSIVE)
                val scholarshiptab = ScholarshipFragment()
                transaction.replace(R.id.nav, scholarshiptab, "scholarshipTab")
                transaction.addToBackStack("scholarshipTab")
            }
            R.id.magazineTab -> {
                fm.popBackStack("magazineTab", FragmentManager.POP_BACK_STACK_INCLUSIVE)
                val magazinetab = MagazineFragment()
                transaction.replace(R.id.nav, magazinetab, "magazineTab")
                transaction.addToBackStack("magazineTab")
            }
            R.id.profileTab -> {
                fm.popBackStack("profileTab", FragmentManager.POP_BACK_STACK_INCLUSIVE)
                val profiletab = ProfileFragment()
                transaction.replace(R.id.nav, profiletab, "profileTab")
                transaction.addToBackStack("profileTab")
            }
        }

        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        transaction.commit()
        transaction.isAddToBackStackAllowed
        return true
    }

    // fragment 클릭했을 때 자동적으로 하단바 아이콘 변경 ( 뒤로가기 눌렀을 때 호출 )
    private fun updateBottomMenu(navigation: BottomNavigationView) {
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
