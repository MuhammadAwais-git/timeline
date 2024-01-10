package com.translate.language.timelinemoduleonmap

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.translate.language.timelinemoduleonmap.Utils.LocationHelper
import com.translate.language.timelinemoduleonmap.Utils.LocationRepository
import com.translate.language.timelinemoduleonmap.Utils.Osmhelper
import com.translate.language.timelinemoduleonmap.Utils.Osmhelper.Companion.setMarkerIconAsPhoto
import com.translate.language.timelinemoduleonmap.databinding.ActivityMainBinding
import com.translate.language.timelinemoduleonmap.service.Restarter
import com.translate.language.timelinemoduleonmap.timeline.LocationService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay
import java.util.Locale


class MainActivity : AppCompatActivity() {
    private lateinit var locationRepository: LocationRepository
    private lateinit var binding: ActivityMainBinding

    companion object {
        var currentLat: Double = 0.0
        var currentLong: Double = 0.0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val ctx = applicationContext
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx))
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.cardTimeline.setOnClickListener {
            startActivity(Intent(this,TimeLineActivity::class.java))
        }
        binding.cardMyPlaces.setOnClickListener {
            startActivity(Intent(this,LocationHistoryActivity::class.java))
        }
        checkLocationPermission()

        Intent(applicationContext, LocationService::class.java).apply {
            action = LocationService.ACTION_START
            startService(this)
        }
       /* val serviceIntent = Intent(this, LocationService::class.java)
        startService( serviceIntent)*/
       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(Intent(this, LocationService::class.java))
        } else {
            startService(Intent(this, LocationService::class.java))
        }*/

    }

    private fun checkLocationPermission() {
        CoroutineScope(Dispatchers.IO).launch() {
            LocationHelper.internetCTCheck(this@MainActivity)
            LocationHelper.isProviderCTTruckEnabled(this@MainActivity)
            val isPermissionDone =
                LocationHelper.islocationCTTruckPerMisstionProvided(this@MainActivity)
            if (isPermissionDone) {
                withContext(Dispatchers.Main) {
                    getCurrentLocation()
                }
            }
        }
    }

    private fun getCurrentLocation() {

        locationRepository = LocationRepository(this, object : MyLocationInterface {
            override fun onLocationChange(location: Location?) {
                currentLat = location!!.latitude
                currentLong = location.longitude
                Log.d("TAG", "onLocationChange: currentLat $currentLat currentLong $currentLong")
                locationRepository.stopLocation()

                val geocoder = Geocoder(
                    this@MainActivity,
                    Locale.getDefault()
                )

                try {
                    val addreses = geocoder.getFromLocation(
                        location.latitude, location.longitude,
                        1
                    ).toString()

                    val adders = addreses.split(":").toTypedArray()
                    val add1 = adders[1]
                    val addres2 = add1.split("]").toTypedArray()
                    val finaladdres = addres2[0]

                    initializeMap(currentLat, currentLong)

                } catch (e: Exception) {
                    Log.e("TAG", "onLocationChange: ${e.message}")
                }
            }
        })

    }

    private fun initializeMap(currentLat: Double, currentLong: Double) {
        binding.mapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE)
        binding.mapView.setBuiltInZoomControls(true)
        binding.mapView.setMultiTouchControls(true)
        val mapController: IMapController = binding.mapView.controller
        Osmhelper.zoomWithAnimate(mapController, GeoPoint(currentLat, currentLong), 17)
        val startPoint = GeoPoint(currentLat, currentLong)
        mapController.setCenter(startPoint)
        binding.mapView.setBuiltInZoomControls(false)

        val marker = Marker(binding.mapView)
        marker.title = ("Current Location")
        marker.textLabelFontSize = 11

        marker.icon = null
        marker.position = startPoint
        val icon = BitmapFactory.decodeResource(resources, R.drawable.location_off)
        setMarkerIconAsPhoto(this,marker, icon!!)
        binding.mapView.overlays.add(marker)
    }
    override fun onDestroy() {
        //stopService(mServiceIntent);
        val broadcastIntent = Intent()
        broadcastIntent.action = "restartservice"
        broadcastIntent.setClass(this, Restarter::class.java)
        this.sendBroadcast(broadcastIntent)
        super.onDestroy()
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getCurrentLocation()
                }
            }
        }
    }
}