package com.translate.language.timelinemoduleonmap.histroydata

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class HistoryModelFactory(private val repository: HistoryRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HistoryViewModel::class.java)) {
            return HistoryViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
