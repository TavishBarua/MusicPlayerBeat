package com.tavish.musicplayerbeat.Activities

import android.content.Intent
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.view.animation.TranslateAnimation
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.tavish.musicplayerbeat.Helpers.*
import com.tavish.musicplayerbeat.Models.BeatDto
import com.tavish.musicplayerbeat.R

class MPlayerActivity : AppCompatActivity(), View.OnClickListener {


     private val cardView_hidden by binder<CardView>(R.id.cv_mplayer_hidden)
     private val cardView_main by binder<CardView>(R.id.cv_mplayer_main)
     private val btn_next by binder<ImageButton>(R.id.btn_next)
     private val view_custom_toolbar by binder<View>(R.id.custom_toolbar)
     lateinit var readWriteSongPermissionHandler : RequestPermissionHandler
     var songList:MutableList<BeatDto> = mutableListOf()
    lateinit var songManager: SongManager
    var sharedPreferences: SharedPreferences? = null
    lateinit var editor: SharedPreferences.Editor
    var file= Environment.getDataDirectory()
     val storage_check_pref="intent_memory"
     val song_list_pref="intent_songs"
     var current_memory:Float=0.0f
     val context = this



    companion object {
        val mediaPlayer:MediaPlayer=MediaPlayer()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mplayer)



        cardView_main.setOnTouchListener(object :OnSwipeTouchListener(this){
            override fun onSwipeTop() {
                cardView_hidden.visibility=View.VISIBLE
            }

        })

    }

    fun toolbarClick(view: View){
        when(view.id){
            R.id.btn_back ->  { val intent= Intent(this, MainActivity::class.java)
                startActivity(intent)}
        }


    }

    fun slideUp(view: View){
        var animate = TranslateAnimation(0f,0f,(view.height).toFloat(),0f);
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.startAnimation(animate);
    }

    override fun onClick(v: View?) {
        when(v!!.id){

        }
    }



}
