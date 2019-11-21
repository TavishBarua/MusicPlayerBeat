package com.tavish.musicplayerbeat.Fragments

import android.content.*
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.tavish.musicplayerbeat.Adapters.CurrentPlayingBottomBarAdapter
import com.tavish.musicplayerbeat.Common
import com.tavish.musicplayerbeat.Helpers.SharedPrefHelper
import com.tavish.musicplayerbeat.Models.SongDto

import com.tavish.musicplayerbeat.R
import com.tavish.musicplayerbeat.Utils.Constants
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class CurrentPlayingBottomBarFragment : Fragment(), View.OnClickListener {

    val job = SupervisorJob()
    /*override val coroutineContext: CoroutineContext
        get() */
    private var mView: View? = null


    private var mFloatingActionButton: FloatingActionButton? = null
    private var mDurationTextView: TextView? = null
    private var mApp: Common? = null
    private var mRecyclerView: RecyclerView? = null
    private var mCurrentBottomBarAdapter: CurrentPlayingBottomBarAdapter? = null
    private var songs: MutableList<SongDto>? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_current_playing_bottom_bar, container, false)
        mApp = activity?.applicationContext as Common
        mRecyclerView = mView?.findViewById(R.id.rr_bottom_songs)
        mView?.visibility = View.GONE
        mRecyclerView?.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        mCurrentBottomBarAdapter = CurrentPlayingBottomBarAdapter(this)
        mRecyclerView?.adapter = mCurrentBottomBarAdapter
        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(mRecyclerView)

        mDurationTextView = mView?.findViewById(R.id.txt_duration)
        mFloatingActionButton = mView?.findViewById(R.id.fab)
        mFloatingActionButton?.setOnClickListener(this)
       // updateUI()

        mRecyclerView?.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    val linearLayoutManager = mRecyclerView?.layoutManager as LinearLayoutManager
                    val newPos = linearLayoutManager.findFirstCompletelyVisibleItemPosition()
                    var oldPos: Int?
                    if (mApp?.isServiceRunning()!!) {
                        oldPos = mApp?.mService?.mSongPos
                    } else {
                        oldPos = SharedPrefHelper.getInstance()
                            .getInt(SharedPrefHelper.Key.CURRENT_SONG_POSITION)
                    }
                    if (newPos != -1) {
                        if (mApp?.isServiceRunning()!! && newPos != oldPos) {
                            SharedPrefHelper.getInstance()
                                .put(SharedPrefHelper.Key.SONG_CURRENT_SEEK_DURATION, 0)
                            mApp?.mService?.setSelectedSong(newPos)
                        } else if (oldPos != newPos) {
                            mApp?.getPlayBackStarter()?.playSongs(songs!!, newPos)
                        }
                    }


                }
            }
        })

        return mView
    }

    override fun onResume() {
        super.onResume()
        updateUI()
    }


    override fun onStart() {
        super.onStart()
        activity?.registerReceiver(
            mUIUpdateReceiver,
            IntentFilter(Constants.ACTION_UPDATE_NOW_PLAYING_UI)
        )
    }

    override fun onStop() {
        super.onStop()
        activity?.unregisterReceiver(mUIUpdateReceiver)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fab -> {
                mApp?.getPlayBackStarter()?.playPauseFromBottomBar()
            }
        }
    }

    val mUIUpdateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.hasExtra(Constants.ACTION_PLAY_PAUSE)!!) {
                if (mApp?.isServiceRunning()!!) {
                    if (mApp?.mService?.mMediaPlayer1?.isPlaying!!) {
                        mFloatingActionButton?.setImageResource(R.drawable.pause)
                    } else {
                        mFloatingActionButton?.setImageResource(R.drawable.play)
                    }
                }
            } else {
                updateUI()
            }
        }
    }

    fun updateUI() {
        if (mApp?.isServiceRunning()!!) {
            mCurrentBottomBarAdapter?.updateSongData(mApp?.mService?.mSongs)
            if (mApp?.isServiceRunning()!!) {
                if (mApp?.mService?.mMediaPlayer1?.isPlaying!!) {
                    mFloatingActionButton?.setImageResource(R.drawable.pause)
                } else {
                    mFloatingActionButton?.setImageResource(R.drawable.play)
                }
            }

            mView?.visibility = View.VISIBLE
            mRecyclerView?.scrollToPosition(mApp?.mService?.mSongPos!!)

        } else {
            //  songs = mApp?.getDBAccessHelper()?.getQueue()
            /*  object : AsyncTask<Void?, Void?, Void?>() {
                  var position: Int? = null
                  override fun doInBackground(vararg params: Void?): Void? {
                      songs = mApp?.getDBAccessHelper()?.getQueue()
                      position = SharedPrefHelper.getInstance()
                          .getInt(SharedPrefHelper.Key.CURRENT_SONG_POSITION, 0)
                      return null
                  }



                  override fun onPostExecute(aVoid: Void?) {
                      super.onPostExecute(aVoid)
                      if (songs?.size!! > 0) {
                          mCurrentBottomBarAdapter?.updateSongData(songs)
                          mRecyclerView?.scrollToPosition(position!!)
                          // mDurationTextView?.text = Common.convertMillisToSecs()  to implement seekbar for this crappy duration
                          mView?.visibility = View.VISIBLE

                      } else {
                          mView?.visibility = View.GONE

                      }
                  }
              }.execute()*/
            var position: Int? = null
            MainScope().launch {
                withContext(Dispatchers.IO) {
                    songs = mApp?.getDBAccessHelper()?.getQueue()
                    position = SharedPrefHelper.getInstance()
                        .getInt(SharedPrefHelper.Key.CURRENT_SONG_POSITION, 0)
                }
               /* val result1 = async {
                    songs = mApp?.getDBAccessHelper()?.getQueue()
                    position = SharedPrefHelper.getInstance()
                        .getInt(SharedPrefHelper.Key.CURRENT_SONG_POSITION, 0)
                }
                result1.await()*/
                if (songs?.size!! > 0) {
                    mCurrentBottomBarAdapter?.updateSongData(songs)
                    mRecyclerView?.scrollToPosition(position!!)
                    // mDurationTextView?.text = Common.convertMillisToSecs()  to implement seekbar for this crappy duration
                    mView?.visibility = View.VISIBLE

                } else {
                    mView?.visibility = View.GONE

                }
            }

        }
    }

}
