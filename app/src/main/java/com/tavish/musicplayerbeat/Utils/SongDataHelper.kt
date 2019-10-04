package com.tavish.musicplayerbeat.Utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import com.squareup.picasso.Picasso
import com.tavish.musicplayerbeat.Common
import com.tavish.musicplayerbeat.Models.SongDto
import java.lang.Exception

class SongDataHelper {

    private var mApp: Common? = null


    //Song parameters.
    var mTitle: String? = null
    var mArtist: String? = null
    var mAlbum: String? = null
    var mAlbumArtist: String? = null


    var mDuration: Long = 0
    var mFilePath: String? = null
    var mGenre: String? = null
    var mId: Long = 0
    var mAlbumArtPath: String? = null
    var mSource: String? = null
    var mLocalCopyPath: String? = null
    var mSavedPosition: Long = 0
    var mAlbumArt: Bitmap? = null
    var mColor: Int = 0


    fun populateSongData(
        context: Context,
        songs: MutableList<SongDto>?,
        index: Int
    ) {

        mApp = context.applicationContext as Common

        if (songs == null && mApp?.isServiceRunning()!! && mApp?.mService != null) {


            mApp?.mService?.mSongs?.get(index)?.apply {
                mId = _id!!
                mTitle = _title
                mAlbum = _album
                mArtist = _artist
                mDuration = _duration!!
                mFilePath = _path
                mAlbumArtPath = MusicUtils.getAlbumArtUri(_albumId!!).toString()


            }

            /* this.mId = mApp?.mService?.mSongs?.get(index)?._id!!
             this.mTitle = mApp?.mService?.mSongs?.get(index)?._title!!
             this.mAlbum = mApp?.mService?.mSongs?.get(index)?._album!!
             this.mArtist = mApp?.mService?.mSongs?.get(index)?._artist!!
             this.mDuration = mApp?.mService?.mSongs?.get(index)?._duration!!
             this.mFilePath = mApp?.mService?.mSongs?.get(index)?._path!!
             this.mAlbumArtPath = MusicUtils.getAlbumArtUri(mApp?.mService?.mSongs?.get(index)?._albumId!!).toString()
 */
            Picasso.get().load(mAlbumArtPath).into(object : com.squareup.picasso.Target {
                override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                    mAlbumArt = bitmap
                }

                override fun onPrepareLoad(placeHolderDrawable: Drawable?) {

                }

                override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                    Logger.log("Falied to load image from ::SongDataHelper class")
                }


            })


        } else {

            songs?.get(index)?.apply {
                mId = _id!!
                mTitle = _title
                mAlbum = _album
                mArtist = _artist
                mDuration = _duration!!
                mFilePath = _path
                mAlbumArtPath = MusicUtils.getAlbumArtUri(_albumId!!).toString()

            }
           /* this.mId = songs?.get(index)?._id!!
            this.mTitle = songs.get(index)._title!!
            this.mAlbum = songs.get(index)._album!!
            this.mArtist = songs.get(index)._artist!!
            this.mDuration = songs.get(index)._duration!!
            this.mFilePath = songs.get(index)._path!!
            this.mAlbumArtPath = MusicUtils.getAlbumArtUri(songs.get(index)._albumId!!).toString()*/
        }


    }


}