package com.hofit.hofituser.ui.fragment.user_location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase
import com.hofit.hofituser.R
import com.hofit.hofituser.repository.DataStoreImpl
import com.hofit.hofituser.ui.UserMainPage
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*


class UserLocation : Fragment() {

    private var sharedPref: SharedPreferences? = null

    lateinit var dataStoreImpl: DataStoreImpl

    private val REQUEST_CHECK_SETTINGS = 100
    private val UPDATE_INTERVAL_IN_MILLISECONDS: Long = 10000
    private val FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS: Long = 1000
    private val TAG = UserLocation::class.java.simpleName

    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private var mSettingClient: SettingsClient? = null
    private var mLocationRequest: LocationRequest? = null
    private var mLocationSettingsRequest: LocationSettingsRequest? = null
    private var mLocationCallback: LocationCallback? = null
    private var mCurrentLocation: Location? = null
    private var mRequestingLocationUpdates = false
    private var geocoder: Geocoder? = null

    private val firebase = FirebaseFirestore.getInstance().collection("hofit_user")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (restorePrefData()) {
            val intent = Intent(requireActivity(), UserMainPage::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
        return inflater.inflate(R.layout.fragment_user_location, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dataStoreImpl = DataStoreImpl(requireContext())

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        mSettingClient = LocationServices.getSettingsClient(requireActivity())
        geocoder = Geocoder(requireActivity(), Locale.getDefault())

        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                mCurrentLocation = locationResult.lastLocation
                val latitude = mCurrentLocation!!.latitude
                val longitude = mCurrentLocation!!.longitude
                val addressList = geocoder!!.  getFromLocation(latitude, longitude, 1)
                val fullAddress = addressList?.get(0)?.getAddressLine(0)
                val locality = addressList?.get(0)?.locality
                GlobalScope.launch {
                    dataStoreImpl.storeLocation(locality!!)
                    saveLocationToStore(fullAddress!!, locality)
                }
            }
        }

        mLocationRequest = LocationRequest.create()
            .setInterval(UPDATE_INTERVAL_IN_MILLISECONDS)
            .setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)

        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(mLocationRequest!!)
        mLocationSettingsRequest = builder.build()

        val buttonLocation = view.findViewById<Button>(R.id.location_detect)
        buttonLocation.setOnClickListener {
            Dexter.withContext(requireActivity())
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(object : PermissionListener {
                    override fun onPermissionGranted(permissionGrantedResponse: PermissionGrantedResponse) {
                        mRequestingLocationUpdates = true
                        startLocationUpdates()
                        savePrefData()
                        stopLocationUpdates()
                        Handler(Looper.myLooper()!!).postDelayed({
                            val intent = Intent(requireActivity(), UserMainPage::class.java)
                            startActivity(intent)
                            requireActivity().finish()
                        }, 3000)
                    }

                    override fun onPermissionDenied(permissionDeniedResponse: PermissionDeniedResponse) {
                        if (permissionDeniedResponse.isPermanentlyDenied) {
                            openSettings()
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permissionRequest: PermissionRequest,
                        permissionToken: PermissionToken
                    ) {
                        permissionToken.continuePermissionRequest()
                    }
                }).check()
        }

    }

    private fun saveLocationToStore(fullAddress: String, locality: String) {
        val currentUser = Firebase.auth.currentUser!!.uid
        val address = hashMapOf(
            "user_city" to locality,
            "user_address" to fullAddress
        )
        firebase.document(currentUser)
            .collection("address_management").document("auto_location")
            .set(address, SetOptions.merge())
    }

    private fun openSettings() {
        val intent = Intent()
        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        mSettingClient!!.checkLocationSettings(mLocationSettingsRequest!!)
            .addOnSuccessListener(requireActivity()) {
                mFusedLocationClient!!.requestLocationUpdates(
                    mLocationRequest!!,
                    mLocationCallback!!,
                    Looper.myLooper()
                )
            }
            .addOnFailureListener(requireActivity()) { e: Exception ->
                when ((e as ApiException).statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                        Log.i(
                            TAG,
                            "Location settings are not satisfies. Attempting to upgrade location settings"
                        )
                        try {
                            val rae = e as ResolvableApiException
                            rae.startResolutionForResult(
                                requireActivity(),
                                REQUEST_CHECK_SETTINGS
                            )
                        } catch (sie: SendIntentException) {
                            Log.i(TAG, "PendingIntent unable to execute request")
                        }
                    }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                        val errorMessage =
                            "Location setting are inadequate, and cannot be fixed here. Fix in Settings"
                        Log.e(TAG, errorMessage)
                        Toast.makeText(requireActivity(), errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }

    private fun stopLocationUpdates() {
        mFusedLocationClient!!.removeLocationUpdates(mLocationCallback!!)
            .addOnCompleteListener(
                requireActivity()
            ) {
                Log.d(TAG, "Location updates stopped")
            }
    }

    private fun checkPermissions(): Boolean {
        val permissionState =
            ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        return permissionState == PackageManager.PERMISSION_GRANTED
    }

    private fun savePrefData() {
        sharedPref = requireActivity().getSharedPreferences("pref", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPref!!.edit()
        editor.putBoolean("isFirstTImeRun", true)
        editor.apply()
    }

    private fun restorePrefData(): Boolean {
        sharedPref = requireActivity().getSharedPreferences("pref", Context.MODE_PRIVATE)
        return sharedPref!!.getBoolean("isFirstTImeRun", false)

    }

    override fun onResume() {
        super.onResume()
        if (mRequestingLocationUpdates && checkPermissions()) {
            startLocationUpdates()
        }
    }

    override fun onPause() {
        super.onPause()
        if (mRequestingLocationUpdates) {
            stopLocationUpdates()
        }
    }
}