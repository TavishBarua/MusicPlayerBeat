package com.tavish.musicplayerbeat.Adapters

import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.widget.ImageViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.tavish.musicplayerbeat.Activities.MPlayerActivity
import com.tavish.musicplayerbeat.Common
import com.tavish.musicplayerbeat.Fragments.CurrentPlayingBottomBarFragment
import com.tavish.musicplayerbeat.Models.SongDto
import com.tavish.musicplayerbeat.R
import com.tavish.musicplayerbeat.Utils.Constants
import kotlinx.android.synthetic.main.current_playing_bottom_item.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CurrentPlayingBottomBarAdapter( fragment:CurrentPlayingBottomBarFragment) :
    RecyclerView.Adapter<CurrentPlayingBottomBarAdapter.ItemViewHolder>() {
    var mFragment:CurrentPlayingBottomBarFragment?=null
    init {
        mFragment=fragment
    }

  //  var mContext: Context = context
    var songList: MutableList<SongDto> = mutableListOf()
   // private var mApp: Common? = mContext.applicationContext as? Common

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
            val view:View = LayoutInflater.from(parent.context).inflate(R.layout.current_playing_bottom_item, parent, false)
            return ItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return if(songList==null) 0 else songList.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {

        val uri = ContentUris.withAppendedId(
            Constants.sArtworkUri,
            songList[holder.adapterPosition]._albumId!!
        )
        Picasso.get()
            .load(uri)
            .placeholder(R.mipmap.ic_launcher_round)
            .into(holder.songImage)

        holder.title?.text = songList[holder.adapterPosition]._title
            holder.artist?.text = songList[holder.adapterPosition]._artist


    }

    fun updateSongData(mSongList:MutableList<SongDto>?){
       songList = mSongList!!
     //   notifyDataSetChanged()
        CoroutineScope(Dispatchers.Main).launch {
            notifyDataSetChanged()
        }
    }


     inner class ItemViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!){

         var title:AppCompatTextView? = null
         var artist:AppCompatTextView? = null
         var songImage:AppCompatImageView? = null

        init {
            title = itemView?.findViewById(R.id.txt_bottom_title)
            artist = itemView?.findViewById(R.id.txt_bottom_artist)
            songImage=itemView?.findViewById(R.id.img_bottom_song)
            itemView?.setOnClickListener{ mFragment?.startActivity(Intent(mFragment!!.activity, MPlayerActivity::class.java)) }
        }


       /* fun bindItems(item: SongDto) {
            itemView.txt_bottom_title.text = item._title
            itemView.txt_bottom_artist.text = item._artist

            val uri = ContentUris.withAppendedId(
                Constants.sArtworkUri,
                item._albumId!!
            )

            Picasso.get()
                .load(uri)
                .placeholder(R.mipmap.ic_launcher_round)
                .into(itemView.img_bottom_song)


            itemView.setOnClickListener{ mFragment?.startActivity(Intent(mFragment!!.activity, MPlayerActivity::class.java)) }

        }*/





    }

}