package com.tavish.musicplayerbeat.Helpers

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import com.tavish.musicplayerbeat.Models.BeatDto
import java.io.File
import android.os.StatFs





class SongManager{

    val path = Environment.getDataDirectory()


    companion object {
        fun convertDuration(duration: Long): String {
            var out: String? = null
            var hours: Long = 0
            try {
                hours = duration / 3600000
            } catch (e: Exception) {
                // TODO Auto-generated catch block
                e.printStackTrace()
                return out!!
            }

            val remaining_minutes = (duration - hours * 3600000) / 60000
            var minutes = remaining_minutes.toString()
            if (minutes.equals(0)) {
                minutes = "00"
            }
            val remaining_seconds = duration - hours * 3600000 - remaining_minutes * 60000
            var seconds = remaining_seconds.toString()
            if (seconds.length < 2) {
                seconds = "00"
            } else {
                seconds = seconds.substring(0, 2)
            }

            if (hours > 0) {
                out = "$hours:$minutes:$seconds"
            } else {
                out = "$minutes:$seconds"
            }

            return out
        }

        fun megaBytesAvailable(file:File): Float{
            val stat = StatFs(file.path)
            val bytesAvailable = stat.blockSizeLong * stat.availableBlocksLong
            return bytesAvailable / (1024f * 1024f)

        }
    }







   /* @SuppressLint("Recycle")
    fun getPlayList(context: Context?):MutableList<BeatDto> {
        val fileList:MutableList<BeatDto> = mutableListOf()

        try {
            val selection:String = MediaStore.Audio.Media.IS_MUSIC +"!=0"
            val sortOrder:String = MediaStore.Audio.Media.DISPLAY_NAME + " ASC"
            var songCursor:Cursor? = context?.contentResolver?.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,null,selection,null,sortOrder)
            if (songCursor != null && songCursor.moveToFirst()){
                var songId:Int=songCursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val songArtist:Int=songCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                val songTitle:Int=songCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                val songData:Int=songCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
                val songAlbum:Int=songCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
               // val songDisplayName:Int=songCursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME)
                val songDuration:Int=songCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
                val songYear:Int=songCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.YEAR)
                val songAlbumID:Int = songCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)



                while (songCursor.moveToNext()){
                    val msongId:Long?=songCursor.getLong(songId)
                    val msongArtist:String?=songCursor.getString(songArtist)
                    val msongTitle:String?=songCursor.getString(songTitle)
                    val msongData:String?=songCursor.getString(songData)
                    val msongAlbum:String?=songCursor.getString(songAlbum)
                    val msongDuration:String?=songCursor.getString(songDuration)
                    val msongYear:String?=songCursor.getString(songYear)
                    val msongAlbumID:Long?=songCursor.getLong(songAlbumID)


                    fileList.add(BeatDto(msongId,msongArtist,msongTitle,msongAlbum,msongDuration,msongYear, msongAlbumID))
                }
            }
        }catch (ex:Exception){
            System.out.println(ex.stackTrace)
        }
        return fileList
    }*/





}