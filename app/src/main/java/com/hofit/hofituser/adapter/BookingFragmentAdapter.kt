package com.hofit.hofituser.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.hofit.hofituser.ui.fragment.user_dashboard.booking_summary.booking_status.CancelBookings
import com.hofit.hofituser.ui.fragment.user_dashboard.booking_summary.booking_status.CompleteSessions
import com.hofit.hofituser.ui.fragment.user_dashboard.booking_summary.booking_status.UpcomingSessions

class BookingFragmentAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        if (position == 1) {
            return CompleteSessions()
        } else if (position == 2) {
            return CancelBookings()
        }
        return UpcomingSessions()
    }
}