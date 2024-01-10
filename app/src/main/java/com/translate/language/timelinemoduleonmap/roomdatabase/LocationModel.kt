package com.translate.language.timelinemoduleonmap.roomdatabase

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "location_table")
data class LocationModel(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val serializedLocations: String,
    val currentDate: String
)


