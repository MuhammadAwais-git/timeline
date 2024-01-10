package com.translate.language.timelinemoduleonmap.histroydata

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.translate.language.timelinemoduleonmap.roomdatabase.LocationRoomDB
import com.translate.language.timelinemoduleonmap.roomdatabase.RoomReop
import com.translate.language.timelinemoduleonmap.roommodel.RoomHistoryModel
import com.translate.language.timelinemoduleonmap.roommodel.RoomLocationModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HistoryViewModel(private val repository: HistoryRepository) : ViewModel(){


    fun insertHistoryDBEntity(mLocationDBEntityModel: RoomHistoryModel) {
        viewModelScope.launch {
            Dispatchers.IO
            repository.insertHistoryLocationDB(mLocationDBEntityModel)
        }
    }
    fun getAllData() = repository.getAllHistoryModels()
}
