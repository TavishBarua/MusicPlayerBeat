package com.tavish.musicplayerbeat.Models

import android.os.Parcel
import android.os.Parcelable
import com.tavish.musicplayerbeat.Utils.MParcelable

class SongDto(
    var _id: Long?,
    var _title: String?,
    var _album: String?,
    var _albumId: Long?,
    var _artist: String?,
    var _artistId: Long?,
    var _path: String?,
    var _trackNumber: Int?,
    var _duration: Long?
) : MParcelable,Cloneable {

    constructor(parcel: Parcel) : this(
        parcel.readValue(Long::class.java.classLoader) as? Long,
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Long::class.java.classLoader) as? Long,
        parcel.readString(),
        parcel.readValue(Long::class.java.classLoader) as? Long,
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Long::class.java.classLoader) as? Long
    )

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeValue(_id)
        dest.writeString(_title)
        dest.writeString(_album)
        dest.writeValue(_albumId)
        dest.writeValue(_artist)
        dest.writeValue(_artistId)
        dest.writeValue(_path)
        dest.writeValue(_trackNumber)
        dest.writeValue(_duration)
    }

    @Throws(CloneNotSupportedException::class)
    public override fun clone(): Any {
        return super.clone()
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SongDto> {
        override fun createFromParcel(parcel: Parcel): SongDto {
            return SongDto(parcel)
        }

        override fun newArray(size: Int): Array<SongDto?> {
            return arrayOfNulls(size)
        }
    }
}