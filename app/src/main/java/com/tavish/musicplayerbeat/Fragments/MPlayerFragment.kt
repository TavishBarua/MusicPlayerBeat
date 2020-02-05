package com.tavish.musicplayerbeat.Fragments

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.transition.TransitionInflater
import android.util.Log
import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.view.animation.ScaleAnimation
import android.widget.RelativeLayout
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatSeekBar
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.core.widget.ImageViewCompat
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.AppBarLayout
import com.tavish.musicplayerbeat.Activities.EqualizerActivity
import com.tavish.musicplayerbeat.Adapters.PlayerPagerAdapter
import com.tavish.musicplayerbeat.Common
import com.tavish.musicplayerbeat.Helpers.RequestPermissionHandler
import com.tavish.musicplayerbeat.Helpers.SharedPrefHelper
import com.tavish.musicplayerbeat.Helpers.SongManager
import com.tavish.musicplayerbeat.Models.BeatDto
import com.tavish.musicplayerbeat.Models.SongDto
import com.tavish.musicplayerbeat.R
import com.tavish.musicplayerbeat.Utils.Constants
import com.yarolegovich.discretescrollview.DiscreteScrollView
import com.yarolegovich.discretescrollview.transform.ScaleTransformer
import kotlinx.android.synthetic.main.fragment_mplayer.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [MPlayerFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [MPlayerFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MPlayerFragment : Fragment(), View.OnClickListener {

    private var listener: OnFragmentInteractionListener? = null

    private var mView: View? = null

    private lateinit var cardView_main: CardView
    private lateinit var btn_next: AppCompatImageButton
    private lateinit var img_play_pause: ImageViewCompat
    private lateinit var rr_play_pause: RelativeLayout
    private lateinit var btn_repeat: AppCompatImageButton
    private lateinit var btn_equalizer: AppCompatImageButton
    private lateinit var tv_tb_song_title: AppCompatTextView


    private lateinit var tv_current_duration: AppCompatTextView


    private lateinit var tv_total_duration: AppCompatTextView
    private lateinit var mSeekBar: AppCompatSeekBar


    private lateinit var mToolBar: Toolbar

    private lateinit var mAppBarLayout: AppBarLayout

    //private val view_custom_toolbar by binder<View>(R.id.custom_toolbar)
    lateinit var readWriteSongPermissionHandler: RequestPermissionHandler


    var songList: MutableList<BeatDto> = mutableListOf()
    lateinit var songManager: SongManager
    var sharedPreferences: SharedPreferences? = null


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
    private var shuffleFlag: Int? = 0

companion object{
    fun newInstance(pos: Int, songItem: SongDto):MPlayerFragment{
        val playerPagerFragment= MPlayerFragment()
        val bundle=Bundle()
        bundle.putInt("image", pos)
        bundle.putParcelable("transition_name",songItem)
        playerPagerFragment.arguments = bundle

        return playerPagerFragment

    }
}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        postponeEnterTransition();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity?.window?.sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(
                android.R.transition.move
            )
            activity?.window?.sharedElementEnterTransition?.duration=1000
        }
        activity?.window?.sharedElementReturnTransition = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_mplayer, container, false)
        mContext = activity?.applicationContext!!
        mAppBarLayout = mView?.findViewById(R.id.id_toolbar_container)!!
        mToolBar = mView?.findViewById(R.id.toolbar)!!
        btn_next = mView?.findViewById(R.id.btn_next)!!
       // img_play_pause = mView?.findViewById<ImageViewCompat>(R.id.img_play_pause)!!

        rr_play_pause = mView?.findViewById(R.id.ll_play_pause)!!
        btn_repeat = mView?.findViewById(R.id.btn_repeat)!!
        btn_equalizer = mView?.findViewById(R.id.btn_equalizer)!!

        tv_tb_song_title = mToolBar.findViewById(R.id.tv_tb_song_title)
        tv_current_duration = mView?.findViewById(R.id.songCurrentDurationLabel)!!
        tv_total_duration = mView?.findViewById(R.id.songTotalDurationLabel)!!
        mSeekBar = mView?.findViewById(R.id.seekBar1)!!

        mApp = mContext as Common
        if (mApp?.isServiceRunning()!!) {
            mSongs = mApp?.mService?.getSongList()
        } else {
            mSongs = mApp?.getDBAccessHelper()?.getQueue()
        }
        mHandler = Handler()
        mSongViewer = mView?.findViewById(R.id.viewPager)

        initViewPager()
        mSeekBar.setOnSeekBarChangeListener(onSeekBarChangeListener)



        (activity as AppCompatActivity).setSupportActionBar(mToolBar)
        (activity as AppCompatActivity).supportActionBar?.let {
            it.setDisplayShowTitleEnabled(false)
            it.setDisplayHomeAsUpEnabled(true)
        }
        mToolBar.setNavigationOnClickListener {activity?.onBackPressed()}
        val params = mAppBarLayout.layoutParams as RelativeLayout.LayoutParams
        params.topMargin = Common.getStatusBarHeight(mContext!!)
        params.bottomMargin = 0
        mAppBarLayout.layoutParams = params

        rr_play_pause.setOnClickListener(this)
        btn_next.setOnClickListener(this)
        mView?.btn_prev?.setOnClickListener(this)
        btn_repeat.setOnClickListener(this)
        btn_equalizer.setOnClickListener(this)



            return mView
    }



    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.ll_play_pause -> {

                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                if (mApp?.isServiceRunning()!!) {
                    if (mApp?.mService?.isMusicPlaying()!!) {
                        animatePauseToPlay()
                        mHandler?.removeCallbacks (seekBarUpdateRunnable)
                    } else {
                        animatePlayToPause()
                        mHandler?.post (seekBarUpdateRunnable)
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
                CoroutineScope(Dispatchers.Main).launch {
                    mApp?.getPlayBackStarter()?.playPauseFromBottomBar()
                }


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
                SharedPrefHelper.getInstance()
                    .put(SharedPrefHelper.Key.SONG_CURRENT_SEEK_DURATION, 0)

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
                SharedPrefHelper.getInstance()
                    .put(SharedPrefHelper.Key.SONG_CURRENT_SEEK_DURATION, 0)
            }
            R.id.btn_repeat -> {
                when (SharedPrefHelper.getInstance().getInt(
                    SharedPrefHelper.Key.REPEAT_MODE,
                    Constants.REPEAT_OFF
                )) {
                    Constants.REPEAT_OFF -> {
                        if (mApp?.isServiceRunning()!!) {
                            mApp?.mService?.setOriginalOne()
                        }
                        SharedPrefHelper.getInstance()
                            .put(SharedPrefHelper.Key.REPEAT_MODE, Constants.REPEAT_PLAYLIST)
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

            R.id.btn_equalizer -> {
                val intent = Intent(v.context, EqualizerActivity::class.java)
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

    var seekBarUpdateRunnable = object : Runnable {
        override fun run() {
            try {
                if (mApp?.mService != null && mApp?.mService?.mMediaPlayerPrepared!!) {
                    var  currentPosition = mApp?.mService?.mMediaPlayer1?.currentPosition!!
                    val currentPositionInSecs = (currentPosition.div(1000))
                    mSeekBar.progress = currentPositionInSecs

                    tv_current_duration.text =
                        Common.convertMillisToSecs(mSeekBar.progress.times(1000))
                    mHandler?.postDelayed(this, 100)
                }


            } catch (ex: Exception) {
                Log.i(ContentValues.TAG, "MPlayerActivity: seekbar  $ex")
            }
        }
    }

    val onSeekBarChangeListener = object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            try {
                if (mApp?.mService != null && mApp?.mService?.mMediaPlayerPrepared!!) {
                    val currentDuration = mApp?.mService?.mMediaPlayer1?.duration
                    seekBar?.max = currentDuration?.div(1000) as Int
                    if (fromUser) {
                        seekBar?.progress
                    }


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
            (mView?.img_play_pause?.width?.div(2))!!.toFloat(),
            (mView?.img_play_pause?.height?.div(2))!!.toFloat()
        )
        scaleOut.apply {
            duration = 150
            interpolator = AccelerateInterpolator()
        }

        val scaleIn = ScaleAnimation(
            0.0f, 1.0f, 0.0f, 1.0f,
            (mView?.img_play_pause?.width?.div(2))!!.toFloat(),
            (mView?.img_play_pause?.height?.div(2))!!.toFloat()
        )
        scaleIn.apply {
            duration = 150
            interpolator = DecelerateInterpolator()
        }

        scaleOut.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {

            }

            override fun onAnimationEnd(animation: Animation?) {
                mView?.img_play_pause?.apply {

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
                mView?.img_play_pause?.apply {

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

        mView?.img_play_pause?.startAnimation(scaleOut)

    }

    private fun initViewPager() {
        try {
            var abcd  = arguments?.getParcelableArrayList<SongDto>("transition_name")
            //  mViewPager?.visibility = View.INVISIBLE
            mRecyclerViewPagerAdapter = PlayerPagerAdapter(mContext!!)
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
            var pos: Int? = 0


            if (mApp?.isServiceRunning()!!) {
                mSongViewer?.scrollToPosition(mApp?.mService?.mSongPos!!)
                tv_tb_song_title.text = mApp?.mService?.mSong?._title
            }
            //     mViewPager?.setCurrentItem(mApp?.mService?.mSongPos!!, false)

            else {
                pos = SharedPrefHelper.getInstance()
                    .getInt(SharedPrefHelper.Key.CURRENT_SONG_POSITION, 0)
                mSongViewer?.scrollToPosition(pos)
                tv_tb_song_title.text = mSongs!![pos]._title
            }

            var dur: Int? = 0
            if (mApp?.mService != null && mApp?.mService?.mMediaPlayerPrepared!!) {
                dur = mApp?.mService?.mMediaPlayer1?.duration!!
                tv_total_duration.text = Common.convertMillisToSecs(dur)
            }

            /*  val fadeAnimation = FadeAnimation(mVelocityViewPager, 600, 0.0f, 1.0f, DecelerateInterpolator(2.0f))
              fadeAnimation.animate()*/

        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        //Handler().postDelayed({ mSongViewer?.setOffscreenItems(3) }, 500)


    }


    val mPageChangeListener =
        DiscreteScrollView.OnItemChangedListener<PlayerPagerAdapter.SongPickerViewHolder> { p0, pos ->
            if (mApp?.isServiceRunning()!! && mApp?.mService?.getSongList()?.size != 1) {
                if (pos != mApp?.mService?.mSongPos) {
                    mApp?.mService?.setSelectedSong(pos)
                    SharedPrefHelper.getInstance()
                        .put(SharedPrefHelper.Key.SONG_CURRENT_SEEK_DURATION, 0)
                    //  mHandler?.postDelayed({ mApp?.mService?.setSelectedSong(pos) }, 200)
                }
            }
        }

    fun repeatButton() {
        when (SharedPrefHelper.getInstance().getInt(
            SharedPrefHelper.Key.REPEAT_MODE,
            Constants.REPEAT_OFF
        )) {
            Constants.REPEAT_OFF -> btn_repeat.setImageResource(R.drawable.btn_repeat_off)
            Constants.REPEAT_PLAYLIST -> btn_repeat.setImageResource(R.drawable.btn_repeat)
            Constants.REPEAT_SONG -> btn_repeat.setImageResource(R.drawable.btn_repeat_once)
            Constants.SHUFFLE_ON -> btn_repeat.setImageResource(R.drawable.btn_shuffle_on)
        }

    }




    override fun onAttach(context: Context) {
        super.onAttach(context)
       /* if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }*/
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {
        fun onFragmentInteraction(uri: Uri)
    }



}
