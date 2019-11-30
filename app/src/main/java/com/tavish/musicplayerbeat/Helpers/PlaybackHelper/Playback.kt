package com.tavish.musicplayerbeat.Helpers.PlaybackHelper

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import com.tavish.musicplayerbeat.Common
import com.tavish.musicplayerbeat.Helpers.MediaHelpers.MusicService
import com.tavish.musicplayerbeat.Helpers.SharedPrefHelper
import com.tavish.musicplayerbeat.Models.SongDto
import com.tavish.musicplayerbeat.Utils.Constants
import java.util.ArrayList


class Playback(context: Context): MusicService.PrepareServiceListener{

/*
    companion object {

        val PLAY_PAUSE_SONG = 230
        val PLAY_SONGS = 231

    }*/

    private var SONG_CASE = 0
    private var WHICH_CASE = 0

    private var mPos: Int = 0


    private val mContext: Context = context
    private var mApp: Common = mContext.applicationContext as Common
    private var mSongs: MutableList<SongDto>? = null
    private var mSong: SongDto? = null


    fun playSongs(songs: MutableList<SongDto>, pos: Int) {
        mSongs = songs
        mPos = pos
        SharedPrefHelper.getInstance().put(SharedPrefHelper.Key.SONG_CURRENT_SEEK_DURATION, 0)
        WHICH_CASE = Constants.PLAY_SONGS
        if (!mApp.isServiceRunning())
            startService()
        else {
            mApp.mService?.setSongList(mSongs!!)
            mApp.mService?.setSelectedSong(mPos)

        }
    }

    fun playPauseSongs() {
        WHICH_CASE = Constants.PLAY_PAUSE_SONG
        if (!mApp.isServiceRunning()) {
            startService()
        } else {
            mApp.mService?.playPauseSong()
        }
    }

    fun playSongs() {
        WHICH_CASE = Constants.PLAY_SONG
        if (!mApp.isServiceRunning()) {
            startService()
        } else {
            mApp.mService?.startPlaying()
        }
    }

    fun pauseSong() {
        if (!mApp.isServiceRunning()) {
            mApp.mService?.stopPlaying()
        }
    }

    fun nextSong() {
        WHICH_CASE = Constants.NEXT_SONG
        if (!mApp.isServiceRunning()) {
            startService()
        } else {
            mApp.mService?.nextSong()
        }
    }

    fun previousSong() {
        WHICH_CASE = Constants.PREVIOUS_SONG
        if (!mApp.isServiceRunning()) {
            startService()
        } else {
            mApp.mService?.previousSong()
        }
    }

    fun playPauseFromBottomBar() {
        WHICH_CASE = Constants.PLAY_PAUSE_SONG_FROM_BOTTOM_BAR
        if (!mApp.isServiceRunning()) {
            startService()
        } else {
            mApp.mService?.playPauseSong()
        }
    }


    private fun startService() {
        val intent = Intent(mContext, MusicService::class.java)
        mContext.startService(intent)
    }

    override fun onServiceRunning(musicService: MusicService) {
        mApp = mContext.applicationContext as Common
        mApp.mService?.prepareServiceListener = this

        when(WHICH_CASE){

        Constants.PAUSE_SONG,
        Constants.PLAY_SONG ->{
            mApp.mService?.playPauseSong()
        }
        Constants.PLAY_SONGS->{
           /* mApp.mService?.apply {
                setSongList(mSongs)
                setSelectedSong(mPos)
            }*/

            mApp.mService?.setSongList(mSongs!!)
            mApp.mService?.setSelectedSong(mPos)

        }
        Constants.PLAY_PAUSE_SONG_FROM_BOTTOM_BAR->{
            mApp.mService?.setSelectedSong(SharedPrefHelper.getInstance().getInt(SharedPrefHelper.Key.CURRENT_SONG_POSITION,0))
        }



        }

    }




}