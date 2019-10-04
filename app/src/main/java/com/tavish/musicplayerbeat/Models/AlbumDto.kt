package com.tavish.musicplayerbeat.Models

import android.os.Parcel
import android.os.Parcelable
import com.tavish.musicplayerbeat.Utils.MParcelable

class AlbumDto(
    var _id: Long?,
    var _album: String?,
    var _artist: String?,
    var _albumart: String?
) : MParcelable,Cloneable {

    constructor(parcel: Parcel) : this(
        parcel.readValue(Long::class.java.classLoader) as? Long,
        parcel.readString(),
        parcel.readString(),
        parcel.readString()

    )

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeValue(_id)
        dest.writeString(_album)
        dest.writeValue(_artist)
        dest.writeString(_albumart)
    }

    @Throws(CloneNotSupportedException::class)
    public override fun clone(): Any {
        return super.clone()
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AlbumDto> {
        override fun createFromParcel(parcel: Parcel): AlbumDto {
            return AlbumDto(parcel)
        }

        override fun newArray(size: Int): Array<AlbumDto?> {
            return arrayOfNulls(size)
        }
    }
}