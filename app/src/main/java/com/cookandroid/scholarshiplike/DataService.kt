package com.cookandroid.scholarshiplike

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import kotlin.properties.Delegates


class DataService : Service() {
    @Suppress("PrivatePropertyName")
    private val TAG = javaClass.simpleName

    // Binder given to clients
    private val binder = DataLocalBinder()

    // DB list
    private val userDb = Firebase.firestore.collection("Users")
//    private val scholarshipDb = Firebase.firestore.collection("Scholarship").document("Nation").collection("ScholarshipList")
    private val scholarshipDb = Firebase.firestore.collectionGroup("scholarshipList")
    private val magazineDb = Firebase.firestore.collection("Magazine")

    private val user = Firebase.auth.currentUser!!

    private var mMagazineList: ArrayList<String> by Delegates.observable(
        arrayListOf(), { _, _, _ ->
            getLikeMagazine()
        })

    private var mScholarshipList: ArrayList<String> by Delegates.observable(
        arrayListOf(), { _, _, _ ->
//            getLikeScholarship()
        })

    /** method for clients  */
    val mMagazine: ArrayList<Post> = arrayListOf()
    val mScholarship: ArrayList<Scholarship> = arrayListOf()

    inner class DataLocalBinder : Binder() {
        fun getService(): DataService = this@DataService
    }

    override fun onCreate() {
        super.onCreate()
        Log.w(TAG, "onCreate()")

        val scope = CoroutineScope(Job() + Dispatchers.IO)
        Log.d(TAG, "coroutine launch")

        scope.launch {
            Log.d(TAG, "coroutine active")
            getLikeContent()
            joinAll()
            Log.d(TAG, "coroutine dead")
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.w(TAG, "onStartCommand()")
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent): IBinder {
        Log.w(TAG, "onBind()")
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.w(TAG, "onUnbind()")
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        Log.w(TAG, "onDestroy()")
        super.onDestroy()
    }

    @Suppress("UNCHECKED_CAST")
    suspend fun getLikeContent() {
        Log.d(TAG, "likeContent() launch")
        withContext(Dispatchers.IO) {
            Log.d(TAG, "likeContent() active")
            userDb.document(user.uid).get().addOnSuccessListener { document ->
                mMagazineList = document.data!!["likeMagazine"] as ArrayList<String>
                mScholarshipList = document.data!!["likeScholarship"] as ArrayList<String>

                Log.w(TAG, "$mMagazineList : magazineList")
                Log.w(TAG, "$mScholarshipList : scholarshipList")

            }.addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: $exception")
            }
        }
    }

    private fun getLikeMagazine() {
        Log.d(TAG, "likeMagazine() active")
        mMagazineList.removeAt(0)
        magazineDb.whereIn("title", mMagazineList).get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.w(TAG, document.id)
                    val data = document.toObject<Post>()
                    Log.w(TAG, "$data")
                    mMagazine.add(data)
                }
            }.addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: $exception")
            }
    }

    private fun getLikeScholarship() {
        Log.d(TAG, "likeScholarship() active")
        mScholarshipList.removeAt(0)
        Log.d(TAG, "mScholarshipList[0] : "+mScholarshipList[0])
//        for (scholName in mScholarshipList) {
//            Log.d(TAG, "scholName : $scholName")
            scholarshipDb.whereIn(FieldPath.documentId(), mScholarshipList).get()
                .addOnSuccessListener { it ->
                    for (doc in it) {
                        Log.w(TAG, doc.id)
//                    setDataShape(mScholarship, it)
                        Log.w(TAG, "setDataShape() active")

                        //  필드값 가져오기
                        val paymentType = doc["paymentType"].toString()
                        val period = doc["period"] as Map<String, Timestamp>
                        val startdate = period.get("startDate")?.toDate()
                        val enddate = period.get("endDate")?.toDate()
                        val startdate2 = period.get("startDate2")?.toDate()
                        val enddate2 = period.get("endDate2")?.toDate()
                        val institution = doc["paymentInstitution"].toString()

                        val date = SimpleDateFormat("yyyy-MM-dd") // 날짜 형식으로 변환

                        if ((startdate2 == null) && enddate2 == null) {
                            if (startdate == null && enddate == null) {
                                val item = Scholarship(
                                    paymentType,
                                    doc.id,
                                    "자동 신청",
                                    "",
                                    "",
                                    "",
                                    institution
                                )
                                mScholarship.add(item)
                                Log.w(TAG, "item : $item")
                            } else if (startdate == enddate) {
                                val item = Scholarship(
                                    paymentType,
                                    doc.id,
                                    "추후 공지",
                                    "",
                                    "",
                                    "",
                                    institution
                                )
                                mScholarship.add(item)
                                Log.w(TAG, "item : $item")
                            } else {
                                val item = Scholarship(
                                    paymentType,
                                    doc.id,
                                    date.format(startdate!!),
                                    date.format(enddate!!),
                                    "",
                                    "",
                                    institution
                                )
                                mScholarship.add(item)
                                Log.w(TAG, "item : $item")
                            }
                        } else {
                            val item = Scholarship(
                                paymentType,
                                doc.id,
                                date.format(startdate!!),
                                date.format(enddate!!),
                                date.format(startdate2!!),
                                date.format(enddate2!!),
                                institution
                            )
                            mScholarship.add(item)
                            Log.w(TAG, "item : $item")
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents : $exception")
                }
//        }

//        scholarshipDb.document(mScholarshipList[0]).get()
//            .addOnSuccessListener { it ->
//                Log.w(TAG, it.id)
//                setDataShape(mScholarship, it)
//            }

//        scholarshipDb.whereIn("title", mScholarshipList).get()
//        scholarshipDb.whereArrayContains(FieldPath.documentId(), mScholarshipList).get()
//        scholarshipDb.whereIn(FieldPath.documentId(), mScholarshipList).get()
//        scholarshipDb.where(firebase.firestore.FieldPath.documentId(), 'in',mScholarshipList).get()
//        mScholarshipList.removeAt(0)
////        for (sh in mScholarshipList) {
//            scholarshipDb.document(sh).get()
//                .addOnSuccessListener { result ->
////                for (document in result) {
////                    if()
////                    Log.w(TAG, document.id)
////                    val data = document.toObject<Scholarship>()
////                    Log.w(TAG, "$data")
////                    mScholarship.add(data)
////                }
//                    Log.w(TAG, "scholarshipDb get() success")
//                    Log.w(TAG, result.exists().toString())
////                for(scholName in mScholarshipList) {
////                    Log.w(TAG, "scholName : $scholName")
//                    for (document in result) {
//                        Log.w(TAG, "document : $document")
////                        if(scholName == document.id) {
//                        Log.w(TAG, document.id)
//                        val data = document.toObject<Scholarship>()
//                        Log.w(TAG, "$data")
//                    if (data != null) {
//                        mScholarship.add(data)
//                    }
//                        }
////                    }
////                }
//                }.addOnFailureListener { exception ->
//                    Log.w(TAG, "Error getting documents: $exception")
//                }
////        }
        Log.w(TAG, "mScholarship : $mScholarship")
    }

    private fun setDataShape(list : MutableList<Scholarship>, snap: DocumentSnapshot) {
        Log.w(TAG, "setDataShape() active")

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
                Log.w(TAG, "item : $item")
            }
            else if(startdate == enddate){
                val item = Scholarship(paymentType, snap.id, "추후 공지", "", "", "", institution)
                list.add(item)
                Log.w(TAG, "item : $item")
            }
            else{
                val item = Scholarship(paymentType, snap.id, date.format(startdate!!), date.format(enddate!!), "", "", institution)
                list.add(item)
                Log.w(TAG, "item : $item")
            }
        }
        else{
            val item = Scholarship(paymentType, snap.id, date.format(startdate!!), date.format(enddate!!), date.format(startdate2!!), date.format(enddate2!!), institution)
            list.add(item)
            Log.w(TAG, "item : $item")
        }
    }
}