package com.tavish.musicplayerbeat.Helpers.AsyncTasks

import android.os.AsyncTask
import android.widget.Toast
import com.tavish.musicplayerbeat.Common
import com.tavish.musicplayerbeat.Helpers.SharedPrefHelper
import com.tavish.musicplayerbeat.Models.SongDto
import com.tavish.musicplayerbeat.R
import java.util.*


class AsyncAddSongs constructor(var name:String?,  addToQueue: Boolean,song:SongDto?):AsyncTask<Void,Void, Boolean>(){


    private var mAddToQueue = addToQueue
    private var mSongs:MutableList<SongDto>? = null
    private var mSong: SongDto? = song
    private var mApp: Common? = null
    private var mName: String? = name
        constructor(name:String?,addToQueue:Boolean,songs:MutableList<SongDto>):this(
            name,addToQueue,null
            ){
                mName = name
                mAddToQueue = addToQueue
                mSongs=songs
        }



    override fun doInBackground(vararg params: Void?): Boolean {
        mApp = Common.getInstance()?.applicationContext as Common
        if(mApp?.isServiceRunning()!!){
            if(mAddToQueue){
                if (mSong!=null){
                    mApp?.mService?.getSongList()?.add(mSong!!)
                }else{
                    mApp?.mService?.getSongList()?.addAll(mSongs!!)
                }
            }else{
                if (mSong!=null){
                    mApp?.mService?.getSongList()?.add(mApp?.mService?.mSongPos!! + 1, mSong!!)
                }else{
                    mApp?.mService?.getSongList()?.addAll(mApp?.mService?.mSongPos!! + 1, mSongs!!)
                }
            }
        }else{

            val songs = mApp?.getDBAccessHelper()?.getQueue()
            val pos = SharedPrefHelper.getInstance().getInt(SharedPrefHelper.Key.CURRENT_SONG_POSITION, 0)
            if (songs?.size == 0) {
                return false
            }
            if (mAddToQueue) {
                if (mSong != null) {
                    songs?.add(mSong!!)
                } else {
                    songs?.addAll(mSongs!!)
                }
            } else {
                if (mSong != null) {
                    songs?.add(pos + 1, mSong!!)
                } else {
                    songs?.addAll(pos + 1, mSongs!!)
                }
            }
           // mApp?.getDBAccessHelper()?.saveQueue(songs!!)
        }
        return true;
    }

    override fun onPostExecute(result: Boolean?) {
        super.onPostExecute(result)
        if (result!!) {
            if (mAddToQueue) {
                var message = Common.getInstance()?.getString(R.string.added_to_queue)
                message = "$mName $message"
                Toast.makeText(Common.getInstance(), message, Toast.LENGTH_SHORT).show()
            } else {
                var message = Common.getInstance()?.getString(R.string.will_be_played_next)
                message = "$mName $message"
                Toast.makeText(Common.getInstance(), message, Toast.LENGTH_SHORT).show()

            }
        } else {
            Toast.makeText(Common.getInstance(), R.string.queue_is_empty, Toast.LENGTH_SHORT).show()
        }
    }
}