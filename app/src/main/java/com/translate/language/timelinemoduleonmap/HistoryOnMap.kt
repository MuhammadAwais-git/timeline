package com.translate.language.timelinemoduleonmap

import android.graphics.BitmapFactory
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.appcompat.app.AppCompatActivity
import com.translate.language.timelinemoduleonmap.Utils.LocationRepository
import com.translate.language.timelinemoduleonmap.Utils.Osmhelper
import com.translate.language.timelinemoduleonmap.databinding.ActivityTimeLineBinding
import com.translate.language.timelinemoduleonmap.roomdatabase.MyLatLngLocation
import com.translate.language.timelinemoduleonmap.roommodel.RoomHistoryModel
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay

class HistoryOnMap : AppCompatActivity() {

    private lateinit var binding: ActivityTimeLineBinding
    private lateinit var locationRepository: LocationRepository
    var currentLat: Double = 0.0
    var currentLong: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val ctx = applicationContext
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx))
        binding = ActivityTimeLineBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getCurrentLocation()




        val intent = intent
        if (intent != null && intent.hasExtra("history_model")) {
            val roomHistoryModel = intent.getParcelableExtra<RoomHistoryModel>("history_model")
            if (roomHistoryModel != null) {

                drawPolylineOnMap(roomHistoryModel.list)
            }
        }

    }


    private fun getCurrentLocation() {

        locationRepository = LocationRepository(this, object : MyLocationInterface {
            override fun onLocationChange(location: Location?) {
                currentLat = location!!.latitude
                currentLong = location.longitude
                locationRepository.stopLocation()
                initializeMap(currentLat, currentLong)

            }
        })

    }

    private fun initializeMap(currentLat: Double, currentLong: Double) {
        binding.mapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE)
        binding.mapView.setBuiltInZoomControls(true)
        binding.mapView.setMultiTouchControls(true)
        val mapController: IMapController = binding.mapView.controller
        val startPoint = GeoPoint(currentLat, currentLong)
        Osmhelper.zoomWithAnimate(mapController, startPoint, 17)
        mapController.setCenter(startPoint)
        binding.mapView.setBuiltInZoomControls(false)

        val mRotationGestureOverlay = RotationGestureOverlay(this, binding.mapView)
        mRotationGestureOverlay.isEnabled = true
        binding.mapView.overlays.add(mRotationGestureOverlay)

        val marker = Marker(binding.mapView)
        marker.title = ("Current Location")
        marker.textLabelFontSize = 11

        marker.icon = null
//        marker.position = origion

        val icon = BitmapFactory.decodeResource(resources, R.drawable.location_off)
        Osmhelper.setMarkerIconAsPhoto(this, marker, icon!!)
        binding.mapView.overlays.add(marker)
    }

    private fun parseGeoPointsFromList(pairList: List<Pair<Double?, Double?>>): List<GeoPoint> {
        val geoPoints = mutableListOf<GeoPoint>()

        for (pair in pairList) {
            val latitude = pair.first
            val longitude = pair.second

            if (latitude != null && longitude != null && latitude != 0.0 && longitude != 0.0) {
                geoPoints.add(GeoPoint(latitude, longitude))
            }
        }

        return geoPoints
    }
/*
      private fun parseGeoPointsFromList(locationList: List<LocationModel>): List<GeoPoint> {
          val geoPoints = mutableListOf<GeoPoint>()

          for (location in locationList) {
              val latitude = location.latitude
              val longitude = location.longitude

              if (latitude != null && longitude != null) {
                  geoPoints.add(GeoPoint(latitude, longitude))
              }
          }

          return geoPoints
      }
*/

    private fun drawPolylineOnMap(coordinates: List<MyLatLngLocation>) {
        binding.mapView.overlays.clear()
        val path = Polyline()
        val geoPoints = mutableListOf<GeoPoint>()

        for (coordinate in coordinates) {
            val latitude = coordinate.lati?.toDoubleOrNull()
            val longitude = coordinate.lngi?.toDoubleOrNull()

            // Check if latitude and longitude are valid
            if (latitude != null && longitude != null) {
                geoPoints.add(GeoPoint(latitude, longitude))
            }
        }

        path.setPoints(geoPoints)
        path.color = Color.RED
        path.width = 10.0f
        binding.mapView.overlays.add(path)
        binding.mapView.invalidate()
    }



}

