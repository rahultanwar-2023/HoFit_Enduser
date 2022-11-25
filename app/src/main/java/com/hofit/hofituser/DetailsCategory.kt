package com.hofit.hofituser

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.hofit.hofituser.models.CentersModel
import com.hofit.hofituser.ui.UserHome.CenterAdapter
import java.util.*


class DetailsCategory : Fragment() {

    lateinit var mTitleText: TextView
    lateinit var mCateTitle: String
    lateinit var mCenterCity: String
    lateinit var detailsRecyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_details_category, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mCateTitle = requireArguments().getString("category_title").toString()
        mCenterCity = requireArguments().getString("center_city").toString()

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
        val list = mutableListOf<CentersModel>()

        FirebaseFirestore.getInstance().collection("super_admin").document("rohit-20072022")
            .collection("sports_centers")
            .whereEqualTo("center_city", mCenterCity).whereEqualTo("center_category", mCateTitle.lowercase(Locale.getDefault()))
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val title = document.get("center_name").toString()
                    val type = document.get("center_category").toString()
                    val image = document.get("center_image").toString()
                    val rating = document.get("center_rating").toString()
                    val id = document.get("center_id").toString()
                    list.add(CentersModel(id, title, type, image, rating))
                }
                val adapter = CenterAdapter(requireActivity(), list)
                detailsRecyclerView.adapter = adapter
                adapter.setOnItemClickListener(object : CenterAdapter.OnItemClickListener {
                    override fun onItemClick(position: Int) {
                        findNavController().navigate(R.id.action_detailsCategory_to_centerFullDetails2, Bundle().apply {
                            putString("center_id", list[position].center_id)
                            putString("center_title", mCateTitle)
                        })
                    }

                })
            }
            .addOnFailureListener { exception ->
                Log.d("Firebase Exception", "Error getting documents: ", exception)
            }
    }


}