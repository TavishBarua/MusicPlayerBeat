package com.tavish.musicplayerbeat.Helpers.MediaHelpers

import android.content.ContentValues
import android.content.Context
import android.content.SharedPreferences
import android.database.Cursor
import android.net.Uri
import android.preference.PreferenceManager
import android.provider.BaseColumns
import android.provider.MediaStore
import com.tavish.musicplayerbeat.Common
import com.tavish.musicplayerbeat.DB.DBHelper
import com.tavish.musicplayerbeat.Helpers.SharedPrefHelper
import com.tavish.musicplayerbeat.Models.AlbumDto
import com.tavish.musicplayerbeat.Models.BeatDto
import com.tavish.musicplayerbeat.Models.SongDto
import com.tavish.musicplayerbeat.Utils.Logger
import com.tavish.musicplayerbeat.Utils.MusicUtils
import java.io.File

object MusicCursor {


    fun buildMusicLibrary(): Boolean{

        val common=Common.getInstance() as Common
        if (!shouldScan()){
            return false
        }
        try {
            val query =
                "_id in (select genre_id from audio_genres_map where audio_id in (select _id from audio_meta where is_music != 0))"

            common.getDBAccessHelper().writableDatabase.beginTransaction()

            val columns = arrayOf(MediaStore.Audio.Genres._ID, MediaStore.Audio.Genres.NAME)

            val cursor = Common.getInstance()?.contentResolver?.query (
                MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI,
                columns,
                query,
                null,
                MediaStore.Audio.Genres.NAME
            )

            try {
                common.getDBAccessHelper().writableDatabase.delete(DBHelper.GENRES_TABLE,null,null)
                if (cursor!=null&&cursor.moveToNext()){
                    do {
                        val genre = ContentValues()
                        genre.put(DBHelper.GENRE_ID, cursor.getString(0))
                        genre.put(DBHelper.GENRE_NAME, cursor.getString(1))

                        val albums = getAlbumsSelection("GENRES", cursor.getString(0))
                        if (albums != null && albums.size > 0) {
                            genre.put(
                                DBHelper.GENRE_ALBUM_ART,
                                MusicUtils.getAlbumArtUri(albums.get(0)._id!!).toString()
                            )
                           // genre.put(DBHelper.NO_OF_ALBUMS_IN_GENRE, "" + albums.size)
                            genre.put(DBHelper.NO_OF_ALBUMS_IN_GENRE, "${albums.size}")
                        }
                        common.getDBAccessHelper().writableDatabase.insert(DBHelper.GENRES_TABLE,null, genre)

                    }while (cursor.moveToNext());
                }

                val artistCols = arrayOf(
                    MediaStore.Audio.Artists._ID,
                    MediaStore.Audio.Artists.ARTIST,
                    MediaStore.Audio.Artists.NUMBER_OF_TRACKS,
                    MediaStore.Audio.Artists.NUMBER_OF_ALBUMS
                )

                val artistCursor = Common.getInstance()?.contentResolver?.query(
                    MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI,
                    artistCols,
                    null, null,
                    MediaStore.Audio.Artists.DEFAULT_SORT_ORDER
                )
                common.getDBAccessHelper().writableDatabase.delete(DBHelper.ARTIST_TABLE, null, null)

                if (artistCursor !=null && artistCursor.moveToFirst()){

                 //   val path = File(Common.getInstance().get()?.cacheDir, "artistThumbnails").getAbsolutePath() + "/"
                  val path = "${File(Common.getInstance()?.cacheDir, "artistThumbnails").getAbsolutePath()} /"
                  do {
                    val artist=ContentValues()
                    artist.put(DBHelper.ARTIST_ID, artistCursor.getString(artistCursor.getColumnIndex(MediaStore.Audio.Artists._ID)))
                    artist.put(DBHelper.ARTIST_NAME, artistCursor.getString(artistCursor.getColumnIndex(MediaStore.Audio.Artists._ID)))
                    artist.put(DBHelper.NO_OF_TRACKS_BY_ARTIST, artistCursor.getString(artistCursor.getColumnIndex(MediaStore.Audio.Artists._ID)))
                    artist.put(DBHelper.NO_OF_ALBUMS_BY_ARTIST, artistCursor.getString(artistCursor.getColumnIndex(MediaStore.Audio.Artists._ID)))

                      val albums = getAlbumsSelection(
                          "ARTIST",
                          artistCursor.getString(artistCursor.getColumnIndex(MediaStore.Audio.Artists._ID))
                      )
                    if (albums!=null && albums.size>0){
                        val cacheFile =
                            File(path + artistCursor.getString(artistCursor.getColumnIndex(MediaStore.Audio.Artists._ID)))
                            if (cacheFile.exists())
                                artist.put(DBHelper.ARTIST_ALBUM_ART,"file:\\ ${cacheFile.absolutePath}")
                            else
                                artist.put(DBHelper.ARTIST_ALBUM_ART,"${MusicUtils.getAlbumArtUri(albums.get(0)._id!!)}")
                    }
                    common.getDBAccessHelper().writableDatabase.insert(DBHelper.ARTIST_TABLE,null, artist)


                  }while (artistCursor.moveToNext());


                }


            }catch (ex:Exception){
                ex.printStackTrace()
                Logger.log("ERROR CAUSE ${ex.cause}")
            }finally {
                common.getDBAccessHelper().writableDatabase.setTransactionSuccessful()
                common.getDBAccessHelper().writableDatabase.endTransaction()
                SharedPrefHelper.getInstance().put(SharedPrefHelper.Key.FIRST_LAUNCH,false)
            }

            cursor?.close()
            return true


        }catch (ex:Exception){
            ex.printStackTrace()
            return false
        }

    }

    fun getAlbumsList():MutableList<AlbumDto>{
           // val sort= SharedPrefHelper
            val columns= arrayOf(MediaStore.Audio.Albums._ID,
                                MediaStore.Audio.Albums.ALBUM,
                                MediaStore.Audio.Albums.ARTIST,
                                MediaStore.Audio.Albums.ALBUM_ART)
        val cursor=Common.getInstance()?.contentResolver?.query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
            columns,
            null,
            null,
            null)

        val albums= mutableListOf<AlbumDto>()

        if (cursor !=null && cursor.moveToNext()){
            do run {
                val album = AlbumDto(cursor.getLong(0), cursor.getString(1), cursor.getString(2), cursor.getString(3))
                albums.add(album)
            } while (cursor.moveToNext())
        }
        if(cursor!=null){
            cursor.close()
        }

        return albums

    }

    private fun shouldScan(): Boolean {
        if (SharedPrefHelper.getInstance().getBoolean(SharedPrefHelper.Key.FIRST_LAUNCH,true)!!)
            return true
        else{
            val sharedPreferences=PreferenceManager.getDefaultSharedPreferences(Common.getInstance())
            val scanAt =Integer.parseInt(sharedPreferences.getString("preference_key_scan_frequency", "5")!!)
            val launchCount = SharedPrefHelper.getInstance().getInt(SharedPrefHelper.Key.LAUNCH_COUNT)
            if (scanAt==5){
                return false
            }else if(scanAt==0){
                return true
            }else if (scanAt==launchCount){
                SharedPrefHelper.getInstance().put(SharedPrefHelper.Key.LAUNCH_COUNT,0)
                return true
            }else
                return false
        }
    }



    fun getSongsSelection(from: String, condition:String):MutableList<SongDto>{

        val columns = arrayOf(
            BaseColumns._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.YEAR,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.ARTIST_ID,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.TRACK
        )

        var selection: String? = null
        var uri: Uri? = null
        var selectionArgs: Array<String>? = null
        var sortBy: String? = null

        val songDtoList:MutableList<SongDto> = mutableListOf()

        if (from.equals("SONGS", ignoreCase = true)) run{
            selection= "is_music=1 AND title != ''";
            uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

        }

        val songCursor: Cursor? = Common.getInstance()?.contentResolver?.query(uri!!,columns,selection,null,null)

        val audioIndex = songCursor?.getColumnIndex(MediaStore.Audio.Media._ID)

        if (songCursor != null && songCursor.moveToFirst()) {
            do {
                val song = SongDto(
                    songCursor.getLong(audioIndex!!),
                    songCursor.getString(songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE)),
                    songCursor.getString(songCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)),
                    songCursor.getLong(songCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)),
                    songCursor.getString(songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)),
                    songCursor.getLong(songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID)),
                    songCursor.getString(songCursor.getColumnIndex(MediaStore.Audio.Media.DATA)),
                    songCursor.getInt(songCursor.getColumnIndex(MediaStore.Audio.Media.TRACK)),
                    songCursor.getLong(songCursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
                )
                songDtoList.add(song)
            } while (songCursor.moveToNext())
        }
        if (songCursor != null) {
            songCursor.close()
        }
        return songDtoList
    }


    fun getAlbumsSelection(from: String, condition:String):MutableList<AlbumDto>{

        var selection: String? = null
        var uri: Uri? = null
        var selectionArgs: Array<String>? = null
        var sortBy: String? = null

        if (from.equals("GENRES", ignoreCase = true)) run {
            uri = MediaStore.Audio.Albums.getContentUri("external")
            selection = ("album_info._id IN "
                    + "(SELECT (audio_meta.album_id) album_id FROM audio_meta, audio_genres_map "
                    + "WHERE audio_genres_map.audio_id=audio_meta._id AND audio_genres_map.genre_id=?)")
            selectionArgs = arrayOf(condition)
            sortBy = MediaStore.Audio.Albums.DEFAULT_SORT_ORDER
        }else if (from.equals("ALBUMS", ignoreCase = true)) run {
            selection = "is_music=1 AND title != '' AND " + MediaStore.Audio.Media.ALBUM_ID + "=?"
            uri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI
            selectionArgs = arrayOf(condition)
            sortBy = MediaStore.Audio.Albums.DEFAULT_SORT_ORDER
        }else if(from.equals("ARTISTS", ignoreCase = true)) run {
            uri=MediaStore.Audio.Artists.Albums.getContentUri("external", condition.toLong())
            sortBy=MediaStore.Audio.Albums.DEFAULT_SORT_ORDER
        }

        var albums:MutableList<AlbumDto> = mutableListOf()

        val columns = arrayOf(
            BaseColumns._ID,
            MediaStore.Audio.Albums.ALBUM,
            MediaStore.Audio.Albums.ARTIST,
            MediaStore.Audio.Albums.NUMBER_OF_SONGS
        )
        val cursor = Common.getInstance()?.getContentResolver()?.query(
            uri!!,
            columns,
            selection,
            selectionArgs,
            sortBy
        )

        if (cursor != null && cursor.moveToFirst()) {
            do {
                val album = AlbumDto(cursor.getLong(0), cursor.getString(1), cursor.getString(2), cursor.getString(3))
                albums.add(album)
            } while (cursor.moveToNext())

        }
        cursor?.close()
        return albums

    }

}
