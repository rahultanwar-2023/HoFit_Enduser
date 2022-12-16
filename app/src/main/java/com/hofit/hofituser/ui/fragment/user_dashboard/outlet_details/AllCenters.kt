package com.hofit.hofituser.ui.fragment.user_dashboard.outlet_details

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.hofit.hofituser.R
import com.hofit.hofituser.models.OutletModel
import com.hofit.hofituser.adapter.CenterAdapter


class AllCenters : Fragment() {

    private lateinit var centerRecyclerViewAll: RecyclerView
    private lateinit var mCenterCity: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_all_centers, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mCenterCity = requireArguments().getString("outlet_city").toString()

        showCentersList(view)
    }

    private fun showCentersList(view: View) {
        centerRecyclerViewAll = view.findViewById(R.id.activity_centers_all)
        centerRecyclerViewAll.layoutManager =
            LinearLayoutManager(view.context)
        fetchCenter()
    }

    private fun fetchCenter() {
        val list = mutableListOf<OutletModel>()

        FirebaseFirestore.getInstance().collection("super_admin").document("rohit-20072022")
            .collection("sports_centers")
            .whereEqualTo("outlet_city", mCenterCity)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val id = document.get("outlet_id").toString()
                    val title = document.get("outlet_name").toString()
                    val type = document.get("outlet_category").toString()
                    val image = document.get("outlet_image").toString()
                    list.add(OutletModel(id,title, type, image))
                }
                val adapter = CenterAdapter(requireActivity(), list)
                centerRecyclerViewAll.adapter = adapter
                adapter.setOnItemClickListener(object : CenterAdapter.OnItemClickListener {
                    override fun onItemClick(position: Int) {
                        findNavController().navigate(R.id.action_allCenters_to_centerFullDetails2,Bundle().apply {
                            putString("outlet_id", list[position].outlet_id)
                        })
                    }
                })
            }
            .addOnFailureListener { exception ->
                Log.d("Firebase Exception", "Error getting documents: ", exception)
            }
    }
}