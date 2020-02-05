package com.tavish.musicplayerbeat.Interfaces

import android.widget.ImageView
import com.tavish.musicplayerbeat.Models.SongDto

interface SharedResourceOnItemClickFragment {

     fun onSongItemClickFragment(pos: Int, songItems: MutableList<SongDto>, shareImageView: ImageView)
}