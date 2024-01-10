package com.translate.language.timelinemoduleonmap.timeline

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.LocationServices
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
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LocationService : Service() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var locationClient: LocationClient
    private lateinit var notificationManager: NotificationManager
    private lateinit var notificationBuilder: NotificationCompat.Builder
    private lateinit var roomReop: RoomReop
    private lateinit var historyRepository: HistoryRepository
    private var currentDate: String = ""
    private val myLatLngList = ArrayList<MyLatLngLocation>()
    private val uniqueLocations = HashSet<Pair<String?, String?>>()

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        locationClient = DefaultLocationClient(
            applicationContext,
            LocationServices.getFusedLocationProviderClient(applicationContext)
        )
        roomReop = RoomReop(LocationRoomDB.invoke(this))
        historyRepository = HistoryRepository(LocationRoomDB.invoke(this))
        currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "location",
                "Location Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationBuilder = NotificationCompat.Builder(this, "location")
            .setContentTitle("Tracking location...")
            .setContentText("Location: null")
            .setSmallIcon(R.drawable.location_off)
            .setOngoing(true)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> start()
            ACTION_STOP -> stop()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun start() {
        serviceScope.launch {
            locationClient
                .getLocationUpdates(1000L)
                .catch { e -> e.printStackTrace() }
                .onEach { location ->
                    val latitude = location.latitude.toString()
                    val longitude = location.longitude.toString()
                    val updatedNotification = notificationBuilder.setContentText(
                        "Location: ($latitude, $longitude)"
                    )
                    val locationPair = Pair(latitude, longitude)

                    if (latitude != "" && longitude != "" && !uniqueLocations.contains(locationPair)) {
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
                    notificationManager.notify(1, updatedNotification.build())
                    Log.d("LocationService", "start: Location: ($latitude, $longitude) ")
                }
                .launchIn(serviceScope)
        }

        startForeground(1, notificationBuilder.build())
    }

    private fun stop() {
        stopForeground(true)
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
    }
}

