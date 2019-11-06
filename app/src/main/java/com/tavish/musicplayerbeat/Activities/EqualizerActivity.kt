package com.tavish.musicplayerbeat.Activities

import android.app.backup.SharedPreferencesBackupHelper
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.HapticFeedbackConstants
import android.view.Menu
import android.widget.CompoundButton
import android.widget.SeekBar
import android.widget.Spinner
import android.widget.Switch
import androidx.appcompat.widget.*
import androidx.core.view.MenuItemCompat
import com.tavish.musicplayerbeat.Common
import com.tavish.musicplayerbeat.Helpers.SharedPrefHelper
import com.tavish.musicplayerbeat.Helpers.binder
import com.tavish.musicplayerbeat.R

class EqualizerActivity : AppCompatActivity() {


    companion object {
        private val NUM_BAND_VIEWS = 10
        private val SEEKBAR_MAX = 1000
    }

    private var bassBoosterLevel: Int? = null
    private var virtualizerLevel: Int? = null
    private var preAmpLevel: Int? = null

    private var mSpinnerPreset: Spinner? = null
    private var mSeekBarBassBoost: AppCompatSeekBar? = null
    private var mSeekBarVirtualizer: AppCompatSeekBar? = null
 //   private var mSeekBarPresetReverb: AppCompatSeekBar? = null
    private var btn_toggleEQ: SwitchCompat? = null


    private var thirtyOneHzLevel = 16
    private var sixtyTwoHzLevel = 16
    private var oneHunderedTwentyFiveHzLevel = 16
    private var twoHundredFiftyHzLevel = 16
    private var fiveHundredHzLevel = 16
    private var oneKHzLevel = 16
    private var twoKHzLevel = 16
    private var fourKHzLevel = 16
    private var eightKHzLevel = 16
    private var sixteenKHzLevel = 16

    private var mToolbar: Toolbar? = null
    private var mContext: Context? = null
    private var mApp: Common? = null


    /*   private var mTextViewBandLevels: Array<AppCompatTextView>? = null
       private var mSeekBarBandLevels: Array<AppCompatSeekBar>? = null*/
    private var mSeekBarPreAmpLevel: SeekBar? = null

    private val mThirtyOneHzSeekBar by binder<SeekBar>(R.id.seekbar_equalizer_band_0)
    private val mSixtyTwoHzSeekBar by binder<SeekBar>(R.id.seekbar_equalizer_band_1)
    private val mOneHunderedTwentyFiveHzSeekBar by binder<SeekBar>(R.id.seekbar_equalizer_band_2)
    private val mTwoHundredFiftyHzSeekBar by binder<SeekBar>(R.id.seekbar_equalizer_band_3)
    private val mFiveHundredHzSeekBar by binder<SeekBar>(R.id.seekbar_equalizer_band_4)
    private val mOneKHzSeekBar by binder<SeekBar>(R.id.seekbar_equalizer_band_5)
    private val mTwoKHzSeekBar by binder<SeekBar>(R.id.seekbar_equalizer_band_6)
    private val mFourKHzSeekBar by binder<SeekBar>(R.id.seekbar_equalizer_band_7)
    private val mEightKHzSeekBar by binder<SeekBar>(R.id.seekbar_equalizer_band_8)
    private val mSixteenKHzSeekBar by binder<SeekBar>(R.id.seekbar_equalizer_band_9)


    private var mSeekBarListener: SeekBar.OnSeekBarChangeListener? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_equalizer)
        mContext = applicationContext
        mApp = mContext?.applicationContext as Common
        /* mTextViewBandLevels=Array(NUM_BAND_VIEWS){AppCompatTextView(this)}
         mSeekBarBandLevels=Array(NUM_BAND_VIEWS){AppCompatSeekBar(this)}*/



        mToolbar = findViewById(R.id.eq_toolbar)
        setSupportActionBar(mToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setTitle("EQUALIZER")
        supportActionBar?.setDisplayShowTitleEnabled(true)
        mToolbar?.setNavigationOnClickListener { v -> onBackPressed() }
        obtainPalleteView()
        mSeekBarListener = object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, seekBarProg: Int, fromUser: Boolean) {
                when (seekBar?.id) {
                    //31Hz
                    R.id.seekbar_equalizer_band_0 -> {
                        try {
                            val thirtyOneHz = mApp?.mService?.getEqualizerHelper()?.getHQEqualizer()
                                ?.getBand(31000)
                            seekBarValueChanger(seekBarProg, thirtyOneHz)
                        } catch (ex: Exception) {
                            ex.printStackTrace()
                        }

                        if ((seekBarProg == 31 || seekBarProg == 0) && fromUser) {
                            seekBar.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                        }

                        thirtyOneHzLevel = seekBarProg
                    }

                    //62Hz
                    R.id.seekbar_equalizer_band_1 -> {
                        try {
                            val sixtyTwoHz = mApp?.mService?.getEqualizerHelper()?.getHQEqualizer()
                                ?.getBand(62000)
                            seekBarValueChanger(seekBarProg, sixtyTwoHz)
                        } catch (ex: Exception) {
                            ex.printStackTrace()
                        }

                        if ((seekBarProg == 31 || seekBarProg == 0) && fromUser) {
                            seekBar.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                        }

                        sixtyTwoHzLevel = seekBarProg
                    }

                    //125Hz
                    R.id.seekbar_equalizer_band_2 -> {
                        try {
                            val oneTwentyFive =
                                mApp?.mService?.getEqualizerHelper()?.getHQEqualizer()
                                    ?.getBand(125000)
                            seekBarValueChanger(seekBarProg, oneTwentyFive)
                        } catch (ex: Exception) {
                            ex.printStackTrace()
                        }

                        if ((seekBarProg == 31 || seekBarProg == 0) && fromUser) {
                            seekBar.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                        }

                        oneHunderedTwentyFiveHzLevel = seekBarProg
                    }

                    //250KHz
                    R.id.seekbar_equalizer_band_3 -> {
                        try {
                            val twoFiftyHz = mApp?.mService?.getEqualizerHelper()?.getHQEqualizer()
                                ?.getBand(250000)
                            seekBarValueChanger(seekBarProg, twoFiftyHz)
                        } catch (ex: Exception) {
                            ex.printStackTrace()
                        }

                        if ((seekBarProg == 31 || seekBarProg == 0) && fromUser) {
                            seekBar.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                        }

                        twoHundredFiftyHzLevel = seekBarProg
                    }

                    //500KHz
                    R.id.seekbar_equalizer_band_4 -> {
                        try {
                            val fiveHundredHz =
                                mApp?.mService?.getEqualizerHelper()?.getHQEqualizer()
                                    ?.getBand(500000)
                            seekBarValueChanger(seekBarProg, fiveHundredHz)
                        } catch (ex: Exception) {
                            ex.printStackTrace()
                        }

                        if ((seekBarProg == 31 || seekBarProg == 0) && fromUser) {
                            seekBar.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                        }

                        fiveHundredHzLevel = seekBarProg
                    }

                    //1KHz
                    R.id.seekbar_equalizer_band_5 -> {
                        try {
                            val oneKhz = mApp?.mService?.getEqualizerHelper()?.getHQEqualizer()
                                ?.getBand(1000000)
                            seekBarValueChanger(seekBarProg, oneKhz)
                        } catch (ex: Exception) {
                            ex.printStackTrace()
                        }

                        if ((seekBarProg == 31 || seekBarProg == 0) && fromUser) {
                            seekBar.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                        }

                        oneKHzLevel = seekBarProg
                    }

                    //2KHz
                    R.id.seekbar_equalizer_band_6 -> {
                        try {
                            val twoKhz = mApp?.mService?.getEqualizerHelper()?.getHQEqualizer()
                                ?.getBand(2000000)
                            seekBarValueChanger(seekBarProg, twoKhz)
                        } catch (ex: Exception) {
                            ex.printStackTrace()
                        }

                        if ((seekBarProg == 31 || seekBarProg == 0) && fromUser) {
                            seekBar.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                        }

                        twoKHzLevel = seekBarProg
                    }

                    //4KHz
                    R.id.seekbar_equalizer_band_7 -> {
                        try {
                            val fourKhz = mApp?.mService?.getEqualizerHelper()?.getHQEqualizer()
                                ?.getBand(4000000)
                            seekBarValueChanger(seekBarProg, fourKhz)
                        } catch (ex: Exception) {
                            ex.printStackTrace()
                        }

                        if ((seekBarProg == 31 || seekBarProg == 0) && fromUser) {
                            seekBar.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                        }

                        fourKHzLevel = seekBarProg
                    }

                    //8KHz
                    R.id.seekbar_equalizer_band_8 -> {
                        try {
                            val eightKhz = mApp?.mService?.getEqualizerHelper()?.getHQEqualizer()
                                ?.getBand(8000000)
                            seekBarValueChanger(seekBarProg, eightKhz)
                        } catch (ex: Exception) {
                            ex.printStackTrace()
                        }

                        if ((seekBarProg == 31 || seekBarProg == 0) && fromUser) {
                            seekBar.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                        }

                        eightKHzLevel = seekBarProg
                    }

                    //16KHz
                    R.id.seekbar_equalizer_band_9 -> {
                        try {
                            val sixteenKhz = mApp?.mService?.getEqualizerHelper()?.getHQEqualizer()
                                ?.getBand(16000000)
                            seekBarValueChanger(seekBarProg, sixteenKhz)
                        } catch (ex: Exception) {
                            ex.printStackTrace()
                        }

                        if ((seekBarProg == 31 || seekBarProg == 0) && fromUser) {
                            seekBar.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                        }

                        sixteenKHzLevel = seekBarProg
                    }

                    R.id.seekbar_bassbooster -> {
                        bassBoosterLevel = seekBarProg
                        if (mApp?.isServiceRunning()!!) {
                            mApp?.mService?.getEqualizerHelper()?.getBassBoost()
                                ?.setStrength(bassBoosterLevel!!.toShort())
                        }
                        if ((seekBarProg == 5 || seekBarProg == 990) && fromUser) {
                            seekBar.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                        }
                    }

                    R.id.seekbar_virtualizer -> {
                        virtualizerLevel = seekBarProg
                        if (mApp?.isServiceRunning()!!) {
                            mApp?.mService?.getEqualizerHelper()?.getVirtualizer()
                                ?.setStrength(virtualizerLevel!!.toShort())
                        }
                        if ((seekBarProg == 5 || seekBarProg == 990) && fromUser) {
                            seekBar.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                        }
                    }

                    R.id.seekbar_preamp -> {
                        preAmpLevel = seekBarProg
                        if (mApp?.isServiceRunning()!!) {
                           // mApp?.mService?.getEqualizerHelper()?.getPreAmp()?.level= preAmpLevel!!.toFloat()
                            if (seekBarProg == 3) {
                                mApp?.mService?.getEqualizerHelper()?.getPreAmp()?.level= 0.0F
                            } else if (seekBarProg != 3) {
                                if (seekBarProg == 0) {
                                    mApp?.mService?.getEqualizerHelper()?.getPreAmp()?.level= -3.0F
                                } else
// to be changed: DANGER
                                    mApp?.mService?.getEqualizerHelper()?.getPreAmp()?.level=  2.5F
                            }
                        }
                        if ((seekBarProg == 0 || seekBarProg == 6) && fromUser) {
                            seekBar.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                        }


                    }

                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }


        }


        mThirtyOneHzSeekBar.setOnSeekBarChangeListener(mSeekBarListener)
        mSixtyTwoHzSeekBar.setOnSeekBarChangeListener(mSeekBarListener)
        mOneHunderedTwentyFiveHzSeekBar.setOnSeekBarChangeListener(mSeekBarListener)
        mTwoHundredFiftyHzSeekBar.setOnSeekBarChangeListener(mSeekBarListener)
        mFiveHundredHzSeekBar.setOnSeekBarChangeListener(mSeekBarListener)
        mOneKHzSeekBar.setOnSeekBarChangeListener(mSeekBarListener)
        mTwoKHzSeekBar.setOnSeekBarChangeListener(mSeekBarListener)
        mFourKHzSeekBar.setOnSeekBarChangeListener(mSeekBarListener)
        mEightKHzSeekBar.setOnSeekBarChangeListener(mSeekBarListener)
        mSixteenKHzSeekBar.setOnSeekBarChangeListener(mSeekBarListener)
        mSeekBarBassBoost?.setOnSeekBarChangeListener(mSeekBarListener)
        mSeekBarVirtualizer?.setOnSeekBarChangeListener(mSeekBarListener)
        mSeekBarPreAmpLevel?.setOnSeekBarChangeListener(mSeekBarListener)


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toggle_switch_menu, menu)
        val menuItem = menu?.findItem(R.id.myswitch)
        val view = menuItem?.actionView
        btn_toggleEQ = view?.findViewById(R.id.switchButton)
        btn_toggleEQ?.apply {
            //  isChecked = SharedPrefHelper.getInstance().getBoolean(SharedPrefHelper.Key.IS_EQUALIZER_ACTIVE, false)
            setOnCheckedChangeListener(eqEnableState);

        }
        return true
    }

    fun obtainPalleteView() {

        mSpinnerPreset = findViewById(R.id.spinner_equalizerb_preset)


        /*mTextViewBandLevels?.apply {
            get(0).findViewById<AppCompatTextView>(R.id.textview_equalizer_band_0)  //31 Hz
            get(1).findViewById<AppCompatTextView>(R.id.textview_equalizer_band_1)  //62 Hz
            get(2).findViewById<AppCompatTextView>(R.id.textview_equalizer_band_2)  //125 Hz
            get(3).findViewById<AppCompatTextView>(R.id.textview_equalizer_band_3)  //250 Hz
            get(4).findViewById<AppCompatTextView>(R.id.textview_equalizer_band_4)  //500 Hz
            get(5).findViewById<AppCompatTextView>(R.id.textview_equalizer_band_5)  //1 KHz
            get(6).findViewById<AppCompatTextView>(R.id.textview_equalizer_band_6)  //2 KHz
            get(7).findViewById<AppCompatTextView>(R.id.textview_equalizer_band_7)  //4 KHz
            get(8).findViewById<AppCompatTextView>(R.id.textview_equalizer_band_8)  //8 KHz
            get(9).findViewById<AppCompatTextView>(R.id.textview_equalizer_band_9)  //16 KHz
        }
*/
        /* mSeekBarBandLevels?.apply {
             get(0).findViewById<AppCompatSeekBar>(R.id.seekbar_equalizer_band_0)  //31 Hz
             get(1).findViewById<AppCompatSeekBar>(R.id.seekbar_equalizer_band_1)  //62 Hz
             get(2).findViewById<AppCompatSeekBar>(R.id.seekbar_equalizer_band_2)  //125 Hz
             get(3).findViewById<AppCompatSeekBar>(R.id.seekbar_equalizer_band_3)  //250 Hz
             get(4).findViewById<AppCompatSeekBar>(R.id.seekbar_equalizer_band_4)  //500 Hz
             get(5).findViewById<AppCompatSeekBar>(R.id.seekbar_equalizer_band_5)  //1 KHz
             get(6).findViewById<AppCompatSeekBar>(R.id.seekbar_equalizer_band_6)  //2 KHz
             get(7).findViewById<AppCompatSeekBar>(R.id.seekbar_equalizer_band_7)  //4 KHz
             get(8).findViewById<AppCompatSeekBar>(R.id.seekbar_equalizer_band_8)  //8 KHz
             get(9).findViewById<AppCompatSeekBar>(R.id.seekbar_equalizer_band_9)  //16 KHz
         }*/
        mSeekBarVirtualizer = findViewById(R.id.seekbar_virtualizer)
        mSeekBarBassBoost = findViewById(R.id.seekbar_bassbooster)
        mSeekBarPreAmpLevel = findViewById(R.id.seekbar_preamp)
    }

    val eqEnableState = object : CompoundButton.OnCheckedChangeListener {
        override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {

            if (btn_toggleEQ?.isChecked!!) {
                SharedPrefHelper.getInstance()
                    .put(SharedPrefHelper.Key.IS_EQUALIZER_ACTIVE, true)
                if (mApp?.isServiceRunning()!!) {
                    mApp?.mService?.getEqualizerHelper()?.createHQEqualizer()
                    mApp?.mService?.getEqualizerHelper()?.createBassBoost()
                    mApp?.mService?.getEqualizerHelper()?.createHQVizualizer()
                    mApp?.mService?.getEqualizerHelper()?.createVirtualizer()
                    mApp?.mService?.getEqualizerHelper()?.createPreAmp()

                    mApp?.mService?.getEqualizerHelper()?.apply {
                        getBassBoost()?.enabled = true
                        getVirtualizer()?.enabled = true
                        getHQEqualizer()?.enabled = true
                        getHQVisualizer()?.enabled = true
                        getPreAmp()?.enabled = true
                    }
                }
            } else {
                SharedPrefHelper.getInstance().put(SharedPrefHelper.Key.IS_EQUALIZER_ACTIVE, false)
                if (mApp?.isServiceRunning()!!) {
                    mApp?.mService?.getEqualizerHelper()?.apply {
                        getBassBoost()?.enabled = false
                        getVirtualizer()?.enabled = false
                        getHQEqualizer()?.enabled = false
                        getHQVisualizer()?.enabled = false
                        getPreAmp()?.enabled = false
                    }
                }

            }


        }
    }


    fun seekBarValueChanger(seekBarProg: Int?, hertzValue: Short?) {
        if (seekBarProg == 16) {
            mApp?.mService?.getEqualizerHelper()?.getHQEqualizer()?.setBandLevel(hertzValue!!, 0)
        } else if (seekBarProg != 16) {
            if (seekBarProg == 0) {
                mApp?.mService?.getEqualizerHelper()?.getHQEqualizer()
                    ?.setBandLevel(hertzValue!!, -1500)
            } else
                mApp?.mService?.getEqualizerHelper()?.getHQEqualizer()?.setBandLevel(
                    hertzValue!!,
                    (if (seekBarProg!! > 16) ((seekBarProg.minus(16)) * 100).toShort() else (-(seekBarProg.plus(
                        16
                    )) * 100).toShort())
                )
        }
    }

}
