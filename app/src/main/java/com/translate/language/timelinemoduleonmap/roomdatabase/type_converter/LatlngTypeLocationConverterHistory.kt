package com.translate.language.timelinemoduleonmap.roomdatabase.type_converter

import androidx.room.TypeConverter
import com.google.gson.reflect.TypeToken
import com.google.gson.Gson
import com.translate.language.timelinemoduleonmap.roomdatabase.MyLatLngLocation
import java.lang.reflect.Type
import java.util.*


object LatlngTypeLocationConverterHistory {
    var gson = Gson()
    @TypeConverter
    fun stringToUserModelList(data: String?): List<MyLatLngLocation> {
        if (data == null) {
            return Collections.emptyList()
        }
        val listType: Type = object : TypeToken<List<MyLatLngLocation?>?>() {}.type
        return LatlngTypeLocationConverterHistory.gson.fromJson<List<MyLatLngLocation>>(data, listType)
    }

    @TypeConverter
    fun someObjectListToString(someObjects: List<MyLatLngLocation?>?): String {
        return LatlngTypeLocationConverterHistory.gson.toJson(someObjects)
    }
}