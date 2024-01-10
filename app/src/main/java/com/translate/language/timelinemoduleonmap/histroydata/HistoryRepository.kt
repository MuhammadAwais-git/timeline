package com.translate.language.timelinemoduleonmap.histroydata

import androidx.lifecycle.LiveData
import com.translate.language.timelinemoduleonmap.roomdatabase.LocationRoomDB
import com.translate.language.timelinemoduleonmap.roommodel.RoomHistoryModel
import com.translate.language.timelinemoduleonmap.roommodel.RoomLocationModel

class HistoryRepository(private val mHistoryDb: LocationRoomDB) {

    suspend fun insertHistoryLocationDB(mLocationDBEntityModel: RoomHistoryModel) =
        mHistoryDb.getHistoryDao().insertHistoryLocation(mLocationDBEntityModel)

    fun getAllHistoryModels(): LiveData<List<RoomHistoryModel>> = mHistoryDb.getHistoryDao().getAllHistoryLocations()

}

