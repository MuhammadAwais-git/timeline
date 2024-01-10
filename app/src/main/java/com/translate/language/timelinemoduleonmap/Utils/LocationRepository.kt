package com.translate.language.timelinemoduleonmap.Utils

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import android.util.Log
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.translate.language.timelinemoduleonmap.MyLocationInterface

class LocationRepository(context: Context, myListener: MyLocationInterface) {
    var fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    private var mContext: Context? = null
    private var myListener: MyLocationInterface? = null

    val locationRequest: com.google.android.gms.location.LocationRequest =
        com.google.android.gms.location.LocationRequest.create().apply {
            interval = 1000
            fastestInterval = 1000
            //numUpdates = 2
            /* numUpdates = 100
             setNumUpdates(100)*/
            priority = com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY

        }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            locationResult
            for (location in locationResult.locations) {
                setLocationData(location)
            }
        }
    }

    init {
        mContext = context
        this.myListener = myListener

        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            startLocation()
        } else {
//            val dialog = LocationEnableRequestDialogueBox(context)
//            dialog.show()
        }
    }


    @SuppressLint("MissingPermission")
    fun startLocation() {
        Log.i("setLocationData", "onActive : ")
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                location?.also {
                    setLocationData(it)
                }
            }
        startLocationUpdates()
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.myLooper()!!
        )
    }

    private fun setLocationData(location: Location?) {
        if (location != null) {
            Log.i("setLocationData", ":1122 " + location.longitude.toString())
            myListener!!.onLocationChange(location)
            //to stop refreshing location
            //  stopLocation()
        }
    }

    fun stopLocation() {
        Log.i("setLocationData", "onInactive : ")
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

}