package com.hofit.hofituser

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.firestore.FirebaseFirestore
import com.hofit.hofituser.models.ActivityCategoryData
import com.hofit.hofituser.models.PassModel
import com.hofit.hofituser.models.PromoModel
import com.hofit.hofituser.ui.OneKey.OnePassAdapter
import com.hofit.hofituser.ui.UserHome.AllCategoryAdapter
import com.hofit.hofituser.ui.UserHome.CategoryAdapter
import com.hofit.hofituser.ui.UserHome.PromoAdapter


class OneKey : Fragment() {

    private lateinit var viewPager2: ViewPager2
    private lateinit var handler: Handler
    private lateinit var keyAdapter: PromoAdapter
    private val TAG = OneKey::class.java.simpleName

    private lateinit var onePassAdapter: OnePassAdapter
    private lateinit var onePassRecyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_one_key, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewPager2 = view.findViewById(R.id.key_viewPager)
        handler = Handler(Looper.myLooper()!!)
        fetchPromoImages()
        viewPager2.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                handler.removeCallbacks(runnable)
                handler.postDelayed(runnable, 2000)
            }
        })

        fetchPasses(view)

    }

    private fun fetchPasses(view: View) {
        onePassRecyclerView = view.findViewById(R.id.recyclerview_show_pass)
        onePassRecyclerView.layoutManager = LinearLayoutManager(view.context)
        fetchPass()
    }

    private fun fetchPass() {
        val list = mutableListOf<PassModel>()

        FirebaseFirestore.getInstance().collection("super_admin").document("rohit-20072022")
            .collection("one_key_pass")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val passMonth = document.get("pass_month").toString()
                    val mainPrice = document.get("pass_actual_price").toString()
                    val mainPriceInFormat = "₹$mainPrice"
                    val passDiscount = document.get("pass_discount_price").toString()
                    val passDiscountInFormat = "₹$passDiscount"
                    val passEmi = document.get("pass_emi_cost").toString()
                    val passEmiInFormat = "₹$passEmi/month*"
                    val passBill = document.get("pass_bill_type").toString()
                    val passContent1 = document.get("pass_content_1").toString()
                    val passContent2 = document.get("pass_content_2").toString()
                    val passContent3 = document.get("pass_content_3").toString()
                    val passContent4 = document.get("pass_content_4").toString()
                    val sessionLimit = document.get("pass_session_limit").toString()
                    list.add(PassModel(passMonth, mainPriceInFormat, passDiscountInFormat, passEmiInFormat, passBill, passContent1, passContent2, passContent3, passContent4, sessionLimit))
                }
                onePassAdapter = OnePassAdapter(requireContext(), list)
                onePassRecyclerView.adapter = onePassAdapter
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Error getting documents: ", exception)
            }
    }

    private val runnable = Runnable {
        viewPager2.currentItem = viewPager2.currentItem + 1
    }

    private fun fetchPromoImages() {
        val keyList = ArrayList<PromoModel>()

        FirebaseFirestore.getInstance().collection("super_admin").document("rohit-20072022")
            .collection("promo_image")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val keyImage = document.get("image_url").toString()
                    Log.i(TAG, "Image Url $keyImage")
                    keyList.add(PromoModel(keyImage))
                }
                keyAdapter = PromoAdapter(requireContext(), keyList, viewPager2)
                viewPager2.adapter = keyAdapter
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Error getting documents: ", exception)
            }
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(runnable)
    }

    override fun onResume() {
        super.onResume()
        handler.postDelayed(runnable, 2000)
    }

}