package com.tavish.musicplayerbeat.Helpers

import android.content.Context
import android.content.SharedPreferences
import com.tavish.musicplayerbeat.Common

public class SharedPrefHelper(context: Context) {


    companion object{

        private val MUSIC_PLAYER_PREFERENCE = "MUSIC_PLAYER_PREFERENCES"
      //  private var mSharedPrefs: SharedPrefHelper? = null
        private lateinit var mPref:SharedPreferences
        private var sSharedPrefs: SharedPrefHelper?=null
        private var mEditor:SharedPreferences.Editor?=null
        private var mBulkUpdate = false

        fun getInstance():SharedPrefHelper{
            if(sSharedPrefs==null)
                sSharedPrefs = SharedPrefHelper(Common.getInstance()?.applicationContext!!)
        return sSharedPrefs!!
        }






    }
    init {
        mPref=context.getSharedPreferences(MUSIC_PLAYER_PREFERENCE, Context.MODE_PRIVATE)

    }

    fun getString(key: Key, defaultValue: String): String? {
        return mPref.getString(key.name, defaultValue)
    }

    fun getString(key: Key): String? {
        return mPref.getString(key.name, null)
    }


    fun getPref(): SharedPreferences {
        return mPref
    }

    fun getBoolean(key: Key, defaultValue: Boolean): Boolean {
        return mPref.getBoolean(key.name, defaultValue)
    }

    fun put(key: Key, `val`: Int) {
        doEdit()
        mEditor?.putInt(key.name, `val`)
        doCommit()
    }

    fun put(key: Key, `val`: Boolean) {
        doEdit()
        mEditor?.putBoolean(key.name, `val`)
        doCommit()
    }

    fun put(key: Key, `val`: String) {
        doEdit()
        mEditor?.putString(key.name, `val`)
        doCommit()
    }

    fun getInt(key: Key): Int {
        return mPref.getInt(key.name, 0)
    }

    fun getInt(key: Key, defaultValue: Int): Int {
        return mPref.getInt(key.name, defaultValue)
    }



    private fun doEdit() {
        if (!mBulkUpdate && mEditor == null) {
            mEditor = mPref.edit()
        }
    }

    private fun doCommit() {
        if (!mBulkUpdate && mEditor != null) {
            mEditor?.commit()
            mEditor = null
        }
    }

    enum class Key {

        /**
         * Sorting orders
         */

        ARTIST_SORT_ORDER,
        ALBUM_SORT_ORDER,
        SONG_SORT_ORDER,
        TITLES, PREVIOUS_VERSION_CODE,

        SONG_SORT_TYPE,
        ARTIST_SORT_TYPE,
        ALBUM_SORT_TYPE,

        SONG_QUEUE,
        REPEAT_MODE,
        SHUFFLE_MODE,

        SONG_POSITION,
        SONG_SEEK_POSITION,
        CURRENT_SONG_POSITION,

        SONG_CURRENT_SEEK_DURATION,
        PREVIOUS_ROOT_DIR,
        LAST_PRESET_NAME,
        SONG_TOTAL_SEEK_DURATION,
        RECENTLY_ADDED_WEEKS,
        FIRST_LAUNCH,
        GENRE_SORT_ORDER,
        GENRE_SORT_TYPE,
        COLORS,
        TABS, LAUNCH_COUNT, IS_EQUALIZER_ACTIVE, RECENT_SEARCH
    }

}