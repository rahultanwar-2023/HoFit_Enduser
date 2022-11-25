package com.hofit.hofituser

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.hofit.hofituser.utils.NetworkViewModel
import com.hofit.hofituser.utils.SplashViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class NetworkCheck : AppCompatActivity() {

    private lateinit var mProgressBar: LinearProgressIndicator
    private lateinit var mCheckStatus: TextView

    private val networkViewModel: NetworkViewModel by viewModels()

    private val splashViewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().apply {
            setKeepOnScreenCondition {
                splashViewModel.isLoading.value
            }
        }
        setContentView(R.layout.activity_network_check)

        mProgressBar = findViewById(R.id.progressBar)
        mCheckStatus = findViewById(R.id.network_status)

        mProgressBar.visibility = View.VISIBLE

        val handler = Handler(Looper.myLooper()!!)
        handler.postDelayed({//Do something after delay
            checkNetworkStatus()
        }, 6000)


    }

    private fun checkNetworkStatus() {
        lifecycleScope.launchWhenCreated {

            networkViewModel.isConnected.collectLatest {
                if (it) {
                    mProgressBar.visibility = View.INVISIBLE
                    mCheckStatus.visibility = View.VISIBLE
                    mCheckStatus.text = "Welcome To HoFIT"
                    val handler = Handler(Looper.myLooper()!!)
                    handler.postDelayed({//Do something after delay
                        startActivity(Intent(this@NetworkCheck, MainActivity::class.java))
                        finish()
                    }, 1000)
                } else {
                    mProgressBar.visibility = View.INVISIBLE
                    mCheckStatus.text = "No Internet Connection"
                    mCheckStatus.visibility = View.VISIBLE
                }
            }
        }
    }
}