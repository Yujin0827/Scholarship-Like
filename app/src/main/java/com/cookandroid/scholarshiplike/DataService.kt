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

class DataService : Service() {
    @Suppress("PrivatePropertyName")
    private val TAG = javaClass.simpleName

    // Binder given to clients
    private val binder = DataLocalBinder()

    private val user_db = Firebase.firestore.collection("Users")
    private val scholarship_db = Firebase.firestore.collection("Scholarship")
    private val magazine_db = Firebase.firestore.collection("Magazine")

    private val user = Firebase.auth.currentUser

    // DB list
    private var magazineList: ArrayList<String> = arrayListOf()
    private var sholarshipList: ArrayList<String> = arrayListOf()

    /** method for clients  */
    val magazine: ArrayList<Post> = arrayListOf()
    val scholar: ArrayList<Scholarship> = arrayListOf()

    inner class DataLocalBinder : Binder() {
        fun getService(): DataService = this@DataService
    }

    override fun onCreate() {
        super.onCreate()
        Log.w(TAG, "onCreate()")

        if (user != null) {
            user_db.document(user.uid).get().addOnSuccessListener { document ->
                if (document.data!!["likeContent"] != null) {
                    val data = document.data!!["likeContent"] as Map<*, *>

                    @Suppress("UNCHECKED_CAST")
                    magazineList = data["magazine"] as ArrayList<String>
                    Log.w(TAG, "$magazineList : magazineList")
                }

                magazine_db.whereIn("title", magazineList).get()
                    .addOnSuccessListener { result ->
                        for (document in result) {
                            Log.w(TAG, document.id)
                            val data = document.toObject<Post>()
                            Log.w(TAG, data.toString())
                            magazine.add(data)
                        }
                    }.addOnFailureListener { exception ->
                        Log.w(TAG, "Error getting documents: $exception")
                    }

            }.addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: $exception")
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
        Log.w(TAG, "onStartCommand()")
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

}