package com.tavish.musicplayerbeat.BroadcastReceivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.tavish.musicplayerbeat.Common
import com.tavish.musicplayerbeat.Utils.Constants


class NotificationBroadcastReceiver : BroadcastReceiver() {
    private var mCommon: Common? = null

    override fun onReceive(context: Context, intent: Intent) {
        mCommon = context.applicationContext as Common

        if (intent.hasExtra("INDEX")) {
            if (mCommon!!.isServiceRunning()) {
                mCommon!!.mService?.setSelectedSong(intent.extras!!.getInt("INDEX"))
            }
            return
        }

        try {
            val action = intent.action
            if (action!!.equals(Constants.ACTION_NEXT, ignoreCase = true)) {
                mCommon?.mService?.nextSong()
            } else if (action.equals(Constants.ACTION_PAUSE, ignoreCase = true)) {
                mCommon?.mService?.playPauseSong()
            } else if (action.equals(Constants.ACTION_PREVIOUS, ignoreCase = true)) {
                mCommon?.mService?.previousSong()
            } else if (action.equals(Constants.ACTION_STOP, ignoreCase = true)) {
                mCommon?.mService?.stopSelf()
            }
        } catch (e: Exception) {
        }

    }
}