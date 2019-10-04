package com.tavish.musicplayerbeat.Models

import android.os.Parcel
import android.os.Parcelable
import com.tavish.musicplayerbeat.Utils.MParcelable

class GenreDto(
    var _genreId: Long?,
    var _genreName: String?,
    var _genreAlbumArt: String?,
    var _noOfAlbumsInGenre: Int?
) : MParcelable,Cloneable {

    constructor(parcel: Parcel) : this(
        parcel.readValue(Long::class.java.classLoader) as? Long,
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Long::class.java.classLoader) as? Int
    )

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeValue(_genreId)
        dest.writeString(_genreName)
        dest.writeValue(_genreAlbumArt)
        dest.writeValue(_noOfAlbumsInGenre)
    }

    @Throws(CloneNotSupportedException::class)
    public override fun clone(): Any {
        return super.clone()
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<GenreDto> {
        override fun createFromParcel(parcel: Parcel): GenreDto {
            return GenreDto(parcel)
        }

        override fun newArray(size: Int): Array<GenreDto?> {
            return arrayOfNulls(size)
        }
    }
}