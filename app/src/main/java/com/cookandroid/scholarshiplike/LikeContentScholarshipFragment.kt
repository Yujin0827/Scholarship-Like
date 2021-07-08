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
import com.cookandroid.scholarshiplike.adapter.ScholarshipRecyclerViewAdapter
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_recycler.*

class LikeContentScholarshipFragment : Fragment() {
    private lateinit var listAdapter: ScholarshipRecyclerViewAdapter
    private var db = Firebase.firestore
    private var scholarList: ArrayList<String> = arrayListOf()
    private var scholar: ArrayList<Scholarship> = arrayListOf()
    private lateinit var mContext : Context
    private val user = Firebase.auth.currentUser

    private val TAG = "LikeScholarFragment"

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context

        var ref = db.collection("Users")

        Log.w(TAG, "Load Firestore")
        if (user != null) {
            ref.document(user.uid).get().
            addOnSuccessListener { document ->
                if (document.data != null){
                    if (document.data!!.get("likeContent") != null) {
                        val data = document.data!!["likeContent"] as Map<String, String>
                        scholarList = data["scholarship"] as ArrayList<String>
                    }

                    Log.w(TAG, scholarList.toString())

                    db.collection("국가").get()
                        .addOnSuccessListener { result ->
                            for (document in result) {
                                Log.w(TAG, document.id)
                                for(title in scholarList){
                                    if(document.id == title){
                                        scholar.add(Scholarship(document.id,"adfadsf","adfasdf",true))
                                    }
                                }

                                Log.w(TAG, scholar.toString())

                            }
                            listAdapter.submitList(scholar)
                        }.addOnFailureListener { exception ->
                            Log.w(TAG, "Error getting documents: $exception")
                        }
                }
            }.addOnFailureListener { exception ->
                // 실패할 경우
                Log.w(TAG, "Error getting documents: $exception")
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_recycler, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Fragment에서 전달받은 list를 넘기면서 ListAdapter 생성
        listAdapter = ScholarshipRecyclerViewAdapter(scholar,mContext)
        listView.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        // RecyclerView.adapter에 지정
        listView.adapter = listAdapter

    }
}