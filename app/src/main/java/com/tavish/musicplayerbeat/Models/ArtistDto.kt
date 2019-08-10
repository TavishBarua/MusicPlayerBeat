package com.tavish.musicplayerbeat.Models

import android.os.Parcel
import android.os.Parcelable
import com.tavish.musicplayerbeat.Utils.MParcelable

class ArtistDto(
    var _artistId: Long?,
    var _artistName: String?,
    var _artistAlbumArt: String?,
    var _noOfTracksByArtist: Int?,
    var _noOfAlbumsByArtist: Int?
) : MParcelable,Cloneable {

    constructor(parcel: Parcel) : this(
        parcel.readValue(Long::class.java.classLoader) as? Long,
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Long::class.java.classLoader) as? Int,
        parcel.readValue(Long::class.java.classLoader) as? Int


    )

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeValue(_artistId)
        dest.writeString(_artistName)
        dest.writeValue(_artistAlbumArt)
        dest.writeValue(_noOfTracksByArtist)
        dest.writeValue(_noOfAlbumsByArtist)
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