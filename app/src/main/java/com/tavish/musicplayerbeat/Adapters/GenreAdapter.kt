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
import com.tavish.musicplayerbeat.Helpers.MediaHelpers.MusicCursor
import com.tavish.musicplayerbeat.Models.GenreDto
import com.tavish.musicplayerbeat.R
import com.tavish.musicplayerbeat.Utils.Constants
import com.tavish.musicplayerbeat.Utils.MusicUtils
import kotlinx.android.synthetic.main.placeholder_grid_item.view.*

class GenreAdapter(genreFragment: GenreFragment): RecyclerView.Adapter<GenreAdapter.GenreViewHolder>(), View.OnClickListener {


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

    override fun onClick(v: View?) {

        /*if(v?.id==R.id.overflow){
            mAlbumFragment
        }*/
    }

    inner class GenreViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!), AdapterView.OnItemClickListener {

        fun bindItems(item: GenreDto) {
            itemView.gridViewTitleText.text=item._genreName
            itemView.gridViewSubText.text=item._noOfAlbumsInGenre.toString()

            val params = itemView.grid_Img_Album.layoutParams as RelativeLayout.LayoutParams
            params.width = mWidth
            params.height = mWidth
            itemView.grid_Img_Album.layoutParams = params

            Picasso.get()
                .load(MusicUtils.getAlbumArtUri(genreList?.get(position)?._genreId!!).toString())
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
            if (mGenreFragment?.IsAlbumsEmpty
                    (MusicCursor.getAlbumsSelection("GENRES",""+genreList?.get(adapterPosition)?._genreId),adapterPosition)!!)
                return

            val bundle= Bundle()
            bundle.putString(Constants.HEADER_TITLE,genreList?.get(adapterPosition)?._genreName)
            genreList?.get(adapterPosition)?._noOfAlbumsInGenre?.let { bundle.putInt(Constants.HEADER_SUB_TITLE, it) }
            bundle.putString(Constants.FROM_WHERE,"GENRES")
            genreList?.get(adapterPosition)?._genreAlbumArt?.let { bundle.putString(Constants.COVER_PATH, it) }
            bundle.putLong(Constants.SELECTION_VALUE,genreList?.get(adapterPosition)?._genreId!!)



        }



    }

}