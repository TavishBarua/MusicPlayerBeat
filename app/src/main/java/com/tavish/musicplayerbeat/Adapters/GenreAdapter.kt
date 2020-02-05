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
import com.tavish.musicplayerbeat.Activities.MainActivity
import com.tavish.musicplayerbeat.Common
import com.tavish.musicplayerbeat.Fragments.GenreFragment
import com.tavish.musicplayerbeat.Fragments.TrackCardSubFragment
import com.tavish.musicplayerbeat.Helpers.MediaHelpers.MusicCursor
import com.tavish.musicplayerbeat.Models.GenreDto
import com.tavish.musicplayerbeat.R
import com.tavish.musicplayerbeat.Utils.Constants
import com.tavish.musicplayerbeat.Utils.MusicUtils
import kotlinx.android.synthetic.main.placeholder_grid_item.view.*

class GenreAdapter(genreFragment: GenreFragment): RecyclerView.Adapter<GenreAdapter.GenreViewHolder>(){


    private var genreList: MutableList<GenreDto>? = null
    private val mGenreFragment: GenreFragment? = genreFragment
    val mWidth = Common.getItemWidth()



    companion object {
        val mediaPlayer: MediaPlayer = MediaPlayer()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenreViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.placeholder_grid_item,parent,false)
        return GenreViewHolder(view)
    }


    fun updateData(data: MutableList<GenreDto>?) {
        this.genreList = data
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return genreList?.size ?: 0
    }

    override fun onBindViewHolder(genreViewHolder: GenreViewHolder, pos: Int) {

        //  myViewHolder.itemView.txt_title.isSelected=true
        genreViewHolder.bindItems(genreList!![pos])

    }


    inner class GenreViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!),View.OnClickListener{

        fun bindItems(item: GenreDto) {
            itemView.tv_grid_title.text=item._genreName
            itemView.tv_grid_sub_title.text=item._noOfAlbumsInGenre.toString()

            val params = itemView.img_grid_Album.layoutParams as RelativeLayout.LayoutParams
            params.width = mWidth
            params.height = mWidth
            itemView.img_grid_Album.layoutParams = params

            Picasso.get()
                .load(genreList?.get(adapterPosition)?._genreAlbumArt)
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
            if (mGenreFragment?.IsAlbumsEmpty
                    (MusicCursor.getAlbumsSelection("GENRES",""+genreList?.get(adapterPosition)?._genreId),adapterPosition)!!)
                return

            val bundle= Bundle()
            bundle.putString(Constants.TRACK_HEADER_TITLE,genreList?.get(adapterPosition)?._genreName)
            genreList?.get(adapterPosition)?._noOfAlbumsInGenre?.let { bundle.putInt(Constants.TRACK_HEADER_SUB_TITLE, it) }
            bundle.putString(Constants.FROM_WHERE,"GENRES")
            genreList?.get(adapterPosition)?._genreAlbumArt?.let { bundle.putString(Constants.COVER_PATH, it) }
            bundle.putLong(Constants.SELECTION_VALUE,genreList?.get(adapterPosition)?._genreId!!)

            val trackCardSubFragment = TrackCardSubFragment()
            trackCardSubFragment.arguments = bundle
            (mGenreFragment.activity as MainActivity).addFragment(trackCardSubFragment)
        }


    }

}