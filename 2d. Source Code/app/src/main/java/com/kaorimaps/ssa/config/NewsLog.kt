package com.kaorimaps.ssa.config

import android.app.Activity
import android.graphics.Bitmap
import android.os.Parcel
import android.os.Parcelable
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.kaorimaps.ssa.R

class NewsLog (val id: String?, val title: String?, val summary: String?, val contents: String?, val date: String?, val img: String?) : Parcelable{
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(title)
        parcel.writeString(summary)
        parcel.writeString(contents)
        parcel.writeString(date)
        parcel.writeString(img)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<NewsLog> {
        override fun createFromParcel(parcel: Parcel): NewsLog {
            return NewsLog(parcel)
        }

        override fun newArray(size: Int): Array<NewsLog?> {
            return arrayOfNulls(size)
        }
    }
}

class NewsListAdapter(private val context: Activity, private val title: ArrayList<String>, private val tanggal: ArrayList<String>, private val summary: ArrayList<String>, private val imgid: ArrayList<Bitmap>)
    : ArrayAdapter<String>(context, R.layout.news_item, title) {

    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        val inflater = context.layoutInflater
        val rowView = inflater.inflate(R.layout.news_item, null, true)

        val titleText = rowView.findViewById(R.id.title) as TextView
        val imageView = rowView.findViewById(R.id.iconView) as ImageView
        val tanggalText = rowView.findViewById(R.id.tanggal) as TextView
        val summaryText = rowView.findViewById(R.id.summary) as TextView

        titleText.text = title[position]
        tanggalText.text = tanggal[position]
        summaryText.text = summary[position]
        imageView.setImageBitmap(imgid[position])

        return rowView
    }
}