package com.tavish.musicplayerbeat.DB

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.tavish.musicplayerbeat.Models.ArtistDto
import com.tavish.musicplayerbeat.Utils.Logger

class DBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {


     var mDB:SQLiteDatabase?=null

    private var mContext: Context
        init {
        mContext=context
        }

    companion object{
        var mDBHelper: DBHelper?=null

        val DATABASE_NAME = "BeatDrop.db"
        val DATABASE_VERSION = 1
        val SONGS_TABLE = "SongsTable"
        val _ID="id"

        /**
         * Genre table and columns.
         */
        val GENRES_TABLE = "GenresTable"
        val GENRE_ID = "genreId"
        val GENRE_NAME = "genreName"
        val NO_OF_ALBUMS_IN_GENRE = "noOfAlbumsInGenre"
        val GENRE_ALBUM_ART = "genreAlbumArt"



        /**
         * Artist table and columns.
         */


        val ARTIST_TABLE = "ArtistTable"
        val ARTIST_NAME = "artistName"
        val NO_OF_ALBUMS_BY_ARTIST = "noOfAlbumsByArtist"
        val NO_OF_TRACKS_BY_ARTIST = "noOfTracksByArtist"
        val ARTIST_ALBUM_ART = "artistAlbumArt"

        /**
         * Favorites Tables and song columns.
         */
        val FAVORITES_TABLE = "FavoritesTable"
        val SONG_ID = "songId"
        val SONG_TITLE = "songTitle"
        val SONG_ARTIST = "songArtist"
        val SONG_DURATION = "songDuration"
        val SONG_PATH = "songPath"
        val SONG_ALBUM = "songAlbum"
        val ALBUM_ID = "albumId"
        val TRACK_NO = "trackNo"
        val ARTIST_ID = "artistId"

        val songsTableCols = arrayOf(
            SONG_ID,
            SONG_TITLE,
            SONG_ARTIST,
            SONG_ALBUM,
            SONG_DURATION,
            ALBUM_ID,
            ARTIST_ID,
            TRACK_NO,
            SONG_PATH
        )

        val songsColTypes = arrayOf("TEXT", "TEXT", "TEXT", "TEXT", "TEXT", "TEXT", "TEXT", "TEXT", "TEXT")


        @Synchronized
        fun getDBHelper(context: Context):DBHelper{
            if (mDBHelper== null)
                mDBHelper=DBHelper(context)
            return mDBHelper!!
        }


    }

    @Synchronized
    fun getDB():SQLiteDatabase{
        if (mDB==null)
            mDB= writableDatabase

        return mDB!!
    }

    fun getAllArtist():MutableList<ArtistDto>{
        val artists:MutableList<ArtistDto> =  mutableListOf()
        val query="SELECT * FROM "+ ARTIST_TABLE+ " ORDER BY "
        val cursor= getDB().rawQuery(query, null)
        if (cursor !=null && cursor.moveToFirst()){
            do {
                val artist:ArtistDto= ArtistDto(
                    cursor.getLong(cursor.getColumnIndex(ARTIST_ID)),
                    cursor.getString(cursor.getColumnIndex(ARTIST_NAME)),
                    cursor.getString(cursor.getColumnIndex(ARTIST_ALBUM_ART)),
                    cursor.getInt(cursor.getColumnIndex(NO_OF_TRACKS_BY_ARTIST)),
                    cursor.getInt(cursor.getColumnIndex(NO_OF_ALBUMS_BY_ARTIST))
                )
                artists.add(artist)
            }while (cursor.moveToNext())

            cursor.close()
        }
        return artists
    }





    override fun onCreate(db: SQLiteDatabase?) {
        val createSongsTable = buildCreateStatement(
            SONGS_TABLE,
            songsTableCols,
            songsColTypes
        )

        db?.execSQL(createSongsTable)

        Logger.log("EQ TABLE CREATED")
    }



    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    }

    private fun buildCreateStatement(
        tableName: String,
        columnNames: Array<String>,
        columnTypes: Array<String>
    ): String {
        var createStatement = ""
        if (columnNames.size == columnTypes.size) {
            createStatement += ("CREATE TABLE IF NOT EXISTS " + tableName + "("
                    +
                    _ID + " INTEGER PRIMARY KEY, ")

            for (i in columnNames.indices) {

                if (i == columnNames.size - 1) {
                    createStatement += (columnNames[i]
                            + " "
                            + columnTypes[i]
                            + ")")
                } else {
                    createStatement += (columnNames[i]
                            + " "
                            + columnTypes[i]
                            + ", ")
                }
            }
        }
        return createStatement
    }





}