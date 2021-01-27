package com.kaori.ssa.config

import android.app.Activity
import android.os.Parcel
import android.os.Parcelable
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.kaori.ssa.R

class InfoLog(val id: String?, val title: String?, val content: String?) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(title)
        parcel.writeString(content)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<InfoLog> {
        override fun createFromParcel(parcel: Parcel): InfoLog {
            return InfoLog(parcel)
        }

        override fun newArray(size: Int): Array<InfoLog?> {
            return arrayOfNulls(size)
        }
    }


}

class InfoListAdapter(
    private val context: Activity,
    private val title: ArrayList<String>,
    private val content: ArrayList<String>
)
    : ArrayAdapter<String>(context, R.layout.info_item, title){


    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        val inflater = context.layoutInflater
        val rowView = inflater.inflate(R.layout.info_item, null, true)

        val titleText = rowView.findViewById<TextView>(R.id.title)
        val contentText = rowView.findViewById<TextView>(R.id.content)

        titleText.text = title[position]
        contentText.text = content[position]

        return rowView
    }
}