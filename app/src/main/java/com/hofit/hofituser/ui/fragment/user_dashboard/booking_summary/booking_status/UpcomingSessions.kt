package com.hofit.hofituser.ui.fragment.user_dashboard.booking_summary.booking_status

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.hofit.hofituser.R
import com.hofit.hofituser.adapter.UpcomingBookingAdapter
import com.hofit.hofituser.models.UpcomingBookingModel


class UpcomingSessions : Fragment() {

    private lateinit var upcomingBookingAdapter: UpcomingBookingAdapter
    private lateinit var onePassRecyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_upcoming_sessions, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onePassRecyclerView = view.findViewById(R.id.recyclerview_show_upcoming_booking)
        onePassRecyclerView.layoutManager = LinearLayoutManager(view.context)
        fetchBooking()

    }

    private fun fetchBooking() {
        val list = mutableListOf<UpcomingBookingModel>()

        val auth = Firebase.auth.currentUser!!.uid

        FirebaseFirestore.getInstance().collection("hofit_user").document(auth)
            .collection("my_bookings").document("bookings").collection("upcoming_booking")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val bookingOutlet = document.get("outlet_name").toString()
                    val bookingCategory = document.get("outlet_type").toString()
                    val bookingTime = document.get("session_schedule").toString()
                    val bookingAddress = document.get("outlet_address").toString()
                    list.add(
                        UpcomingBookingModel(
                            bookingOutlet,
                            bookingCategory,
                            bookingTime,
                            bookingAddress
                        )
                    )
                }
                upcomingBookingAdapter = UpcomingBookingAdapter(requireContext(), list)
                onePassRecyclerView.adapter = upcomingBookingAdapter
            }
            .addOnFailureListener { }
    }

}