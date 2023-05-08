package com.hofit.hofituser.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentContainerView
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.hofit.hofituser.R

class UserMainPage : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var userCityShow: TextView

    private lateinit var progressCircle: ProgressBar
    private lateinit var layoutMain: ConstraintLayout
    private lateinit var fragmentContainerView : FragmentContainerView
    private lateinit var bottomNav: BottomNavigationView


    private lateinit var fireBase: CollectionReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_main_page)
        init()

        try {
            fireBase = Firebase.firestore
                .collection("super_admin")
                .document("rohit-20072022")
                .collection("sports_centers")

        } catch (_: Exception) {
        }


        val toolbar = findViewById<View>(R.id.id_toolbar) as MaterialToolbar
        setSupportActionBar(toolbar)

        showUserCity()

    }

    private fun init() {
        userCityShow = findViewById(R.id.user_location_show)
        progressCircle = findViewById(R.id.progress_outlet_status)
        layoutMain = findViewById(R.id.progress_outlet_status1)
        fragmentContainerView = findViewById(R.id.mainContent)
        bottomNav = findViewById(R.id.bottomNavigationBar)
    }

    private fun showUserCity() {
        val user = Firebase.auth.currentUser!!.uid
        FirebaseFirestore.getInstance().collection("hofit_user").document(user).collection("address_management").document("auto_location")
            .get()
            .addOnSuccessListener { result ->
                if (result != null){
                    val city = result.get("user_city").toString()
                    userCityShow.text = city
                    fireBase
                        .whereEqualTo("outlet_city", city)
                        .whereEqualTo("outlet_regis_status", "Verified")
                        .get()
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                var location: String? = null
                                for (document in it.result) {
                                    Log.i(
                                        "CheckOut",
                                        "onCreateView: ${document.getString("outlet_number")}"
                                    )
                                    location = document.getString("outlet_city").toString()
                                }
                                if (location == city) {

                                    progressCircle.visibility = View.GONE
                                    fragmentContainerView.visibility = View.VISIBLE
                                    bottomNav.visibility = View.VISIBLE
                                    val navHostFragment =
                                        supportFragmentManager.findFragmentById(R.id.mainContent) as NavHostFragment
                                    navController = navHostFragment.navController

                                    val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationBar)
                                    NavigationUI.setupWithNavController(bottomNavigationView, navController)

                                } else {
                                    progressCircle.visibility = View.GONE
                                    layoutMain.visibility = View.VISIBLE
                                }
                            }
                        }
                }
            }
    }
}