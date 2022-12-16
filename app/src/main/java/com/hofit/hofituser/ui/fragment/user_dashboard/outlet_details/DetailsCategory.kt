package com.hofit.hofituser.ui.fragment.user_dashboard.outlet_details

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.hofit.hofituser.R
import com.hofit.hofituser.adapter.CenterAdapter
import com.hofit.hofituser.models.OutletModel


class DetailsCategory : Fragment() {

    private lateinit var mTitleText: TextView
    lateinit var mCateTitle: String
    private lateinit var mCenterCity: String
    private lateinit var detailsRecyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_details_category, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mCateTitle = requireArguments().getString("outlet_title").toString()
        mCenterCity = requireArguments().getString("outlet_city").toString()

        mTitleText = view.findViewById(R.id.cat_title)
        mTitleText.text = resources.getString(R.string.detail_category, mCateTitle)


        showCentersList(view)

    }

    private fun showCentersList(view: View) {
        detailsRecyclerView = view.findViewById(R.id.detail_category)
        detailsRecyclerView.layoutManager =
            LinearLayoutManager(view.context)
        fetchCenter()
    }

    private fun fetchCenter() {
        val list = mutableListOf<OutletModel>()

        FirebaseFirestore.getInstance().collection("super_admin").document("rohit-20072022")
            .collection("sports_centers")
            .whereEqualTo("outlet_city", mCenterCity).whereEqualTo("outlet_category", mCateTitle)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val id = document.get("outlet_id").toString()
                    val title = document.get("outlet_name").toString()
                    val type = document.get("outlet_category").toString()
                    val image = document.get("outlet_image").toString()
                    //val rating = document.get("center_rating").toString()
                    list.add(OutletModel(id, title, type, image))
                }
                val adapter = CenterAdapter(requireActivity(), list)
                detailsRecyclerView.adapter = adapter
                adapter.setOnItemClickListener(object : CenterAdapter.OnItemClickListener {
                    override fun onItemClick(position: Int) {
                        findNavController().navigate(R.id.action_detailsCategory_to_centerFullDetails2, Bundle().apply {
                            putString("outlet_id", list[position].outlet_id)
                            putString("outlet_title", mCateTitle)
                        })
                    }

                })
            }
            .addOnFailureListener { exception ->
                Log.d("Firebase Exception", "Error getting documents: ", exception)
            }
    }


}