package com.tavish.musicplayerbeat.Utils

import android.net.Uri

class Constants{

    companion object {
        val sArtworkUri = Uri
            .parse("content://media/external/audio/albumart")

        val REQUEST_PERMISSIONS = 33

        val UAMP_BROWSABLE_ROOT = "/"
        val UAMP_EMPTY_ROOT = "@empty@"
        val UAMP_RECOMMENDED_ROOT = "__RECOMMENDED__"
        val UAMP_ALBUMS_ROOT = "__ALBUMS__"

        val REGULAR = "regular"
        val SMALL_TABLET = "small_tablet"
        val LARGE_TABLET = "large_tablet"
        val XLARGE_TABLET = "xlarge_tablet"

        //Device orientation constants.
        val ORIENTATION_PORTRAIT = 0
        val ORIENTATION_LANDSCAPE = 1

        val REGULAR_SCREEN_PORTRAIT = 0
        val REGULAR_SCREEN_LANDSCAPE = 1
        val SMALL_TABLET_PORTRAIT = 2
        val SMALL_TABLET_LANDSCAPE = 3
        val LARGE_TABLET_PORTRAIT = 4
        val LARGE_TABLET_LANDSCAPE = 5
        val XLARGE_TABLET_PORTRAIT = 6
        val XLARGE_TABLET_LANDSCAPE = 7

        val HEADER_TITLE = "HEADER_TITLE"
        val HEADER_SUB_TITLE = "HEADER_SUB_TITLE"
        val FROM_WHERE = "FROM_WHERE"
        val SELECTION_VALUE = "SELECTION_VALUE"


        val COVER_PATH = "COVER_PATH"



        val MEDIA_SEARCH_SUPPORTED = "android.media.browse.SEARCH_SUPPORTED"

        val imageUriRoot = "android.resource://com.tavish.musicplayerbeat.next/drawable/"

        const val METADATA_KEY_BEAT_FLAGS = "com.tavish.musicplayerbeat.METADATA_KEY_BEAT_FLAGS"

        const val NETWORK_FAILURE = "com.tavish.musicplayerbeat.media.session.NETWORK_FAILURE"
    }







}