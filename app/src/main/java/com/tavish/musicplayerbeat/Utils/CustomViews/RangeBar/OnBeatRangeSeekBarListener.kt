package com.tavish.musicplayerbeat.Utils.CustomViews.RangeBar

interface OnBeatRangeSeekBarListener {

        fun onProgressChanged(seekBar: BeatRangeBarView?, progress: Int, fromUser: Boolean)
        fun onStartTrackingTouch(seekBar: BeatRangeBarView?)
        fun onStopTrackingTouch(seekBar: BeatRangeBarView?)

}