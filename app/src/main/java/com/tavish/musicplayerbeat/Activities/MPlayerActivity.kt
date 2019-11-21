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
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.google.android.material.appbar.AppBarLayout
import com.tavish.musicplayerbeat.Adapters.PlayerPagerAdapter
import com.tavish.musicplayerbeat.Common
import com.tavish.musicplayerbeat.Helpers.*
import com.tavish.musicplayerbeat.Models.BeatDto
import com.tavish.musicplayerbeat.Models.SongDto
import com.tavish.musicplayerbeat.R
import com.tavish.musicplayerbeat.Utils.Constants
import com.yarolegovich.discretescrollview.DiscreteScrollView
import com.yarolegovich.discretescrollview.InfiniteScrollAdapter
import com.yarolegovich.discretescrollview.transform.ScaleTransformer
import kotlinx.android.synthetic.main.activity_mplayer.*
import kotlinx.coroutines.*
import java.lang.Runnable


class MPlayerActivity : AppCompatActivity(), View.OnClickListener {


    private var wrapper: InfiniteScrollAdapter<*>? = null
    // private val cardView_hidden by binder<CardView>(R.id.cv_mplayer_hidden)
    private val cardView_main by binder<CardView>(R.id.cv_mplayer_main)
    private val btn_next by binder<ImageButton>(R.id.btn_next)
    private val img_play_pause by binder<ImageView>(R.id.img_play_pause)
    private val rr_play_pause by binder<RelativeLayout>(R.id.ll_play_pause)
    private val btn_repeat by binder<ImageButton>(R.id.btn_repeat)
    private val btn_equalizer by binder<ImageButton>(R.id.btn_equalizer)
    private val toolbar by binder<Toolbar>(R.id.toolbar)
    private lateinit var tv_tb_song_title: AppCompatTextView


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


    private var mRecyclerViewPagerAdapter: PlayerPagerAdapter? = null
    //     private var mViewPager: ViewPager? = null
    private var mSongViewer: DiscreteScrollView? = null
    // Application Context
    private var mApp: Common? = null


    private var mContext: Context? = null
    private var isUserScroll = true
    private var USER_SCROLL: Boolean? = false
    private var CURRENT_POS: Int? = -1
    private var shuffleFlag: Int? = 0

    private var jobStart: Job? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mplayer)

        mFragments = mutableListOf()
        mContext = applicationContext

        tv_tb_song_title = toolbar.findViewById(R.id.tv_tb_song_title)
        /* if(SharedPrefHelper.getInstance().getInt(SharedPrefHelper.Key.SHUFFLE_MODE,Constants.SHUFFLE_OFF)==Constants.SHUFFLE_ON)
         shuffleFlag=1*/
        mApp = mContext as Common
        if (mApp?.isServiceRunning()!!) {
            mSongs = mApp?.mService?.getSongList()
        } else {
            mSongs = mApp?.getDBAccessHelper()?.getQueue()
        }


        mHandler = Handler()
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        mSongViewer = findViewById(R.id.viewPager)

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

        rr_play_pause.setOnClickListener(this)
        btn_next.setOnClickListener(this)
        btn_prev.setOnClickListener(this)
        btn_repeat.setOnClickListener(this)
        btn_equalizer.setOnClickListener(this)

    }


    override fun onDestroy() {
        super.onDestroy()

        mRecyclerViewPagerAdapter = null
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
                    mHandler?.postDelayed(seekBarUpdateRunnable, 500)
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
                var newPos = mSongViewer?.currentItem?.plus(1)
                //   var newPos = wrapper?.getRealPosition(mSongViewer?.currentItem?.plus(1)!!)
                if (newPos!! < mRecyclerViewPagerAdapter?.itemCount!!) {
                    //  mViewPager?.setCurrentItem(newPos, true)
                    mSongViewer?.smoothScrollToPosition(newPos)
                } else {
                    if (mApp?.getSharedPrefHelper()?.getInt(
                            SharedPrefHelper.Key.REPEAT_MODE,
                            Constants.REPEAT_OFF
                        ) == Constants.REPEAT_PLAYLIST
                    )
                        mSongViewer?.scrollToPosition(0)
                    else
                        Toast.makeText(mContext, "No Songs to Skip.", Toast.LENGTH_SHORT).show()
                }

            }

            R.id.btn_prev -> {
                // var newPos = mViewPager?.currentItem?.minus(1)
                var newPos = mSongViewer?.currentItem?.minus(1)
                if (newPos!! > -1) {
                    mSongViewer?.smoothScrollToPosition(newPos)

                    //   mSongViewer?.addScrollStateChangeListener(mPageChangeListener)
                } else {
                    // doubt in this line
                    //mViewPager?.setCurrentItem(0,false)
                    mSongViewer?.smoothScrollToPosition(0)
                }
            }
            R.id.btn_repeat -> {
                when (SharedPrefHelper.getInstance().getInt(SharedPrefHelper.Key.REPEAT_MODE, Constants.REPEAT_OFF)) {
                    Constants.REPEAT_OFF -> {
                        if (mApp?.isServiceRunning()!!) {
                            mApp?.mService?.setOriginalOne()
                        }
                        SharedPrefHelper.getInstance().put(SharedPrefHelper.Key.REPEAT_MODE, Constants.REPEAT_PLAYLIST)
                    }

                    Constants.REPEAT_PLAYLIST -> SharedPrefHelper.getInstance().put(
                        SharedPrefHelper.Key.REPEAT_MODE,
                        Constants.REPEAT_SONG
                    )
                    Constants.REPEAT_SONG -> SharedPrefHelper.getInstance().put(
                        SharedPrefHelper.Key.REPEAT_MODE,
                        Constants.SHUFFLE_ON
                    )
                    Constants.SHUFFLE_ON -> SharedPrefHelper.getInstance().put(
                        SharedPrefHelper.Key.REPEAT_MODE,
                        Constants.REPEAT_OFF
                    )
                }
                repeatButton()
            }

            R.id.btn_equalizer->{
                val intent= Intent(this, EqualizerActivity::class.java)
                startActivity(intent)

            }

            /*  R.id.btn_shuffle ->{
                  if(SharedPrefHelper.getInstance().getInt(SharedPrefHelper.Key.SHUFFLE_MODE, Constants.SHUFFLE_OFF)== Constants.SHUFFLE_OFF){
                      if(mApp?.isServiceRunning()!!){
                          mApp?.mService?.setShuffledOne()
                          shuffleFlag=1
                      }
                      SharedPrefHelper.getInstance().put(SharedPrefHelper.Key.SHUFFLE_MODE,Constants.SHUFFLE_ON)
                  }else{
                      if (mApp?.isServiceRunning()!!){
                          mApp?.mService?.setOriginalOne()
                          shuffleFlag=2
                      }
                      SharedPrefHelper.getInstance().put(SharedPrefHelper.Key.SHUFFLE_MODE,Constants.SHUFFLE_OFF)
                  }

               //   shuffleButton()
              }*/
        }
    }

    fun repeatButton() {
        when (SharedPrefHelper.getInstance().getInt(SharedPrefHelper.Key.REPEAT_MODE, Constants.REPEAT_OFF)) {
            Constants.REPEAT_OFF -> btn_repeat.setImageResource(R.drawable.btn_repeat_off)
            Constants.REPEAT_PLAYLIST -> btn_repeat.setImageResource(R.drawable.btn_repeat)
            Constants.REPEAT_SONG -> btn_repeat.setImageResource(R.drawable.btn_repeat_once)
            Constants.SHUFFLE_ON -> btn_repeat.setImageResource(R.drawable.btn_shuffle_on)
        }

    }

    /* fun shuffleButton(){
         if(SharedPrefHelper.getInstance().getInt(SharedPrefHelper.Key.SHUFFLE_MODE,Constants.SHUFFLE_OFF)==Constants.SHUFFLE_OFF)
             btn_shuffle.setImageResource(R.drawable.btn_shuffle_off)
         //to change :: else part
         else
             btn_shuffle.setImageResource(R.drawable.btn_shuffle_on)
     }*/

    var mUpdateUIReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.hasExtra(Constants.UPDATE_UI)!!) {
                try {
                    /* if(shuffleFlag!!.equals(1)){
                         mSongViewer?.adapter = mRecyclerViewPagerAdapter
                         shuffleFlag =0
                     }*/
                    var newPos = mApp?.mService?.mSongPos

                    //  var currentPos = mViewPager?.currentItem
                    var currentPos = mSongViewer?.currentItem
                    if (currentPos != newPos) {
                        if (newPos!! > 0 && Math.abs(newPos - currentPos!!) <= 5) {
                            //  scrollViewPager(newPos, true, 1, false)
                            // mViewPager?.setCurrentItem(newPos, true)
                            mSongViewer?.smoothScrollToPosition(newPos)
                        } else {
                            // mViewPager?.setCurrentItem(newPos, false)
                            mSongViewer?.scrollToPosition(newPos)
                        }

                        var abc:Int? = 0
                        if(mApp?.mService?.mMediaPlayerPrepared!!){
                            abc = mApp?.mService?.mMediaPlayer1?.currentPosition!!
                        }
                            mSeekBar.max = abc?.div(1000)!!


                        mSeekBar.progress = 0


                        /*if (songInfoBottomSheetDialog != null) {
                            songInfoBottomSheetDialog.getAdapter().notifyDataSetChanged()
                        }*/
                    }
                    mHandler?.post(seekBarUpdateRunnable)
                    img_play_pause.setImageResource(R.drawable.pause)


                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            } else if (intent.hasExtra(Constants.ACTION_PLAY_PAUSE)) {
                if (mApp?.isServiceRunning()!!) {
                    if (mApp?.mService?.isMusicPlaying()!!) {
                        img_play_pause.setImageResource(R.drawable.pause)
                        mHandler?.post(seekBarUpdateRunnable)
                        // mHandler?.removeCallbacks(seekBarUpdateRunnable)
                    } else {
                        img_play_pause.setImageResource(R.drawable.play)
                        mHandler?.removeCallbacks(seekBarUpdateRunnable)
                    }
                }
            }
            tv_tb_song_title.text = mApp?.mService?.mSong?._title

            if(mApp?.mService?.mMediaPlayerPrepared!!)
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
        repeatButton()
        // shuffleButton()
    }


    var seekBarUpdateRunnable: Runnable = object : Runnable {
        override fun run() {
            try {
                var currentPosition=0;
                    if(mApp?.mService?.mMediaPlayerPrepared!!)
                    currentPosition = mApp?.mService?.mMediaPlayer1?.currentPosition!!
                    val currentPositionInSecs = (currentPosition.div(1000)) as Int
                    mSeekBar.progress = currentPositionInSecs

                    tv_current_duration.text =
                        Common.convertMillisToSecs(mSeekBar.progress.times(1000))
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
                    if (fromUser)
                        mSeekBar.progress
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) {

        }

        override fun onStopTrackingTouch(seekBar: SeekBar?) {
            val seekBarPos = seekBar?.progress!!
            if (mApp?.isServiceRunning()!!)
                mApp?.mService?.mMediaPlayer1?.seekTo(seekBarPos.times(1000))

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
            mRecyclerViewPagerAdapter = PlayerPagerAdapter(this)
            mSongViewer?.apply {
                mSongViewer?.adapter = mRecyclerViewPagerAdapter
                setOffscreenItems(3)
                setItemTransitionTimeMillis(70)
                setItemTransformer(ScaleTransformer.Builder().setMinScale(0.8f).build())
                //  addScrollStateChangeListener(mPageChangeListener)
                addOnItemChangedListener(mPageChangeListener)
                //  addOnPageChangeListener(mPageChangeListener)
            }

            /* mViewPager?.apply {
                 adapter = mRecyclerViewPagerAdapter
               //  clipChildren=false
                 offscreenPageLimit = 0

                 addOnPageChangeListener(mPageChangeListener)
                 //setPageTransformer(false, CarouselEffectTransformer(this@MPlayerActivity))
             }*/
            /*  val intent= Intent().extras
              tv_tb_song_title.text = intent.getString("song_name")*/

            //  LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver, IntentFilter("song_dto"))
            tv_tb_song_title.text = mApp?.mService?.mSong?._title

            var dur:Int? = 0
            if(mApp?.mService?.mMediaPlayerPrepared!!){
                dur = mApp?.mService?.mMediaPlayer1?.duration!!
            }

            tv_total_duration.text = Common.convertMillisToSecs(dur!!)

            if (mApp?.isServiceRunning()!!)
            //     mViewPager?.setCurrentItem(mApp?.mService?.mSongPos!!, false)
                mSongViewer?.scrollToPosition(mApp?.mService?.mSongPos!!)
            else {
                val pos = SharedPrefHelper.getInstance().getInt(SharedPrefHelper.Key.CURRENT_SONG_POSITION, 0)
                mSongViewer?.scrollToPosition(pos)
            }


            /*  val fadeAnimation = FadeAnimation(mVelocityViewPager, 600, 0.0f, 1.0f, DecelerateInterpolator(2.0f))
              fadeAnimation.animate()*/

        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        //Handler().postDelayed({ mSongViewer?.setOffscreenItems(3) }, 500)


    }

    /*  var mBroadcastReceiver= object: BroadcastReceiver() {
          override fun onReceive(context: Context?, intent: Intent?) {
             val name = intent?.getStringExtra("song_name")
              tv_tb_song_title.text = name
          }
      }*/


    /* val mPageChangeListener = object :DiscreteScrollView.ScrollStateChangeListener<PlayerPagerAdapter.SongPickerViewHolder> {
         override fun onScroll(
             position: Float,
             p1: Int,
             p2: Int,
             p3: PlayerPagerAdapter.SongPickerViewHolder?,
             p4: PlayerPagerAdapter.SongPickerViewHolder?
         ) {
             if(position==1.0f|| position==-1.0f||position==0.0f)
                 USER_SCROLL=true
         }

         override fun onScrollEnd(p0: PlayerPagerAdapter.SongPickerViewHolder, pos: Int) {
             if (mApp?.isServiceRunning()!! && mApp?.mService?.getSongList()?.size != 1) {
                 if (pos != mApp?.mService?.mSongPos) {
                     if(USER_SCROLL!!)
                     mHandler?.postDelayed({ mApp?.mService?.setSelectedSong(pos) }, 500)

                 }
             }
         }

         override fun onScrollStart(p0: PlayerPagerAdapter.SongPickerViewHolder, pos: Int) {
         }
     }*/

    val mPageChangeListener =
        object : DiscreteScrollView.OnItemChangedListener<PlayerPagerAdapter.SongPickerViewHolder> {
            override fun onCurrentItemChanged(p0: PlayerPagerAdapter.SongPickerViewHolder?, pos: Int) {

                if (mApp?.isServiceRunning()!! && mApp?.mService?.getSongList()?.size != 1) {
                    if (pos != mApp?.mService?.mSongPos) {
                        mApp?.mService?.setSelectedSong(pos)
                        // mHandler?.postDelayed({ mApp?.mService?.setSelectedSong(pos) }, 500)
                    }
                }
            }

        }


}
