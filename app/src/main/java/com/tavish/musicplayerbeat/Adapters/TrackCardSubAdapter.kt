package com.tavish.musicplayerbeat.Adapters

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.tavish.musicplayerbeat.Activities.MPlayerActivity
import com.tavish.musicplayerbeat.Activities.MainActivity
import com.tavish.musicplayerbeat.Activities.SearchActivity
import com.tavish.musicplayerbeat.Fragments.TrackCardSubFragment
import com.tavish.musicplayerbeat.Fragments.TrackSubSongFragment
import com.tavish.musicplayerbeat.Helpers.MediaHelpers.MusicCursor
import com.tavish.musicplayerbeat.Models.AlbumDto
import com.tavish.musicplayerbeat.Models.SongDto
import com.tavish.musicplayerbeat.R
import com.tavish.musicplayerbeat.Utils.Constants
import com.tavish.musicplayerbeat.Utils.MusicUtils
import kotlinx.android.synthetic.main.placeholder_grid_item.view.*

class TrackCardSubAdapter(trackCardSubFragment:TrackCardSubFragment): RecyclerView.Adapter<TrackCardSubAdapter.TrackSubViewHolder>() {

    private var mContent:MutableList<AlbumDto>?=null
    private var mTrackCardSubFragment:TrackCardSubFragment?=trackCardSubFragment
    private var mRelativeLayout: RelativeLayout? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackSubViewHolder {
        val view =  LayoutInflater.from(parent.context).inflate(R.layout.placeholder_grid_item,parent,false)
        mRelativeLayout = view?.findViewById(R.id.gridAlbumItemLayout)
       /* val params  = mRelativeLayout?.layoutParams
        params?.width = 200
        params?.height = 260
        mRelativeLayout?.layoutParams = params*/
        return TrackSubViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mContent?.size!!
    }


    override fun onBindViewHolder(holder: TrackSubViewHolder, position: Int) {

       holder.tv_TrackTitle?.text= mContent?.get(position)?._album
       holder.tv_TrackSubTitle?.text= mContent?.get(position)?._artist

        Picasso.get().load(MusicUtils.getAlbumArtUri(mContent?.get(position)?._id!!))
            .placeholder(R.drawable.ic_song_placeholder)
            .into(holder.img_TrackArt)


        holder.itemView.setOnClickListener {

            if (mTrackCardSubFragment?.checkSongsEmpty(MusicCursor.getSongsSelection("ALBUMS",mContent?.get(position)?._id.toString()),position)!!)
                return@setOnClickListener

            val bundle= Bundle()
            bundle.run {
                putString(Constants.TRACK_HEADER_TITLE, mContent?.get(position)?._album);
                putString(Constants.TRACK_HEADER_SUB_TITLE, mContent?.get(position)?._artist);
                putString(Constants.FROM_WHERE, "ALBUMS");
                putLong(Constants.SELECTION_VALUE, mContent?.get(position)?._id!!)

            }
            val trackSubFragment=TrackSubSongFragment()
            trackSubFragment.arguments=bundle


            // ACTION: needs to be reconsider the format
           if(mTrackCardSubFragment?.activity is MainActivity){
               (mTrackCardSubFragment?.activity as MainActivity).addFragment(trackSubFragment)
           }
            if(mTrackCardSubFragment?.activity is SearchActivity){
                (mTrackCardSubFragment?.activity as SearchActivity).addFragment(trackSubFragment)
            }
            if(mTrackCardSubFragment?.activity is MPlayerActivity){
                (mTrackCardSubFragment?.activity as MPlayerActivity).addFragment(trackSubFragment)
            }

        }
    }

    fun updateContent(content:MutableList<AlbumDto>){
        mContent=content
        notifyDataSetChanged()
    }



    inner class TrackSubViewHolder(itemView: View?): RecyclerView.ViewHolder(itemView!!) {
        val tv_TrackTitle= itemView?.findViewById<AppCompatTextView>(R.id.tv_grid_title)
        val tv_TrackSubTitle= itemView?.findViewById<AppCompatTextView>(R.id.tv_grid_sub_title)
        val img_TrackArt= itemView?.findViewById<AppCompatImageView>(R.id.img_grid_Album)



    }
}
