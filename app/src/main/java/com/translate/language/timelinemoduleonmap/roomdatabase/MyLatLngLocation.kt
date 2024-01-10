package com.translate.language.timelinemoduleonmap.roomdatabase

import android.os.Parcel
import android.os.Parcelable

data class MyLatLngLocation(val lati:String?, val lngi:String?): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString()
    ) {
    }
    fun toPair(): Pair<Double?, Double?> {
        val latitude = lati?.toDoubleOrNull()
        val longitude = lngi?.toDoubleOrNull()

        return Pair(latitude, longitude)
    }
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(lati)
        parcel.writeString(lngi)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MyLatLngLocation> {
        override fun createFromParcel(parcel: Parcel): MyLatLngLocation {
            return MyLatLngLocation(parcel)
        }

        override fun newArray(size: Int): Array<MyLatLngLocation?> {
            return arrayOfNulls(size)
        }
    }

}
