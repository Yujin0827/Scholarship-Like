package com.cookandroid.scholarshiplike

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cookandroid.scholarshiplike.adapter.MagazineRecyclerViewAdapter
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_recycler.*


class HomeSearchMagazineFragment : Fragment() {

    private var  dataList: ArrayList<Post> = arrayListOf()
    private var searchDataList: ArrayList<Post> = arrayListOf()
    private lateinit var listAdapter: MagazineRecyclerViewAdapter
    private lateinit var mContext : Context
    private val db = Firebase.firestore
    private val sRef = db.collection("장학금")
        .document("교내").collection("강원")
        .document("강원대").collection("학과")

    override fun onAttach(context: Context) {
        super.onAttach(context)
        sRef // 작업할 문서
            .get()      // 문서 가져오기
            .addOnSuccessListener { result ->
                for (document in result) {  // 가져온 문서들은 result에 들어감
                    val item = Post(document.id,".",".")
                    dataList.add(item)
                }
                Log.w("load firebase data", "Success")

            }
            .addOnFailureListener { exception ->
                // 실패할 경우
                Log.w("MainActivity", "Error getting documents: $exception")
            }

        mContext = context as Activity
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_recycler, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Fragment에서 전달받은 list를 넘기면서 ListAdapter 생성

        listAdapter = MagazineRecyclerViewAdapter(searchDataList, mContext)
        listAdapter.submitList(searchDataList)
        listView.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        // RecyclerView.adapter에 지정
        listView.adapter = listAdapter
    }


    fun update(s: String){
        searchDataList.clear()

        for (item in dataList) {
            if (item.title.contains(s)) {
                searchDataList.add(item)

            }
        }
        Log.w("Data input", searchDataList.toString())

        if(::listAdapter.isInitialized)
            listAdapter.notifyDataSetChanged()
    }
}