package com.tavish.musicplayerbeat.Adapters

import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.view.View
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.tavish.musicplayerbeat.Activities.MPlayerActivity
import com.tavish.musicplayerbeat.Models.BeatDto
import com.tavish.musicplayerbeat.R
import com.tavish.musicplayerbeat.Utils.Constants
import kotlinx.android.synthetic.main.placeholder_song_item.view.*
import com.tavish.musicplayerbeat.Activities.MainActivity
import com.tavish.musicplayerbeat.Common
import com.tavish.musicplayerbeat.Models.SongDto
import com.tavish.musicplayerbeat.Utils.MusicUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.ArrayList
import android.view.animation.AlphaAnimation
import android.R.attr.animation
import android.app.Activity
import android.os.Parcelable
import android.util.Log

import android.view.animation.TranslateAnimation
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat


class SongAdapter(val context: Context) : RecyclerView.Adapter<SongAdapter.SongViewHolder>() {

    var mContext: Context = context;
    val songList: MutableList<SongDto> = mutableListOf()
    private var mApp: Common? = mContext.applicationContext as? Common

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.placeholder_song_item, parent, false)
        return SongViewHolder(view)
    }

    override fun getItemCount(): Int {
        return songList.size
    }

    fun update(data: MutableList<SongDto>) {
        songList.clear()
        songList.addAll(data)
        CoroutineScope(Dispatchers.Main).launch {
            notifyDataSetChanged()
        }

    }

    override fun onBindViewHolder(songViewHolder: SongViewHolder, pos: Int) {

        songViewHolder.itemView.img_song.transitionName="song_image$pos"
        //  myViewHolder.itemView.txt_title.isSelected=true
        songViewHolder.bindItems(songList[pos])
        if(pos%2==0)
            setAnimation(songViewHolder.itemView,-400f)
        else
            setAnimation(songViewHolder.itemView,400f)

    }

    inner class SongViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!), View.OnClickListener{


        fun bindItems(item: SongDto) {
            itemView.txt_title.text = item._title
            itemView.txt_artist.text = item._artist
            itemView.txt_duration.text = MusicUtils.makeShortTimeString(mContext, item._duration!! / 1000)

            // itemView.isSelected=true
            val uri = ContentUris.withAppendedId(
                Constants.sArtworkUri,
                item._albumId!!
            )
            Picasso.get()
                .load(uri)
                .placeholder(R.mipmap.ic_launcher_round)
                .into(itemView.img_song)

                itemView.setOnClickListener(this)

            //  val iv_song_img by lazy<TextView?> { itemView?.findViewById(R.id.img_song) }
        }

        override fun onClick(v: View?) {
            /* when (view!!.id) {
                 R.id.placeholder_song_cardview -> {
                     val intent = Intent(view.context, MainActivity::class.java)
                     intent.putExtra("pos", position)
                 }

             }*/
            mApp?.getPlayBackStarter()?.playSongs(songList, adapterPosition)
            var intent = Intent(mContext, MPlayerActivity::class.java)
            val pair = androidx.core.util.Pair<View,String>(v?.img_song, v?.img_song?.transitionName)
            val options =
                ActivityOptionsCompat.makeSceneTransitionAnimation(mContext as Activity, pair)
            intent.putParcelableArrayListExtra("data",songList as ArrayList<out Parcelable>) // songlist shouldnt be send
            Log.d("TRANS NAME",v?.img_song?.transitionName)
            mContext.startActivity(intent,options.toBundle())
        }



    }

    private fun setAnimation(view: View, fromX: Float) {
        val animation = TranslateAnimation(
            fromX, 0.0f, 0.0f, 0.0f
        )
        animation.duration = 250
        //animation.fillAfter = true
        view.startAnimation(animation)
    }

}