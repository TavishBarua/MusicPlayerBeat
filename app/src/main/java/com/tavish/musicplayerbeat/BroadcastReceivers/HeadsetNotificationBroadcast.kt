package com.tavish.musicplayerbeat.BroadcastReceivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.view.KeyEvent
import com.tavish.musicplayerbeat.Common


class HeadsetNotificationBroadcast : BroadcastReceiver() {

    private var mApp: Common? = null

    override fun onReceive(context: Context?, intent: Intent?) {

        run {
            val keyEvent = intent?.getExtras()!!.get(Intent.EXTRA_KEY_EVENT) as KeyEvent
            if (keyEvent.action != KeyEvent.ACTION_DOWN)
                return
            when (keyEvent.keyCode) {
                KeyEvent.KEYCODE_HEADSETHOOK -> mApp?.getPlayBackStarter()?.playSongs()
                KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE -> mApp?.getPlayBackStarter()?.pauseSong()
                KeyEvent.KEYCODE_MEDIA_PLAY -> mApp?.getPlayBackStarter()?.playSongs()
                KeyEvent.KEYCODE_MEDIA_PAUSE -> mApp?.getPlayBackStarter()?.pauseSong()
                KeyEvent.KEYCODE_MEDIA_STOP -> mApp?.getPlayBackStarter()?.pauseSong()
                KeyEvent.KEYCODE_MEDIA_NEXT -> mApp?.getPlayBackStarter()?.nextSong()
                KeyEvent.KEYCODE_MEDIA_PREVIOUS -> mApp?.getPlayBackStarter()?.previousSong()
                else -> {
                }
            }
        }

    }


}