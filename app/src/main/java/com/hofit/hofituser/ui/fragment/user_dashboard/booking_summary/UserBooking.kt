package com.hofit.hofituser.ui.fragment.user_dashboard.booking_summary

import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.hofit.hofituser.R
import com.hofit.hofituser.adapter.BookingFragmentAdapter

class UserBooking : AppCompatActivity() {

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager2: ViewPager2
    private lateinit var mImageBackBTN: ImageButton

    private var bookingFragmentAdapter: BookingFragmentAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_booking)

        mImageBackBTN = findViewById(R.id.backBtnImplement)
        mImageBackBTN.setOnClickListener {
            finish()
        }

        tabLayout = findViewById(R.id.tabBooking)
        viewPager2 = findViewById(R.id.view_pager2)

        tabLayout.addTab(tabLayout.newTab().setText("Upcoming"))
        tabLayout.addTab(tabLayout.newTab().setText("Completed"))
        tabLayout.addTab(tabLayout.newTab().setText("Canceled"))

        val fragmentManager = supportFragmentManager
        bookingFragmentAdapter = BookingFragmentAdapter(fragmentManager, lifecycle)
        viewPager2.adapter = bookingFragmentAdapter

        tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewPager2.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })


        viewPager2.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                tabLayout.selectTab(tabLayout.getTabAt(position))
            }
        })

    }
}