package com.hofit.hofituser

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.hofit.hofituser.models.ActivityCategoryData
import com.hofit.hofituser.ui.UserHome.AllCategoryAdapter
import com.hofit.hofituser.ui.UserHome.CategoryAdapter

class AllCate : Fragment() {

    lateinit var gridRecyclerViewAll: RecyclerView
    lateinit var mCenterCity: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_all_cate, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        showAllCategoriesInGrid(view)

        mCenterCity = requireArguments().getString("center_city").toString()
    }

    private fun showAllCategoriesInGrid(view: View) {
        gridRecyclerViewAll = view.findViewById(R.id.grid_recyclerViewAll)
        gridRecyclerViewAll.layoutManager = GridLayoutManager(view.context, 3)
        fetchCategory()
    }

    private fun fetchCategory() {
        val list = mutableListOf<ActivityCategoryData>()

        FirebaseFirestore.getInstance().collection("super_admin").document("rohit-20072022")
            .collection("activity_categories")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val title = document.get("title").toString()
                    val url = document.get("url").toString()
                    list.add(ActivityCategoryData(title, url))
                    Log.i("All Category TAG", "Image title $title and Image Url $url")
                }
                val adapter = AllCategoryAdapter(requireView().context, list)
                gridRecyclerViewAll.adapter = adapter
                adapter.setOnItemClickListener(object : AllCategoryAdapter.OnItemClickListener {
                    override fun onItemClick(position: Int) {
                        findNavController().navigate(
                            R.id.action_allCate_to_detailsCategory,
                            Bundle().apply {
                                putString("category_title", list[position].title)
                                putString("center_city", mCenterCity)
                            })
                    }
                })
            }
            .addOnFailureListener { exception ->
                Log.d("All Category TAG", "Error getting documents: ", exception)
            }
    }

}