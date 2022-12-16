package com.hofit.hofituser.ui.fragment.user_dashboard

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.hofit.hofituser.ui.fragment.user_manage.PassManage
import com.hofit.hofituser.ui.fragment.user_dashboard.profile_create.ProfileCreation
import com.hofit.hofituser.R
import com.hofit.hofituser.ui.fragment.user_dashboard.booking_summary.UserBooking
import com.hofit.hofituser.ui.MainActivity


class UserSettings : Fragment() {

    lateinit var mEditProfile: MaterialButton
    lateinit var mBuyKey: MaterialButton
    lateinit var mBookings: MaterialButton
    private lateinit var mLogout: MaterialButton

    //Pass Show if Active
    private lateinit var mPassDetails: MaterialButton
    lateinit var mShowPassShortDetails: ConstraintLayout
    lateinit var mNoActiveImage: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_user_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)

        val currentUser = FirebaseAuth.getInstance().currentUser!!.uid
        FirebaseFirestore.getInstance().collection("hofit_user").document(currentUser)
            .get()
            .addOnSuccessListener { result ->
                if (result != null) {
                    val mPassActive = result.get("is_pass_active").toString()

                    if (mPassActive == "true") {
                        mNoActiveImage.visibility = View.INVISIBLE
                        mShowPassShortDetails.visibility = View.VISIBLE
                        mPassDetails.visibility = View.VISIBLE
                    } else {
                        mShowPassShortDetails.visibility = View.INVISIBLE
                        mNoActiveImage.visibility = View.VISIBLE
                        mPassDetails.visibility = View.INVISIBLE
                    }
                }
            }

        mPassDetails.setOnClickListener {
            val intent = Intent(view.context, PassManage::class.java)
            startActivity(intent)
        }

        mEditProfile.setOnClickListener {
            val intent = Intent(view.context, ProfileCreation::class.java)
            startActivity(intent)
        }

        mBuyKey.setOnClickListener {
            findNavController().navigate(R.id.action_userSettings_to_oneKey)
        }

        mBookings.setOnClickListener {
            val intent = Intent(view.context, UserBooking::class.java)
            startActivity(intent)
        }

        mLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            sendToStart()
        }
    }

    private fun sendToStart() {
        val settings = requireContext().getSharedPreferences("pref", Context.MODE_PRIVATE)
        settings.edit().remove("isFirstTImeRun").commit()
        val intent = Intent(requireActivity(), MainActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    private fun init(view: View) {
        mEditProfile = view.findViewById(R.id.edit_profile)
        mBuyKey = view.findViewById(R.id.buy_key)
        mBookings = view.findViewById(R.id.check_bookings)
        mLogout = view.findViewById(R.id.logout_user)
        mPassDetails = view.findViewById(R.id.manage_your_pass)
        mShowPassShortDetails = view.findViewById(R.id.one_key_layout)
        mNoActiveImage = view.findViewById(R.id.no_active_plan)
    }
}