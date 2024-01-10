package com.translate.language.timelinemoduleonmap.roomdatabase

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.translate.language.timelinemoduleonmap.roommodel.RoomLocationModel

@Dao
interface LocationDao {
    @Insert
    suspend fun insertLocation(location: RoomLocationModel)

    @Delete
    suspend fun deleteLocationDB(mHikingDBEntityModel: RoomLocationModel)

    @Query("SELECT * FROM RoomLocationModel")
    fun getAllLocations(): LiveData<List<RoomLocationModel>>

    @Query("DELETE FROM RoomLocationModel WHERE date = :date")
    suspend fun deleteLocationByDate(date: String)

}
