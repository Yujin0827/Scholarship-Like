package com.cookandroid.scholarshiplike

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.*
import kotlinx.android.synthetic.main.fragment_magazine.*

class MagazineFragment : Fragment() {
    lateinit var postlistAdapter: MagazineRecyclerViewAdapter
    private var firestore : FirebaseFirestore? = null // Firestore 인스턴스
    var postList : ArrayList<Post> = arrayListOf()
    private lateinit var mContext1 : Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext1 = context;

        //파이어스토어 매거진 데이터 불러오기
        firestore = FirebaseFirestore.getInstance() // Firestore 인스턴스 초기화

        firestore?.collection("장학라이크")?.document("매거진")?.collection("전체")?.get()?.addOnSuccessListener { result ->
            // 성공할 경우
            postList?.clear()

            for (document in result) {  // 가져온 문서들은 result에 들어감
                val item = Post(document["title"] as String, document["category"] as String, document["contents"] as String)
                postList.add(item)
            }
            Log.d("postList", postList.toString())
            postlistAdapter.submitList(postList)
        }?.addOnFailureListener { exception ->
            // 실패할 경우
            Log.w("MainActivity", "Error getting documents: $exception")
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.v("OnCreate", "ENTER")

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_magazine, container, false)
        Log.v("OnCreateView", "ENTER")

        postlistAdapter = MagazineRecyclerViewAdapter(postList,mContext1)
        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.v("OnViewCreated", "ENTER")
        magazinerecyclerView.layoutManager = LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false)
        magazinerecyclerView.setHasFixedSize(true) //리사이클러뷰 성능 개선 방안
        magazinerecyclerView.adapter = postlistAdapter
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