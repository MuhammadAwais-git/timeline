package com.translate.language.timelinemoduleonmap.roomdatabase

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class LocationDBViewModelFactory(private val repository: RoomReop) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LocationDBViewModel::class.java)) {
            return LocationDBViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
