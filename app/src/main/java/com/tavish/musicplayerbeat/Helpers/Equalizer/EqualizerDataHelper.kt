package com.tavish.musicplayerbeat.Helpers.Equalizer

import com.h6ah4i.android.media.IBasicMediaPlayer
import com.h6ah4i.android.media.IMediaPlayerFactory
import com.h6ah4i.android.media.IReleasable
import com.h6ah4i.android.media.audiofx.*
import com.h6ah4i.android.media.opensl.OpenSLMediaPlayerContext
import com.h6ah4i.android.media.opensl.audiofx.OpenSLPresetReverb
import com.h6ah4i.android.media.standard.audiofx.StandardPresetReverb


class EqualizerDataHelper(factory: IMediaPlayerFactory?, player:IBasicMediaPlayer, equalizer:Boolean, openSLMediaPlayerContext: OpenSLMediaPlayerContext) : IReleasable {
    private var _mFactory = factory
    private var mEqualizerStatus: Boolean? = equalizer
    private var mPlayer: IBasicMediaPlayer? = player
    private var mHQEqualizer: IEqualizer? = createHQEqualizer()
    private var mHQVisualizer: IHQVisualizer? = createHQVizualizer()
    private var mVirtualizer: IVirtualizer? = createVirtualizer()
    private var mBassBoost: IBassBoost? = createBassBoost()
    private var mPresetReverb: IPresetReverb? = createPresetReverb()
    private var mPreAmp: IPreAmp? = createPreAmp()
    private var mLoudnessEnhancer: ILoudnessEnhancer? = null
    private var mEnvironmentalReverb: IEnvironmentalReverb? = null


    private var mOpenSLMediaPlayerContext: OpenSLMediaPlayerContext? = openSLMediaPlayerContext


//val mVirtualizer:OpenSLVirtualizer? = OpenSLVirtualizer(OpenSLMediaPlayerContext(mContext,get))

     fun createHQEqualizer(): IEqualizer? {
        if (mHQEqualizer == null) {
            try {
                mHQEqualizer = _mFactory?.createHQEqualizer()
                mHQEqualizer?.enabled=mEqualizerStatus!!
            } catch (e: UnsupportedOperationException) {
                // the effect is not supported
                e.printStackTrace()
            }

        }
        return mHQEqualizer
    }

    fun getHQEqualizer(): IEqualizer? {
        return mHQEqualizer
    }


     fun createHQVizualizer():IHQVisualizer?{
        if (mHQVisualizer==null){
            try {
                mHQVisualizer =_mFactory?.createHQVisualizer()
                mHQVisualizer?.enabled=mEqualizerStatus!!
            }catch (e:UnsupportedOperationException ){ }
        }
        return mHQVisualizer
    }

    fun getHQVisualizer(): IHQVisualizer? {
        return mHQVisualizer
    }

     fun createBassBoost(): IBassBoost? {
        if (mBassBoost == null) {
            try {
                mBassBoost = _mFactory?.createBassBoost(mPlayer!!)
                mBassBoost?.enabled=mEqualizerStatus!!
            } catch (e: UnsupportedOperationException) {
                // the effect is not supported
            } catch (e: IllegalArgumentException) {
            }
        }
        return mBassBoost
    }

    fun getBassBoost(): IBassBoost? {
        return mBassBoost
    }


    fun createVirtualizer(): IVirtualizer? {
        if (mVirtualizer == null) {
            try {
                mVirtualizer = _mFactory?.createVirtualizer(mPlayer!!)
                mVirtualizer?.enabled=mEqualizerStatus!!
            } catch (e: UnsupportedOperationException) {
                // the effect is not supported
            } catch (e: IllegalArgumentException) {
            }
        }
        return mVirtualizer
    }

    fun getVirtualizer(): IVirtualizer? {
        return mVirtualizer
    }

    fun createPresetReverb(): IPresetReverb? {
        if (mPresetReverb == null) {
            try {
                mPresetReverb = StandardPresetReverb(1,mPlayer?.audioSessionId!!)
            } catch (e: UnsupportedOperationException) {
                // the effect is not supported
            } catch (e: IllegalArgumentException) {
            }
        }
        return mPresetReverb
    }

    fun getPresetReverb(): IPresetReverb? {
        return mPresetReverb
    }

    fun createPreAmp(): IPreAmp? {
        if (mPreAmp == null) {
            try {
                mPreAmp = _mFactory?.createPreAmp()
            } catch (e: UnsupportedOperationException) {
                // the effect is not supported
            } catch (e: IllegalArgumentException) {
            }
        }
        return mPreAmp
    }

    fun getPreAmp(): IPreAmp? {
        return mPreAmp
    }




    override fun release() {
        releaseAllPlayerResources()
        releaseFactory()
    }



    private fun releaseAllPlayerResources() {

        releaseBassBoost()
      //  releaseVirtualizer()
       // releaseEqualizer()
      //  releaseLoudnessEnhancer()
        releasePresetReverb()
       // releaseEnvironmentalReverb()
        //releaseVisualizer()
        releaseHQVisualizer()
        releaseHQEqualizer()
        releasePreAmp()
    }

    private fun safeRelease(obj: IReleasable?) {
        try {
            obj?.release()
        } catch (e: Exception) {
        }

    }

    private fun releaseFactory() {
        safeRelease(_mFactory)
        _mFactory = null
    }


    private fun releaseBassBoost() {
        safeRelease(mBassBoost)
        mBassBoost = null
    }


    private fun releaseHQVisualizer() {
        safeRelease(mHQVisualizer)
        mHQVisualizer = null
    }

    private fun releaseHQEqualizer() {
        safeRelease(mHQEqualizer)
        mHQEqualizer = null
    }

    private fun releasePresetReverb() {
        safeRelease(mPresetReverb)
        mPresetReverb = null
    }

    private fun releasePreAmp() {
        safeRelease(mPresetReverb)
        mPresetReverb = null
    }





}
