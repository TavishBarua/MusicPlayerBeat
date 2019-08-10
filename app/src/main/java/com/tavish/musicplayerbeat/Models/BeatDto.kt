package com.tavish.musicplayerbeat.Models

import android.os.Parcel
import android.os.Parcelable
import com.tavish.musicplayerbeat.Utils.MParcelable
import java.io.Serializable

class BeatDto(
    val songId: Long?,
    val songTitle: String?,
    val songArtist: String?,
    val songAlbum: String?,
    val songDuration: Long?,
    val songYear: String?,
    val songAlbumID: Long?,
    val ArtistID: Long?,
    val Data: String?,
    val Track: Int?
):MParcelable, Serializable {

  /*  var mSongID:Long?=null
    var mArtist:String? = null
    var mTitle:String? = null
    // var mPath:String? = null
    var mAlbum:String? = null
    var mDuration:String? = null
    //var mGenre:String? = null
    var mYear:String? = null
    var mAlbumID:Long?=null*/
    constructor(parcel: Parcel) : this(
        parcel.readValue(Long::class.java.classLoader) as? Long,
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Long::class.java.classLoader) as? Long,
        parcel.readString(),
        parcel.readValue(Long::class.java.classLoader) as? Long,
        parcel.readValue(Long::class.java.classLoader) as? Long,
        parcel.readString(),
        parcel.readValue(Long::class.java.classLoader) as? Int
    )
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(songId)
        parcel.writeString(songTitle)
        parcel.writeString(songArtist)
        parcel.writeString(songAlbum)
        parcel.writeValue(songDuration)
        parcel.writeString(songYear)
        parcel.writeValue(songAlbumID)
        parcel.writeValue(ArtistID)
        parcel.writeString(Data)
        parcel.writeValue(ArtistID)
    }

/*    override fun describeContents(): Int {
        return 0
    }*/

    companion object CREATOR : Parcelable.Creator<BeatDto> {
        override fun createFromParcel(parcel: Parcel): BeatDto {
            return BeatDto(parcel)
        }

        override fun newArray(size: Int): Array<BeatDto?> {
            return arrayOfNulls(size)
        }
    }


}