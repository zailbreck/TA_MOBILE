package com.kaorimaps.ssa.config

import android.app.Activity
import android.graphics.Bitmap
import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.kaorimaps.ssa.R

class DestLog (val id: String?, val name: String?, val summary: String?, val contents: String?, val address: String?,val distance: String?, val lat: Double, val lng: Double,  val img: String?) :
    Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(summary)
        parcel.writeString(contents)
        parcel.writeString(address)
        parcel.writeString(distance)
        parcel.writeDouble(lat)
        parcel.writeDouble(lng)
        parcel.writeString(img)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DestLog> {
        override fun createFromParcel(parcel: Parcel): DestLog {
            return DestLog(parcel)
        }

        override fun newArray(size: Int): Array<DestLog?> {
            return arrayOfNulls(size)
        }
    }
}

class DestListAdapter(private val context: Activity, private val name: ArrayList<String>,  private val address: ArrayList<String>, private val content: ArrayList<String>, private val distance: ArrayList<String> ,  private val imgid: ArrayList<Bitmap> )
    : ArrayAdapter<String>(context, R.layout.dest_item, name) {

    @RequiresApi(Build.VERSION_CODES.M)
    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        val inflater = context.layoutInflater
        val rowView = inflater.inflate(R.layout.dest_item, null, true)

        val titleText = rowView.findViewById<TextView>(R.id.title)
        val imageView = rowView.findViewById<ImageView>(R.id.iconView)
        val tanggalText = rowView.findViewById<TextView>(R.id.address)
        val distanceText = rowView.findViewById<TextView>(R.id.distance)
        val contentText = rowView.findViewById<TextView>(R.id.content)

        titleText.text = name[position]
        tanggalText.text = address[position]
        contentText.text = content[position]
        distanceText.text = distance[position]
        imageView.setImageBitmap(imgid[position])

        return rowView

    }
}