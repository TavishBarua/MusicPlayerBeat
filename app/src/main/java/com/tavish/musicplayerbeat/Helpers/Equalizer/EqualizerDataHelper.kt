package com.tavish.musicplayerbeat.Helpers.Equalizer

import com.h6ah4i.android.media.IMediaPlayerFactory
import com.h6ah4i.android.media.IReleasable
import com.h6ah4i.android.media.audiofx.*
import com.h6ah4i.android.media.opensl.OpenSLMediaPlayerContext
import com.h6ah4i.android.media.opensl.audiofx.OpenSLBassBoost


class EqualizerDataHelper(factory: IMediaPlayerFactory?, audioSessionId:Int, equalizer:Boolean, openSLMediaPlayerContext: OpenSLMediaPlayerContext) : IReleasable {
    private var mHQEqualizer: IEqualizer? = null
    private var mHQVisualizer: IHQVisualizer? = null
    private var mVirtualizer: IVirtualizer? = null
    private var mBassBoost: IBassBoost? = null
    private var mLoudnessEnhancer: ILoudnessEnhancer? = null
    private var mEnvironmentalReverb: IEnvironmentalReverb? = null
    private var mPresetReverb: IPresetReverb? = null
    private var mPreAmp: IPreAmp? = null
    private var mAudioSessionId: Int? = audioSessionId
    private var mEqualizerStatus: Boolean? = equalizer
    private var mOpenSLMediaPlayerContext: OpenSLMediaPlayerContext? = openSLMediaPlayerContext

    private var _mFactory = factory
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
                mBassBoost = _mFactory?.createBassBoost(mAudioSessionId!!)
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
                mVirtualizer = _mFactory?.createVirtualizer(mAudioSessionId!!)
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
                mPresetReverb = _mFactory?.createPresetReverb()
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
      //  releasePresetReverb()
       // releaseEnvironmentalReverb()
        //releaseVisualizer()
        releaseHQVisualizer()
        releaseHQEqualizer()
        //releasePreAmp()
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





}
