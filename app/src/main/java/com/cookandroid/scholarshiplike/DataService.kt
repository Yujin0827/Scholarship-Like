package com.cookandroid.scholarshiplike

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlin.properties.Delegates

class DataService : Service() {
    @Suppress("PrivatePropertyName")
    private val TAG = javaClass.simpleName

    // Binder given to clients
    private val binder = DataLocalBinder()

    // DB list
    private val userDb = Firebase.firestore.collection("Users")
    private val scholarshipDb = Firebase.firestore.collection("Scholarship")
    private val magazineDb = Firebase.firestore.collection("Magazine")

    private val user = Firebase.auth.currentUser!!


    private var mMagazineList: ArrayList<String> by Delegates.observable(
        arrayListOf(), { _, _, _ ->
            getLikeMagazine()
        })

    private var mScholarshipList: ArrayList<String> by Delegates.observable(
        arrayListOf(), { _, _, _ ->
            getLikeScholarship()
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
                val likeContent = document.data!!["likeContent"] as Map<*, *>

                mMagazineList = likeContent["magazine"] as ArrayList<String>
                mScholarshipList = likeContent["scholarship"] as ArrayList<String>

                Log.w(TAG, "$mMagazineList : magazineList")
                Log.w(TAG, "$mScholarshipList : scholarshipList")

            }.addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: $exception")
            }
        }
    }

    private fun getLikeMagazine() {
        Log.d(TAG, "likeMagazine() active")
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
        scholarshipDb.whereIn("title", mScholarshipList).get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.w(TAG, document.id)
                    val data = document.toObject<Scholarship>()
                    Log.w(TAG, "$data")
                    mScholarship.add(data)
                }
            }.addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: $exception")
            }
    }
}