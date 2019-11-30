package com.tavish.musicplayerbeat.Helpers.MediaHelpers

import android.app.*
import android.content.ComponentName
import android.content.ContentUris
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaMetadata
import android.media.MediaPlayer
import android.media.audiofx.PresetReverb
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.media.session.MediaButtonReceiver.handleIntent
import com.h6ah4i.android.media.IBasicMediaPlayer
import com.h6ah4i.android.media.IMediaPlayerFactory
import com.h6ah4i.android.media.hybrid.HybridMediaPlayerFactory
import com.h6ah4i.android.media.opensl.OpenSLMediaPlayer
import com.h6ah4i.android.media.opensl.OpenSLMediaPlayerContext
import com.h6ah4i.android.media.opensl.OpenSLMediaPlayerFactory
import com.h6ah4i.android.media.standard.StandardMediaPlayerFactory
import com.tavish.musicplayerbeat.Activities.MPlayerActivity
import com.tavish.musicplayerbeat.BroadcastReceivers.HeadsetNotificationBroadcast
import com.tavish.musicplayerbeat.Common
import com.tavish.musicplayerbeat.Helpers.Equalizer.EqualizerDataHelper
import com.tavish.musicplayerbeat.Helpers.SharedPrefHelper
import com.tavish.musicplayerbeat.Models.SongDto
import com.tavish.musicplayerbeat.R
import com.tavish.musicplayerbeat.Utils.AudioManagerHelper
import com.tavish.musicplayerbeat.Utils.Constants
import com.tavish.musicplayerbeat.Utils.Logger
import com.tavish.musicplayerbeat.Utils.SongDataHelper
import kotlinx.coroutines.*
import okhttp3.Dispatcher
import java.lang.NumberFormatException
import java.lang.Runnable
import java.lang.ref.WeakReference
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.Exception
import kotlin.random.Random
import kotlin.reflect.jvm.internal.impl.utils.CollectionsKt

class MusicService : Service() {

    private var mContext: Context? = null
    private var mService: Service? = null
    var mSong: SongDto? = null

    //var mSongList: MutableList<SongDto>? = null


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


    // var mMediaPlayer1: MediaPlayer? = null

    var mMediaSession: MediaSessionCompat? = null

    var factory: IMediaPlayerFactory? = null
    var mMediaPlayer1: IBasicMediaPlayer? = null
    private var mEqualizerDataHelper: EqualizerDataHelper? = null
    private var openSLMediaPlayerContext: OpenSLMediaPlayerContext? = null
    private var openSLMediaPlayerContextParam: OpenSLMediaPlayerContext.Parameters? = null


    // var songDataHelper: SongDataHelper? = null


    var mMediaPlayerPrepared = false


    var onErrorListener =
        IBasicMediaPlayer.OnErrorListener { mp, what, extra ->
            Log.i(TAG, "MediaPlayer: onError  $what $extra")
            true
        }
    //  mp, what, extra -> true


    override fun onCreate() {
        super.onCreate()
        mContext = this
        mService = this

        //factory = StandardMediaPlayerFactory(this)
        //   factory = OpenSLMediaPlayerFactory(applicationContext)
        factory = HybridMediaPlayerFactory(applicationContext)
        mMediaPlayer1 = factory!!.createMediaPlayer()


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

        for (song in mSongs) {
            try {
                mOriginalSongs.add(song.clone() as SongDto)
                mShuffledSongs.add(song._trackNumber!!)
            } catch (e: CloneNotSupportedException) {
                e.printStackTrace()
                Logger.log(e.message!!)
            }

        }

        initMediaPlayers()

        initCustomAudioFX()



        mHandler = Handler()
        mBundle = Bundle()

        mMediaIntent = Intent()
        mMediaIntent!!.action = Constants.MEDIA_INTENT
        mApp!!.mService = this

        mAudioManager = applicationContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        mAudioManagerHelper = AudioManagerHelper()

        /*if (SharedPrefHelper.getInstance().getInt(SharedPrefHelper.Key.SHUFFLE_MODE, Constants.SHUFFLE_OFF) == Constants.SHUFFLE_ON) {
            setShuffledOne()
        }*/



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
            MainScope().launch(Dispatchers.Default) {
                handleIntent(intent)
            }

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

    private val mOnCompletionListener = IBasicMediaPlayer.OnCompletionListener {


        val repeat_pref = SharedPrefHelper.getInstance()
            .getInt(SharedPrefHelper.Key.REPEAT_MODE, Constants.REPEAT_OFF)
        if (SharedPrefHelper.getInstance().getInt(
                SharedPrefHelper.Key.REPEAT_MODE,
                Constants.REPEAT_OFF
            ) == Constants.SHUFFLE_ON
        ) {

            mSongPos = (0..mOriginalSongs.size).random()
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


    private var onPreparedListener =
        IBasicMediaPlayer.OnPreparedListener {
            mMediaPlayerPrepared = true
            mMediaPlayer1?.seekTo(SharedPrefHelper.getInstance().getInt(SharedPrefHelper.Key.SONG_CURRENT_SEEK_DURATION))
            mMediaPlayer1?.setOnCompletionListener(mOnCompletionListener)


            if (mPlayingForFirstTime) {
                mPlayingForFirstTime = false

            }else{

            }


            applyMediaPlayerEQ(mSongDataHelper?.mId)
            startPlaying()
           // mHandler?.postDelayed({startPlaying()},200)


            val intent = Intent(Constants.ACTION_UPDATE_NOW_PLAYING_UI)
            intent.putExtra(Constants.UPDATE_UI, true)
            sendBroadcast(intent)
        }

    fun delayFunction(function: ()-> Unit, delay: Long) {
        mHandler?.postDelayed(function, delay)
    }


    val sendUpdatesToUI = object : Runnable {
        override fun run() {
            sendIntentDataMedia()
            mHandler?.postDelayed(this, 200)
        }

    }

    fun notificationUpdate() {

        startForeground(1056, mediaPlayerNotification())

        //updateWidgets()

        mMediaSession?.setMetadata(
            MediaMetadataCompat.Builder()
                .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, mSongDataHelper?.mAlbumArt)
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, mSongDataHelper?.mArtist)
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, mSongDataHelper?.mAlbum)
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, mSongDataHelper?.mTitle)
                .build()
        )
    }


    fun mediaPlayerNotification(): Notification {
        val intent: Intent? = Intent(applicationContext, MPlayerActivity::class.java)
        intent?.putExtra("LAUNCHED FROM NOTIFS", true)
        val pendingIntent = PendingIntent.getActivity(applicationContext, 0, intent, 0)
        var action: NotificationCompat.Action? = null
        if (!isMusicPlaying()!!)
            action = generateAction(R.drawable.play, "PLAY", Constants.ACTION_PAUSE)
        else
            action = generateAction(R.drawable.pause, "PAUSE", Constants.ACTION_PAUSE)

        val builder: NotificationCompat.Builder
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = NotificationCompat.Builder(this, channelId())

        } else {
            builder = NotificationCompat.Builder(this)
        }

        return builder
            .addAction(generateAction(R.mipmap.btn_prev, "Previous", Constants.ACTION_PREVIOUS))
            .addAction(action)
            .addAction(generateAction(R.mipmap.btn_next, "Next", Constants.ACTION_NEXT))
            .setSmallIcon(R.mipmap.icn_beatdrop)
            .setContentTitle(mSongDataHelper?.mTitle)
            .setContentIntent(pendingIntent)
            .setContentText(mSongDataHelper?.mAlbum)
            .setLargeIcon(mSongDataHelper?.mAlbumArt)
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setShowActionsInCompactView(0, 1, 2)
                    .setMediaSession(mMediaSession?.sessionToken)
            )
            .build()

    }

    private fun generateAction(
        icon: Int?,
        title: String?,
        intentAct: String?
    ): NotificationCompat.Action {
        val intent: Intent? = Intent(applicationContext, MusicService::class.java)
        intent?.action = intentAct
        val pendingIntent = PendingIntent.getService(applicationContext, 1, intent, 0)
        return NotificationCompat.Action.Builder(icon!!, title, pendingIntent).build()

    }

    private fun channelId(): String {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = "NOTIFS_CHANNEL_NAME"
            val importance = NotificationManager.IMPORTANCE_LOW
            val notificationChannel = NotificationChannel("7000", channelName, importance)
            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(
                notificationChannel
            )
        }

        return "7000"

    }


    fun saveIt() {

        CoroutineScope(Dispatchers.IO).launch {
            mApp?.getDBAccessHelper()?.saveQueue(mSongs)
            SharedPrefHelper.getInstance().put(SharedPrefHelper.Key.CURRENT_SONG_POSITION, mSongPos)

            if (mService!=null && mMediaPlayerPrepared) {
              var pos = mMediaPlayer1?.currentPosition!!
              var dur = mMediaPlayer1?.duration!!
                SharedPrefHelper.getInstance()
                    .put(SharedPrefHelper.Key.SONG_CURRENT_SEEK_DURATION, pos)
                SharedPrefHelper.getInstance().put(SharedPrefHelper.Key.SONG_TOTAL_SEEK_DURATION, dur)
            }


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

            if (mService!=null&& mMediaPlayerPrepared) {
                val abc: Int? = mMediaPlayer1?.currentPosition!!
                mBundle?.putLong("position", abc!!.toLong())
            }



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

    fun setOriginalOne() {
        mSongs.clear()
        mSongs.addAll(mOriginalSongs)
    }

    fun initMediaPlayers() {
        //  mMediaPlayer1 = MediaPlayer()
        mMediaPlayer1?.setWakeMode(this, PowerManager.PARTIAL_WAKE_LOCK)
        mMediaPlayer1?.setAudioAttributes(
            com.h6ah4i.android.media.compat.AudioAttributes.Builder().setContentType(
                com.h6ah4i.android.media.compat.AudioAttributes.CONTENT_TYPE_MUSIC
            ).build()
        )

        if (mSongs.size > 0) {
            startSong()
        }

    }

    fun startSong() {
        mHandler?.removeCallbacks(sendUpdatesToUI)

        mSong = mSongs[mSongPos]
        mMediaPlayer1?.reset()
        mMediaPlayerPrepared = false
        try {
            //  val songDataHelper =
            mSongDataHelper = SongDataHelper()
            mSongDataHelper?.populateSongData(mContext!!, null, mSongPos)
            //   mApp?.getDBAccessHelper()?.insertSongCount(mSongs.get(mSongPos))
            // mApp?.getDBAccessHelper()?.addToRecentlyPlayed(mSongs.get(mSongPos))
            mMediaPlayer1?.setDataSource(mContext!!, getUri(mSongs[mSongPos]._id!!))
            mMediaPlayer1?.setOnPreparedListener(onPreparedListener)
            // mMediaPlayer1?.setOnPreparedListener(PreparedListener())
            mMediaPlayer1?.setOnErrorListener(onErrorListener)
            //mMediaPlayer1?.prepare()
            mMediaPlayer1?.prepareAsync()
        } catch (ex: Exception) {
            Log.i(TAG, "mMediaPlayer setDataSource e: $ex")
        }
    }

    fun getUri(audioId: Long): Uri {
        return ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, audioId)
    }

    private val startMP_IfPrepared = object : Runnable {
        override fun run() {
            if (mService!=null && mMediaPlayerPrepared) {
                startPlaying()
            } else {
                mHandler?.postDelayed(this, 100)
            }
        }
    }


    // For audio Focus
    fun startPlaying() {
        if (mService!=null && mMediaPlayerPrepared) {
            if (!mMediaPlayer1?.isPlaying!! && requestAudioFocus()) {
              //  mMediaPlayer1?.start()
               mHandler?.postDelayed({mMediaPlayer1?.start()},450)
                mHandler?.removeCallbacks(startMP_IfPrepared)
                mHandler?.postDelayed(sendUpdatesToUI, 600)
            }
        } else {
            mHandler?.post(startMP_IfPrepared)
        }
        //Play pause intent to display the correct UI throughout the entire app.
       // sendPlayPauseBroadcast()
      //  mHandler?.postDelayed({sendPlayPauseBroadcast()},500)
        mHandler?.postDelayed({sendPlayPauseBroadcast();notificationUpdate()},500)

    }

    fun stopPlaying() {
        if (mMediaPlayer1?.isPlaying!!) {
            mMediaPlayer1?.pause()
            mAudioManager?.abandonAudioFocus(audioFocusChangeListener)
            mHandler?.removeCallbacks(sendUpdatesToUI)
        }
        notificationUpdate()
        stopForeground(false)
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
            Toast.makeText(
                applicationContext,
                R.string.unable_to_get_audio_focus,
                Toast.LENGTH_LONG
            ).show()
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
                    mAudioManagerHelper?.mCurrentVolume!! + mAudioManagerHelper?.mStepUpIncrement!!,
                    0
                )
                mAudioManagerHelper?.mCurrentVolume =
                    mAudioManager?.getStreamVolume(AudioManager.STREAM_MUSIC)!!
                mHandler?.postDelayed(this, 50)
            }
        }
    }

    private val duckDownVolumeRunnable = object : Runnable {
        override fun run() {
            if (mAudioManagerHelper?.mCurrentVolume!! > mAudioManagerHelper?.mTargetVolume!!) {
                mAudioManager?.setStreamVolume(
                    AudioManager.STREAM_MUSIC,
                    mAudioManagerHelper?.mCurrentVolume!! - mAudioManagerHelper?.mStepDownIncrement!!,
                    0
                )
                mAudioManagerHelper?.mCurrentVolume =
                    mAudioManager?.getStreamVolume(AudioManager.STREAM_MUSIC)!!
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
            mAudioManagerHelper?.mCurrentVolume =
                mAudioManager?.getStreamVolume(AudioManager.STREAM_MUSIC)!!
            mAudioManagerHelper?.mOriginalVolume =
                mAudioManager?.getStreamVolume(AudioManager.STREAM_MUSIC)!!
            mHandler?.post(duckDownVolumeRunnable)
        } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
            if (mAudioManagerHelper?.isAudioDucked!!) {
                mAudioManagerHelper?.mTargetVolume = mAudioManagerHelper?.mOriginalVolume!!
                mAudioManagerHelper?.mStepUpIncrement = 1
                mAudioManagerHelper?.mCurrentVolume =
                    mAudioManager?.getStreamVolume(AudioManager.STREAM_MUSIC)!!
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

    fun initCustomAudioFX() {
        try {
            openSLMediaPlayerContextParam = OpenSLMediaPlayerContext.Parameters()
            // openSLMediaPlayerContextParam?.options = OpenSLMediaPlayerContext.OPTION_USE_PRESET_REVERB
            //  openSLMediaPlayerContextParam?.options = OpenSLMediaPlayerContext.OPTION_USE_HQ_EQUALIZER

            openSLMediaPlayerContext =
                OpenSLMediaPlayerContext(mContext, openSLMediaPlayerContextParam)
            mEqualizerDataHelper = EqualizerDataHelper(
                factory,
                mMediaPlayer1!!,
                SharedPrefHelper.getInstance().getBoolean(
                    SharedPrefHelper.Key.IS_EQUALIZER_ACTIVE,
                    false
                ), openSLMediaPlayerContext!!
            )
        } catch (e: UnsupportedOperationException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getEqualizerHelper(): EqualizerDataHelper? {
        return mEqualizerDataHelper
    }

    /*  fun applyMediaPlayerHQ(){
          if (mEqualizerDataHelper==null)
              return
          try {

          }catch (ex:Exception){}
      }*/


    override fun onDestroy() {
        super.onDestroy()
        mApp?.setIsServiceRunning(false)
        mHandler?.removeCallbacks(sendUpdatesToUI)

    }

    /* private class NextSongAsyncTask internal constructor(context: MusicService) : AsyncTask<Void, Void, String>() {

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
     }*/

    fun nextSong() {
        SharedPrefHelper.getInstance().put(SharedPrefHelper.Key.SONG_CURRENT_SEEK_DURATION, 0)
        MainScope().launch(Dispatchers.Main) {
            if (mSongs.size != 1) {

                if (mSongPos < mSongs.size - 1) {
                    mSongPos +=1
                    startSong()
                } else {
                    mSongPos = 0
                    startSong()
                }
            }
        }

    }


    fun previousSong() {
        MainScope().launch(Dispatchers.Main) {


            if (mService!=null && mMediaPlayerPrepared) {
                val currentPos= mMediaPlayer1?.currentPosition
                if (currentPos!! >= 5000) {
                    mMediaPlayer1?.seekTo(0)
                } else {
                    val size= mSongs.size
                    if (size > 1) {
                        if (mSongPos > 0) {
                            mSongPos --
                            startSong()
                        } else {
                            mSongPos = size-1
                            startSong()
                        }
                    }
                }
            }

            }

    }

    fun applyMediaPlayerEQ(songId: Long?) {
        if (mEqualizerDataHelper == null)
            return
        try {
            val thirtyOneHzBand = mEqualizerDataHelper?.getHQEqualizer()?.getBand(31000)
            val sixtyTwoHz = mEqualizerDataHelper?.getHQEqualizer()?.getBand(62000)
            val oneTwentyFive =
                mEqualizerDataHelper?.getHQEqualizer()
                    ?.getBand(125000)
            val twoFiftyHz = mEqualizerDataHelper?.getHQEqualizer()
                ?.getBand(250000)
            val fiveHundredHz =
                mEqualizerDataHelper?.getHQEqualizer()
                    ?.getBand(500000)
            val oneKhz = mEqualizerDataHelper?.getHQEqualizer()
                ?.getBand(1000000)
            val twoKhz = mEqualizerDataHelper?.getHQEqualizer()
                ?.getBand(2000000)
            val fourKhz = mEqualizerDataHelper?.getHQEqualizer()
                ?.getBand(4000000)
            val eightKhz = mEqualizerDataHelper?.getHQEqualizer()
                ?.getBand(8000000)
            val sixteenKhz = mEqualizerDataHelper?.getHQEqualizer()
                ?.getBand(16000000)


            var eqValues = mApp?.getDBAccessHelper()?.getEQValues()

            if (eqValues!![0] == 16) {
                mEqualizerDataHelper?.getHQEqualizer()?.setBandLevel(thirtyOneHzBand!!, 0.toShort())
            } else if (eqValues[0]!! < 16) {

                if (eqValues[0] == 0) {
                    mEqualizerDataHelper?.getHQEqualizer()
                        ?.setBandLevel(thirtyOneHzBand!!, (-1500).toShort())
                } else {
                    mEqualizerDataHelper?.getHQEqualizer()
                        ?.setBandLevel(thirtyOneHzBand!!, (-(16 - eqValues[0]!!) * 100).toShort())
                }

            } else if (eqValues[0]!! > 16) {
                mEqualizerDataHelper?.getHQEqualizer()
                    ?.setBandLevel(thirtyOneHzBand!!, ((eqValues[0]!! - 16) * 100).toShort())
            }

            if (eqValues[1] == 16) {
                mEqualizerDataHelper?.getHQEqualizer()?.setBandLevel(sixtyTwoHz!!, 0.toShort())
            } else if (eqValues[1]!! < 16) {

                if (eqValues[1] == 0) {
                    mEqualizerDataHelper?.getHQEqualizer()
                        ?.setBandLevel(sixtyTwoHz!!, (-1500).toShort())
                } else {
                    mEqualizerDataHelper?.getHQEqualizer()
                        ?.setBandLevel(sixtyTwoHz!!, (-(16 - eqValues[1]!!) * 100).toShort())
                }

            } else if (eqValues[1]!! > 16) {
                mEqualizerDataHelper?.getHQEqualizer()
                    ?.setBandLevel(sixtyTwoHz!!, ((eqValues[1]!! - 16) * 100).toShort())
            }

            if (eqValues[2] == 16) {
                mEqualizerDataHelper?.getHQEqualizer()?.setBandLevel(oneTwentyFive!!, 0.toShort())
            } else if (eqValues[2]!! < 16) {

                if (eqValues[2] == 0) {
                    mEqualizerDataHelper?.getHQEqualizer()
                        ?.setBandLevel(oneTwentyFive!!, (-1500).toShort())
                } else {
                    mEqualizerDataHelper?.getHQEqualizer()
                        ?.setBandLevel(oneTwentyFive!!, (-(16 - eqValues[2]!!) * 100).toShort())
                }

            } else if (eqValues[2]!! > 16) {
                mEqualizerDataHelper?.getHQEqualizer()
                    ?.setBandLevel(oneTwentyFive!!, ((eqValues[2]!! - 16) * 100).toShort())
            }


            if (eqValues[3] == 16) {
                mEqualizerDataHelper?.getHQEqualizer()?.setBandLevel(twoFiftyHz!!, 0.toShort())
            } else if (eqValues[3]!! < 16) {

                if (eqValues[3] == 0) {
                    mEqualizerDataHelper?.getHQEqualizer()
                        ?.setBandLevel(twoFiftyHz!!, (-1500).toShort())
                } else {
                    mEqualizerDataHelper?.getHQEqualizer()
                        ?.setBandLevel(twoFiftyHz!!, (-(16 - eqValues[3]!!) * 100).toShort())
                }

            } else if (eqValues[3]!! > 16) {
                mEqualizerDataHelper?.getHQEqualizer()
                    ?.setBandLevel(twoFiftyHz!!, ((eqValues[3]!! - 16) * 100).toShort())
            }

            if (eqValues[4] == 16) {
                mEqualizerDataHelper?.getHQEqualizer()?.setBandLevel(fiveHundredHz!!, 0.toShort())
            } else if (eqValues[4]!! < 16) {

                if (eqValues[4] == 0) {
                    mEqualizerDataHelper?.getHQEqualizer()
                        ?.setBandLevel(fiveHundredHz!!, (-1500).toShort())
                } else {
                    mEqualizerDataHelper?.getHQEqualizer()
                        ?.setBandLevel(fiveHundredHz!!, (-(16 - eqValues[4]!!) * 100).toShort())
                }

            } else if (eqValues[4]!! > 16) {
                mEqualizerDataHelper?.getHQEqualizer()
                    ?.setBandLevel(fiveHundredHz!!, ((eqValues[4]!! - 16) * 100).toShort())
            }

            if (eqValues[5] == 16) {
                mEqualizerDataHelper?.getHQEqualizer()?.setBandLevel(oneKhz!!, 0.toShort())
            } else if (eqValues[5]!! < 16) {

                if (eqValues[5] == 0) {
                    mEqualizerDataHelper?.getHQEqualizer()
                        ?.setBandLevel(oneKhz!!, (-1500).toShort())
                } else {
                    mEqualizerDataHelper?.getHQEqualizer()
                        ?.setBandLevel(oneKhz!!, (-(16 - eqValues[5]!!) * 100).toShort())
                }

            } else if (eqValues[5]!! > 16) {
                mEqualizerDataHelper?.getHQEqualizer()
                    ?.setBandLevel(oneKhz!!, ((eqValues[5]!! - 16) * 100).toShort())
            }

            if (eqValues[6] == 16) {
                mEqualizerDataHelper?.getHQEqualizer()?.setBandLevel(twoKhz!!, 0.toShort())
            } else if (eqValues[6]!! < 16) {

                if (eqValues[6] == 0) {
                    mEqualizerDataHelper?.getHQEqualizer()
                        ?.setBandLevel(twoKhz!!, (-1500).toShort())
                } else {
                    mEqualizerDataHelper?.getHQEqualizer()
                        ?.setBandLevel(twoKhz!!, (-(16 - eqValues[6]!!) * 100).toShort())
                }

            } else if (eqValues[6]!! > 16) {
                mEqualizerDataHelper?.getHQEqualizer()
                    ?.setBandLevel(twoKhz!!, ((eqValues[6]!! - 16) * 100).toShort())
            }

            if (eqValues[7] == 16) {
                mEqualizerDataHelper?.getHQEqualizer()?.setBandLevel(fourKhz!!, 0.toShort())
            } else if (eqValues[7]!! < 16) {

                if (eqValues[7] == 0) {
                    mEqualizerDataHelper?.getHQEqualizer()
                        ?.setBandLevel(fourKhz!!, (-1500).toShort())
                } else {
                    mEqualizerDataHelper?.getHQEqualizer()
                        ?.setBandLevel(fourKhz!!, (-(16 - eqValues[7]!!) * 100).toShort())
                }

            } else if (eqValues[7]!! > 16) {
                mEqualizerDataHelper?.getHQEqualizer()
                    ?.setBandLevel(fourKhz!!, ((eqValues[7]!! - 16) * 100).toShort())
            }

            if (eqValues[8] == 16) {
                mEqualizerDataHelper?.getHQEqualizer()?.setBandLevel(eightKhz!!, 0.toShort())
            } else if (eqValues[8]!! < 16) {

                if (eqValues[8] == 0) {
                    mEqualizerDataHelper?.getHQEqualizer()
                        ?.setBandLevel(eightKhz!!, (-1500).toShort())
                } else {
                    mEqualizerDataHelper?.getHQEqualizer()
                        ?.setBandLevel(eightKhz!!, (-(16 - eqValues[8]!!) * 100).toShort())
                }

            } else if (eqValues[8]!! > 16) {
                mEqualizerDataHelper?.getHQEqualizer()
                    ?.setBandLevel(eightKhz!!, ((eqValues[8]!! - 16) * 100).toShort())
            }

            if (eqValues[9] == 16) {
                mEqualizerDataHelper?.getHQEqualizer()?.setBandLevel(sixteenKhz!!, 0.toShort())
            } else if (eqValues[9]!! < 16) {

                if (eqValues[9] == 0) {
                    mEqualizerDataHelper?.getHQEqualizer()
                        ?.setBandLevel(sixteenKhz!!, (-1500).toShort())
                } else {
                    mEqualizerDataHelper?.getHQEqualizer()
                        ?.setBandLevel(sixteenKhz!!, (-(16 - eqValues[9]!!) * 100).toShort())
                }

            } else if (eqValues[9]!! > 16) {
                mEqualizerDataHelper?.getHQEqualizer()
                    ?.setBandLevel(sixteenKhz!!, ((eqValues[9]!! - 16) * 100).toShort())
            }

            mEqualizerDataHelper?.getVirtualizer()?.setStrength(eqValues[10]!!.toShort())
            mEqualizerDataHelper?.getBassBoost()?.setStrength(eqValues[11]!!.toShort())

            if (eqValues[13] == 0) {
                mEqualizerDataHelper?.getPresetReverb()?.preset = PresetReverb.PRESET_NONE
            } else if (eqValues[13] == 1) {
                mEqualizerDataHelper?.getPresetReverb()?.preset = PresetReverb.PRESET_LARGEHALL
            } else if (eqValues[13] == 2) {
                mEqualizerDataHelper?.getPresetReverb()?.preset = PresetReverb.PRESET_LARGEROOM
            } else if (eqValues[13] == 3) {
                mEqualizerDataHelper?.getPresetReverb()?.preset = PresetReverb.PRESET_MEDIUMHALL
            } else if (eqValues[13] == 4) {
                mEqualizerDataHelper?.getPresetReverb()?.preset = PresetReverb.PRESET_MEDIUMROOM
            } else if (eqValues[13] == 5) {
                mEqualizerDataHelper?.getPresetReverb()?.preset = PresetReverb.PRESET_SMALLROOM
            } else if (eqValues[13] == 6) {
                mEqualizerDataHelper?.getPresetReverb()?.preset = PresetReverb.PRESET_PLATE
            }

        } catch (ex: Exception) {

        }
    }


    /* private class PrevSongAsyncTask internal constructor(context: MusicService) : AsyncTask<Void, Void, String>() {

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
     }*/


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

    /*inner class PreparedListener:IBasicMediaPlayer.OnPreparedListener{
        override fun onPrepared(mp: IBasicMediaPlayer?) {
            mMediaPlayerPrepared = true
            mMediaPlayer1?.setOnCompletionListener(mOnCompletionListener)
            // mMediaPlayer1?.seekTo(PreferencesHelper.getInstance().getInt(PreferencesHelper.Key.SONG_CURRENT_SEEK_DURATION))

            if (mPlayingForFirstTime) {
                mPlayingForFirstTime = false
            }

            // applyMediaPlayerEQ(getSongDataHelper().getId())
            startPlaying()

            val intent = Intent(Constants.ACTION_UPDATE_NOW_PLAYING_UI)
            intent.putExtra(Constants.UPDATE_UI, true)
            sendBroadcast(intent)
        }

    }*/


}