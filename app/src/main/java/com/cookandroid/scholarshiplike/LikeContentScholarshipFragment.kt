package com.cookandroid.scholarshiplike

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cookandroid.scholarshiplike.adapter.MagazineRecyclerViewAdapter
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_recycler.*
import java.text.SimpleDateFormat

class LikeContentScholarshipFragment : Fragment() {
    @Suppress("PrivatePropertyName")
    private val TAG = javaClass.simpleName

    private lateinit var listAdapter: ScholarshipRecyclerViewAdapter
    private lateinit var mContext : Context
    private var scholarshipList: ArrayList<Scholarship> = arrayListOf()

    lateinit var auth: FirebaseAuth // Firebase Auth
    private var db = Firebase.firestore // Firebase


    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
        auth = Firebase.auth
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_recycler, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.w(TAG, "onViewCreated()")

        // 서비스의 데이터와 연결
//        scholarshipList = (activity as LikeContentActivity).mService.mScholarship
        getLikeDate()
        Log.w(TAG, "$scholarshipList : SCHOLARSHIP")

    }


    override fun onDestroy() {
        super.onDestroy()
        Log.w(TAG, "destroy")
    }

    fun getLikeDate() {
        val user = auth.currentUser // 현재 유저
        var scholarNameList = arrayListOf<String>()// 좋아요 누른 장학금 이름 저장

        // 사용자가 좋아요 누른 장학금 배열 이름 가져오기
        if (user != null) {
            db.collection("Users")
                .document(user.uid)
                .get()
                .addOnSuccessListener { result ->
                    scholarNameList = result["likeScholarship"] as ArrayList<String>
                    scholarNameList.removeAt(0)
                    Log.d(TAG, scholarNameList.toString())

                    for(scholName in scholarNameList) {
                        Log.d(TAG, scholName)
                        if (scholName != null) {
                            db.collection("Scholarship")
                                .document("Nation")
                                .collection("ScholarshipList")
                                .document(scholName)
                                .get()
                                .addOnSuccessListener { doc ->
                                    if(doc.data != null) {
                                        Log.d(TAG, doc.id)
                                        setDataShape(scholarshipList, doc)
                                        setAdapter()
                                    }
                                }
                                .addOnFailureListener { it ->
                                    Log.d(TAG, it.toString())
                                }

                            db.collection("Scholarship")
                                .document("OutScholar")
                                .collection("ScholarshipList")
                                .document(scholName)
                                .get()
                                .addOnSuccessListener { doc ->
                                    if(doc.data != null) {
                                        Log.d(TAG, doc.id)
                                        setDataShape(scholarshipList, doc)
                                        setAdapter()
                                    }
                                }
                                .addOnFailureListener { it ->
                                    Log.d(TAG, it.toString())
                                }

                            db.collection("Scholarship")
                                .document("UnivScholar")
                                .collection("ScholarshipList")
                                .document(scholName)
                                .get()
                                .addOnSuccessListener { doc ->
                                    if(doc.data != null) {
                                        Log.d(TAG, doc.id)
                                        setDataShape(scholarshipList, doc)
                                        setAdapter()
                                    }
                                }
                                .addOnFailureListener { it ->
                                    Log.d(TAG, it.toString())
                                }
                        }
                        else {
                            Log.d(TAG, "else : " + scholName)
                        }
                    }
                }
        }
    }

    private fun setDataShape(list : MutableList<Scholarship>, snap: DocumentSnapshot) {

        //  필드값 가져오기
        val paymentType = snap["paymentType"].toString()
        val period = snap["period"] as Map<String, Timestamp>
        val startdate = period.get("startDate")?.toDate()
        val enddate = period.get("endDate")?.toDate()
        val startdate2 = period.get("startDate2")?.toDate()
        val enddate2 = period.get("endDate2")?.toDate()
        val institution = snap["paymentInstitution"].toString()

        val date = SimpleDateFormat("yyyy-MM-dd") // 날짜 형식으로 변환

        if((startdate2 == null)&& enddate2 == null){
            if(startdate == null && enddate == null){
                val item = Scholarship(paymentType, snap.id, "자동 신청", "", "", "", institution)
                list.add(item)
                Log.d(TAG, "item : $item")
            }
            else if(startdate == enddate){
                val item = Scholarship(paymentType, snap.id, "추후 공지", "", "", "", institution)
                list.add(item)
                Log.d(TAG, "item : $item")
            }
            else{
                val item = Scholarship(paymentType, snap.id, date.format(startdate!!), date.format(enddate!!), "", "", institution)
                list.add(item)
                Log.d(TAG, "item : $item")
            }
        }
        else{
            val item = Scholarship(paymentType, snap.id, date.format(startdate!!), date.format(enddate!!), date.format(startdate2!!), date.format(enddate2!!), institution)
            list.add(item)
            Log.d(TAG, "item : $item")
        }
    }

    fun setAdapter() {
        Log.d(TAG, "ScholarshipList is not null!")
        Log.w(TAG, "$scholarshipList : SCHOLARSHIP")
        // Fragment 에서 전달받은 list 를 넘기면서 ListAdapter 생성
        listAdapter = ScholarshipRecyclerViewAdapter(scholarshipList, mContext)
        listAdapter.submitList(scholarshipList)

        listView.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        listView.adapter = listAdapter //어댑터 연결
    }
}