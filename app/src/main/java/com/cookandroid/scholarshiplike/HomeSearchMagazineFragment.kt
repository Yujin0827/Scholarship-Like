package com.cookandroid.scholarshiplike

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_recycler.*


class HomeSearchMagazineFragment : Fragment() {


    private lateinit var listAdapter: MagazineRecyclerViewAdapter

    private var  dataList: ArrayList<Post> = arrayListOf()
    private var searchDataList: ArrayList<Post> = arrayListOf()
    lateinit var mContext : Context
    lateinit var searchData: String
    private var db = Firebase.firestore
    private val sRef = db.collection("장학라이크")
        .document("매거진").collection("전체")

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        sRef // 작업할 문서
            .get()      // 문서 가져오기
            .addOnSuccessListener { result ->
                for (document in result) {  // 가져온 문서들은 result에 들어감
                    val item = Post(document.id,".",".")
                    searchDataList.add(item)
                    Log.w("DataList", searchDataList.toString())
                }
                Log.w("load firebase data", "Success")

            }
            .addOnFailureListener { exception ->
                // 실패할 경우
                Log.w("MainActivity", "Error getting documents: $exception")
            }

        return inflater.inflate(com.cookandroid.scholarshiplike.R.layout.fragment_recycler, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Fragment에서 전달받은 list를 넘기면서 ListAdapter 생성

        listAdapter = MagazineRecyclerViewAdapter(searchDataList,mContext)
        listAdapter.submitList(searchDataList)
        listView.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        // RecyclerView.adapter에 지정
        listView.adapter = listAdapter
    }


    fun update(s: String){
        sRef.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
            // ArrayList 비워줌
            searchData = s
            searchDataList.clear()

            for (snapshot in querySnapshot!!.documents) {
                if (snapshot.getString("title")!!.contains(searchData)) {
                    var item = snapshot.toObject(Post::class.java)
                    searchDataList.add(item!!)
                }
            }
        }
    }

}