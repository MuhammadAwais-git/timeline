package com.translate.language.timelinemoduleonmap.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.translate.language.timelinemoduleonmap.MyLocationInterface
import com.translate.language.timelinemoduleonmap.R
import com.translate.language.timelinemoduleonmap.Utils.LocationRepository
import com.translate.language.timelinemoduleonmap.histroydata.HistoryRepository
import com.translate.language.timelinemoduleonmap.roomdatabase.LocationRoomDB
import com.translate.language.timelinemoduleonmap.roomdatabase.MyLatLngLocation
import com.translate.language.timelinemoduleonmap.roomdatabase.RoomReop
import com.translate.language.timelinemoduleonmap.roommodel.RoomHistoryModel
import com.translate.language.timelinemoduleonmap.roommodel.RoomLocationModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LocationService : Service() {
    private lateinit var locationRepository: LocationRepository
    private lateinit var roomReop: RoomReop
    private lateinit var historyRepository: HistoryRepository
    private var currentDate: String = ""
    private val myLatLngList = ArrayList<MyLatLngLocation>()
    private val uniqueLocations = HashSet<Pair<Double?, Double?>>()

    companion object {

        private val uniqueLocations = HashSet<Pair<Double?, Double?>>()

        const val CHANNEL_ID = "ALARM_SERVICE_CHANNEL"
    }


/*    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        roomReop = RoomReop(LocationRoomDB.invoke(this))
        historyRepository = HistoryRepository(LocationRoomDB.invoke(this))
        currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }*/
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        roomReop = RoomReop(LocationRoomDB.invoke(this))
        historyRepository = HistoryRepository(LocationRoomDB.invoke(this))
        loadPreviousData()
        currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }
    private fun loadPreviousData() {
       /* roomReop.getAllData().observeForever { previousData ->
            Log.d("Service", "loadPreviousData:observeForever $previousData")
            previousData?.forEach { roomLocationModel ->
                Log.d("Service", "loadPreviousData:forEach $roomLocationModel")
                 roomLocationModel.getList()?.let {
                     Log.d("Service", "loadPreviousData:roomLocationModel $it")
                     if (it.isNotEmpty() && it !=null) {
                        *//* myLatLngList.addAll(it)
                         uniqueLocations.addAll(it.map { it.toPair() })*//*
                     }
                 }
            }
        }*/
    }



    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "TimeLine Location Service",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(
                NotificationManager::class.java
            )
            manager.createNotificationChannel(channel)
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.d("Service", "onStartCommand: AlarmService")
        getCurrentLocation()
        Toast.makeText(this, "onStartCommand", Toast.LENGTH_SHORT).show()
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("TimeLine")
            .setContentText("Location Running in background")
            .setSmallIcon(R.drawable.baseline_circle_notifications_24)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .build()
        startForeground(1, notification)
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        Log.d("onLocationChange:latitude", "super.onDestroy(): ")
        super.onDestroy()
        Log.d("onLocationChange:latitude", "onDestroy: ")
        val broadcastIntent = Intent()
        broadcastIntent.action = "restartservice"
        broadcastIntent.setClass(this, Restarter::class.java)
        this.sendBroadcast(broadcastIntent)
    }
   /* override fun onDestroy() {
        super.onDestroy()
        Log.d("onLocationChange:latitude", "onDestroy: ")
        stopForeground(true)
    }*/

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private fun getCurrentLocation() {
        locationRepository = LocationRepository(context = this.applicationContext, object : MyLocationInterface {
            override fun onLocationChange(location: Location?) {
                val latitude = location?.latitude
                val longitude = location?.longitude
                Log.d("TAG", "onLocationChange:latitude $latitude  longitude $longitude  ,")
//                Toast.makeText(this@LocationService, "onLocationChange latitude $latitude  longitude $longitude", Toast.LENGTH_SHORT).show()


                val locationPair = Pair(latitude, longitude)

                if (latitude != 0.0 && longitude != 0.0 && !uniqueLocations.contains(locationPair)) {
                    uniqueLocations.add(locationPair)
                    myLatLngList.add(MyLatLngLocation(latitude.toString(), longitude.toString()))
                    Log.d("TAG", "onLocationChange: myLatLngList Service $myLatLngList")

                    val model = RoomLocationModel(
                        currentDate,
                        myLatLngList
                    )
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            roomReop.deleteLocationByDate(currentDate)
                            roomReop.insertLocationDB(model)
                        } catch (e: Exception) {
                            Log.e("LocationService", "Error inserting location list: ${e.message}")
                        }
                    }
                    val newDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                    if (newDate != currentDate) {
                        // Date has changed, save the data in the history table
                        val historyModel = RoomHistoryModel(currentDate, myLatLngList)
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                historyRepository.insertHistoryLocationDB(historyModel)
                                currentDate = newDate
                                myLatLngList.clear()
                            } catch (e: Exception) {
                                Log.e("LocationService", "Error inserting history location list: ${e.message}")
                            }
                        }
                    }
                }
            }
        })
    }


    private fun saveLocationListToDatabase(myLatLngList: ArrayList<MyLatLngLocation>) {
        val currentDateString = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
        if (currentDate != currentDateString && myLatLngList.isNotEmpty()) {
            currentDate = currentDateString

            CoroutineScope(Dispatchers.IO).launch {
                try {
//                    roomReop.deleteLocationDB(RoomLocationModel(currentDate, myLatLngList))
//                    roomReop.deleteLocationByDate(currentDate)
                    roomReop.insertLocationDB(RoomLocationModel(currentDate, myLatLngList))
                } catch (e: Exception) {
                    Log.e("LocationService", "Error inserting location list: ${e.message}")
                }
            }
        }
    }
}