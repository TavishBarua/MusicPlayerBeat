package com.tavish.musicplayerbeat.DB

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteClosable
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.tavish.musicplayerbeat.Models.ArtistDto
import com.tavish.musicplayerbeat.Models.GenreDto
import com.tavish.musicplayerbeat.Models.SongDto
import com.tavish.musicplayerbeat.Utils.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import java.io.Closeable
import java.text.SimpleDateFormat
import java.util.*

class DBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {


    var mDB: SQLiteDatabase? = null

    private var mContext = context

    companion object {
        var mDBHelper: DBHelper? = null

        val DATABASE_NAME = "BeatDrop.db"
        val DATABASE_VERSION = 1
        val SONGS_TABLE = "SongsTable"
        val RECENTLY_PLAYED_TABLE = "RecentlyPlayedTable"
        val _ID = "id"
        val DATE = "date"

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

        /**
         * Top tracks table.
         */
        val TOP_TRACKS_TABLE = "TopTracksTable"
        val SONG_COUNT = "songCount"

        @Synchronized
        fun getDBHelper(context: Context): DBHelper {
            if (mDBHelper == null)
                mDBHelper = DBHelper(context)
            return mDBHelper!!
        }
    }

    @Synchronized
    fun getDB(): SQLiteDatabase {
        if (mDB == null)
            mDB = writableDatabase

        return mDB!!
    }

    fun getAllArtist(): MutableList<ArtistDto> {
        val artists: MutableList<ArtistDto> = mutableListOf()
        val query = "SELECT * FROM " + ARTIST_TABLE
        val cursor = getDB().rawQuery(query, null)
        if (cursor != null && cursor.moveToFirst()) {
            do {
                val artist = ArtistDto(
                    cursor.getLong(cursor.getColumnIndex(ARTIST_ID)),
                    cursor.getString(cursor.getColumnIndex(ARTIST_NAME)),
                    cursor.getString(cursor.getColumnIndex(ARTIST_ALBUM_ART)),
                    cursor.getInt(cursor.getColumnIndex(NO_OF_TRACKS_BY_ARTIST)),
                    cursor.getInt(cursor.getColumnIndex(NO_OF_ALBUMS_BY_ARTIST))
                )
                artists.add(artist)
            } while (cursor.moveToNext())

            cursor.close()
        }
        return artists
    }

    fun getAllGenre(): MutableList<GenreDto> {
        val genres: MutableList<GenreDto> = mutableListOf()
        val query = "SELECT * FROM " + GENRES_TABLE
        val cursor = getDB().rawQuery(query, null)
        if (cursor != null && cursor.moveToFirst()) {
            do {
                val genre = GenreDto(
                    cursor.getLong(cursor.getColumnIndex(GENRE_ID)),
                    cursor.getString(cursor.getColumnIndex(GENRE_NAME)),
                    cursor.getString(cursor.getColumnIndex(GENRE_ALBUM_ART)),
                    cursor.getInt(cursor.getColumnIndex(NO_OF_ALBUMS_IN_GENRE))
                )
                genres.add(genre)
            } while (cursor.moveToNext())
            cursor.close()
        }
        return genres
    }

    fun insertSongCount(songDto: SongDto) {
        val values = ContentValues();
        val cursor =
            getDB().rawQuery("SELECT * FROM " + TOP_TRACKS_TABLE + " WHERE " + SONG_ID + "= " + songDto._id, null)

        if (cursor != null && cursor.moveToFirst()) {
            if (cursor.getString(cursor.getColumnIndex(SONG_COUNT)) != null) {
                val songCount = cursor.getInt(1) + 1

                values.put(SONG_ID, songDto._id)
                values.put(SONG_TITLE, songDto._title)
                values.put(SONG_ARTIST, songDto._artist)
                values.put(SONG_DURATION, songDto._duration)
                values.put(SONG_PATH, songDto._path)
                values.put(SONG_ALBUM, songDto._album)
                values.put(ALBUM_ID, songDto._albumId)
                values.put(TRACK_NO, songDto._trackNumber)
                values.put(ARTIST_ID, songDto._artistId)
                values.put(SONG_COUNT, songCount)
                getDB().update(TOP_TRACKS_TABLE, values, SONG_ID + "= " + songDto._id, null)
            }

        } else {
            values.put(SONG_ID, songDto._id)
            values.put(SONG_TITLE, songDto._title)
            values.put(SONG_ARTIST, songDto._artist)
            values.put(SONG_DURATION, songDto._duration)
            values.put(SONG_PATH, songDto._path)
            values.put(SONG_ALBUM, songDto._album)
            values.put(ALBUM_ID, songDto._albumId)
            values.put(TRACK_NO, songDto._trackNumber)
            values.put(ARTIST_ID, songDto._artistId)
            values.put(SONG_COUNT, 0)
            try {
                getDB().insertOrThrow(TOP_TRACKS_TABLE, null, values)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }

    }

    fun addToRecentlyPlayed(songDto: SongDto) {
        val values = ContentValues()
        values.put(SONG_ID, songDto._id)
        values.put(SONG_TITLE, songDto._title)
        values.put(SONG_ARTIST, songDto._artist)
        values.put(SONG_DURATION, songDto._duration)
        values.put(SONG_PATH, songDto._path)
        values.put(SONG_ALBUM, songDto._album)
        values.put(ALBUM_ID, songDto._albumId)
        values.put(TRACK_NO, songDto._trackNumber)
        values.put(ARTIST_ID, songDto._artistId)

        values.put(DATE, SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(Date()))
        getDB().insertOrThrow(RECENTLY_PLAYED_TABLE, null, values)

    }

    suspend fun saveQueue(songs: MutableList<SongDto>) = withContext(Dispatchers.IO) {


        getDB().apply {
            beginTransaction()
            delete(SONGS_TABLE, null, null)
            songs.forEachIndexed { _, songDto ->
                val contentValues = ContentValues()
                contentValues.apply {
                    put(SONG_ID, songDto._id)
                    put(SONG_TITLE, songDto._title);
                    put(SONG_ARTIST, songDto._artist);
                    put(SONG_DURATION, songDto._duration);
                    put(SONG_PATH, songDto._path);
                    put(SONG_ALBUM, songDto._album);
                    put(ALBUM_ID, songDto._albumId);
                    put(TRACK_NO, songDto._trackNumber);
                    put(ARTIST_ID, songDto._artistId);
                    getDB().insert(SONGS_TABLE, null, contentValues)

                }
            }
            setTransactionSuccessful()
            endTransaction()
        }

    }


    override fun onCreate(db: SQLiteDatabase?) {

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

        val createSongsTable = buildCreateStatement(
            SONGS_TABLE,
            songsTableCols,
            songsColTypes
        )


        val artistTableCols =
            arrayOf(ARTIST_ID, ARTIST_NAME, NO_OF_ALBUMS_BY_ARTIST, NO_OF_TRACKS_BY_ARTIST, ARTIST_ALBUM_ART)

        val artistColTypes = arrayOf("TEXT", "TEXT", "TEXT", "TEXT", "TEXT")

        val createArtistPlayedTable = buildCreateStatement(
            ARTIST_TABLE,
            artistTableCols,
            artistColTypes
        )

        val genresTableCols = arrayOf(GENRE_ID, GENRE_NAME, NO_OF_ALBUMS_IN_GENRE, GENRE_ALBUM_ART)
        val genresColTypes = arrayOf("TEXT", "TEXT", "TEXT", "TEXT")

        val createGenresTable = buildCreateStatement(
            GENRES_TABLE,
            genresTableCols,
            genresColTypes
        )


        db?.execSQL(createSongsTable)
        db?.execSQL(createArtistPlayedTable)
        db?.execSQL(createGenresTable)



        Logger.log("EQ TABLE CREATED")
    }


    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        onCreate(db)
    }

    /*@Throws(Throwable::class)
   protected fun finalize(){
        try{
            getDB().close()
        }catch (ex:Exception){
            ex.printStackTrace()
        }
   }*/

    @Synchronized
    override fun close() {
        if (mDB != null) {
            mDB!!.close()
        }
        super.close()
    }

    fun getQueue(): MutableList<SongDto> {
        val songs: MutableList<SongDto> = mutableListOf()
        val cursor = getDB().rawQuery("SELECT * FROM $SONGS_TABLE", null)
        if (cursor.moveToFirst()) {
            do {
                val song = SongDto(
                    cursor.getLong(cursor.getColumnIndex(SONG_ID)),
                    cursor.getString(cursor.getColumnIndex(SONG_TITLE)),
                    cursor.getString(cursor.getColumnIndex(SONG_ALBUM)),
                    cursor.getLong(cursor.getColumnIndex(ALBUM_ID)),
                    cursor.getString(cursor.getColumnIndex(SONG_ARTIST)),
                    cursor.getLong(cursor.getColumnIndex(ARTIST_ID)),
                    cursor.getString(cursor.getColumnIndex(SONG_PATH)),
                    cursor.getInt(cursor.getColumnIndex(TRACK_NO)),
                    cursor.getLong(cursor.getColumnIndex(SONG_DURATION))
                )

                songs.add(song)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return songs
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