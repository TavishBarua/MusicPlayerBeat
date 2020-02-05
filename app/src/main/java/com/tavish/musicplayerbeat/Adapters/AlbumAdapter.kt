package com.tavish.musicplayerbeat.Adapters

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.tavish.musicplayerbeat.Common
import com.tavish.musicplayerbeat.Activities.MainActivity
import com.tavish.musicplayerbeat.Activities.SearchActivity
import com.tavish.musicplayerbeat.Fragments.AlbumFragment
import com.tavish.musicplayerbeat.Fragments.TrackCardSubFragment
import com.tavish.musicplayerbeat.Fragments.TrackSubSongFragment
import com.tavish.musicplayerbeat.Helpers.MediaHelpers.MusicCursor
import com.tavish.musicplayerbeat.Models.AlbumDto
import com.tavish.musicplayerbeat.R
import com.tavish.musicplayerbeat.Utils.Constants
import com.tavish.musicplayerbeat.Utils.MusicUtils
import kotlinx.android.synthetic.main.placeholder_grid_item.view.*

class AlbumAdapter(albumFragment: AlbumFragment): RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder>(){


    private var songList: MutableList<AlbumDto>? = null
    private val mAlbumFragment:AlbumFragment? = albumFragment
    val mWidth = Common.getItemWidth()



    companion object {
        val mediaPlayer: MediaPlayer = MediaPlayer()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.placeholder_grid_item,parent,false)
        return AlbumViewHolder(view)
    }


    fun updateData(data: MutableList<AlbumDto>?) {
        this.songList = data
        notifyDataSetChanged()
    }



    override fun getItemCount(): Int {
        return songList?.size ?: 0
    }

    override fun onBindViewHolder(albumViewHolder: AlbumViewHolder, pos: Int) {

        //  myViewHolder.itemView.txt_title.isSelected=true
        albumViewHolder.bindItems(songList!![pos])

    }



    inner class AlbumViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!), View.OnClickListener{




        fun bindItems(item: AlbumDto) {
            itemView.tv_grid_title.text=item._album
            itemView.tv_grid_sub_title.text=item._artist



            val params = itemView.img_grid_Album.layoutParams as RelativeLayout.LayoutParams
            params.width = mWidth
            params.height = mWidth
            itemView.img_grid_Album.layoutParams = params

            Picasso.get()
                .load(MusicUtils.getAlbumArtUri(songList?.get(position)?._id!!).toString())
                .fit()
                .centerCrop()
                .placeholder(R.mipmap.icn_beatdrop)
                .into(itemView.img_grid_Album)

            itemView.setOnClickListener(this)
            //  val iv_song_img by lazy<TextView?> { itemView?.findViewById(R.id.img_song) }
        }


        override fun onClick(view: View?) {
            val id:Int=view!!.id
            when(id){
                R.id.placeholder_album_cardview->{
                    val intent= Intent(view.context, MainActivity::class.java)
                    intent.putExtra("pos", position)
                }
            }
            if (mAlbumFragment?.IsAlbumEmpty
                    (MusicCursor.getSongsSelection("ALBUMS",""+songList?.get(adapterPosition)?._id),adapterPosition)!!)
                return

            val bundle=Bundle()
            bundle.putString(Constants.TRACK_HEADER_TITLE,songList?.get(adapterPosition)?._album)
            bundle.putString(Constants.TRACK_HEADER_SUB_TITLE,songList?.get(adapterPosition)?._artist)
            bundle.putString(Constants.FROM_WHERE,"ALBUMS")
            bundle.putLong(Constants.SELECTION_VALUE,songList?.get(adapterPosition)?._id!!)

            val trackSubFragment = TrackSubSongFragment()
            trackSubFragment.arguments = bundle
            (mAlbumFragment.activity as MainActivity).addFragment(trackSubFragment)

        }


    }

}