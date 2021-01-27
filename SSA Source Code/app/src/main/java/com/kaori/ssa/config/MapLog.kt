package com.kaori.ssa.config

import android.os.Parcel
import android.os.Parcelable

class MapLog(val lat: Double, val lng: Double, val rad: Double, val title: String?, val icon: String?) :
    Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeDouble(lat)
        parcel.writeDouble(lng)
        parcel.writeDouble(rad)
        parcel.writeString(title)
        parcel.writeString(icon)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MapLog> {
        override fun createFromParcel(parcel: Parcel): MapLog {
            return MapLog(parcel)
        }

        override fun newArray(size: Int): Array<MapLog?> {
            return arrayOfNulls(size)
        }
    }
}

class RuteLog(val lat: Double, val lng: Double) : Parcelable{
    constructor(parcel: Parcel) : this(
        parcel.readDouble(),
        parcel.readDouble()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeDouble(lat)
        parcel.writeDouble(lng)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<RuteLog> {
        override fun createFromParcel(parcel: Parcel): RuteLog {
            return RuteLog(parcel)
        }

        override fun newArray(size: Int): Array<RuteLog?> {
            return arrayOfNulls(size)
        }
    }

}