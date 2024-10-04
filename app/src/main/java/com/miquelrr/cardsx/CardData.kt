package com.miquelrr.cardsx

import android.os.Parcel
import android.os.Parcelable

data class CardData(
    val imageUrl: String,
    val title: String,
    val description: String,
    val externalLink: String,
    val cardViewType: Int,
    var isSelected: Boolean = false
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readByte() != 0.toByte()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(imageUrl)
        parcel.writeString(title)
        parcel.writeString(description)
        parcel.writeString(externalLink)
        parcel.writeInt(cardViewType)
        parcel.writeByte(if (isSelected) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CardData> {
        override fun createFromParcel(parcel: Parcel): CardData {
            return CardData(parcel)
        }

        override fun newArray(size: Int): Array<CardData?> {
            return arrayOfNulls(size)
        }
    }
}