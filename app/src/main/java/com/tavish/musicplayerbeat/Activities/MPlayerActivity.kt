package com.tavish.musicplayerbeat.Activities

import android.content.*
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.util.Log
import android.view.HapticFeedbackConstants
import android.view.View
import android.view.animation.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.google.android.material.appbar.AppBarLayout
import com.tavish.musicplayerbeat.Adapters.PlayerPagerAdapter
import com.tavish.musicplayerbeat.Common
import com.tavish.musicplayerbeat.Helpers.*
import com.tavish.musicplayerbeat.Models.BeatDto
import com.tavish.musicplayerbeat.Models.SongDto
import com.tavish.musicplayerbeat.R
import com.tavish.musicplayerbeat.Utils.Constants
import kotlinx.coroutines.*
import java.lang.Runnable


class MPlayerActivity : AppCompatActivity(), View.OnClickListener {


    // private val cardView_hidden by binder<CardView>(R.id.cv_mplayer_hidden)
    private val cardView_main by binder<CardView>(R.id.cv_mplayer_main)
    private val btn_next by binder<ImageButton>(R.id.btn_next)
    private val img_play_pause by binder<ImageView>(R.id.img_play_pause)

    private val ll_play_pause by binder<LinearLayoutCompat>(R.id.ll_play_pause)


    private val tv_current_duration by binder<TextView>(R.id.songCurrentDurationLabel)
    private val tv_total_duration by binder<TextView>(R.id.songTotalDurationLabel)


    private val mSeekBar by binder<SeekBar>(R.id.seekBar1)

    private val mToolBar by binder<Toolbar>(R.id.toolbar)

    private val mAppBarLayout by binder<AppBarLayout>(R.id.id_toolbar_container)


    //private val view_custom_toolbar by binder<View>(R.id.custom_toolbar)
    lateinit var readWriteSongPermissionHandler: RequestPermissionHandler
    var songList: MutableList<BeatDto> = mutableListOf()
    lateinit var songManager: SongManager


    var sharedPreferences: SharedPreferences? = null
    lateinit var editor: SharedPreferences.Editor
    var file = Environment.getDataDirectory()
    val storage_check_pref = "intent_memory"
    val song_list_pref = "intent_songs"
    var current_memory: Float = 0.0f

    var mFragments: MutableList<Fragment>? = null
    var mSongs: MutableList<SongDto>? = null


    private var mHandler: Handler? = null
    private var mViewPagerAdapter: PlayerPagerAdapter? = null
    private var mViewPager: ViewPager? = null


    // Application Context
    private var mApp: Common? = null
    private var mContext: Context? = null
    private var isUserScroll = true

    private var jobStart: Job? = null


    companion object {
        val mediaPlayer: MediaPlayer = MediaPlayer()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mplayer)

        mFragments = mutableListOf()
        mContext = applicationContext
        mApp = mContext as Common
        if (mApp?.isServiceRunning()!!) {
            mSongs = mApp?.mService?.getSongList()
        } else {
            mSongs = mApp?.getDBAccessHelper()?.getQueue()
        }

        mHandler = Handler()
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        mViewPager = findViewById(R.id.viewPager)





        initViewPager()

        mSeekBar.setOnSeekBarChangeListener(onSeekBarChangeListener)

        /*  cardView_main.setOnTouchListener(object : OnSwipeTouchListener(this) {
              override fun onSwipeTop() {
                  cardView_hidden.visibility = View.VISIBLE
              }
          })*/

        val params = mAppBarLayout.getLayoutParams() as RelativeLayout.LayoutParams
        params.topMargin = Common.getStatusBarHeight(this)
        params.bottomMargin = 0
        mAppBarLayout.layoutParams = params

        ll_play_pause.setOnClickListener(this)
    }


    override fun onDestroy() {
        super.onDestroy()

        mViewPagerAdapter = null
        Log.d("DESTROYED", "DESTROYED")

    }


    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.ll_play_pause -> {

                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                if (mApp?.isServiceRunning()!!) {
                    if (mApp?.mService?.isMusicPlaying()!!) {
                        animatePauseToPlay()
                        mHandler?.removeCallbacks(seekBarUpdateRunnable)
                    } else {
                        animatePlayToPause()
                        mHandler?.post(seekBarUpdateRunnable)
                    }
                } else {
                    animatePlayToPause()
                    mHandler?.postDelayed(seekBarUpdateRunnable, 1500)
                }

                /* @SuppressLint("StaticFieldLeak")
                 object : AsyncTask<Void, Void, Void>() {
                     override fun doInBackground(vararg params: Void?): Void? {
                         mApp?.getPlayBackStarter()?.playPauseFromBottomBar()
                         return null
                     }
                 }*/
                mApp?.getPlayBackStarter()?.playPauseFromBottomBar()

            }

            R.id.btn_next -> {


            }


        }
    }

    var mUpdateUIReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.hasExtra(Constants.UPDATE_UI)!!) {
                try {
                    var newPos = mApp?.mService?.mSongPos
                    var currentPos = mViewPager?.currentItem
                    if(currentPos != newPos){
                        if(newPos!! > 0 && Math.abs(newPos-currentPos!!) <=5){
                            /*scrollViewPager(newPosition, true, 1, false)*/
                        }else{
                            mViewPager?.setCurrentItem(newPos, false)
                        }

                        mSeekBar.max  = mApp?.mService?.mMediaPlayer1?.duration?.div(1000)!!
                        mSeekBar.progress = 0


                        /*if (songInfoBottomSheetDialog != null) {
                            songInfoBottomSheetDialog.getAdapter().notifyDataSetChanged()
                        }*/
                    }
                    mHandler?.post(seekBarUpdateRunnable)
                    img_play_pause.setImageResource(R.drawable.pause)
                } catch (ex: Exception) {
                }
            }else if(intent.hasExtra(Constants.ACTION_PLAY_PAUSE)){
                if(mApp?.isServiceRunning()!!){
                    if(mApp?.mService?.isMusicPlaying()!!){
                        img_play_pause.setImageResource(R.drawable.pause)
                        mHandler?.removeCallbacks(seekBarUpdateRunnable)
                    }else{
                        img_play_pause.setImageResource(R.drawable.play)
                        mHandler?.post(seekBarUpdateRunnable)
                    }
                }
            }
            tv_total_duration.text = Common.convertMillisToSecs(mApp?.mService?.mMediaPlayer1?.duration!!)
        }


    }


    override fun onStart() {
        super.onStart()
        registerReceiver(mUpdateUIReceiver, IntentFilter(Constants.ACTION_UPDATE_NOW_PLAYING_UI))
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(mUpdateUIReceiver)
    }

    override fun onResume() {
        super.onResume()


        if (mApp?.isServiceRunning()!!) {
            if (mApp?.mService?.isMusicPlaying()!!) {
                img_play_pause.setImageResource(R.drawable.pause)
                mHandler?.post(seekBarUpdateRunnable)
            } else {
                img_play_pause.setImageResource(R.drawable.play)
                mHandler?.removeCallbacks(seekBarUpdateRunnable)
            }
        } else {
            img_play_pause.setImageResource(R.drawable.play)
        }


    }


    var seekBarUpdateRunnable: Runnable = object : Runnable {
        override fun run() {
            try {
                val currentPosition = mApp?.mService?.mMediaPlayer1?.currentPosition
                val currentPositionInSecs = (currentPosition?.div(1000)) as Int
                mSeekBar.progress = currentPositionInSecs

                tv_current_duration.text = Common.convertMillisToSecs(mSeekBar.progress.times(1000))
                mHandler?.postDelayed(this, 100)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

    val onSeekBarChangeListener = object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            try {
                if (mApp?.mService?.mMediaPlayerPrepared!!) {
                    val currentDuration = mApp?.mService?.mMediaPlayer1?.duration
                    mSeekBar.max = currentDuration?.div(1000) as Int
                    if(fromUser)
                        mSeekBar.progress
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) {

        }

        override fun onStopTrackingTouch(seekBar: SeekBar?) {

        }

    }

    private fun animatePauseToPlay() {
        playPauseGame(R.drawable.play)
    }

    private fun animatePlayToPause() {

        playPauseGame(R.drawable.pause)
    }

    fun playPauseGame(resId: Int) {
        //Scale out the pause button.
        val scaleOut = ScaleAnimation(
            1.0f, 0.0f, 1.0f, 0.0f,
            (img_play_pause.width / 2).toFloat(),
            (img_play_pause.height / 2).toFloat()
        )
        scaleOut.apply {
            duration = 150
            interpolator = AccelerateInterpolator()
        }

        val scaleIn = ScaleAnimation(
            0.0f, 1.0f, 0.0f, 1.0f,
            (img_play_pause.width / 2).toFloat(),
            (img_play_pause.height / 2).toFloat()
        )
        scaleIn.apply {
            duration = 150
            interpolator = DecelerateInterpolator()
        }

        scaleOut.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {

            }

            override fun onAnimationEnd(animation: Animation?) {
                img_play_pause.apply {

                    setImageResource(resId)
                    if (resId == R.drawable.play)
                        setPadding(0, 0, -5, 0)
                    else
                        setPadding(0, 0, 0, 0)

                    startAnimation(scaleIn)
                }
            }

            override fun onAnimationStart(animation: Animation?) {

            }
        })

        scaleIn.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {
            }

            override fun onAnimationEnd(animation: Animation?) {
                img_play_pause.apply {

                    scaleX = 1.0f
                    scaleY = 1.0f
                    id = resId
                    scaleX = 1.2f
                    scaleY = 1.2f
                }
            }

            override fun onAnimationStart(animation: Animation?) {
            }
        })

        img_play_pause.startAnimation(scaleOut)

    }


    private fun initViewPager() {
        try {
            //  mViewPager?.visibility = View.INVISIBLE
            mViewPagerAdapter = PlayerPagerAdapter(this, supportFragmentManager)
            mViewPager?.apply {
                adapter = mViewPagerAdapter
                offscreenPageLimit = 0
                addOnPageChangeListener(mPageChangeListener)
            }
            tv_total_duration.text = Common.convertMillisToSecs(mApp?.mService?.mMediaPlayer1?.duration!!)
            if (mApp?.isServiceRunning()!!)
                mViewPager?.setCurrentItem(mApp?.mService?.mSongPos!!, false)
            else {
                val pos = SharedPrefHelper.getInstance().getInt(SharedPrefHelper.Key.CURRENT_SONG_POSITION, 0)
                mViewPager?.setCurrentItem(pos, false)
            }

            /*  val fadeAnimation = FadeAnimation(mVelocityViewPager, 600, 0.0f, 1.0f, DecelerateInterpolator(2.0f))
              fadeAnimation.animate()*/

        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        Handler().postDelayed({ mViewPager?.offscreenPageLimit = 10 }, 1000)


    }

    val mPageChangeListener = object : ViewPager.OnPageChangeListener {
        override fun onPageScrollStateChanged(scrollState: Int) {
            if (scrollState == ViewPager.SCROLL_STATE_DRAGGING) isUserScroll = true
        }

        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            if (mApp?.isServiceRunning()!! && mApp?.mService?.getSongList()?.size !== 1) {
                if (positionOffset == 0.0f && position != mApp?.mService?.mSongPos) {
                    if (isUserScroll) {
                        mHandler?.postDelayed({ mApp?.mService?.setSelectedSong(position) }, 200)
                    }
                }
            }
        }

        override fun onPageSelected(position: Int) {

        }


    }


    fun toolbarClick(view: View) {
        when (view.id) {
            /*  R.id.btn_back ->  { val intent= Intent(this, MainActivity::class.java)
                  startActivity(intent)}*/
        }
    }

    fun slideUp(view: View) {
        var animate = TranslateAnimation(0f, 0f, (view.height).toFloat(), 0f);
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.startAnimation(animate);
    }


}
