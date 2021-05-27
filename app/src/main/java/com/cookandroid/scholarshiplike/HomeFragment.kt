package com.cookandroid.scholarshiplike

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment


class HomeFragment : Fragment() {

    lateinit var scholarCnt : TextView  // 000님은 최대 0건의 장학금을 받을 수 있어요!
    lateinit var like : ImageView       // 좋아요 페이지로 이동 버튼
    lateinit var alarm : ImageView      // 알람 페이지로 이동 버튼
    lateinit var searchWin : ImageView  // 검색창 페이지로 이동 버튼
    lateinit var kosafWeb : ImageView   // 한국장학재단 사이트로 이동 버튼
    lateinit var univWeb : ImageView    // 교내 사이트로 이동 버튼
    lateinit var guessWeb : ImageView   // ? 이동 버튼

    val scholarshiptab = ScholarshipFragment()   // fragment_scholarship 변수

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        scholarCnt = view.findViewById<TextView>(R.id.scholarCnt)   // hometab의 scholarCnt 변수 생성
        like = view.findViewById<ImageView>(R.id.like)              // hometab의 좋아요 버튼 변수 생성
        alarm = view.findViewById<ImageView>(R.id.alarm)            // hometab의 알람 버튼 변수 생성
        searchWin = view.findViewById<ImageView>(R.id.searchWin)    // hometab의 검색창 버튼 변수 생성
        kosafWeb = view.findViewById<ImageView>(R.id.kosafWeb)      // hometab의 한국장학재단 사이트 이동 버튼 변수 생성
        univWeb = view.findViewById<ImageView>(R.id.univWeb)        // hometab의 교내 사이트 이동 버튼 변수 생성
        guessWeb = view.findViewById<ImageView>(R.id.guessWeb)      // hometab의 ? 사이트 이동 버튼 변수 생성

        // 장학금 탭으로 이동
        scholarCnt.setOnClickListener {
            activity?.getSupportFragmentManager()?.beginTransaction()
                ?.replace(R.id.nav, scholarshiptab, "scholarshipTab")
                ?.commit()
        }

        return view
    }

    // fragment -> activity 화면 이동
    override fun onActivityCreated(savedInstanceState: Bundle?) {

        // 좋아요 누른 게시물 페이지(LikeActivity)로 이동
        like.setOnClickListener {
            activity?.let {
                val intent = Intent(context, LikeContentActivity::class.java)
                startActivity(intent)
            }
        }

        // 알림 페이지(AlarmActivity)로 이동
        alarm.setOnClickListener {
            activity?.let {
                val intent = Intent(context, AlarmActivity::class.java)
                startActivity(intent)
            }
        }

        // 검색창(SearchWinActivity)으로 이동
        searchWin.setOnClickListener {
            activity?.let {
                val intent = Intent(it, HomeSearchActivity::class.java)
                it?.startActivity(intent)
            }
        }

        // 한국장학재단 웹사이트로 이동
        kosafWeb.setOnClickListener {
            var uri = Uri.parse("www.kosaf.go.kr")
            var intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }

        // 교내 웹사이트로 이동
        univWeb.setOnClickListener {
            var uri = Uri.parse("www.kosaf.go.kr")
            var intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }

        // ? 웹사이트로 이동
        guessWeb.setOnClickListener {
            var uri = Uri.parse("www.kosaf.go.kr")
            var intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }

        super.onActivityCreated(savedInstanceState)
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
}


