package com.tavish.musicplayerbeat.Utils

import android.net.Uri

class Constants{

    companion object {
      val sArtworkUri = Uri
         .parse("content://media/external/audio/albumart")

     const val REQUEST_PERMISSIONS = 33



     const val UAMP_BROWSABLE_ROOT = "/"
     const val UAMP_EMPTY_ROOT = "@empty@"
     const val UAMP_RECOMMENDED_ROOT = "__RECOMMENDED__"
     const val UAMP_ALBUMS_ROOT = "__ALBUMS__"

     const val REGULAR = "regular"
     const val SMALL_TABLET = "small_tablet"
     const val LARGE_TABLET = "large_tablet"
     const val XLARGE_TABLET = "xlarge_tablet"

      //const val SHUFFLE_OFF = 0


        //Device orientation constants.
      const val ORIENTATION_PORTRAIT = 0
      const val ORIENTATION_LANDSCAPE = 1

      const val REGULAR_SCREEN_PORTRAIT = 0
      const val REGULAR_SCREEN_LANDSCAPE = 1
      const val SMALL_TABLET_PORTRAIT = 2
      const val SMALL_TABLET_LANDSCAPE = 3
      const val LARGE_TABLET_PORTRAIT = 4
      const val LARGE_TABLET_LANDSCAPE = 5
      const val XLARGE_TABLET_PORTRAIT = 6
      const val XLARGE_TABLET_LANDSCAPE = 7


      const  val PLAY_PAUSE_SONG = 230
      const  val PLAY_SONGS = 231

      const  val PREVIOUS_SONG = 234
      const  val NEXT_SONG = 235
      const  val PLAY_SONG = 236
      const val PAUSE_SONG = 237
      const val PLAY_PAUSE_SONG_FROM_BOTTOM_BAR = 238
      const val PICK_FROM_GALLERY = 239

      const val HEADER_TITLE = "HEADER_TITLE"
      const val HEADER_SUB_TITLE = "HEADER_SUB_TITLE"
      const val FROM_WHERE = "FROM_WHERE"
      const val SELECTION_VALUE = "SELECTION_VALUE"




        const val COVER_PATH = "COVER_PATH"

        const val UPDATE_UI = "UPDATE_UI"

        //Repeat mode constants.
        const val REPEAT_OFF = 0
        const val REPEAT_PLAYLIST = 1
        const val REPEAT_SONG = 2
        const val SHUFFLE_ON = 3
        //const val A_B_REPEAT = 3

        const val JUST_UPDATE_UI = "JUST_UPDATE_UI"

        const val MEDIA_INTENT = "com.android.music.metachanged"


        const val ACTION_PLAY_PAUSE = " com.tavish.musicplayerbeat.action.PLAY_PAUSE"
        const val ACTION_STOP = "com.tavish.musicplayerbeat.action.STOP"
        const val ACTION_NEXT = "com.tavish.musicplayerbeat.action.NEXT"
        const val ACTION_PREVIOUS = "com.tavish.musicplayerbeat.action.PREVIOUS"
        const val ACTION_PAUSE = "com.tavish.musicplayerbeat.action.PAUSE"

        const  val MEDIA_SEARCH_SUPPORTED = "android.media.browse.SEARCH_SUPPORTED"

        const val imageUriRoot = "android.resource://com.tavish.musicplayerbeat.next/drawable/"

        const val METADATA_KEY_BEAT_FLAGS = "com.tavish.musicplayerbeat.METADATA_KEY_BEAT_FLAGS"

        const val NETWORK_FAILURE = "com.tavish.musicplayerbeat.media.session.NETWORK_FAILURE"

        const val ACTION_UPDATE_NOW_PLAYING_UI = "com.tavish.musicplayerbeat.action.UPDATE_NOW_PLAYING_UI"
    }







}