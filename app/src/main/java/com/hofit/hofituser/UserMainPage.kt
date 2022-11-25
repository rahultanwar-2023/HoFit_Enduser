package com.hofit.hofituser

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class UserMainPage : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var userCityShow: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_main_page)
        init()

        val toolbar = findViewById<View>(R.id.id_toolbar) as MaterialToolbar
        setSupportActionBar(toolbar)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.mainContent) as NavHostFragment
        navController = navHostFragment.navController

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationBar)
        NavigationUI.setupWithNavController(bottomNavigationView, navController)

        showUserCity()
    }

    private fun init() {
        userCityShow = findViewById(R.id.user_location_show)
    }

    private fun showUserCity() {
        val user = Firebase.auth.currentUser!!.uid
        FirebaseFirestore.getInstance().collection("hofit_user").document(user).collection("address_management").document("auto_location")
            .get()
            .addOnSuccessListener { result ->
                if (result != null){
                    val city = result.get("user_city").toString()
                    userCityShow.text = city
                }
            }
    }
}