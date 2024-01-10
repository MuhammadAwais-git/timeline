package com.translate.language.timelinemoduleonmap.roomdatabase

import androidx.lifecycle.LiveData
import com.translate.language.timelinemoduleonmap.roommodel.RoomLocationModel

class RoomReop(private val streetViewSTRoomDB: LocationRoomDB) {
    suspend fun insertLocationDB(mLocationDBEntityModel: RoomLocationModel) =
        streetViewSTRoomDB.getLocationDBDao().insertLocation(mLocationDBEntityModel)

    suspend fun deleteLocationDB(mLocationDBEntityModel: RoomLocationModel) =
        streetViewSTRoomDB.getLocationDBDao().deleteLocationDB(mLocationDBEntityModel)

    suspend fun deleteLocationByDate(date: String) {
        streetViewSTRoomDB.getLocationDBDao().deleteLocationByDate(date)
    }

    fun getAllData(): LiveData<List<RoomLocationModel>> = streetViewSTRoomDB.getLocationDBDao().getAllLocations()

}

