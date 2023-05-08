package com.hofit.hofituser.ui.fragment.user_dashboard
import android.content.Intent
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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.hofit.hofituser.R
import com.hofit.hofituser.adapter.CategoryAdapter
import com.hofit.hofituser.adapter.CenterAdapter
import com.hofit.hofituser.adapter.PromoAdapter
import com.hofit.hofituser.models.ActivityCategoryData
import com.hofit.hofituser.models.OutletModel
import com.hofit.hofituser.models.PromoModel
import com.hofit.hofituser.ui.fragment.user_dashboard.section_onekey.OneKeyShow
import com.hofit.hofituser.ui.fragment.user_location.UserLocation
class UserHome : Fragment() {
    private lateinit var viewPager3: ViewPager2
    private lateinit var handler1: Handler
    private lateinit var promoAdapter: PromoAdapter
    private val TAG = UserLocation::class.java.simpleName
    private lateinit var gridRecyclerView: RecyclerView
    private lateinit var centerRecyclerView: RecyclerView
    private lateinit var scrollView: ScrollView

    private lateinit var viewPager4: ViewPager2
    private lateinit var keyAdapter: PromoAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_user_home, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewPager3 = view.findViewById(R.id.promo_viewPager)
        handler1 = Handler(Looper.myLooper()!!)
        fetchPromoImages()
        viewPager4 = view.findViewById(R.id.key2_viewPager)
        fetchPromoImages2()
        viewPager4.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        viewPager4.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                handler1.removeCallbacks(runnable1)
                handler1.postDelayed(runnable1, 2000)
            }
        })

        val buttonGetKey = view.findViewById<TextView>(R.id.get_key)
        buttonGetKey.setOnClickListener {
            val intent = Intent(requireContext(), OneKeyShow::class.java)
            startActivity(intent)
        }


        val viewAllCenter = view.findViewById<TextView>(R.id.view_all_center)

        val user = Firebase.auth.currentUser!!.uid
        FirebaseFirestore.getInstance().collection("hofit_user").document(user)
            .collection("address_management").document("auto_location")
            .get()
            .addOnSuccessListener { result ->
                if (result != null) {
                    val city = result.get("user_city").toString()
                    showCentersList(view, city)

                    viewPager3.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
                    viewPager3.registerOnPageChangeCallback(object :
                        ViewPager2.OnPageChangeCallback() {
                        override fun onPageSelected(position: Int) {
                            super.onPageSelected(position)
                            handler1.removeCallbacks(runnable)
                            handler1.postDelayed(runnable, 3000)
                        }
                    })

                    showCategoriesInGrid(view, city)
                    val viewAllCategory = view.findViewById<TextView>(R.id.view_all_category)
                    viewAllCategory.setOnClickListener {
                        findNavController().navigate(
                            R.id.action_userHome_to_allCate,
                            Bundle().apply {
                                putString("outlet_city", city)
                            }
                        )
                    }

                    viewAllCenter.setOnClickListener {
                        findNavController().navigate(
                            R.id.action_userHome_to_allCenters,
                            Bundle().apply {
                                putString("outlet_city", city)
                            }
                        )
                    }
                }
            }



        scrollView = view.findViewById(R.id.scroll_view)
        scrollView.isSmoothScrollingEnabled
    }
    private val runnable = Runnable {
        viewPager3.currentItem = viewPager3.currentItem + 1
    }
    //To Fetch Promo Images from Firebase
    private fun fetchPromoImages() {
        val promoList = ArrayList<PromoModel>()

        FirebaseFirestore.getInstance().collection("super_admin").document("rohit-20072022")
            .collection("promotion_slider")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val keyImage = document.get("url").toString()
                    promoList.add(PromoModel(keyImage))
                }
                promoAdapter = PromoAdapter(requireActivity(), promoList, viewPager3)
                viewPager3.adapter = promoAdapter
            }
            .addOnFailureListener {}
    }

    //To Fetch Outlet from Firebase
    private fun showCentersList(view: View, city: String) {
        centerRecyclerView = view.findViewById(R.id.all_activity_centers)
        centerRecyclerView.layoutManager =
            LinearLayoutManager(view.context, LinearLayoutManager.HORIZONTAL, false)
        fetchCenter(city)
    }
    private fun fetchCenter(city: String) {
        val list = mutableListOf<OutletModel>()

        FirebaseFirestore.getInstance().collection("super_admin").document("rohit-20072022")
            .collection("sports_centers")
            .whereEqualTo("outlet_city", city)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val id = document.get("outlet_id").toString()
                    val title = document.get("outlet_name").toString()
                    val type = document.get("outlet_category").toString()
                    val image = document.get("outlet_image").toString()
                    list.add(OutletModel(id, title, type, image))
                }
                val adapter = CenterAdapter(requireActivity(), list)
                centerRecyclerView.adapter = adapter
                adapter.setOnItemClickListener(object : CenterAdapter.OnItemClickListener {
                    override fun onItemClick(position: Int) {
                        findNavController().navigate(
                            R.id.action_userHome_to_centerFullDetails2,
                            Bundle().apply {
                                putString("outlet_id", list[position].outlet_id)
                            })
                    }
                })

            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Error getting documents: ", exception)
            }
    }
    //To Fetch Categories of outlet from Firebase
    private fun showCategoriesInGrid(view: View, city: String) {
        gridRecyclerView = view.findViewById(R.id.grid_recyclerView)
        gridRecyclerView.layoutManager = GridLayoutManager(view.context, 3)
        fetchCategory(city)
    }
    private fun fetchCategory(city: String) {
        val list = mutableListOf<ActivityCategoryData>()

        FirebaseFirestore.getInstance().collection("super_admin").document("rohit-20072022")
            .collection("activity_categories")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val title = document.get("title").toString()
                    val url = document.get("url").toString()
                    list.add(ActivityCategoryData(title, url))
                }
                val adapter = CategoryAdapter(requireActivity(), list)
                gridRecyclerView.adapter = adapter
                adapter.setOnItemClickListener(object : CategoryAdapter.onItemClickListener {
                    override fun onItemClick(position: Int) {
                        findNavController().navigate(
                            R.id.action_userHome_to_detailsCategory,
                            Bundle().apply {
                                putString("outlet_title", list[position].title)
                                putString("outlet_city", city)
                            })
                    }
                })
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Error getting documents: ", exception)
            }
    }

    private val runnable1 = Runnable {
        viewPager4.currentItem = viewPager4.currentItem + 1
    }

    private fun fetchPromoImages2() {
        val keyList = ArrayList<PromoModel>()

        FirebaseFirestore.getInstance().collection("super_admin").document("rohit-20072022")
            .collection("oneKey_slider")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val keyImage = document.get("url").toString()
                    keyList.add(PromoModel(keyImage))
                }
                keyAdapter = PromoAdapter(requireContext(), keyList, viewPager4)
                viewPager4.adapter = keyAdapter
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