package com.cookandroid.scholarshiplike
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_recycler.*


class LikeContentMagazineFragment : Fragment() {
    private lateinit var listAdapter: MagazineRecyclerViewAdapter
    private var db = Firebase.firestore
    private var magazineList: ArrayList<String> = arrayListOf()
    private var magazine: ArrayList<Post> = arrayListOf()
    private lateinit var mContext : Context
    private val user = Firebase.auth.currentUser

    val TAG = "LikeContentMagazine"

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
                        magazineList = data["magazine"] as ArrayList<String>
                    }

                    Log.w(TAG, magazineList.toString())

                    db.collection("매거진").whereIn("title", magazineList).get()
                        .addOnSuccessListener { result ->
                            for (document in result) {
                                Log.w(TAG, document.id)
                                val data = document.toObject<Post>()
                                Log.w(TAG, data.toString())
                                magazine.add(data)
                            }
                            listAdapter.submitList(magazine)
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
        listAdapter = MagazineRecyclerViewAdapter(magazine, mContext)

        listView.layoutManager = GridLayoutManager(activity, 2) //그리드 레아이웃 지정
        listView.adapter = listAdapter //어댑터 연결
    }
}