package com.tavish.musicplayerbeat.Utils

import android.util.Log

object Logger {
    private val TAG = "BeatDrop Tavish Barua:-"

    fun log(log: String) {
        Log.d(TAG, log)
    }

    fun exp(log: String) {
        Log.e(TAG, log)
    }
}
