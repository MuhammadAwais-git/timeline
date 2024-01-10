package com.translate.language.timelinemoduleonmap.roomdatabase

import android.app.Application
import androidx.room.Room

class MyApplication : Application() {

    companion object {
//        lateinit var database: AppDatabase
//            private set
    }

    override fun onCreate() {
        super.onCreate()
//        database = Room.databaseBuilder(this, AppDatabase::class.java, "location_database").build()
    }
//    val locationDBViewModel: LocationDBViewModel by lazy {
//        val repository = RoomReop(database)
//        LocationDBViewModel(repository)
//    }
}
