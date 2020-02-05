package com.tavish.musicplayerbeat.Fragments

import android.app.Activity
import android.content.*
import android.os.Bundle
import android.os.Handler
import android.transition.TransitionInflater
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.widget.AppCompatSeekBar
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.tavish.musicplayerbeat.Adapters.CurrentPlayingBottomBarAdapter
import com.tavish.musicplayerbeat.Common
import com.tavish.musicplayerbeat.Helpers.SharedPrefHelper
import com.tavish.musicplayerbeat.Interfaces.SharedResourceOnItemClickActivity
import com.tavish.musicplayerbeat.Interfaces.SharedResourceOnItemClickFragment
import com.tavish.musicplayerbeat.Models.SongDto
import com.tavish.musicplayerbeat.R
import com.tavish.musicplayerbeat.Utils.Constants
import kotlinx.coroutines.*


class CurrentPlayingBottomBarFragment : Fragment(), View.OnClickListener, SharedResourceOnItemClickFragment {


    companion object{
        fun newInstance(context: Context?): CurrentPlayingBottomBarFragment {
            val fragment = CurrentPlayingBottomBarFragment()
            fragment.enterTransition = TransitionInflater.from(context).inflateTransition(
                R.transition.slide_bottom
            )
            fragment.reenterTransition = TransitionInflater.from(context).inflateTransition(
                R.transition.slide_left
            )
            fragment.exitTransition = TransitionInflater.from(context).inflateTransition(
                R.transition.slide_left
            )
            return fragment
        }
    }

    /*override val coroutineContext: CoroutineContext
        get() */
    private var mView: View? = null


    private var mFloatingActionButton: FloatingActionButton? = null
    private var mDurationTextView: TextView? = null
    private var mApp: Common? = null
    private var mRecyclerView: RecyclerView? = null
    private var mCurrentBottomBarAdapter: CurrentPlayingBottomBarAdapter? = null
    private var songs: MutableList<SongDto>? = null
    private var mSeekBarBottom: AppCompatSeekBar? = null
    private var mHandler: Handler? = null
    private var mActivity: Activity? = null


    private var mSharedResourceOnItemClickActivity: SharedResourceOnItemClickActivity? = null




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_current_playing_bottom_bar, container, false)
        mApp = activity?.applicationContext as Common
        mView?.visibility = View.GONE
        mRecyclerView = mView?.findViewById(R.id.rr_bottom_songs)

        mRecyclerView?.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
       mCurrentBottomBarAdapter = CurrentPlayingBottomBarAdapter(mApp?.getDBAccessHelper()?.getQueue()!!,this)
       // mCurrentBottomBarAdapter = CurrentPlayingBottomBarAdapter(this)
        mRecyclerView?.adapter = mCurrentBottomBarAdapter
        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(mRecyclerView)

        mDurationTextView = mView?.findViewById(R.id.txt_bottom_duration)
        mFloatingActionButton = mView?.findViewById(R.id.fab)
        mSeekBarBottom = mView?.findViewById(R.id.seek_bar_bottom)
        mHandler = Handler()

        mFloatingActionButton?.setOnClickListener(this)
        mSeekBarBottom?.setOnSeekBarChangeListener(mSeekBarChangeListener)
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
                            SharedPrefHelper.getInstance()
                                .put(SharedPrefHelper.Key.SONG_CURRENT_SEEK_DURATION, 0)
                            mApp?.getPlayBackStarter()?.playSongs(songs!!, newPos)
                        }
                    }


                }
            }
        })

        return mView
    }




    override fun onSongItemClickFragment(pos: Int, songItems: MutableList<SongDto>, shareImageView: ImageView) {
        /*val songFragment = MPlayerFragment.newInstance(pos,songItem)
        fragmentManager
            ?.beginTransaction()
            ?.addSharedElement(shareImageView, ViewCompat.getTransitionName(shareImageView)!!)
            ?.addToBackStack(CurrentPlayingBottomBarFragment::class.java.simpleName)
            ?.replace(R.id.rl_main, songFragment)
            ?.commit()*/

        mSharedResourceOnItemClickActivity?.onSongItemClickActivity(pos,songItems,shareImageView)
       /* val intent = Intent(context,MPlayerFragment::class.java)
        val pair = androidx.core.util.Pair<View,String>(shareImageView, ViewCompat.getTransitionName(shareImageView)!!)
        val pair1 = androidx.core.util.Pair<Int,String>(pos, ViewCompat.getTransitionName(shareImageView)!!)
        val pair2 = androidx.core.util.Pair<SongDto,String>(songItem, ViewCompat.getTransitionName(shareImageView)!!)
        val options =
            ActivityOptionsCompat.makeSceneTransitionAnimation(context as Activity, pair)
        intent.putExtra("data",songItem)
        startActivity(intent,options.toBundle())*/

    }



    override fun onResume() {
        super.onResume()
        updateUI()
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mActivity = context as Activity
        mSharedResourceOnItemClickActivity = mActivity as SharedResourceOnItemClickActivity

    }


    override fun onDetach() {
        super.onDetach()
        mActivity = null
    }

    override fun onStart() {
        super.onStart()
        mActivity?.registerReceiver(
            mUIUpdateReceiver,
            IntentFilter(Constants.ACTION_UPDATE_NOW_PLAYING_UI)
        )
    }

    override fun onStop() {
        super.onStop()
        mActivity?.unregisterReceiver(mUIUpdateReceiver)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fab -> {
                mApp?.getPlayBackStarter()?.playPauseFromBottomBar()
                try {
                    var dur: Int? = 0
                    if (mApp?.mService != null && mApp?.mService?.mMediaPlayerPrepared!!) {
                        dur = mApp?.mService?.mMediaPlayer1?.duration
                        setSeekbarDuration(dur!!)
                    }

                } catch (ex: Exception) {
                    Log.i(ContentValues.TAG, "MediaPlayer: Fab Click Error  $ex")
                    mFloatingActionButton?.setImageResource(R.drawable.play)
                }
            }
        }
    }

    private fun setSeekbarDuration(duration: Int) {
        mSeekBarBottom?.max = duration
        if (mApp?.mService != null && mApp?.mService?.mMediaPlayerPrepared!!) {
            val pos = mApp?.mService?.mMediaPlayer1?.currentPosition!!
            mSeekBarBottom?.progress = pos
            mHandler?.postDelayed(seekBarRunnable, 1000)
            mDurationTextView?.text = (Common.convertMillisToSecs(mSeekBarBottom?.getProgress()!!))
        }
        // mApp?.mService?.mMediaPlayer1?.seekTo(SharedPrefHelper.getInstance().getInt(SharedPrefHelper.Key.SONG_CURRENT_SEEK_DURATION))

    }

    private val mSeekBarChangeListener = object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            if (mApp?.isServiceRunning()!!) {
                try {
                    var currentSongDuration: Int? = 0
                    if (mApp?.mService != null && mApp?.mService?.mMediaPlayerPrepared!!){
                        currentSongDuration = mApp?.mService?.mMediaPlayer1?.duration
                        seekBar?.max = currentSongDuration!!
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) {
            mHandler?.removeCallbacks (seekBarRunnable)
        }

        override fun onStopTrackingTouch(seekBar: SeekBar?) {
            val seekBarPosition = seekBar?.progress
            if (mApp?.isServiceRunning()!!) {
                mApp?.mService?.mMediaPlayer1?.seekTo(seekBarPosition!!)
                mHandler?.post (seekBarRunnable)
            } else {
                SharedPrefHelper.getInstance()
                    .put(SharedPrefHelper.Key.SONG_CURRENT_SEEK_DURATION, seekBarPosition!!)
                mDurationTextView?.text = Common.convertMillisToSecs(mSeekBarBottom?.progress!!)
            }
        }
    }

    var seekBarRunnable = object : Runnable {
        override fun run() {
            try {
                if (mApp?.mService != null && mApp?.mService?.mMediaPlayerPrepared!!) {
                    val  currentPosition = mApp?.mService?.mMediaPlayer1?.currentPosition
                    mSeekBarBottom?.progress = currentPosition!!
                    mDurationTextView?.text = Common.convertMillisToSecs(mSeekBarBottom?.progress!!)
                    if (mApp?.isServiceRunning()!!) {
                        if (mApp?.mService?.mMediaPlayer1?.isPlaying!!) {
                            mHandler?.postDelayed(this, 990)
                        } else {
                            mHandler?.removeCallbacks(this)
                        }
                    } else {
                        mHandler?.removeCallbacks(this)
                    }
                }

            } catch (ex: Exception) {

            }
        }

    }


    var mUIUpdateReceiver = object : BroadcastReceiver() {
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
            try {
                var dur: Int? = 0
                if (mApp?.mService != null && mApp?.mService?.mMediaPlayerPrepared!!) {
                    dur = mApp?.mService?.mMediaPlayer1?.duration
                    setSeekbarDuration(dur!!)
                }

            } catch (ex: Exception) {
                Log.i(ContentValues.TAG, "MediaPlayer: Fab Click Error  $ex")
            }
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
            var position: Int? = 0
            var seekBarPosition: Int? = 0
            CoroutineScope(Dispatchers.Main).launch {
                withContext(Dispatchers.IO) {
                    songs = mApp?.getDBAccessHelper()?.getQueue()
                    position = SharedPrefHelper.getInstance()
                        .getInt(SharedPrefHelper.Key.CURRENT_SONG_POSITION, 0)
                    seekBarPosition = SharedPrefHelper.getInstance()
                        .getInt(SharedPrefHelper.Key.SONG_CURRENT_SEEK_DURATION, 0)

                }

                if (songs?.size!! > 0) {
                    mCurrentBottomBarAdapter?.updateSongData(songs)
                    mSeekBarBottom?.max = SharedPrefHelper.getInstance()
                        .getInt(SharedPrefHelper.Key.SONG_TOTAL_SEEK_DURATION, 0)
                    mSeekBarBottom?.progress = seekBarPosition!!
                    mRecyclerView?.scrollToPosition(position!!)
                    mDurationTextView?.text =
                        Common.convertMillisToSecs(mSeekBarBottom?.progress!!)  //to implement seekbar for this crappy duration
                    mView?.visibility = View.VISIBLE

                } else {
                    mView?.visibility = View.GONE

                }

            }


            /* val result1 = async {
                 songs = mApp?.getDBAccessHelper()?.getQueue()
                 position = SharedPrefHelper.getInstance()
                     .getInt(SharedPrefHelper.Key.CURRENT_SONG_POSITION, 0)
             }
             result1.await()*/

        }

    }


}
