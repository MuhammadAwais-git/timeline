package com.translate.language.timelinemoduleonmap

import android.graphics.BitmapFactory
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.translate.language.timelinemoduleonmap.Utils.LocationRepository
import com.translate.language.timelinemoduleonmap.Utils.Osmhelper
import com.translate.language.timelinemoduleonmap.databinding.ActivityTimeLineBinding
import com.translate.language.timelinemoduleonmap.roomdatabase.LocationDBViewModel
import com.translate.language.timelinemoduleonmap.roomdatabase.LocationDBViewModelFactory
import com.translate.language.timelinemoduleonmap.roomdatabase.LocationDao
import com.translate.language.timelinemoduleonmap.roomdatabase.LocationModel
import com.translate.language.timelinemoduleonmap.roomdatabase.LocationRoomDB
import com.translate.language.timelinemoduleonmap.roomdatabase.MyLatLngLocation
import com.translate.language.timelinemoduleonmap.roomdatabase.RoomReop
import com.translate.language.timelinemoduleonmap.roommodel.RoomLocationModel
import org.osmdroid.api.IMapController
import org.osmdroid.bonuspack.routing.OSRMRoadManager
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay

class TimeLineActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTimeLineBinding
    private lateinit var locationRepository: LocationRepository
    var currentLat: Double = 0.0
    var currentLong: Double = 0.0
    var origion: GeoPoint? = null
    var dest: GeoPoint? = null
    var arrayList = ArrayList<GeoPoint>()
    var arrayListFromReop = ArrayList<GeoPoint>()
    var wayPoints = ArrayList<GeoPoint>()
    private var roadManager: OSRMRoadManager? = null
    private lateinit var locationDao: LocationDao
    private lateinit var roomReop: RoomReop
    private lateinit var mLocationDBViewModel: LocationDBViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val ctx = applicationContext
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx))
        binding = ActivityTimeLineBinding.inflate(layoutInflater)
        setContentView(binding.root)
        roomReop = RoomReop(LocationRoomDB.invoke(this))
        initializeMap(MainActivity.currentLat, MainActivity.currentLong)
//        getCurrentLocation()
        /*

                val database = AppDatabase.getDatabase(this)
                val locationDao = database.locationDao()
                val repository = RoomReop(locationDao)
                val viewModelFactory = LocationDBViewModelFactory(repository)
                locationDBViewModel = ViewModelProvider(this, viewModelFactory).get(LocationDBViewModel::class.java)
                roomReop = RoomReop(AppDatabase.getDatabase(this).locationDao())
        */

        mLocationDBViewModel =
            ViewModelProvider(this, LocationDBViewModelFactory(roomReop)).get(LocationDBViewModel::class.java)

       /* roomReop.getAllData().observe(this){roomLocationList ->
//        mLocationDBViewModel.getAllData().observe(this) { roomLocationList ->
            if (roomLocationList != null && roomLocationList.isNotEmpty()) {
                for (roomLocationModel in roomLocationList) {
                    val date = roomLocationModel.date
                    val locationList = roomLocationModel.getList()
                    Log.d("onLocationChange", "LiveData date $date")
                    Log.d("onLocationChange", "LiveData Observer: locationList $locationList")

                }
                val myLatLngList = convertRoomLocationListToMyLatLngList(roomLocationList)
                val geoPoints = convertMyLatLngListToGeoPoints(myLatLngList)
                Log.d("onLocationChange", "LiveData Observer: locationList  geoPoints $geoPoints")

                drawPolylineOnMap(geoPoints)
            } else {
                Log.d("onLocationChange", "LiveData Observer: No data or empty roomLocationList")
            }
        }*/

    }
    private fun convertRoomLocationListToMyLatLngList(roomLocationList: List<RoomLocationModel>): List<MyLatLngLocation> {
        val myLatLngList = mutableListOf<MyLatLngLocation>()
        for (roomLocation in roomLocationList) {
            val list = roomLocation.list

            if (list != null) {
                myLatLngList.addAll(list)
            }
        }
        return myLatLngList
    }
    private fun convertMyLatLngListToGeoPoints(myLatLngList: List<MyLatLngLocation>): List<GeoPoint> {
        val geoPoints = mutableListOf<GeoPoint>()

        for (location in myLatLngList) {
            val latitude = location.lati
            val longitude = location.lngi

            if (latitude != null && longitude != null) {
                geoPoints.add(GeoPoint(latitude.toDouble(), longitude.toDouble()))
            }
        }
        return geoPoints
    }

    private fun getCurrentLocation() {
        locationRepository = LocationRepository(this, object : MyLocationInterface {
            override fun onLocationChange(location: Location?) {
                currentLat = location!!.latitude
                currentLong = location.longitude
                locationRepository.stopLocation()
                initializeMap(currentLat, currentLong)
//                origion=GeoPoint(currentLat,currentLong)
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

    private fun drawPolylineOnMap(coordinates: List<GeoPoint>) {
        binding.mapView.overlays.clear()
        val path = Polyline()
        path.setPoints(coordinates)
        path.color = Color.RED
        path.width = 10.0f
        binding.mapView.overlays.add(path)
        binding.mapView.invalidate()
    }

    private fun retrieveAndDisplayLocations(locationModels: List<LocationModel>) {
        val gson = Gson()
        val arrayListFromReop = ArrayList<GeoPoint>()

        for (locationModel in locationModels) {
            val serializedLocations = locationModel.serializedLocations
            val locationListType = object : TypeToken<List<Pair<Double?, Double?>>>() {}.type
            val locationList: List<Pair<Double?, Double?>> =
                gson.fromJson(serializedLocations, locationListType)

            Log.d(
                "TAG",
                "retrieveAndDisplayLocations: arrayListFromReop locationList $locationList "
            )

            for (location in locationList) {
                val latitude = location.first
                val longitude = location.second

                if (latitude != null && longitude != null) {
                    Log.d(
                        "TAG",
                        "retrieveAndDisplayLocations: arrayListFromReop latitude $latitude longitude $longitude"
                    )

                    arrayListFromReop.add(GeoPoint(latitude, longitude))
                    Log.d(
                        "TAG",
                        "retrieveAndDisplayLocations: arrayListFromReop $arrayListFromReop"
                    )
                }
            }
        }
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
    override fun onResume() {
        super.onResume()

//        initializeMap(currentLat, currentLong)
        roomReop.getAllData().observe(this){roomLocationList ->
//        mLocationDBViewModel.getAllData().observe(this) { roomLocationList ->
            if (roomLocationList != null && roomLocationList.isNotEmpty()) {
                for (roomLocationModel in roomLocationList) {
                    val date = roomLocationModel.date
                    val locationList = roomLocationModel.getList()
                    Log.d("onLocationChange", "LiveData date $date")
                    Log.d("onLocationChange", "LiveData Observer: locationList $locationList")

                }
                val myLatLngList = convertRoomLocationListToMyLatLngList(roomLocationList)
                val geoPoints = convertMyLatLngListToGeoPoints(myLatLngList)
                Log.d("onLocationChange", "LiveData Observer: locationList  geoPoints $geoPoints")

                drawPolylineOnMap(geoPoints)
            } else {
                Log.d("onLocationChange", "LiveData Observer: No data or empty roomLocationList")
            }
        }

    }
    /*
       private fun drawPolylineOnMap(coordinates: List<GeoPoint>) {
           binding.mapView.overlays.clear()

           if (coordinates.size >= 2) {
               val path = Polyline()
               path.setPoints(coordinates)
               path.color = Color.RED
               path.width = 10.0f
               binding.mapView.overlays.add(path)
               binding.mapView.invalidate()

               // Center the map on the polyline
               val mapController = binding.mapView.controller

               mapController.setZoom(18)
           }
       }
      */
    /*  private fun parseGeoPointsFromList(locationList: List<LocationModel>): List<GeoPoint> {
          val geoPoints = mutableListOf<GeoPoint>()

          for (location in locationList) {
              val latitude = location.latitude
              val longitude = location.longitude

              if (latitude != null && longitude != null) {
                  geoPoints.add(GeoPoint(latitude, longitude))
              }
          }

          return geoPoints
      }*/

    /*  mLocationDBViewModel.getAllData().observe(this) {
      Log.d("onLocationChange", "LiveData Observer: Received locationList with size ${it?.size}")
      Log.d("onLocationChange", "LiveData Observer: Received locationList with data ${it}")
      if (it != null && it.isNotEmpty()) {
          val myLatLngArrayList = ArrayList<MyLatLngLocation>()

          for (location in it) {
              location.list?.let {
                  myLatLngArrayList.addAll(it)
              }
          }


          Log.d("onLocationChange", "onLocationChange: myLatLngArrayList $myLatLngArrayList")
      } else {
          Log.d("onLocationChange", "LiveData Observer: No data or empty locationList")
      }
  }*/


    /* val locationListLiveData = roomReop.getAllLocations()
     locationListLiveData.observe(this) { locationModels ->
         Log.d("TAG", "onCreate: locationModels $locationModels")
         retrieveAndDisplayLocations(locationModels)
     }
*/

    /* locationDBViewModel.getAllLocations.observe(this, Observer { locationList ->

         val geoPoints = parseGeoPointsFromList(locationList)

         drawPolylineOnMap(geoPoints)
     })*/
    /*        CoroutineScope(Dispatchers.IO).launch {
                val location = AppDatabase(application).database.locationDao().getAllLocations()

                val geoPointsList = ArrayList<GeoPoint>()

                for (locationEntity in location) {
                    val locationList = Gson().fromJson<List<Pair<Double?, Double?>>>(
                        locationEntity.locationJson,
                        object : TypeToken<List<Pair<Double?, Double?>>>() {}.type
                    )

                    for (pair in locationList) {
                        val latitude = pair.first
                        val longitude = pair.second

                        if (latitude != null && longitude != null) {
                            geoPointsList.add(GeoPoint(latitude, longitude))
                            Log.d("TAG", "onCreate: database $geoPointsList ")
                        }
                    }
                }
            }*/

    /* val arrayList1 = parseGeoPointsFromList(LocationService.locationList.orEmpty())

     if (arrayList1.size >= 2) {
         val intermediatePoints = arrayList1.subList(1, arrayList1.size - 1)
         drawPolylineOnMap(intermediatePoints)
     } else {
         drawPolylineOnMap(arrayList1)
     }
     for (pair in LocationService.locationList.orEmpty()) {
         val latitude = pair.first
         val longitude = pair.second

         if (latitude != 0.0 && longitude != 0.0) {
             arrayList.add(GeoPoint(latitude!!, longitude!!))
             Log.d("MyTimelineActivity", "Listlocation in class $arrayList")
         }
     }
     for (i in arrayList.indices) {
         if (arrayList[0].latitude != 0.0 && arrayList[0].longitude != 0.0) {
             Log.d(
                 "MyTimelineActivity",
                 "0Lat  ${arrayList[0].latitude}   0Lng  ${arrayList[0].longitude} "
             )
             origion = GeoPoint(arrayList[i].latitude, arrayList[i].longitude)
         }
     }*/
}

