package com.translate.language.timelinemoduleonmap.roomdatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.translate.language.timelinemoduleonmap.histroydata.HistoryDao
import com.translate.language.timelinemoduleonmap.roommodel.RoomHistoryModel
import com.translate.language.timelinemoduleonmap.roommodel.RoomLocationModel

@Database(entities = [RoomLocationModel::class, RoomHistoryModel::class], version = 2, exportSchema = false)
abstract class LocationRoomDB : RoomDatabase() {

    abstract fun getLocationDBDao(): LocationDao
    abstract fun getHistoryDao(): HistoryDao

    companion object {
        private const val DB_NAME = "timeline_location.db"
        @Volatile
        private var instance: LocationRoomDB? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also {
                instance = it
            }
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(
            context.applicationContext,
            LocationRoomDB::class.java,
            DB_NAME
        ).build()
    }
}
