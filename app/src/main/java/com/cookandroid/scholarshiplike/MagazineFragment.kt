package com.cookandroid.scholarshiplike

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.firestore.*
import kotlinx.android.synthetic.main.fragment_magazine.*

class MagazineFragment : Fragment() {

    private var firestore : FirebaseFirestore? = null // Firestore 인스턴스
    private var postList : ArrayList<Post> = arrayListOf() // 파이어스토어에서 불러온 데이터 저장하는 리스트 (게시물 리스트)
    private lateinit var postlistAdapter: MagazineRecyclerViewAdapter //매거진 리사이클러뷰 변수 생성
    private lateinit var mContext : Context // 액티비티 정보 저장 변수

    override fun onAttach(context: Context) {
        super.onAttach(context)

        // 액티비티 정보 저장
        mContext = context;

        // 파이어스토어에서 매거진 데이터 불러오기
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_magazine, container, false)

        // 어댑터 저장
        postlistAdapter = MagazineRecyclerViewAdapter(postList,mContext)
        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 매거진 리사이클러뷰-어댑터 연결
        magazinerecyclerView.layoutManager = GridLayoutManager(activity, 2) //그리드 레아이웃 지정
        magazinerecyclerView.setHasFixedSize(true) //리사이클러뷰 성능 개선 방안
        magazinerecyclerView.adapter = postlistAdapter //어댑터 연결
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