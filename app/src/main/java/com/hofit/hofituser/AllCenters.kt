package com.hofit.hofituser

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.hofit.hofituser.models.CentersModel
import com.hofit.hofituser.ui.UserHome.CenterAdapter


class AllCenters : Fragment() {

    lateinit var centerRecyclerViewAll: RecyclerView
    lateinit var mCenterCity: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_all_centers, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mCenterCity = requireArguments().getString("center_city").toString()

        showCentersList(view)
    }

    private fun showCentersList(view: View) {
        centerRecyclerViewAll = view.findViewById(R.id.activity_centers_all)
        centerRecyclerViewAll.layoutManager =
            LinearLayoutManager(view.context)
        fetchCenter()
    }

    private fun fetchCenter() {
        val list = mutableListOf<CentersModel>()

        FirebaseFirestore.getInstance().collection("super_admin").document("rohit-20072022")
            .collection("sports_centers")
            .whereEqualTo("center_city", mCenterCity)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val title = document.get("center_name").toString()
                    val type = document.get("center_category").toString()
                    val image = document.get("center_image").toString()
                    val rating = document.get("center_rating").toString()
                    val id = document.get("center_id").toString()
                    list.add(CentersModel(id,title, type, image, rating))
                }
                val adapter = CenterAdapter(requireActivity(), list)
                centerRecyclerViewAll.adapter = adapter
                adapter.setOnItemClickListener(object : CenterAdapter.OnItemClickListener {
                    override fun onItemClick(position: Int) {
                        findNavController().navigate(R.id.action_allCenters_to_centerFullDetails2,Bundle().apply {
                            putString("center_id", list[position].center_id)
                        })
                    }
                })
            }
            .addOnFailureListener { exception ->
                Log.d("Firebase Exception", "Error getting documents: ", exception)
            }
    }
}