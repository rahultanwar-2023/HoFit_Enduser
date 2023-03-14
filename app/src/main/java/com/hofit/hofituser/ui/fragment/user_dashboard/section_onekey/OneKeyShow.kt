package com.hofit.hofituser.ui.fragment.user_dashboard.section_onekey

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.firestore.FirebaseFirestore
import com.hofit.hofituser.R
import com.hofit.hofituser.adapter.OnePassAdapter
import com.hofit.hofituser.adapter.PromoAdapter
import com.hofit.hofituser.models.PassModel
import com.hofit.hofituser.models.PromoModel

class OneKeyShow : AppCompatActivity() {

    private lateinit var viewPager2: ViewPager2
    private lateinit var handler: Handler
    private lateinit var keyAdapter: PromoAdapter
    private val TAG = OneKeyShow::class.java.simpleName

    private lateinit var onePassAdapter: OnePassAdapter
    private lateinit var onePassRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_one_key)
        viewPager2 = findViewById(R.id.key1_viewPager)
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

        fetchPasses()

        val backToMain = findViewById<ImageButton>(R.id.backToMain1)
        backToMain.setOnClickListener {
            finish()
        }

    }

    private fun fetchPasses() {
        onePassRecyclerView = findViewById(R.id.recyclerview_show_pass1)
        onePassRecyclerView.layoutManager = LinearLayoutManager(this)
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
                    val passAfterDiscount = mainPrice.toInt() - passDiscount.toInt()
                    val passDiscountInFormat = "₹$passAfterDiscount"
                    val passEmi = document.get("pass_emi_cost").toString()
                    val passEmiInFormat = "₹$passEmi/month*"
                    val passBill = document.get("pass_bill_type").toString()
                    val passContent1 = document.get("pass_content_1").toString()
                    val passContent2 = document.get("pass_content_2").toString()
                    val passContent3 = document.get("pass_content_3").toString()
                    val passContent4 = document.get("pass_content_4").toString()
                    val sessionLimit = document.get("pass_session_limit").toString()
                    list.add(
                        PassModel(
                            passMonth,
                            mainPriceInFormat,
                            passDiscountInFormat,
                            passEmiInFormat,
                            passBill,
                            passContent1,
                            passContent2,
                            passContent3,
                            passContent4,
                            sessionLimit
                        )
                    )
                }
                onePassAdapter = OnePassAdapter(this, list)
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
            .collection("promotion_slider")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val keyImage = document.get("url").toString()
                    keyList.add(PromoModel(keyImage))
                }
                keyAdapter = PromoAdapter(this, keyList, viewPager2)
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