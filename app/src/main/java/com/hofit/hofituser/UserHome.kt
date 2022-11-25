package com.hofit.hofituser

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.firestore.FirebaseFirestore
import com.hofit.hofituser.models.ActivityCategoryData
import com.hofit.hofituser.models.CentersModel
import com.hofit.hofituser.models.PromoModel
import com.hofit.hofituser.repository.DataStoreImpl
import com.hofit.hofituser.ui.UserHome.CategoryAdapter
import com.hofit.hofituser.ui.UserHome.CenterAdapter
import com.hofit.hofituser.ui.UserHome.PromoAdapter

class UserHome : Fragment() {

    lateinit var dataStoreImpl: DataStoreImpl
    var getUserCity = ""

    private lateinit var viewPager3: ViewPager2
    private lateinit var handler1: Handler
    private lateinit var promoAdapter: PromoAdapter
    private val TAG = UserLocation::class.java.simpleName

    private lateinit var gridRecyclerView: RecyclerView
    lateinit var centerRecyclerView: RecyclerView

    lateinit var scrollView: ScrollView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_user_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dataStoreImpl = DataStoreImpl(requireContext())

        dataStoreImpl.locationFlow.asLiveData().observe(viewLifecycleOwner) {
            getUserCity = it
            showCentersList(view)
        }


        viewPager3 = view.findViewById(R.id.promo_viewPager)
        handler1 = Handler(Looper.myLooper()!!)
        fetchPromoImages()
        viewPager3.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        viewPager3.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                handler1.removeCallbacks(runnable)
                handler1.postDelayed(runnable, 3000)
            }
        })

        showCategoriesInGrid(view)
        val viewAllCategory = view.findViewById<TextView>(R.id.view_all_category)
        viewAllCategory.setOnClickListener {
            findNavController().navigate(R.id.action_userHome_to_allCate,
                Bundle().apply {
                    putString("center_city", getUserCity)
                }
            )
        }

        val viewAllCenter = view.findViewById<TextView>(R.id.view_all_center)
        viewAllCenter.setOnClickListener {
            findNavController().navigate(R.id.action_userHome_to_allCenters,
                Bundle().apply {
                    putString("center_city", getUserCity)
                }
            )
        }

        scrollView = view.findViewById(R.id.scroll_view)
        scrollView.isSmoothScrollingEnabled
    }

    private val runnable = Runnable {
        viewPager3.currentItem = viewPager3.currentItem + 1
    }

    private fun fetchPromoImages() {
        val promoList = ArrayList<PromoModel>()

        FirebaseFirestore.getInstance().collection("super_admin").document("rohit-20072022")
            .collection("promotion_slider")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val keyImage = document.get("url").toString()
                    Log.i(TAG, "Image Url $keyImage")
                    promoList.add(PromoModel(keyImage))
                }
                promoAdapter = PromoAdapter(requireActivity(), promoList, viewPager3)
                viewPager3.adapter = promoAdapter
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Error getting documents: ", exception)
            }
    }

    private fun showCentersList(view: View) {
        centerRecyclerView = view.findViewById(R.id.all_activity_centers)
        centerRecyclerView.layoutManager =
            LinearLayoutManager(view.context, LinearLayoutManager.HORIZONTAL, false)
        fetchCenter()
    }

    private fun fetchCenter() {
        val list = mutableListOf<CentersModel>()

        FirebaseFirestore.getInstance().collection("super_admin").document("rohit-20072022")
            .collection("sports_centers")
            .whereEqualTo("center_city", getUserCity)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val title = document.get("center_name").toString()
                    val type = document.get("center_category").toString()
                    val image = document.get("center_image").toString()
                    val rating = document.get("center_rating").toString()
                    val id = document.get("center_id").toString()
                    list.add(CentersModel(id, title, type, image, rating))
                    Log.i(
                        TAG,
                        "Center title $title , Center type $type, Center image $image and Center Rating $rating"
                    )
                }
                val adapter = CenterAdapter(requireActivity(), list)
                centerRecyclerView.adapter = adapter
                adapter.setOnItemClickListener(object : CenterAdapter.OnItemClickListener {
                    override fun onItemClick(position: Int) {
                        findNavController().navigate(
                            R.id.action_userHome_to_centerFullDetails2,
                            Bundle().apply {
                                putString("center_id", list[position].center_id)
                            })
                    }
                })

            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Error getting documents: ", exception)
            }
    }

    private fun showCategoriesInGrid(view: View) {
        gridRecyclerView = view.findViewById(R.id.grid_recyclerView)
        gridRecyclerView.layoutManager = GridLayoutManager(view.context, 3)
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
                    Log.i(TAG, "Image title $title and Image Url $url")
                }
                val adapter = CategoryAdapter(requireActivity(), list)
                gridRecyclerView.adapter = adapter
                adapter.setOnItemClickListener(object : CategoryAdapter.onItemClickListener {
                    override fun onItemClick(position: Int) {
                        findNavController().navigate(
                            R.id.action_userHome_to_detailsCategory,
                            Bundle().apply {
                                putString("category_title", list[position].title)
                                putString("center_city", getUserCity)
                            })
                    }
                })
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Error getting documents: ", exception)
            }
    }

    override fun onPause() {
        super.onPause()
        handler1.removeCallbacks(runnable)
    }

    override fun onResume() {
        super.onResume()
        handler1.postDelayed(runnable, 2000)
    }

}