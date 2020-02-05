package com.tavish.musicplayerbeat.DB

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.tavish.musicplayerbeat.Helpers.SharedPrefHelper
import com.tavish.musicplayerbeat.Models.ArtistDto
import com.tavish.musicplayerbeat.Models.GenreDto
import com.tavish.musicplayerbeat.Models.SongDto
import com.tavish.musicplayerbeat.Utils.Constants
import com.tavish.musicplayerbeat.Utils.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class DBHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {


    var mDB: SQLiteDatabase? = null
    var songs: MutableList<SongDto> = mutableListOf()

    private var mContext = context

    companion object {
        var mDBHelper: DBHelper? = null

        val DATABASE_NAME = "BeatDrop.db"
        val DATABASE_VERSION = 1
        val SONGS_TABLE = "SongsTable"
        val RECENTLY_PLAYED_TABLE = "RecentlyPlayedTable"
        val _ID = "_id"
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

        /*EQ Presets*/
        val EQ_PRESETS_TABLE = "EQPresets"
        val PRESET_NAME = "preset_name"
        val EQ_TABLE = "EQTable"
        val EQ_31_Hz = "eq_31_hz"
        val EQ_62_Hz = "eq_62_hz"
        val EQ_125_Hz = "eq_125_hz"
        val EQ_250_Hz = "eq_250_hz"
        val EQ_500_Hz = "eq_500_hz"
        val EQ_1_KHz = "eq_1_Khz"
        val EQ_2_KHz = "eq_2_Khz"
        val EQ_4_KHz = "eq_4_Khz"
        val EQ_8_KHz = "eq_8_Khz"
        val EQ_16_KHz = "eq_16_Khz"
        val EQ_Virtualizer = "eq_virtualizer"
        val EQ_BassBoost = "eq_bass_boost"
        val EQ_PreAmp = "eq_pre_amp"
        val EQ_Reverb = "eq_reverb"


        @Synchronized
        fun getDBHelper(context: Context): DBHelper {
            if (mDBHelper == null)
                mDBHelper = DBHelper(context)
            return mDBHelper!!
        }
    }

    @Synchronized
    fun getDB(): SQLiteDatabase? {
        if (mDB == null)
            mDB = writableDatabase

        return mDB
    }

    fun getAllArtist(): MutableList<ArtistDto> {
        val artists: MutableList<ArtistDto> = mutableListOf()
        val query = "SELECT * FROM " + ARTIST_TABLE
        val cursor = getDB()?.rawQuery(query, null)
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
        val cursor = getDB()?.rawQuery(query, null)
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

    fun searchArtists(name: String): MutableList<ArtistDto> {
        var artistDto: MutableList<ArtistDto> = mutableListOf()
        val query =
            "SELECT * FROM " + ARTIST_TABLE + " WHERE " + ARTIST_NAME + " LIKE '%" + name + "%'"+" ORDER BY " + SharedPrefHelper.getInstance().getString(SharedPrefHelper.Key.ARTIST_SORT_ORDER, ARTIST_NAME)+SharedPrefHelper.getInstance().getString(SharedPrefHelper.Key.ARTIST_SORT_TYPE, Constants.ASCENDING)

        val cursor: Cursor? = getDB()?.rawQuery(query, null)
        if (cursor != null && cursor.moveToFirst()) {
            do {
                val artist = ArtistDto(
                    cursor.getLong(cursor.getColumnIndex(DBHelper.ARTIST_ID)),
                    cursor.getString(cursor.getColumnIndex(DBHelper.ARTIST_NAME)),
                    cursor.getString(cursor.getColumnIndex(DBHelper.ARTIST_ALBUM_ART)),
                    cursor.getInt(cursor.getColumnIndex(DBHelper.NO_OF_TRACKS_BY_ARTIST)),
                    cursor.getInt(cursor.getColumnIndex(DBHelper.NO_OF_ALBUMS_BY_ARTIST))
                )
                artistDto.add(artist)
            } while (cursor.moveToNext())
            cursor.close()
        }
        return artistDto

    }

    fun searchGenre(name: String): MutableList<GenreDto> {
        val genres: MutableList<GenreDto> = mutableListOf()
        val query =
            "SELECT * FROM $GENRES_TABLE WHERE $GENRE_NAME LIKE '%$name%'"
        val cursor = getDB()?.rawQuery(query,null)
        if (cursor != null && cursor.moveToFirst()){
            do {
                val genre = GenreDto(cursor.getLong(cursor.getColumnIndex(GENRE_ID)),
                    cursor.getString(cursor.getColumnIndex(GENRE_NAME)),
                    cursor.getString(cursor.getColumnIndex(GENRE_ALBUM_ART)),
                    cursor.getInt(cursor.getColumnIndex(NO_OF_ALBUMS_IN_GENRE)))
                genres.add(genre)
            }while (cursor.moveToNext())
            cursor.close()
        }
        return genres
    }


    fun insertSongCount(songDto: SongDto) {
        val values = ContentValues();
        val cursor =
            getDB()?.rawQuery(
                "SELECT * FROM " + TOP_TRACKS_TABLE + " WHERE " + SONG_ID + "= " + songDto._id,
                null
            )

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
                getDB()?.update(TOP_TRACKS_TABLE, values, SONG_ID + "= " + songDto._id, null)
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
                getDB()?.insertOrThrow(TOP_TRACKS_TABLE, null, values)
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
        getDB()?.insertOrThrow(RECENTLY_PLAYED_TABLE, null, values)

    }

    suspend fun saveQueue(songs: MutableList<SongDto>) = withContext(Dispatchers.IO) {
        getDB()?.apply {
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
                    getDB()?.insert(SONGS_TABLE, null, contentValues)

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

        val songsColTypes =
            arrayOf("TEXT", "TEXT", "TEXT", "TEXT", "TEXT", "TEXT", "TEXT", "TEXT", "TEXT")

        val createSongsTable = createTableBuilder(
            SONGS_TABLE,
            songsTableCols,
            songsColTypes
        )


        val artistTableCols =
            arrayOf(
                ARTIST_ID,
                ARTIST_NAME,
                NO_OF_ALBUMS_BY_ARTIST,
                NO_OF_TRACKS_BY_ARTIST,
                ARTIST_ALBUM_ART
            )

        val artistColTypes = arrayOf("TEXT", "TEXT", "TEXT", "TEXT", "TEXT")

        val createArtistPlayedTable = createTableBuilder(
            ARTIST_TABLE,
            artistTableCols,
            artistColTypes
        )

        val genresTableCols = arrayOf(GENRE_ID, GENRE_NAME, NO_OF_ALBUMS_IN_GENRE, GENRE_ALBUM_ART)
        val genresColTypes = arrayOf("TEXT", "TEXT", "TEXT", "TEXT")

        val createGenresTable = createTableBuilder(
            GENRES_TABLE,
            genresTableCols,
            genresColTypes
        )

        // size :15
        val eqTableCols = arrayOf(
            PRESET_NAME,
            EQ_31_Hz,
            EQ_62_Hz,
            EQ_125_Hz,
            EQ_250_Hz,
            EQ_500_Hz,
            EQ_1_KHz,
            EQ_2_KHz,
            EQ_4_KHz,
            EQ_8_KHz,
            EQ_16_KHz,
            EQ_Virtualizer,
            EQ_BassBoost,
            EQ_PreAmp,
            EQ_Reverb
        )

        val eqColTypes = arrayOf(
            "TEXT",
            "TEXT",
            "TEXT",
            "TEXT",
            "TEXT",
            "TEXT",
            "TEXT",
            "TEXT",
            "TEXT",
            "TEXT",
            "TEXT",
            "TEXT",
            "TEXT",
            "TEXT",
            "TEXT"
        )


        val createEQTable = createTableBuilder(EQ_TABLE, eqTableCols, eqColTypes)

        /* val eqPresetsTable = arrayOf(PRESET_NAME,EQ_31_Hz, EQ_62_Hz, EQ_125_Hz, EQ_250_Hz, EQ_500_Hz,
             EQ_1_KHz, EQ_2_KHz, EQ_4_KHz, EQ_8_KHz, EQ_16_KHz, EQ_Virtualizer, EQ_BassBoost, EQ_PreAmp, EQ_Reverb)

         val eqPresetsTableCols = arrayOf()
 */

        db?.execSQL(createSongsTable)
        db?.execSQL(createArtistPlayedTable)
        db?.execSQL(createGenresTable)
        db?.execSQL(createEQTable)



        Logger.log("EQ TABLE CREATED")
    }


    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        onCreate(db)
    }

    /*@Throws(Throwable::class)
   protected fun finalize(){
        try{
            getDB()?.close()
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

        if (songs.size <= 0) {
            songs = allsongs()
        }
        return songs
    }

    fun allsongs(): MutableList<SongDto> {
        val cursor = getDB()?.rawQuery("SELECT * FROM $SONGS_TABLE", null)
        if (cursor?.moveToFirst()!!) {
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

    private fun createTableBuilder(
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

    fun getEQPresets(): Cursor {
        val query = "SELECT * FROM " + EQ_TABLE + " WHERE NOT " + _ID + " = 1"
        return getDB()?.rawQuery(query, null)!!
    }

    fun getEQValues(): Array<Int?> {
        val query = "SELECT * FROM " + EQ_TABLE + " WHERE " + _ID + " = 1"
        val cursor = getDB()?.rawQuery(query, null)
        val eqValues = arrayOfNulls<Int>(15)
        if (cursor != null && cursor.count != 0) {
            cursor.moveToFirst()
            if (cursor.getString(cursor.getColumnIndex(PRESET_NAME)).equals("Reserved")) {
                eqValues[0] = cursor.getInt(cursor.getColumnIndex(EQ_31_Hz))
                eqValues[1] = cursor.getInt(cursor.getColumnIndex(EQ_62_Hz))
                eqValues[2] = cursor.getInt(cursor.getColumnIndex(EQ_125_Hz))
                eqValues[3] = cursor.getInt(cursor.getColumnIndex(EQ_250_Hz))
                eqValues[4] = cursor.getInt(cursor.getColumnIndex(EQ_500_Hz))
                eqValues[5] = cursor.getInt(cursor.getColumnIndex(EQ_1_KHz))
                eqValues[6] = cursor.getInt(cursor.getColumnIndex(EQ_2_KHz))
                eqValues[7] = cursor.getInt(cursor.getColumnIndex(EQ_4_KHz))
                eqValues[8] = cursor.getInt(cursor.getColumnIndex(EQ_8_KHz))
                eqValues[9] = cursor.getInt(cursor.getColumnIndex(EQ_16_KHz))
                eqValues[10] = cursor.getInt(cursor.getColumnIndex(EQ_Virtualizer))
                eqValues[11] = cursor.getInt(cursor.getColumnIndex(EQ_BassBoost))
                eqValues[12] = cursor.getInt(cursor.getColumnIndex(EQ_PreAmp))
                eqValues[13] = cursor.getInt(cursor.getColumnIndex(EQ_Reverb))
                eqValues[14] = 1
            } else {
                eqValues[0] = 16
                eqValues[1] = 16
                eqValues[2] = 16
                eqValues[3] = 16
                eqValues[4] = 16
                eqValues[5] = 16
                eqValues[6] = 16
                eqValues[7] = 16
                eqValues[8] = 16
                eqValues[9] = 16
                eqValues[10] = 0
                eqValues[11] = 0
                eqValues[12] = 0
                eqValues[13] = 0
                eqValues[14] = 0
            }

        } else {
            eqValues[0] = 16
            eqValues[1] = 16
            eqValues[2] = 16
            eqValues[3] = 16
            eqValues[4] = 16
            eqValues[5] = 16
            eqValues[6] = 16
            eqValues[7] = 16
            eqValues[8] = 16
            eqValues[9] = 16
            eqValues[10] = 0
            eqValues[11] = 0
            eqValues[12] = 0
            eqValues[13] = 0
            eqValues[14] = 0
        }
        return eqValues
    }

    fun updateEQValues(
        command: String?, preset_name: String?, thirtyOneHz: Int?, sixtyTwoHz: Int?,
        oneHunderedTwentyFiveHzLevel: Int?, twoHundredFiftyHz: Int?,
        fiveHundredHz: Int?, oneKHz: Int?,
        twoKHz: Int?, fourKHz: Int?,
        eightKHz: Int?, sixteenKHz: Int?,
        virtualizer: Short?, bassboost: Short?,
        reverb: Short?, preamp: Float?
    ) {

        val values = ContentValues()
        values.put(PRESET_NAME, preset_name)
        values.put(EQ_31_Hz, thirtyOneHz)
        values.put(EQ_62_Hz, sixtyTwoHz)
        values.put(EQ_125_Hz, oneHunderedTwentyFiveHzLevel)
        values.put(EQ_250_Hz, twoHundredFiftyHz)
        values.put(EQ_500_Hz, fiveHundredHz)
        values.put(EQ_1_KHz, oneKHz)
        values.put(EQ_2_KHz, twoKHz)
        values.put(EQ_4_KHz, fourKHz)
        values.put(EQ_8_KHz, eightKHz)
        values.put(EQ_16_KHz, sixteenKHz)
        values.put(EQ_Virtualizer, virtualizer)
        values.put(EQ_BassBoost, bassboost)
        values.put(EQ_Reverb, reverb)
        values.put(EQ_PreAmp, preamp)

        if (command.equals("UPDATE"))
            getDB()?.update(EQ_TABLE, values, "$_ID = ?", arrayOf("1"))
        else if (command.equals("ADD"))
            getDB()?.insert(EQ_TABLE, null, values)
    }


}