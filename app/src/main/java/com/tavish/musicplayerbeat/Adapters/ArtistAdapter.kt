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
import com.tavish.musicplayerbeat.Fragments.ArtistFragment
import com.tavish.musicplayerbeat.Helpers.MediaHelpers.MusicCursor
import com.tavish.musicplayerbeat.Models.ArtistDto
import com.tavish.musicplayerbeat.R
import com.tavish.musicplayerbeat.Utils.Constants
import com.tavish.musicplayerbeat.Utils.MusicUtils
import kotlinx.android.synthetic.main.placeholder_grid_item.view.*

class ArtistAdapter(artistFragment: ArtistFragment): RecyclerView.Adapter<ArtistAdapter.ArtistViewHolder>(), View.OnClickListener {


    private var artistList: MutableList<ArtistDto>? = null
    private val mArtistFragment: ArtistFragment? = artistFragment
    val mWidth = Common.getItemWidth()



    companion object {
        val mediaPlayer: MediaPlayer = MediaPlayer()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtistViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.placeholder_grid_item,parent,false)
        return ArtistViewHolder(view)
    }


    fun updateData(data: MutableList<ArtistDto>?) {
        this.artistList = data
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return artistList?.size ?: 0
    }

    override fun onBindViewHolder(artistViewHolder: ArtistViewHolder, pos: Int) {

        //  myViewHolder.itemView.txt_title.isSelected=true
        artistViewHolder.bindItems(artistList!![pos])

    }

    override fun onClick(v: View?) {

        /*if(v?.id==R.id.overflow){
            mAlbumFragment
        }*/
    }

    inner class ArtistViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!), AdapterView.OnItemClickListener {

        fun bindItems(item: ArtistDto) {
            itemView.gridViewTitleText.text=item._artistName
            itemView.gridViewSubText.text=item._noOfAlbumsByArtist.toString()

            val params = itemView.grid_Img_Album.layoutParams as RelativeLayout.LayoutParams
            params.width = mWidth
            params.height = mWidth
            itemView.grid_Img_Album.layoutParams = params

            Picasso.get()
                .load(MusicUtils.getAlbumArtUri(artistList?.get( position)?._artistId!!).toString())
                .fit()
                .centerCrop()
                .placeholder(R.mipmap.icn_beatdrop)
                .into(itemView.grid_Img_Album)

            //  val iv_song_img by lazy<TextView?> { itemView?.findViewById(R.id.img_song) }
        }



        @Suppress("NAME_SHADOWING")
        override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            val id:Int=view!!.id
            when(id){
                R.id.placeholder_album_cardview->{
                    val intent= Intent(view.context, MainActivity::class.java)
                    intent.putExtra("pos", position)
                }
            }
            if (mArtistFragment?.IsAlbumsEmpty
                    (MusicCursor.getAlbumsSelection("ARTISTS",""+artistList?.get(adapterPosition)?._artistId),adapterPosition)!!)
                return

            val bundle= Bundle()
            bundle.putString(Constants.HEADER_TITLE,artistList?.get(adapterPosition)?._artistName)
            artistList?.get(adapterPosition)?._noOfAlbumsByArtist?.let { bundle.putInt(Constants.HEADER_SUB_TITLE, it) }
            bundle.putString(Constants.FROM_WHERE,"ARTISTS")
            artistList?.get(adapterPosition)?._noOfAlbumsByArtist?.let { bundle.putInt(Constants.COVER_PATH, it) }
            bundle.putLong(Constants.SELECTION_VALUE,artistList?.get(adapterPosition)?._artistId!!)



        }



    }

}