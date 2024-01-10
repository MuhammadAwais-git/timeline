package com.translate.language.timelinemoduleonmap.roomdatabase

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.translate.language.timelinemoduleonmap.roommodel.RoomLocationModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LocationDBViewModel(private val repository: RoomReop) : ViewModel() {

    fun insertLocationDBEntity(mLocationDBEntityModel: RoomLocationModel) {
        viewModelScope.launch {
            Dispatchers.IO
            repository.insertLocationDB(mLocationDBEntityModel)
        }
    }

    fun deleteLocationDBEntity(mRoomLocationModel: RoomLocationModel) {
        viewModelScope.launch {
            Dispatchers.IO
            repository.deleteLocationDB(mRoomLocationModel)
        }
    }

    fun getAllData() = repository.getAllData()
}
