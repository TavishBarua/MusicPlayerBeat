package com.tavish.musicplayerbeat.Utils

class AudioManagerHelper {

     var mOriginalVolume: Int = 0
     var hasAudioFocus = false
     var isAudioDucked = false
     var mTargetVolume: Int = 0
     var mCurrentVolume: Int = 0
     var mStepDownIncrement: Int = 0
     var mStepUpIncrement: Int = 0
}