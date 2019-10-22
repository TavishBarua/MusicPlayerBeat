package com.tavish.musicplayerbeat.Helpers.MediaHelpers

import android.app.PendingIntent
import android.app.Service
import android.content.ComponentName
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.widget.Toast
import androidx.media.session.MediaButtonReceiver.handleIntent
import com.tavish.musicplayerbeat.BroadcastReceivers.HeadsetNotificationBroadcast
import com.tavish.musicplayerbeat.Common
import com.tavish.musicplayerbeat.Helpers.SharedPrefHelper
import com.tavish.musicplayerbeat.Models.SongDto
import com.tavish.musicplayerbeat.R
import com.tavish.musicplayerbeat.Utils.AudioManagerHelper
import com.tavish.musicplayerbeat.Utils.Constants
import com.tavish.musicplayerbeat.Utils.Logger
import com.tavish.musicplayerbeat.Utils.SongDataHelper
import kotlinx.coroutines.*
import java.lang.NumberFormatException
import java.lang.Runnable
import java.lang.ref.WeakReference
import java.util.*
import kotlin.Exception
import kotlin.random.Random
import kotlin.reflect.jvm.internal.impl.utils.CollectionsKt

class MusicService : Service() {

    private var mContext: Context? = null
    private var mService: Service? = null
    var mSong: SongDto? = null

    var mSongList: MutableList<SongDto>? = null



    /**
     * First time playing flag
     */
    private var mPlayingForFirstTime = true


    var mSongPos = 0
    private var mMediaIntent: Intent? = null

    private var mBundle: Bundle? = null


    lateinit var mSongs: MutableList<SongDto>
    lateinit var mShuffledSongs: MutableList<Int>
    lateinit var mOriginalSongs: MutableList<SongDto>

    var mSongDataHelper: SongDataHelper? = null


    // AudioHelpers

    private var mAudioManager: AudioManager? = null
    private var mAudioManagerHelper: AudioManagerHelper? = null
    private var mApp: Common? = null
    private var mHandler: Handler? = null
    var prepareServiceListener: PrepareServiceListener? = null


    var mMediaPlayer1: MediaPlayer? = null

    var mMediaSession: MediaSessionCompat? = null

    // var songDataHelper: SongDataHelper? = null


    var mMediaPlayerPrepared = false


    var onErrorListener = MediaPlayer.OnErrorListener { mp, what, extra -> true }


    override fun onCreate() {
        super.onCreate()
        mContext = this
        mService = this

        mApp = applicationContext as Common
        mApp?.setIsServiceRunning(true)

        mApp?.mService = this

        mMediaIntent = Intent()
        mMediaIntent?.action = Constants.MEDIA_INTENT

        mMediaSession = MediaSessionCompat(
            applicationContext,
            "AudioPlayer",
            ComponentName(this, HeadsetNotificationBroadcast::class.java),
            null
        )


        mSongs = mutableListOf()
        mSongs.addAll(mApp?.getDBAccessHelper()?.getQueue()!!)
        mOriginalSongs = mutableListOf()

        mShuffledSongs = mutableListOf()

        initMediaPlayers()

        mAudioManager = applicationContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        mAudioManagerHelper = AudioManagerHelper()

        mHandler = Handler()
        mBundle = Bundle()

        mMediaIntent = Intent()
        mMediaIntent!!.action = Constants.MEDIA_INTENT
        mApp!!.mService = this

        /*if (SharedPrefHelper.getInstance().getInt(SharedPrefHelper.Key.SHUFFLE_MODE, Constants.SHUFFLE_OFF) == Constants.SHUFFLE_ON) {
            setShuffledOne()
        }*/

        for (song in mSongs) {
            try {
                mOriginalSongs.add(song.clone() as SongDto)
                mShuffledSongs.add(song._trackNumber!!)
            } catch (e: CloneNotSupportedException) {
                e.printStackTrace()
                Logger.log(e.message!!)
            }

        }

       /* if (SharedPrefHelper.getInstance().getInt(SharedPrefHelper.Key.REPEAT_MODE, Constants.REPEAT_OFF) == Constants.SHUFFLE_ON) {
            setShuffledOne()
        }
*/
        mMediaSession = MediaSessionCompat(
            applicationContext, "AudioPlayer", ComponentName(
                this,
                HeadsetNotificationBroadcast::class.java
            ), null
        )
        mMediaSession!!.isActive = true
        mMediaSession!!.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)

        val intent = Intent(Intent.ACTION_MEDIA_BUTTON)
        val playPauseTrackPendingIntent = PendingIntent.getBroadcast(mContext, 56, intent, 0)
        mMediaSession!!.setMediaButtonReceiver(playPauseTrackPendingIntent)
        mMediaSession!!.setCallback(object : MediaSessionCompat.Callback() {
            override fun onPlay() {
                super.onPlay()
                playPauseSong()
            }

            override fun onPause() {
                super.onPause()
                playPauseSong()
            }

            override fun onSkipToNext() {
                super.onSkipToNext()
            }

            override fun onSkipToPrevious() {
                super.onSkipToPrevious()
            }
        })

        mMediaSession!!.setPlaybackState(
            PlaybackStateCompat.Builder()
                .setState(PlaybackStateCompat.STATE_PLAYING, 2, 1f)
                .setActions(
                    PlaybackStateCompat.ACTION_FAST_FORWARD or
                            PlaybackStateCompat.ACTION_PAUSE or
                            PlaybackStateCompat.ACTION_PLAY or
                            PlaybackStateCompat.ACTION_PLAY_PAUSE or
                            PlaybackStateCompat.ACTION_SKIP_TO_NEXT or
                            PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS or
                            PlaybackStateCompat.ACTION_STOP
                )
                .build()
        )

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null && intent.action != null) {
            handleIntent(intent)
        } else {
            prepareServiceListener = mApp!!.getPlayBackStarter()
            prepareServiceListener!!.onServiceRunning(this)
        }

        return START_NOT_STICKY
    }

    private fun handleIntent(intent: Intent) {
        when {
            intent.action!!.equals(Constants.ACTION_PAUSE, ignoreCase = true) -> playPauseSong()
            intent.action!!.equals(Constants.ACTION_NEXT, ignoreCase = true) -> nextSong()
            intent.action!!.equals(Constants.ACTION_PREVIOUS, ignoreCase = true) -> previousSong()
        }
    }

    private val mOnCompletionListener = MediaPlayer.OnCompletionListener {

        val repeat_pref = SharedPrefHelper.getInstance().getInt(SharedPrefHelper.Key.REPEAT_MODE, Constants.REPEAT_OFF)
        if(SharedPrefHelper.getInstance().getInt(SharedPrefHelper.Key.REPEAT_MODE,Constants.REPEAT_OFF)==Constants.SHUFFLE_ON){

            mSongPos= (0..mOriginalSongs.size).random()
        }
        if (repeat_pref == Constants.REPEAT_OFF || repeat_pref == Constants.REPEAT_PLAYLIST || repeat_pref == Constants.SHUFFLE_ON) {
            if (mSongPos < (mSongs.size - 1)) {
                mSongPos++
                startSong()
            } else {
                mSongPos = 0
                if (repeat_pref == Constants.REPEAT_OFF) stopSelf()
                else if (repeat_pref == Constants.REPEAT_PLAYLIST) startSong()
            }
        } else if (repeat_pref == Constants.REPEAT_SONG) {
            startSong()
        }
    }


    private val onPreparedListener =
        MediaPlayer.OnPreparedListener {
            mMediaPlayerPrepared = true;
            mMediaPlayer1?.setOnCompletionListener(mOnCompletionListener)
            // mMediaPlayer1?.seekTo(PreferencesHelper.getInstance().getInt(PreferencesHelper.Key.SONG_CURRENT_SEEK_DURATION))

            if (mPlayingForFirstTime) {
                mPlayingForFirstTime = false
            }

            // applyMediaPlayerEQ(getSongDataHelper().getId())
            startPlaying();

            val intent = Intent(Constants.ACTION_UPDATE_NOW_PLAYING_UI)
            intent.putExtra(Constants.UPDATE_UI, true)
            sendBroadcast(intent)
        }


    val sendUpdatesToUI = object : Runnable {
        override fun run() {
            sendIntentDataMedia()
            mHandler?.postDelayed(this, 200)
        }

    }

     fun saveIt(){

        runBlocking{
             mApp?.getDBAccessHelper()?.saveQueue(mSongs)
             SharedPrefHelper.getInstance().put(SharedPrefHelper.Key.CURRENT_SONG_POSITION, mSongPos)
             SharedPrefHelper.getInstance()
                 .put(SharedPrefHelper.Key.SONG_CURRENT_SEEK_DURATION, mMediaPlayer1?.currentPosition!!)
             SharedPrefHelper.getInstance().put(SharedPrefHelper.Key.SONG_TOTAL_SEEK_DURATION, mMediaPlayer1?.duration!!)
         }
    }


    @Throws(NumberFormatException::class)
    fun sendIntentDataMedia() {

        mBundle?.putString("track", mSongDataHelper?.mTitle)
        mBundle?.putString("artist", mSongDataHelper?.mArtist)
        mBundle?.putString("album", mSongDataHelper?.mAlbum)
        try {
            mBundle?.putLong("duration", mSongDataHelper?.mDuration!!)

        } catch (ex: Exception) {
            ex.printStackTrace()
            mBundle?.putLong("position", 0)
        }

        try {
            mBundle?.putLong("position", mMediaPlayer1?.currentPosition!!.toLong())
        } catch (e: Exception) {
            e.printStackTrace()
            mBundle?.putLong("position", 0)
        }

        mBundle?.apply {
            putBoolean("playing", true)
            putString("scrobbling_source", "com.tavish.musicplayerbeat.action")
        }

        mMediaIntent?.putExtras(mBundle!!)
        sendBroadcast(mMediaIntent)

    }

    fun isMusicPlaying(): Boolean? {
        try {
            return mMediaPlayer1?.isPlaying
        } catch (ex: java.lang.Exception) {
            ex.printStackTrace()
            return false
        }
    }

    fun getSongList(): MutableList<SongDto> {
        return mSongs
    }

    fun setSongList(listSong: MutableList<SongDto>) {
        mSongs.clear()
       // mShuffledSongs.clear()
        mOriginalSongs.clear()

        mSongs.addAll(listSong)
        for (song in listSong) {
            try {
             //   mShuffledSongs.add(song.clone() as SongDto)
                mOriginalSongs.add(song.clone() as SongDto)
            } catch (ex: Exception) {
                Logger.log(ex.message!!)
                ex.printStackTrace()
            }
        }
    }

    fun setSelectedSong(pos: Int) {
        mSongPos = pos
        if (mSongs.size != 0) {
            startSong()
        }
    }

    // properties??
   /* fun setShuffledOne(){
        Collections.shuffle(mShuffledSongs,java.util.Random(System.nanoTime()))
      //  mSongs.addAll(mShuffledSongs)
    }*/

    fun setOriginalOne(){
        mSongs.clear()
        mSongs.addAll(mOriginalSongs)
    }


    fun initMediaPlayers() {
        mMediaPlayer1 = MediaPlayer()
        mMediaPlayer1?.setWakeMode(this, PowerManager.PARTIAL_WAKE_LOCK)
        mMediaPlayer1?.setAudioAttributes(AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build())

        if (mSongs.size > 0) {
            startSong()
        }

    }

    fun startSong() {
        mHandler?.removeCallbacks(sendUpdatesToUI)
        mMediaPlayerPrepared = false
        mSong = mSongs[mSongPos]
        mMediaPlayer1?.reset()
        try {
            //  val songDataHelper =
            mSongDataHelper = SongDataHelper()
            mSongDataHelper?.populateSongData(mContext!!, null, mSongPos)
            //   mApp?.getDBAccessHelper()?.insertSongCount(mSongs.get(mSongPos))
            // mApp?.getDBAccessHelper()?.addToRecentlyPlayed(mSongs.get(mSongPos))

            mMediaPlayer1?.setDataSource(mContext!!, getUri(mSongs[mSongPos]._id!!))
            mMediaPlayer1?.setOnPreparedListener(onPreparedListener)
            mMediaPlayer1?.setOnErrorListener(onErrorListener)
            mMediaPlayer1?.prepareAsync()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun getUri(audioId: Long): Uri {
        return ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, audioId)
    }


    private val startMP_IfPrepared = object : Runnable {
        override fun run() {
            if (mMediaPlayerPrepared) {
                startPlaying()
            } else {
                mHandler?.postDelayed(this, 100)
            }
        }
    }


    // For audio Focus
    fun startPlaying() {
        if (mMediaPlayerPrepared) {
            if (!mMediaPlayer1?.isPlaying!! && requestAudioFocus()) {
                mMediaPlayer1?.start()
                mHandler?.removeCallbacks(startMP_IfPrepared)
                mHandler?.postDelayed(sendUpdatesToUI, 600)
            }
        } else {
            mHandler?.post(startMP_IfPrepared)
        }
        //Play pause intent to display the correct UI throughout the entire app.
        sendPlayPauseBroadcast()
        //  updateNotification()
    }

    fun stopPlaying() {
        if (mMediaPlayer1?.isPlaying!!) {
            mMediaPlayer1?.pause()
            mAudioManager?.abandonAudioFocus(audioFocusChangeListener)
            mHandler?.removeCallbacks(sendUpdatesToUI)
        }
        //   updateNotification()
        stopForeground(false)
    }

    fun nextSong() {
        SharedPrefHelper.getInstance().put(SharedPrefHelper.Key.SONG_CURRENT_SEEK_DURATION, 0)
        PrevSongAsyncTask(this).execute()
    }

    fun previousSong() {
        NextSongAsyncTask(this).execute()
    }

    private fun sendPlayPauseBroadcast() {
        val intent = Intent(Constants.ACTION_UPDATE_NOW_PLAYING_UI)
        intent.putExtra(Constants.ACTION_PLAY_PAUSE, true)
        sendBroadcast(intent)
    }

    fun playPauseSong() {
        if (!mMediaPlayer1?.isPlaying!!) {
            startPlaying()
          //  mHandler?.removeCallbacks { stopSelf() }
        } else {
            stopPlaying()
            stopForeground(false)
           // mHandler?.postDelayed({ stopSelf() }, 30000)
          //  mHandler?.postDelayed(, 30000)
        }
        sendPlayPauseBroadcast()
        //  SaveQueueAsyncTask(this).execute()
        saveIt()
    }

    private fun requestAudioFocus(): Boolean {
        // needs to be changed
        val result = mAudioManager?.requestAudioFocus(
            audioFocusChangeListener,
            AudioManager.STREAM_MUSIC,
            AudioManager.AUDIOFOCUS_GAIN
        )
        if (result != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            Toast.makeText(applicationContext, R.string.unable_to_get_audio_focus, Toast.LENGTH_LONG).show()
            return false
        } else {
            return true
        }
    }

    private val duckUpVolumeRunnable = object : Runnable {
        override fun run() {
            if (mAudioManagerHelper?.mCurrentVolume!! < mAudioManagerHelper?.mTargetVolume!!) {
                mAudioManager?.setStreamVolume(
                    AudioManager.STREAM_MUSIC,
                    mAudioManagerHelper?.mCurrentVolume!! + mAudioManagerHelper?.mStepUpIncrement!!, 0
                )
                mAudioManagerHelper?.mCurrentVolume = mAudioManager?.getStreamVolume(AudioManager.STREAM_MUSIC)!!
                mHandler?.postDelayed(this, 50)
            }
        }
    }

    private val duckDownVolumeRunnable = object : Runnable {
        override fun run() {
            if (mAudioManagerHelper?.mCurrentVolume!! > mAudioManagerHelper?.mTargetVolume!!) {
                mAudioManager?.setStreamVolume(
                    AudioManager.STREAM_MUSIC,
                    mAudioManagerHelper?.mCurrentVolume!! - mAudioManagerHelper?.mStepDownIncrement!!, 0
                )
                mAudioManagerHelper?.mCurrentVolume = mAudioManager?.getStreamVolume(AudioManager.STREAM_MUSIC)!!
                mHandler?.postDelayed(this, 50)
            }
        }
    }


    private val audioFocusChangeListener = AudioManager.OnAudioFocusChangeListener { focusChange ->
        if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
            try {
                stopPlaying()
                mAudioManagerHelper?.hasAudioFocus = false
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
            mAudioManagerHelper?.isAudioDucked = true
            mAudioManagerHelper?.mTargetVolume = 5
            mAudioManagerHelper?.mStepDownIncrement = 1
            mAudioManagerHelper?.mCurrentVolume = mAudioManager?.getStreamVolume(AudioManager.STREAM_MUSIC)!!
            mAudioManagerHelper?.mOriginalVolume = mAudioManager?.getStreamVolume(AudioManager.STREAM_MUSIC)!!
            mHandler?.post(duckDownVolumeRunnable)
        } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
            if (mAudioManagerHelper?.isAudioDucked!!) {
                mAudioManagerHelper?.mTargetVolume = mAudioManagerHelper?.mOriginalVolume!!
                mAudioManagerHelper?.mStepUpIncrement = 1
                mAudioManagerHelper?.mCurrentVolume = mAudioManager?.getStreamVolume(AudioManager.STREAM_MUSIC)!!
                mHandler?.post(duckUpVolumeRunnable)
                mAudioManagerHelper?.isAudioDucked = false
            } else {
                mAudioManagerHelper?.hasAudioFocus = true
            }
        } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
            if (mMediaPlayer1 != null) {
                stopPlaying()
            }
            mAudioManagerHelper?.hasAudioFocus = false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mApp?.setIsServiceRunning(false)
        mHandler?.removeCallbacks(sendUpdatesToUI)

    }

    private class NextSongAsyncTask internal constructor(context: MusicService) : AsyncTask<Void, Void, String>() {

        private var activityReference: WeakReference<MusicService>? = null

        init {
            activityReference = WeakReference(context)
        }

        override fun doInBackground(vararg params: Void): String {
            if (activityReference?.get()?.mSongs?.size != 1) {

                if (activityReference?.get()?.mSongPos!! < activityReference?.get()?.mSongs?.size!! - 1) {
                    activityReference?.get()?.mSongPos = +1
                    activityReference?.get()?.startSong()
                } else {
                    activityReference?.get()?.mSongPos = 0
                    activityReference?.get()?.startSong()
                }
            }
            return "finished"
        }

        override fun onPostExecute(result: String) {
            // get a reference to the activity if it is still there
            val activity = activityReference?.get() ?: return
            // access Activity member variables or modify the activity's UI
            // activity.mJustAVariable = 123
        }
    }


    private class PrevSongAsyncTask internal constructor(context: MusicService) : AsyncTask<Void, Void, String>() {

        private var activityReference: WeakReference<MusicService>? = null

        init {
            activityReference = WeakReference(context)
        }

        override fun doInBackground(vararg params: Void): String {
            if (activityReference?.get()?.mSongs?.size != 1) {
                if (activityReference?.get()?.mSongPos!! < activityReference?.get()?.mSongs?.size!! - 1) {
                    activityReference?.get()?.mSongPos = +1
                    activityReference?.get()?.startSong()
                } else {
                    activityReference?.get()?.mSongPos = 0
                    activityReference?.get()?.startSong()
                }
            }
            return "finished"
        }

        override fun onPostExecute(result: String) {
            // get a reference to the activity if it is still there
            val activity = activityReference?.get() ?: return
            // access Activity member variables or modify the activity's UI
            // activity.mJustAVariable = 123
        }
    }


    /*  private class SaveQueueAsyncTask internal constructor(context: MusicService) : AsyncTask<Void, Void, String>() {

      private var activityReference: WeakReference<MusicService>? = null

      init {
          activityReference = WeakReference(context)
      }

      override fun doInBackground(vararg params: Void): String {
          activityReference?.get()?.saveIt()
          return "finished"
      }

      override fun onPostExecute(result: String) {

      }
  }*/

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    interface PrepareServiceListener {
        fun onServiceRunning(musicService: MusicService)
    }


}