package com.hofit.hofituser.ui.fragment.user_location

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Granularity
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.hofit.hofituser.R
import com.hofit.hofituser.ui.UserMainPage
import java.util.Locale


class UserLocation : Fragment() {

    private lateinit var progressLocation: ProgressBar

    private var fusedLocationProvider: FusedLocationProviderClient? = null
    private val locationRequest: LocationRequest =
        LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000)
            .setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
            .setMinUpdateIntervalMillis(500)
            .setMinUpdateDistanceMeters(1F)
            .setWaitForAccurateLocation(true)
            .build()

    private var locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val lastLocation = locationResult.lastLocation

            val dLat = lastLocation!!.latitude
            val dLong = lastLocation.longitude

            try {

                val geocoder = Geocoder(requireContext(), Locale.getDefault())

                if (Build.VERSION.SDK_INT >= 33) {

                    geocoder.getFromLocation(
                        dLat, dLong, 1
                    ) { addresses ->
                        val fullAddress = addresses[0].getAddressLine(0)
                        val locality = addresses[0].locality
                        if (locality != null) {
                            saveLocationToStore(fullAddress!!, locality)
                            Toast.makeText(requireContext(), "hello", Toast.LENGTH_SHORT).show()
                        }

                        Handler(Looper.myLooper()!!).postDelayed({
                            progressLocation.visibility = View.INVISIBLE
                            val intent = Intent(activity, UserMainPage::class.java)
                            startActivity(intent)
                            requireActivity().finish()
                        }, 3000)
                    }

                } else {
                    val addresses = geocoder.getFromLocation(dLat, dLong, 1)
                    val fullAddress = addresses!![0].getAddressLine(0)
                    val locality = addresses[0].locality
                    if (locality != null) {
                        saveLocationToStore(fullAddress!!, locality)
                    }

                    Handler(Looper.myLooper()!!).postDelayed({
                        progressLocation.visibility = View.INVISIBLE
                        val intent = Intent(activity, UserMainPage::class.java)
                        startActivity(intent)
                        requireActivity().finish()
                    }, 3000)

                }
            } catch (e: Exception) {
                e.message
            }

        }
    }

    private fun saveLocationToStore(fullAddress: String, locality: String) {
        val currentUser = Firebase.auth.currentUser!!.uid
        val address = hashMapOf(
            "user_city" to locality,
            "user_address" to fullAddress
        )
        Firebase.firestore.collection("hofit_user").document(currentUser)
            .collection("address_management").document("auto_location")
            .set(address, SetOptions.merge())
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_user_location, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fusedLocationProvider = LocationServices.getFusedLocationProviderClient(requireActivity())

        progressLocation = view.findViewById(R.id.location_detect_bar)
        progressLocation.visibility = View.VISIBLE

        checkLocationPermission()

    }

    override fun onResume() {
        super.onResume()
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {

            fusedLocationProvider?.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }
    }

    override fun onPause() {
        super.onPause()
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {

            fusedLocationProvider?.removeLocationUpdates(locationCallback)
        }
    }

    private fun checkLocationPermission() {
        val lm = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        var gps_enabled = false
        var network_enabled = false

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
        } catch (_: java.lang.Exception) {
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        } catch (_: java.lang.Exception) {
        }

        if (!gps_enabled && !network_enabled) {
            AlertDialog.Builder(requireContext())
                .setMessage("Enable your location setting")
                .setPositiveButton("Open") { _, _ ->
                    requireActivity().startActivity(
                        Intent(
                            Settings.ACTION_LOCATION_SOURCE_SETTINGS
                        )
                    )
                }
                .setNegativeButton("Cancel") { _, _ ->
                    requireActivity().finish()
                }
                .show()
        } else {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        requireActivity(),
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                ) {
                    // Show an explanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.
                    AlertDialog.Builder(requireContext())
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton(
                            "OK"
                        ) { _, _ ->
                            //Prompt the user once explanation has been shown
                            requestLocationPermission()
                        }
                        .create()
                        .show()
                } else {
                    // No explanation needed, we can request the permission.
                    requestLocationPermission()
                }
            } else {
                checkBackgroundLocation()
            }
        }
    }

    private fun checkBackgroundLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestBackgroundLocationPermission()
        }
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
            ),
            MY_PERMISSIONS_REQUEST_LOCATION
        )
    }

    private fun requestBackgroundLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ),
                MY_PERMISSIONS_REQUEST_BACKGROUND_LOCATION
            )
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                MY_PERMISSIONS_REQUEST_LOCATION
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        fusedLocationProvider?.requestLocationUpdates(
                            locationRequest,
                            locationCallback,
                            Looper.getMainLooper()
                        )

                        // Now check background location
                        checkBackgroundLocation()
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(requireContext(), "permission denied", Toast.LENGTH_LONG).show()

                    // Check if we are in a state where the user has denied the permission and
                    // selected Don't ask again
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(
                            requireActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION
                        )
                    ) {
                        startActivity(
                            Intent(
                                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts("package", requireContext().packageName, null),
                            ),
                        )
                    }
                }
                return
            }
            MY_PERMISSIONS_REQUEST_BACKGROUND_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        fusedLocationProvider?.requestLocationUpdates(
                            locationRequest,
                            locationCallback,
                            Looper.getMainLooper()
                        )

                        Toast.makeText(
                            requireContext(),
                            "Granted Background Location Permission",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(requireContext(), "permission denied", Toast.LENGTH_LONG).show()
                }
                return

            }
        }
    }

    companion object {
        private const val MY_PERMISSIONS_REQUEST_LOCATION = 99
        private const val MY_PERMISSIONS_REQUEST_BACKGROUND_LOCATION = 66
    }

}
