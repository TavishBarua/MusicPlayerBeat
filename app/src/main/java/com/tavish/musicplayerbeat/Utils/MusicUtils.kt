package com.tavish.musicplayerbeat.Utils

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.text.TextUtils
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.tavish.musicplayerbeat.Helpers.SharedPrefHelper
import com.tavish.musicplayerbeat.Models.AlbumDto
import com.tavish.musicplayerbeat.Models.SongDto
import com.tavish.musicplayerbeat.R


object MusicUtils {

    const val albumArtUri = "content://media/external/audio/albumart"

    var mIsBack = false


    fun makeLabel(context: Context, intPlural: Int, number: Int): String {
        return context.resources.getQuantityString(intPlural, number, number)
    }

    fun getAlbumArtUri(paramInt: Long): Uri? {
        return ContentUris.withAppendedId(Uri.parse(albumArtUri), paramInt)
    }


    fun artistSongCursorBuilder(context: Context, artistID: Long): Cursor? {
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


    fun searchSongs(context: Context, searchString: String): ArrayList<SongDto> {
        return getSongsCursor(
            songCursorBuilder(
                context,
                "title LIKE ?",
                arrayOf("%$searchString%")
            )
        )
    }

    private fun songCursorBuilder(
        context: Context,
        selection: String,
        paramSearch: Array<String>
    ): Cursor {
        var selectionStmt = "is_music=1 AND title != ''"
        val songSortOrder =
            SharedPrefHelper.getInstance().getString(SharedPrefHelper.Key.SONG_SORT_ORDER)

        if (!TextUtils.isEmpty(selection)) {
            selectionStmt += " AND $selection"
        }

        val cursor = context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            arrayOf(
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.TRACK,
                MediaStore.Audio.Media.ARTIST_ID
            ), selectionStmt, paramSearch, songSortOrder
        )

        return cursor

    }

    private fun getSongsCursor(cursor: Cursor): ArrayList<SongDto> {

        var songs = arrayListOf<SongDto>()
        if (cursor != null && cursor.moveToFirst()) {
            do {
                val song = SongDto(
                    cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID)),
                    cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)),
                    cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)),
                    cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)),
                    cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)),
                    cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID)),
                    cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)),
                    cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.TRACK)),
                    cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
                )
                songs.add(song)
            } while (cursor.moveToNext())
        }
        if (cursor != null) {
            cursor.close()
        }

        return songs
    }


    fun searchAlbums(context: Context, searchString: String): ArrayList<AlbumDto> {
        return getAlbumsCursor(
            albumCursorBuilder(
                context,
                "album LIKE ?",
                arrayOf("%$searchString%")
            )
        )
    }

    private fun albumCursorBuilder(
        context: Context,
        selection: String,
        paramSearch: Array<String>
    ): Cursor {
        val sort: String = (SharedPrefHelper.getInstance().getString(
            SharedPrefHelper.Key.ALBUM_SORT_ORDER,
            MediaStore.Audio.Albums.DEFAULT_SORT_ORDER
        )
                + SharedPrefHelper.getInstance().getString(
            SharedPrefHelper.Key.ALBUM_SORT_TYPE,
            Constants.ASCENDING
        ))

        return context.contentResolver.query(
            MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, arrayOf(
                "_id", "album", "artist", "album_art"
            ), selection, paramSearch, sort
        )!!

    }

    private fun getAlbumsCursor(cursor: Cursor): ArrayList<AlbumDto> {

        val albums = arrayListOf<AlbumDto>()

        if (cursor != null && cursor.moveToFirst()) do {
            val album = AlbumDto(
                cursor.getLong(0),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getString(3)
            )
            albums.add(album)
        } while (cursor.moveToNext())
        cursor.close()

        return albums
    }

    fun getDominantColor(bitmap:Bitmap):Int{
        val newBitmap = Bitmap.createScaledBitmap(bitmap, 2,2,true)
        val color = newBitmap.getPixel(1,1)
        newBitmap.recycle()
        return color
    }

    fun getDarkOrLight(color:Int, context: Context):Int{
       /* if(ColorUtils.calculateLuminance(color)>0.50){*/
                return ResourcesCompat.getColor(context.resources,R.color.velvet_white,null)
        /*}else {
            return ResourcesCompat.getColor(context.resources,R.color.smooth_violet,null)
        }*/
    }

    fun slideInFragmentAnimation(layoutView: View, fragmentActivity: FragmentActivity, fragment: Fragment){
        if (mIsBack) return
        val transition = TranslateAnimation(Animation.RELATIVE_TO_SELF,0.0f
                                            ,Animation.RELATIVE_TO_SELF,0.0f
                                            ,Animation.RELATIVE_TO_SELF,0.0f
                                            ,Animation.RELATIVE_TO_SELF,2.0f)
        transition.let {
            it.duration = 250
            it.interpolator = AccelerateInterpolator(2.0f)
        }

        transition.setAnimationListener(object : Animation.AnimationListener{
            override fun onAnimationRepeat(animation: Animation?) {

            }

            override fun onAnimationEnd(animation: Animation?) {
                layoutView.visibility= View.INVISIBLE
                mIsBack=false
                fragmentActivity.supportFragmentManager.let {
                    it.beginTransaction().remove(fragment).commit()
                    it.popBackStack()
                }

            }

            override fun onAnimationStart(animation: Animation?) {

            }
        })

        layoutView.startAnimation(transition)
    }

    fun slideOutFragmentAnimation(layoutView: View){
        val transition = TranslateAnimation(Animation.RELATIVE_TO_SELF,0.0f
            ,Animation.RELATIVE_TO_SELF,0.0f
            ,Animation.RELATIVE_TO_SELF,-2.0f
            ,Animation.RELATIVE_TO_SELF,0.0f)
        transition.let {
            it.duration = 250
        }
        transition.setAnimationListener(object:Animation.AnimationListener{
            override fun onAnimationRepeat(animation: Animation?) {

            }

            override fun onAnimationEnd(animation: Animation?) {

            }

            override fun onAnimationStart(animation: Animation?) {
                layoutView.visibility=View.VISIBLE
            }
        })
        layoutView.startAnimation(transition)
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
