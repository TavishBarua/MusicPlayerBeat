package com.tavish.musicplayerbeat.Adapters

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.RecyclerView
import com.tavish.musicplayerbeat.Activities.MPlayerActivity
import com.tavish.musicplayerbeat.Common
import com.tavish.musicplayerbeat.Models.SongDto
import com.tavish.musicplayerbeat.R
import com.tavish.musicplayerbeat.Utils.MusicUtils

class TrackSubSongAdapter(context: Context, color: Int): RecyclerView.Adapter<TrackSubSongAdapter.TrackSubSongViewHolder>() {

    private var mContext: Context? = context
    private var mContent:MutableList<SongDto>?=null
    private var mLinearLayoutCompat:LinearLayoutCompat?=null
    private var mApp: Common? = mContext?.applicationContext as Common
    private var mColor: Int = color
   // private var mTrackSubSongFragment:TrackSubSongFragment?=trackSubSongFragment

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackSubSongViewHolder {
        val view =  LayoutInflater.from(parent.context).inflate(R.layout.placeholder_song_sub_item,parent,false)
     //   mLinearLayoutCompat = view.findViewById(R.id.ll_placeholder_cv)
      //  mLinearLayoutCompat?.setBackgroundColor(Color.parseColor("#99FFFFFF"))
        return TrackSubSongViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mContent?.size!!
    }


    override fun onBindViewHolder(holder: TrackSubSongViewHolder, position: Int) {
        holder.tv_TrackTitle?.text= mContent?.get(position)?._title
        holder.tv_TrackTitle?.setTextColor(mColor)

        holder.tv_TrackSubTitle?.text= mContent?.get(position)?._artist
        holder.tv_TrackSubTitle?.setTextColor(mColor)

        holder.tv_TrackDuration?.text = MusicUtils.makeShortTimeString(mContext!!,mContent?.get(position)?._duration?.div(1000)!!)
        holder.tv_TrackDuration?.setTextColor(mColor)

        holder.itemView.setOnClickListener {
           mApp?.getPlayBackStarter()?.playSongs(mContent!!,position)
           mContext?.startActivity(Intent(mContext,MPlayerActivity::class.java))
        }
    }

    fun updateContent(content:MutableList<SongDto>){
        mContent=content
        notifyDataSetChanged()

    }



    inner class TrackSubSongViewHolder(itemView: View?): RecyclerView.ViewHolder(itemView!!) {
        val tv_TrackTitle= itemView?.findViewById<AppCompatTextView>(R.id.txt_title)
        val tv_TrackSubTitle= itemView?.findViewById<AppCompatTextView>(R.id.txt_artist)
        val tv_TrackDuration= itemView?.findViewById<AppCompatTextView>(R.id.txt_duration)
    }
}