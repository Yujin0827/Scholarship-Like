package com.cookandroid.scholarshiplike

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.cookandroid.scholarshiplike.adapter.MagazineRecyclerViewAdapter
import com.cookandroid.scholarshiplike.databinding.FragmentMagazineBinding
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.toObject

class MagazineFragment : Fragment() {
    @Suppress("PrivatePropertyName")
    private val TAG = javaClass.simpleName

    // 뷰 바인딩
    private var mBinding: FragmentMagazineBinding? = null
    private val binding get() = mBinding!!

    private var firestore : FirebaseFirestore? = null // Firestore 인스턴스
    private var postList : ArrayList<Post> = arrayListOf() // 파이어스토어에서 불러온 데이터 전체 저장하는 리스트 (게시물 리스트)
    private var postfinanceList : ArrayList<Post> = arrayListOf() //금융 카테고리 저장 리스트
    private var postlifeList : ArrayList<Post> = arrayListOf() //생활 카테고리 저장 리스트
    private var postetcList : ArrayList<Post> = arrayListOf() //기타 카테고리 저장 리스트
    private lateinit var postlistAdapter: MagazineRecyclerViewAdapter //매거진 리사이클러뷰 변수 생성
    private lateinit var mContext : Context // 액티비티 정보 저장 변수

    override fun onAttach(context: Context) {
        super.onAttach(context)

        // 액티비티 정보 저장
        mContext = context

        // 파이어스토어에서 매거진 데이터 불러오기
        firestore = FirebaseFirestore.getInstance() // Firestore 인스턴스 초기화

        firestore?.collection("Magazine")?.get()?.addOnSuccessListener { result ->
            // 성공할 경우
            postList.clear()

            for (document in result) {  // 가져온 문서들은 result에 들어감
                val item = document.toObject<Post>()
                postList.add(item)

                //카테고리 분류
                if(document["cartegory"]=="금융") postfinanceList.add(item) else if(document["cartegory"]=="생활")  postlifeList.add(item) else  postetcList.add(item)
            }
            Log.d("postList", postList.toString())
            postlistAdapter.submitList(postList)
        }?.addOnFailureListener { exception ->
            // 실패할 경우
            Log.w(TAG, "Error getting documents: $exception")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = FragmentMagazineBinding.inflate(inflater, container, false)
        val view = binding.root

        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 어댑터 저장 (최초 1회)
        postlistAdapter = MagazineRecyclerViewAdapter(postList,mContext)
        Mconnect()

        binding.all.isSelected = true

        //버튼 클릭 시 리스트 변경 및 버튼 효과
        binding.all.setOnClickListener {
            postlistAdapter = MagazineRecyclerViewAdapter(postList,mContext)
            Mconnect()
            binding.all.isSelected = true
            binding.finance.isSelected = false
            binding.life.isSelected = false
            binding.etc.isSelected = false
        }
        binding.finance.setOnClickListener {
            postlistAdapter = MagazineRecyclerViewAdapter(postfinanceList,mContext)
            Mconnect()
            binding.all.isSelected = false
            binding.finance.isSelected = true
            binding.life.isSelected = false
            binding.etc.isSelected = false
        }
        binding.life.setOnClickListener {
            postlistAdapter = MagazineRecyclerViewAdapter(postlifeList,mContext)
            Mconnect()
            binding.all.isSelected = false
            binding.finance.isSelected = false
            binding.life.isSelected = true
            binding.etc.isSelected = false
        }
        binding.etc.setOnClickListener {
            postlistAdapter = MagazineRecyclerViewAdapter(postetcList,mContext)
            Mconnect()
            binding.all.isSelected = false
            binding.finance.isSelected = false
            binding.life.isSelected = false
            binding.etc.isSelected = true
        }

    }

    // 매거진 리사이클러뷰-어댑터 연결
    fun Mconnect() {
        binding.magazinerecyclerView.layoutManager = GridLayoutManager(activity, 2) //그리드 레아이웃 지정
        binding.magazinerecyclerView.setHasFixedSize(true) //리사이클러뷰 성능 개선 방안
        binding.magazinerecyclerView.adapter = postlistAdapter //어댑터 연결
    }

    // 프래그먼트 생성시 툴바 hide
    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()

        // 아래 스와이프로 새로고침
        binding.swipeRefreshFragmentMagazine.setOnRefreshListener {
            Handler().postDelayed({// 아래로 스와이핑 이후 1초 후에 리플래쉬 아이콘 없애기
                if (binding.swipeRefreshFragmentMagazine.isRefreshing)
                    binding.swipeRefreshFragmentMagazine.isRefreshing = false
            }, 1000)
            Mconnect()
        }
    }

    // 프래그먼트 종료시 툴바 show
    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
    }

    override fun onDestroy() {
        mBinding = null
        super.onDestroy()
    }
}