package com.cookandroid.scholarshiplike

import android.R
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_recycler.*


class HomeSearchMagazineFragment : Fragment() {
    private lateinit var editText: EditText
    private lateinit var fragmentListener: FragmentListener

    private lateinit var listAdapter: MagazineRecyclerViewAdapter
    private var db = Firebase.firestore
    var dataList: ArrayList<Post> = arrayListOf()
    lateinit var mContext : Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
        val sRef = db.collection("장학금")
            .document("교내").collection("강원")
            .document("강원대").collection("학과")

        sRef // 작업할 문서
            .get()      // 문서 가져오기
            .addOnSuccessListener { result ->
                for (document in result) {  // 가져온 문서들은 result에 들어감
                    val item = Post(document.id,".",".")
                    dataList.add(item)
                }
                listAdapter.submitList(dataList)
                Log.w("MainActivity", "Error aaaaaaa: ")

            }
            .addOnFailureListener { exception ->
                // 실패할 경우
                Log.w("MainActivity", "Error getting documents: $exception")
            }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_recycler, container, false)

        val view: View = inflater.inflate(R.layout.fragment_b, container, false)
        editText = view.findViewById<View>(R.id.input)
        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Fragment에서 전달받은 list를 넘기면서 ListAdapter 생성
        listAdapter = MagazineRecyclerViewAdapter(dataList,mContext)
        listView.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        // RecyclerView.adapter에 지정
        listView.adapter = listAdapter

        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (fragmentListener != null) {
                    fragmentListener.onTextChange(s.toString())
                }
            }
        })
    }

    fun setFragmentListener(listener: FragmentListener?) {
        fragmentListener = listener!!
    }
}