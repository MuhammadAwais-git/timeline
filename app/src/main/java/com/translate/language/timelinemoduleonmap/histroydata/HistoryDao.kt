package com.translate.language.timelinemoduleonmap.histroydata

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.translate.language.timelinemoduleonmap.roommodel.RoomHistoryModel

@Dao
interface HistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistoryLocation(historyLocationModel: RoomHistoryModel)

    @Query("SELECT * FROM RoomHistoryModel")
    fun getAllHistoryLocations(): LiveData<List<RoomHistoryModel>>


}
