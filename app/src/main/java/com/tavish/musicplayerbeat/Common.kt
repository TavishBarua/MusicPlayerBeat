package com.tavish.musicplayerbeat

import android.content.Context
import android.content.res.Resources
import androidx.annotation.NonNull
import androidx.multidex.MultiDexApplication
import com.tavish.musicplayerbeat.DB.DBHelper
import com.tavish.musicplayerbeat.Helpers.MediaHelpers.MusicService
import com.tavish.musicplayerbeat.Helpers.PlaybackHelper.Playback
import com.tavish.musicplayerbeat.Models.AlbumDto
import com.tavish.musicplayerbeat.Models.ArtistDto
import com.tavish.musicplayerbeat.Models.SongDto
import com.tavish.musicplayerbeat.Utils.Constants.Companion.LARGE_TABLET
import com.tavish.musicplayerbeat.Utils.Constants.Companion.LARGE_TABLET_LANDSCAPE
import com.tavish.musicplayerbeat.Utils.Constants.Companion.LARGE_TABLET_PORTRAIT
import com.tavish.musicplayerbeat.Utils.Constants.Companion.ORIENTATION_LANDSCAPE
import com.tavish.musicplayerbeat.Utils.Constants.Companion.ORIENTATION_PORTRAIT
import com.tavish.musicplayerbeat.Utils.Constants.Companion.REGULAR
import com.tavish.musicplayerbeat.Utils.Constants.Companion.REGULAR_SCREEN_LANDSCAPE
import com.tavish.musicplayerbeat.Utils.Constants.Companion.REGULAR_SCREEN_PORTRAIT
import com.tavish.musicplayerbeat.Utils.Constants.Companion.SMALL_TABLET
import com.tavish.musicplayerbeat.Utils.Constants.Companion.SMALL_TABLET_LANDSCAPE
import com.tavish.musicplayerbeat.Utils.Constants.Companion.SMALL_TABLET_PORTRAIT
import com.tavish.musicplayerbeat.Utils.Constants.Companion.XLARGE_TABLET
import com.tavish.musicplayerbeat.Utils.Constants.Companion.XLARGE_TABLET_LANDSCAPE
import com.tavish.musicplayerbeat.Utils.Constants.Companion.XLARGE_TABLET_PORTRAIT
import com.tavish.musicplayerbeat.Utils.Logger
import java.lang.Exception
import java.util.ArrayList


class Common: MultiDexApplication() {



   /*  constructor(context: Context) : this() {
        mContext=context
    }*/

     private var mPlayback: Playback? = null


     var mService: MusicService?=null


    private var mIsServiceRunning = false




    companion object{
                /**
         * Returns the orientation of the device.
         */
        @Volatile private var commonInstance:Context? = null
       /* fun getInstance(context: Context):Common = commonInstance ?:
                synchronized(Common::class){
                    commonInstance ?:
                        Common(context.applicationContext)
                    }

*/

         fun getInstance():Context?{
             return commonInstance
         }

         val orientation:Int
             get() {

                 if ((commonInstance?.getResources()?.getDisplayMetrics()?.widthPixels!! > commonInstance?.getResources()?.getDisplayMetrics()?.heightPixels!!))
                 {
                     return ORIENTATION_LANDSCAPE
                 }
                 else
                 {
                     return ORIENTATION_PORTRAIT
                 }
             }

        /**
         * Returns the current screen configuration of the device.
         */
        fun getDeviceScreenConfiguration(): Int {
            val screenSize = commonInstance?.resources?.getString(R.string.screen_size)
            var landscape = false
            if (orientation == ORIENTATION_LANDSCAPE) {
                landscape = true
            }

            return if (screenSize == REGULAR && !landscape)
                REGULAR_SCREEN_PORTRAIT
            else if (screenSize == REGULAR && landscape)
                REGULAR_SCREEN_LANDSCAPE
            else if (screenSize == SMALL_TABLET && !landscape)
                SMALL_TABLET_PORTRAIT
            else if (screenSize == SMALL_TABLET && landscape)
                SMALL_TABLET_LANDSCAPE
            else if (screenSize == LARGE_TABLET && !landscape)
                LARGE_TABLET_PORTRAIT
            else if (screenSize == LARGE_TABLET && landscape)
                LARGE_TABLET_LANDSCAPE
            else if (screenSize == XLARGE_TABLET && !landscape)
                XLARGE_TABLET_PORTRAIT
            else if (screenSize == XLARGE_TABLET && landscape)
                XLARGE_TABLET_LANDSCAPE
            else
                REGULAR_SCREEN_PORTRAIT
        }

        /**
         * Returns the no of column which will be applied to the grids on different devices
         */
        val getNumberOfColumns:Int
            get(){
                val config = getDeviceScreenConfiguration()
                if (config == REGULAR_SCREEN_PORTRAIT) {
                    return 2
                } else if (config == LARGE_TABLET_LANDSCAPE) {
                    return 6
                } else if (config == LARGE_TABLET_PORTRAIT) {
                    return 4
                } else if (config == REGULAR_SCREEN_LANDSCAPE) {
                    return 4
                } else if (config == XLARGE_TABLET_LANDSCAPE) {
                    return 8

                } else if (config == XLARGE_TABLET_PORTRAIT) {
                    return 6
                }
                return 2
            }


        fun getItemWidth(): Int {
            val metrics = Resources.getSystem().displayMetrics
            return metrics.widthPixels / getNumberOfColumns
        }

        /*
     * Returns the status bar height for the current layout configuration.
     */
        fun getStatusBarHeight(context: Context): Int {
            var result = 0
            val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
            if (resourceId > 0) {
                result = context.resources.getDimensionPixelSize(resourceId)
            }
            return result
        }

        fun convertMillisToSecs(milliseconds:Int):String{

            val secondsValue = milliseconds / 1000 % 60
            val minutesValue = milliseconds / (1000 * 60) % 60
            val hoursValue = milliseconds / (1000 * 60 * 60) % 24

            var seconds = ""
            var minutes = ""
            var hours = ""

            if (secondsValue < 10) {
                seconds = "0$secondsValue"
            } else {
                seconds = "" + secondsValue
            }

            if (minutesValue < 10) {
                minutes = "0$minutesValue"
            } else {
                minutes = "" + minutesValue
            }

            if (hoursValue < 10) {
                hours = "0$hoursValue"
            } else {
                hours = "" + hoursValue
            }

            var output = ""
            if (hoursValue != 0) {
                output = "$hours:$minutes:$seconds"
            } else {
                output = "$minutes:$seconds"
            }

            return output

        }


     }
     override fun onCreate() {
         super.onCreate()
         commonInstance=applicationContext
         mPlayback = Playback(commonInstance!!)


     }










     fun isServiceRunning(): Boolean {
         return mIsServiceRunning
     }

     fun setIsServiceRunning(running: Boolean) {
         mIsServiceRunning = running
     }




     fun getDBAccessHelper(): DBHelper {
        return DBHelper.getDBHelper(commonInstance!!)
    }


     fun getPlayBackStarter(): Playback {
         return mPlayback!!
     }
}

