package com.cookandroid.scholarshiplike

import VerticalItemDecorator
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cookandroid.scholarshiplike.adapter.SupportDetailRecyclerViewAdapter
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlin.concurrent.thread

class SupportFundDetailActivity : AppCompatActivity() {

    private lateinit var listAdapter: SupportDetailRecyclerViewAdapter    // 리사이클러뷰 어댑터


    private var db = Firebase.firestore
    var dataList: MutableList<DetailSupport> = arrayListOf()    // 특정 지원금 리스트
    private lateinit var mContext1: Context     //프래그먼트의 정보 받아오는 컨텍스트 선언





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_support_fund_detail)

        mContext1 = FragmentActivity()



        val recycler : RecyclerView = findViewById(R.id.support_list)
        val mainTitle : TextView = findViewById(R.id.title)

        listAdapter = SupportDetailRecyclerViewAdapter(dataList)
        recycler.addItemDecoration(VerticalItemDecorator(17))
        recycler.addItemDecoration(HorizontalItemDecorator(10))
        recycler.layoutManager = LinearLayoutManager(mContext1, RecyclerView.VERTICAL, false)



        recycler.adapter = listAdapter

        val title = intent.getStringExtra("title")
        Log.w("eeeeeeeeeeeeeeeeeeeeeeeeeeeeeee", title.toString() )
        mainTitle.text = title

        var ref =  db.collectionGroup("Support")
        var cnt = 0

        ref.get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    if(document.id == title){
                        var doc = document.data


                        while(cnt <= doc.size) {
                            Log.w("CNT : ", cnt.toString())


                            val item = document[cnt.toString()] as Map<String, String>?
                            val title_ = item?.get("title").toString()
                            val content = item?.get("content").toString()

                            val i = DetailSupport(title_, content)

                            if (item != null) {
                                dataList.add(i)
                            }

                            cnt++


                            Log.w("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", item.toString())
                        }

                    }
                }
                Log.w("ttttttttttttttttttttttttttttttttttttttttt", dataList.toString())
                listAdapter.submitList(dataList)

            }
    }






    }










