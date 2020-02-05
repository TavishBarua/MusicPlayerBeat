package com.tavish.musicplayerbeat.Activities

import android.content.ContentValues
import android.content.Context
import android.content.DialogInterface
import android.media.audiofx.PresetReverb
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.HapticFeedbackConstants
import android.view.Menu
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.*
import androidx.appcompat.widget.Toolbar
import com.tavish.musicplayerbeat.Common
import com.tavish.musicplayerbeat.DB.DBHelper
import com.tavish.musicplayerbeat.Helpers.SharedPrefHelper
import com.tavish.musicplayerbeat.Helpers.binder
import com.tavish.musicplayerbeat.R
import com.tavish.musicplayerbeat.Utils.CustomViews.RangeBar.BeatRangeBarView
import com.tavish.musicplayerbeat.Utils.CustomViews.RangeBar.OnBeatRangeSeekBarListener
import kotlinx.android.synthetic.main.activity_equalizer.*
import kotlinx.coroutines.*

class EqualizerActivity : AppCompatActivity(), View.OnClickListener {
    companion object {


        private val NUM_BAND_VIEWS = 10
        private val SEEKBAR_MAX = 1000
    }

    private var bassBoosterLevel: Short? = null

    private var virtualizerLevel: Short? = null
    private var preAmpLevel: Float? = null
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
    private var reverbSetting: Short = 0

    private var mToolbar: Toolbar? = null

    private var mContext: Context? = null
    private var mApp: Common? = null
    /*   private var mTextViewBandLevels: Array<AppCompatTextView>? = null
       private var mSeekBarBandLevels: Array<AppCompatSeekBar>? = null*/
    private var mSeekBarPreAmpLevel: SeekBar? = null


  /*  private val mThirtyOneHzSeekBar by binder<BeatRangeBarView>(R.id.seekbar_equalizer_band_0)
    private val mSixtyTwoHzSeekBar by binder<BeatRangeBarView>(R.id.seekbar_equalizer_band_1)
    private val mOneHunderedTwentyFiveHzSeekBar by binder<BeatRangeBarView>(R.id.seekbar_equalizer_band_2)
    private val mTwoHundredFiftyHzSeekBar by binder<BeatRangeBarView>(R.id.seekbar_equalizer_band_3)
    private val mFiveHundredHzSeekBar by binder<BeatRangeBarView>(R.id.seekbar_equalizer_band_4)
    private val mOneKHzSeekBar by binder<BeatRangeBarView>(R.id.seekbar_equalizer_band_5)
    private val mTwoKHzSeekBar by binder<BeatRangeBarView>(R.id.seekbar_equalizer_band_6)
    private val mFourKHzSeekBar by binder<BeatRangeBarView>(R.id.seekbar_equalizer_band_7)
    private val mEightKHzSeekBar by binder<BeatRangeBarView>(R.id.seekbar_equalizer_band_8)
    private val mSixteenKHzSeekBar by binder<BeatRangeBarView>(R.id.seekbar_equalizer_band_9)*/
    private val mLoadPresetBtn by binder<AppCompatButton>(R.id.btn_load_preset)
    private val mSavePresetBtn by binder<AppCompatButton>(R.id.btn_save_preset)


    private var mSeekBarListener1: SeekBar.OnSeekBarChangeListener? = null
   // private var mSeekBarListener: OnBeatRangeSeekBarListener? = null
   // private var mSeekBarListener: BeatRangeBarView.OnBeatRangeBarListener? = null


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

        val reverbPresets: MutableList<String> = mutableListOf()
        reverbPresets.add("None")
        reverbPresets.add("Large Hall")
        reverbPresets.add("Large Room")
        reverbPresets.add("Medium Hall")
        reverbPresets.add("Medium Room")
        reverbPresets.add("Small Room")
        reverbPresets.add("Plate")

        val dataAdapter = ArrayAdapter<String>(this, R.layout.simple_spinner_item, reverbPresets)
        dataAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
        spinner_equalizer_preset.adapter = dataAdapter
        spinner_equalizer_preset.onItemSelectedListener = reverbListener

        mLoadPresetBtn.setOnClickListener(this)
        mSavePresetBtn.setOnClickListener(this)



        mToolbar?.setNavigationOnClickListener { onBackPressed() }
        obtainPalleteView()


        sb_thirty_one_hz.setOnProgressChangeListener { progressVal->
            if(progressVal==16)
                e31hztxt.text="0"
            else if (progressVal>16){
                e31hztxt.text=(progressVal-16).toString()
            }else{
                e31hztxt.text="-"+(16-progressVal)
            }
            val thirtyOneHz = mApp?.mService?.getEqualizerHelper()?.getHQEqualizer()
            ?.getBand(31000)
            seekBarValueChanger(progressVal, thirtyOneHz)
            if ((progressVal == 31 || progressVal == 0)) {
                sb_thirty_one_hz.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            }
            thirtyOneHzLevel = progressVal}

        sb_sixty_two_hz.setOnProgressChangeListener { progressVal->

            if(progressVal==16)
                e62hztxt.text="0"
            else if (progressVal>16){
                e62hztxt.text=(progressVal-16).toString()
            }else{
                e62hztxt.text="-"+(16-progressVal)
            }
            val sixtyTwoHz = mApp?.mService?.getEqualizerHelper()?.getHQEqualizer()
                ?.getBand(62000)
            seekBarValueChanger(progressVal, sixtyTwoHz)
            if ((progressVal == 31 || progressVal == 0)) {
                sb_sixty_two_hz.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            }
            sixtyTwoHzLevel = progressVal
        }


        sb_one_twentyfive_hz.setOnProgressChangeListener { progressVal->
            if(progressVal==16)
                e125hztxt.text="0"
            else if (progressVal>16){
                e125hztxt.text=(progressVal-16).toString()
            }else{
                e125hztxt.text="-"+(16-progressVal)
            }

            val oneTwentyFive =
                mApp?.mService?.getEqualizerHelper()?.getHQEqualizer()
                    ?.getBand(125000)
            seekBarValueChanger(progressVal, oneTwentyFive)
            if ((progressVal == 31 || progressVal == 0)) {
                sb_one_twentyfive_hz.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            }

            oneHunderedTwentyFiveHzLevel = progressVal
        }

        sb_two_fifty_hz.setOnProgressChangeListener { progressVal->
            if(progressVal==16)
                e250hztxt.text="0"
            else if (progressVal>16){
                e250hztxt.text=(progressVal-16).toString()
            }else{
                e250hztxt.text="-"+(16-progressVal)
            }

            val twoFiftyHz = mApp?.mService?.getEqualizerHelper()?.getHQEqualizer()
                ?.getBand(250000)
            seekBarValueChanger(progressVal, twoFiftyHz)

            if ((progressVal == 31 || progressVal == 0)) {
                sb_two_fifty_hz.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            }

            fiveHundredHzLevel = progressVal

        }


        sb_five_hundred_hz.setOnProgressChangeListener { progressVal->

            if(progressVal==16)
                e500hztxt.text="0"
            else if (progressVal>16){
                e500hztxt.text=(progressVal-16).toString()
            }else{
                e500hztxt.text="-"+(16-progressVal)
            }
            val fiveHundredHz =
                mApp?.mService?.getEqualizerHelper()?.getHQEqualizer()
                    ?.getBand(500000)
            seekBarValueChanger(progressVal, fiveHundredHz)

            if ((progressVal == 31 || progressVal == 0)) {
                sb_five_hundred_hz.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            }

            fiveHundredHzLevel = progressVal
        }



        sb_one_kilo_hz.setOnProgressChangeListener { progressVal->
            if(progressVal==16)
                e1khztxt.text="0"
            else if (progressVal>16){
                e1khztxt.text=(progressVal-16).toString()
            }else{
                e1khztxt.text="-"+(16-progressVal)
            }
            val oneKhz = mApp?.mService?.getEqualizerHelper()?.getHQEqualizer()
                ?.getBand(1000000)
            seekBarValueChanger(progressVal, oneKhz)
            if ((progressVal == 31 || progressVal == 0)) {
                sb_one_kilo_hz.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            }

            oneKHzLevel = progressVal
        }



        sb_two_kilo_hz.setOnProgressChangeListener { progressVal->
            if(progressVal==16)
                e2khztxt.text="0"
            else if (progressVal>16){
                e2khztxt.text=(progressVal-16).toString()
            }else{
                e2khztxt.text="-"+(16-progressVal)
            }
            val twoKhz = mApp?.mService?.getEqualizerHelper()?.getHQEqualizer()
                ?.getBand(2000000)
            seekBarValueChanger(progressVal, twoKhz)
            if ((progressVal == 31 || progressVal == 0)) {
                sb_one_kilo_hz.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            }
            twoKHzLevel = progressVal
        }

        sb_four_kilo_hz.setOnProgressChangeListener { progressVal->

            if(progressVal==16)
                e4khztxt.text="0"
            else if (progressVal>16){
                e4khztxt.text=(progressVal-16).toString()
            }else{
                e4khztxt.text="-"+(16-progressVal)
            }
            val fourKhz = mApp?.mService?.getEqualizerHelper()?.getHQEqualizer()
                ?.getBand(8000000)
            seekBarValueChanger(progressVal, fourKhz)
            if ((progressVal == 31 || progressVal == 0)) {
                sb_four_kilo_hz.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            }
            fourKHzLevel = progressVal
        }

        sb_eight_kilo_hz.setOnProgressChangeListener { progressVal->

            if(progressVal==16)
                e8khztxt.text="0"
            else if (progressVal>16){
                e8khztxt.text=(progressVal-16).toString()
            }else{
                e8khztxt.text="-"+(16-progressVal)
            }
            val eightKhz = mApp?.mService?.getEqualizerHelper()?.getHQEqualizer()
                ?.getBand(8000000)
            seekBarValueChanger(progressVal, eightKhz)
            if ((progressVal == 31 || progressVal == 0)) {
                sb_eight_kilo_hz.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            }
            eightKHzLevel = progressVal
        }

        sb_sixteen_kilo_hz.setOnProgressChangeListener { progressVal->
            if(progressVal==16)
                e16khztxt.text="0"
            else if (progressVal>16){
                e16khztxt.text=(progressVal-16).toString()
            }else{
                e16khztxt.text="-"+(16-progressVal)
            }
            val sixteenKhz = mApp?.mService?.getEqualizerHelper()?.getHQEqualizer()
                ?.getBand(16000000)
            seekBarValueChanger(progressVal, sixteenKhz)
            if ((progressVal == 31 || progressVal == 0)) {
                sb_sixteen_kilo_hz.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            }
            sixteenKHzLevel = progressVal
        }


       /* mSeekBarListener = object : OnBeatRangeSeekBarListener {
            override fun onProgressChanged(
                seekBar: BeatRangeBarView?,
                seekBarProg: Int,
                fromUser: Boolean
            ) {
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



                }
            }


            override fun onStartTrackingTouch(seekBar: BeatRangeBarView?) {

            }

            override fun onStopTrackingTouch(seekBar: BeatRangeBarView?) {

            }



        }*/

        mSeekBarListener1 = object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, seekBarProg: Int, fromUser: Boolean) {
                when (seekBar?.id){
                    R.id.seekbar_bassbooster -> {
                        bassBoosterLevel = seekBarProg.toShort()
                        if (mApp?.isServiceRunning()!!) {
                            mApp?.mService?.getEqualizerHelper()?.getBassBoost()
                                ?.setStrength(bassBoosterLevel!!.toShort())
                        }
                        if ((seekBarProg == 5 || seekBarProg == 990) && fromUser) {
                            seekBar.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                        }
                    }

                    R.id.seekbar_virtualizer -> {
                        virtualizerLevel = seekBarProg.toShort()
                        if (mApp?.isServiceRunning()!!) {
                            mApp?.mService?.getEqualizerHelper()?.getVirtualizer()
                                ?.setStrength(virtualizerLevel!!.toShort())
                        }
                        if ((seekBarProg == 5 || seekBarProg == 990) && fromUser) {
                            seekBar.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                        }
                    }

                    R.id.seekbar_preamp -> {
                        preAmpLevel = seekBarProg.toFloat()
                        if (mApp?.isServiceRunning()!!) {
                            // mApp?.mService?.getEqualizerHelper()?.getPreAmp()?.level= preAmpLevel!!.toFloat()
                            mApp?.mService?.getEqualizerHelper()?.getPreAmp()?.level =
                                seekBarProg.plus(1).toFloat().times(0.1f)
                        }
                        if ((seekBarProg == 0 || seekBarProg == 3) && fromUser) {
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
/*
        mThirtyOneHzSeekBar.setOnProgressChangeListener{mSeekBarListener}
        mSixtyTwoHzSeekBar.setOnSeekBarChangeListener(mSeekBarListener)
        mOneHunderedTwentyFiveHzSeekBar.setOnSeekBarChangeListener(mSeekBarListener)
        mTwoHundredFiftyHzSeekBar.setOnSeekBarChangeListener(mSeekBarListener)
        mFiveHundredHzSeekBar.setOnSeekBarChangeListener(mSeekBarListener)
        mOneKHzSeekBar.setOnSeekBarChangeListener(mSeekBarListener)
        mTwoKHzSeekBar.setOnSeekBarChangeListener(mSeekBarListener)
        mFourKHzSeekBar.setOnSeekBarChangeListener(mSeekBarListener)
        mEightKHzSeekBar.setOnSeekBarChangeListener(mSeekBarListener)
        mSixteenKHzSeekBar.setOnSeekBarChangeListener(mSeekBarListener)*/
     /*   mThirtyOneHzSeekBar.setOnProgressChangeListener{mSeekBarListener}
        mSixtyTwoHzSeekBar.setOnProgressChangeListener{mSeekBarListener}
        mOneHunderedTwentyFiveHzSeekBar.setOnProgressChangeListener{mSeekBarListener}
        mTwoHundredFiftyHzSeekBar.setOnProgressChangeListener{mSeekBarListener}
        mFiveHundredHzSeekBar.setOnProgressChangeListener{mSeekBarListener}
        mOneKHzSeekBar.setOnProgressChangeListener{mSeekBarListener}
        mTwoKHzSeekBar.setOnProgressChangeListener{mSeekBarListener}
        mFourKHzSeekBar.setOnProgressChangeListener{mSeekBarListener}
        mEightKHzSeekBar.setOnProgressChangeListener{mSeekBarListener}
        mSixteenKHzSeekBar.setOnProgressChangeListener{mSeekBarListener}*/
        mSeekBarBassBoost?.setOnSeekBarChangeListener(mSeekBarListener1)
        mSeekBarVirtualizer?.setOnSeekBarChangeListener(mSeekBarListener1)
        mSeekBarPreAmpLevel?.setOnSeekBarChangeListener(mSeekBarListener1)

            seekBarSlidersTask()

    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toggle_switch_menu, menu)
        val menuItem = menu?.findItem(R.id.myswitch)
        val view = menuItem?.actionView
        btn_toggleEQ = view?.findViewById(R.id.switchButton)
        btn_toggleEQ?.let {
            it.isChecked = SharedPrefHelper.getInstance().getBoolean(SharedPrefHelper.Key.IS_EQUALIZER_ACTIVE, false)
            it.setOnCheckedChangeListener(eqEnableState)

        }
        return true
    }

    override fun onPause() {
        super.onPause()
        CoroutineScope(Dispatchers.Main).launch{
            setEQValues()
        }

    }
    /*    override fun onStop() {
        super.onStop()
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    override fun onDestroy() {
        super.onDestroy()
    }*/

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_load_preset -> {
                loadPresetDialog().show()

            }
            R.id.btn_save_preset -> {
                val dialog = savePresetDialog()
                dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
                dialog.show()

            }
        }
    }

    private fun loadPresetDialog(): AlertDialog {
        val dialogBuilder = AlertDialog.Builder(this)
        val cursor = mApp?.getDBAccessHelper()?.getEQPresets()
        dialogBuilder.setTitle("Preset")
        dialogBuilder.setCursor(cursor,
            { dialog, which ->
                cursor?.moveToPosition(which)
                dialog?.dismiss()
                thirtyOneHzLevel = cursor?.getInt(cursor.getColumnIndex(DBHelper.EQ_31_Hz))!!
                sixtyTwoHzLevel = cursor.getInt(cursor.getColumnIndex(DBHelper.EQ_62_Hz))
                oneHunderedTwentyFiveHzLevel = cursor.getInt(cursor.getColumnIndex(DBHelper.EQ_125_Hz))
                twoHundredFiftyHzLevel = cursor.getInt(cursor.getColumnIndex(DBHelper.EQ_250_Hz))
                fiveHundredHzLevel = cursor.getInt(cursor.getColumnIndex(DBHelper.EQ_500_Hz))
                oneKHzLevel = cursor.getInt(cursor.getColumnIndex(DBHelper.EQ_1_KHz))
                twoKHzLevel = cursor.getInt(cursor.getColumnIndex(DBHelper.EQ_2_KHz))
                fourKHzLevel = cursor.getInt(cursor.getColumnIndex(DBHelper.EQ_4_KHz))
                eightKHzLevel = cursor.getInt(cursor.getColumnIndex(DBHelper.EQ_8_KHz))
                sixteenKHzLevel = cursor.getInt(cursor.getColumnIndex(DBHelper.EQ_16_KHz))
                virtualizerLevel = cursor.getShort(cursor.getColumnIndex(DBHelper.EQ_Virtualizer))
                bassBoosterLevel = cursor.getShort(cursor.getColumnIndex(DBHelper.EQ_BassBoost))
                reverbSetting = cursor.getShort(cursor.getColumnIndex(DBHelper.EQ_Reverb))
                preAmpLevel = cursor.getFloat(cursor.getColumnIndex(DBHelper.EQ_PreAmp))

                /*Save EQ settings into DB*/
                CoroutineScope(Dispatchers.Main).launch{
                    setEQValues()
                }
                seekBarSlidersTask()
                cursor.close()
            }, DBHelper.PRESET_NAME)

        return dialogBuilder.create()

    }

    private fun savePresetDialog():AlertDialog{
        val dialogBuilder = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.new_eq_preset_dialog, null)

        val etPreset =dialogView.findViewById<AppCompatEditText>(R.id.et_preset_name)


        dialogBuilder.setTitle("Save Preset")
        dialogBuilder.setView(dialogView)
        dialogBuilder.setNegativeButton("Cancel") { dialog, arg1->dialog.dismiss()}
        dialogBuilder.setPositiveButton("Done")
        { dialog, _ ->

            val presetName = etPreset.text.toString()
            mApp?.getDBAccessHelper()?.updateEQValues("ADD",presetName,thirtyOneHzLevel,sixtyTwoHzLevel,oneHunderedTwentyFiveHzLevel,twoHundredFiftyHzLevel,
                fiveHundredHzLevel,oneKHzLevel,twoKHzLevel,fourKHzLevel,eightKHzLevel,sixteenKHzLevel,virtualizerLevel,bassBoosterLevel,
                reverbSetting,preAmpLevel)


            Toast.makeText(mContext,"Preset Saved", Toast.LENGTH_SHORT).show()
            dialog.dismiss()

        }
        return dialogBuilder.create()
    }

    private suspend fun setEQValues(){

        withContext(Dispatchers.IO){
            val currentEQvalues = mApp?.getDBAccessHelper()?.getEQValues()!!
            if(currentEQvalues[14]==1){
                mApp?.getDBAccessHelper()?.updateEQValues("UPDATE","Reserved",thirtyOneHzLevel,sixtyTwoHzLevel,oneHunderedTwentyFiveHzLevel,twoHundredFiftyHzLevel,
                    fiveHundredHzLevel,oneKHzLevel,twoKHzLevel,fourKHzLevel,eightKHzLevel,sixteenKHzLevel,virtualizerLevel,bassBoosterLevel,
                    reverbSetting,preAmpLevel)
            }else{
                mApp?.getDBAccessHelper()?.updateEQValues("ADD","Reserved",thirtyOneHzLevel,sixtyTwoHzLevel,oneHunderedTwentyFiveHzLevel,twoHundredFiftyHzLevel,
                    fiveHundredHzLevel,oneKHzLevel,twoKHzLevel,fourKHzLevel,eightKHzLevel,sixteenKHzLevel,virtualizerLevel,bassBoosterLevel,
                    reverbSetting,preAmpLevel)

            }
        }
    }

    private  fun seekBarSlidersTask(){

        lateinit var eqValues:Array<Int?>

        CoroutineScope(Dispatchers.Main).launch{
            withContext(Dispatchers.IO){
                eqValues = mApp?.getDBAccessHelper()?.getEQValues()!!
            }

            thirtyOneHzLevel = eqValues[0]!!
            sixtyTwoHzLevel = eqValues[1]!!
            oneHunderedTwentyFiveHzLevel = eqValues[2]!!
            twoHundredFiftyHzLevel = eqValues[3]!!
            fiveHundredHzLevel = eqValues[4]!!
            oneKHzLevel = eqValues[5]!!
            twoKHzLevel = eqValues[6]!!
            fourKHzLevel = eqValues[7]!!
            eightKHzLevel = eqValues[8]!!
            sixteenKHzLevel = eqValues[9]!!
            virtualizerLevel = eqValues[10]?.toShort()!!
            bassBoosterLevel = eqValues[11]?.toShort()!!
            preAmpLevel = eqValues[12]?.toFloat()!!
            reverbSetting = eqValues[13]?.toShort()!!


            sb_thirty_one_hz.progress = thirtyOneHzLevel
            sb_sixty_two_hz.progress = sixtyTwoHzLevel
            sb_one_twentyfive_hz.progress = oneHunderedTwentyFiveHzLevel
            sb_two_fifty_hz.progress = twoHundredFiftyHzLevel
            sb_five_hundred_hz.progress =  fiveHundredHzLevel
            sb_one_kilo_hz.progress = oneKHzLevel
            sb_two_kilo_hz.progress = twoKHzLevel
            sb_four_kilo_hz.progress = fourKHzLevel
            sb_eight_kilo_hz.progress = eightKHzLevel
            sb_sixteen_kilo_hz.progress = sixteenKHzLevel
            mSeekBarVirtualizer?.progress = virtualizerLevel?.toInt()!!
            mSeekBarBassBoost?.progress = bassBoosterLevel?.toInt()!!
            mSeekBarPreAmpLevel?.progress = preAmpLevel?.toInt()!!
            mSpinnerPreset?.setSelection(reverbSetting.toInt(),false)
        }




        /*To be Added
        EQ Values code */




    }

    private fun obtainPalleteView() {
        mSpinnerPreset = findViewById(R.id.spinner_equalizer_preset)
        mSeekBarVirtualizer = findViewById(R.id.seekbar_virtualizer)
        mSeekBarBassBoost = findViewById(R.id.seekbar_bassbooster)
        mSeekBarPreAmpLevel = findViewById(R.id.seekbar_preamp)
    }

    val eqEnableState =
        CompoundButton.OnCheckedChangeListener { _, _ ->
            if (btn_toggleEQ?.isChecked!!) {
                SharedPrefHelper.getInstance()
                    .put(SharedPrefHelper.Key.IS_EQUALIZER_ACTIVE, true)
                if (mApp?.isServiceRunning()!!) {
                  /*  mApp?.mService?.getEqualizerHelper()?.getHQEqualizer()?.enabled=true
                    mApp?.mService?.getEqualizerHelper()?.getBassBoost()
                    mApp?.mService?.getEqualizerHelper()?.createHQVizualizer()
                    mApp?.mService?.getEqualizerHelper()?.createVirtualizer()
                    mApp?.mService?.getEqualizerHelper()?.createPreAmp()
                    mApp?.mService?.getEqualizerHelper()?.createPresetReverb()*/

                    mApp?.mService?.getEqualizerHelper()?.apply {
                        getBassBoost()?.enabled = true
                        getVirtualizer()?.enabled = true
                        getHQEqualizer()?.enabled = true
                        getHQVisualizer()?.enabled = true
                        getPreAmp()?.enabled = true
                        getPresetReverb()?.enabled = true
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
                        getPresetReverb()?.enabled = false
                    }
                }
            }
        }

    val reverbListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {
        }

        override fun onItemSelected(
            parent: AdapterView<*>?,
            view: View?,
            position: Int,
            id: Long
        ) {
            SharedPrefHelper.getInstance().put(
                SharedPrefHelper.Key.LAST_PRESET_NAME,
                spinner_equalizer_preset.selectedItem.toString()
            )
            reverbSetting = position.toShort()

            val presetValue = mApp?.mService?.getEqualizerHelper()?.getPresetReverb()

            // may be switch to be implemented
            if (mApp?.isServiceRunning()!!) {
                if (position == 0) {
                    presetValue?.preset = PresetReverb.PRESET_NONE
                    if (presetValue != null)
                        mApp?.mService?.mMediaPlayer1?.attachAuxEffect(presetValue.id)
                    reverbSetting = 0
                } else if (position == 1) {
                    presetValue?.preset = PresetReverb.PRESET_LARGEHALL
                    if (presetValue != null)
                        mApp?.mService?.mMediaPlayer1?.attachAuxEffect(presetValue.id)
                    reverbSetting = 1
                } else if (position == 2) {
                    presetValue?.preset = PresetReverb.PRESET_LARGEROOM
                    if (presetValue != null)
                        mApp?.mService?.mMediaPlayer1?.attachAuxEffect(presetValue.id)
                    reverbSetting = 2
                } else if (position == 3) {
                    presetValue?.preset = PresetReverb.PRESET_MEDIUMHALL
                    if (presetValue != null)
                        mApp?.mService?.mMediaPlayer1?.attachAuxEffect(presetValue.id)
                    reverbSetting = 3
                } else if (position == 4) {
                    presetValue?.preset = PresetReverb.PRESET_MEDIUMROOM
                    if (presetValue != null)
                        mApp?.mService?.mMediaPlayer1?.attachAuxEffect(presetValue.id)
                    reverbSetting = 4
                } else if (position == 5) {
                    presetValue?.preset = PresetReverb.PRESET_SMALLROOM
                    if (presetValue != null)
                        mApp?.mService?.mMediaPlayer1?.attachAuxEffect(presetValue.id)
                    reverbSetting = 5
                } else if (position == 6) {
                    presetValue?.preset = PresetReverb.PRESET_PLATE
                    if (presetValue != null)
                        mApp?.mService?.mMediaPlayer1?.attachAuxEffect(presetValue.id)
                    reverbSetting = 6
                } else
                    reverbSetting = 0

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
                    (if (seekBarProg!! > 16)
                            ((seekBarProg.minus(16))*100).toShort()
                    else ((-seekBarProg!!+16) * 100).toShort())
                )
        }
    }

}
