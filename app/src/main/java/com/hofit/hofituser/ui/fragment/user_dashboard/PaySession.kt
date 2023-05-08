package com.hofit.hofituser.ui.fragment.user_dashboard

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.hofit.hofituser.PaymentPass
import com.hofit.hofituser.R
import com.hofit.hofituser.adapter.SessionAdapter
import com.hofit.hofituser.models.SessionModel

class PaySession : Fragment(){

    private lateinit var mCenterRecyclerView: RecyclerView
    private lateinit var searchView: SearchView

    private var session: ArrayList<SessionModel> = arrayListOf()
    private var matchedSession: ArrayList<SessionModel> = arrayListOf()
    private var sessionAdapter: SessionAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_pay_session, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        performSearch()
        init(view)
        fetchCenter()
    }

    private fun init(view: View) {

        searchView = view.findViewById(R.id.search_center)
        searchView.clearFocus()

        mCenterRecyclerView = view.findViewById(R.id.show_all_session)
        mCenterRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        sessionAdapter = SessionAdapter(requireContext(), session).also {
            mCenterRecyclerView.adapter = it
            mCenterRecyclerView.adapter!!.notifyDataSetChanged()
        }
    }

    private fun performSearch() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                search(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                search(newText)
                return true
            }
        })
    }

    private fun search(text: String?) {
        matchedSession = arrayListOf()

        text?.let {
            session.forEach { sessionP ->
                if (sessionP.session_title.contains(text, true) ||
                    sessionP.session_amount.contains(text, true)
                ) {
                    matchedSession.add(sessionP)
                }
            }
            updateRecyclerView()
            if (matchedSession.isEmpty()) {
                matchedSession = session
            }
            updateRecyclerView()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateRecyclerView() {
        mCenterRecyclerView.apply {
            sessionAdapter?.session = matchedSession
            sessionAdapter?.notifyDataSetChanged()
        }
    }

    private fun fetchCenter() {

        FirebaseFirestore.getInstance().collection("super_admin").document("rohit-20072022")
            .collection("center_sessions")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val id = document.get("session_id").toString()
                    val imageUrl = document.get("url").toString()
                    val title = document.get("session_title").toString()
                    val center = document.get("session_center").toString()
                    val date = document.get("session_date").toString()
                    val time = document.get("session_time").toString()
                    val price = document.get("session_amount").toString()
                    val discount = document.get("session_discount").toString()
                    session.add(
                        SessionModel(
                            imageUrl,
                            id,
                            title,
                            center,
                            date,
                            time,
                            price,
                            discount
                        )
                    )
                }

                mCenterRecyclerView.adapter = sessionAdapter
                sessionAdapter?.setOnItemClickListener(object : SessionAdapter.OnItemClickListener {
                    override fun onItemClick(position: Int) {
                        val timing = session[position].session_date + "," + session[position].session_time

                        val intent = Intent(requireContext(), PaymentPass::class.java)
                        intent.putExtra("layout_id", "Session_id_59")
                        intent.putExtra("session_title", session[position].session_title)
                        intent.putExtra("session_center", session[position].session_center)
                        intent.putExtra("session_date", timing)
                        intent.putExtra("session_amount", session[position].session_amount)
                        intent.putExtra("session_discount", session[position].session_discount)
                        startActivity(intent)
                    }
                })
            }
            .addOnFailureListener { exception ->
                Log.d("TAG", "Error getting documents: ", exception)
            }
    }

}