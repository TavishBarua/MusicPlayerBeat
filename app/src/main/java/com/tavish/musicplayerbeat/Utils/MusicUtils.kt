package com.tavish.musicplayerbeat.Utils

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.tavish.musicplayerbeat.R


object MusicUtils {

    const val albumArtUri="content://media/external/audio/albumart"



    fun getAlbumArtUri(paramInt: Long): Uri {
        return ContentUris.withAppendedId(Uri.parse(albumArtUri), paramInt)
    }


    fun makeArtistSongCursor(context: Context, artistID: Long): Cursor? {
        val contentResolver = context.contentResolver
        //  val artistSongSortOrder = PreferencesHelper.getInstance().getString(PreferencesHelper.Key.SONG_SORT_ORDER)
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val string = "is_music=1 AND title != '' AND artist_id=$artistID"
        return contentResolver.query(
            uri,
            arrayOf("_id", "title", "artist", "album", "duration", "track", "album_id", "_data"),
            string,
            null,
            null
            //   artistSongSortOrder
        )
    }

    fun convertMillisToMinsSecs(milliseconds: Long): String {

        val secondsValue = (milliseconds / 1000).toInt() % 60
        val minutesValue = (milliseconds / (1000 * 60) % 60).toInt()
        val hoursValue = (milliseconds / (1000 * 60 * 60) % 24).toInt()

        var seconds = ""
        var minutes = ""
        var hours = ""

        if (secondsValue < 10) {
            seconds = "0$secondsValue"
        } else {
            seconds = "" + secondsValue
        }

        if (minutesValue < 10) {
            minutes = "0$minutesValue"
        } else {
            minutes = "" + minutesValue
        }

        if (hoursValue < 10) {
            hours = "0$hoursValue"
        } else {
            hours = "" + hoursValue
        }

        var output = " "

        if (hoursValue != 0) {
            output = "$hours:$minutes:$seconds"
        } else {
            output = "$minutes:$seconds"
        }

        return output
    }


    fun makeShortTimeString(context: Context, secs: Long): String {
        var secs = secs
        val hours: Long
        val mins: Long

        hours = secs / 3600
        secs %= 3600
        mins = secs / 60
        secs %= 60

        val durationFormat = context.resources.getString(
            if (hours == 0L) R.string.durationformatshort else R.string.durationformatlong
        )
        return String.format(durationFormat, hours, mins, secs)
    }

    fun isLollipop(): Boolean {
        return Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP
    }




}
