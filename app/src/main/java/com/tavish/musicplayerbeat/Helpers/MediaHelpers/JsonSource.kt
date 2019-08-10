package com.tavish.musicplayerbeat.Helpers.MediaHelpers

import com.tavish.musicplayerbeat.R


@Suppress("unused")
class JsonMusic {
    var id: String = ""
    var title: String = ""
    var album: String = ""
    var artist: String = ""
    var genre: String = ""
    var source: String = ""
    var image: String = ""
    var trackNumber: Long = 0
    var totalTrackCount: Long = 0
    var duration: Long = -1
    var site: String = ""
}

private const val NOTIFICATION_LARGE_ICON_SIZE = 144 // px

/*
private val glideOptions = RequestOptions()
    .fallback(R.mipmap.ic_launcher)
    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)*/
