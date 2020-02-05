package com.tavish.musicplayerbeat.Fragments


import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.TranslateAnimation
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.tavish.musicplayerbeat.Activities.MPlayerActivity
import com.tavish.musicplayerbeat.Activities.MainActivity
import com.tavish.musicplayerbeat.Activities.SearchActivity
import com.tavish.musicplayerbeat.Common
import com.tavish.musicplayerbeat.Models.AlbumDto
import com.tavish.musicplayerbeat.Models.ArtistDto
import com.tavish.musicplayerbeat.Models.GenreDto
import com.tavish.musicplayerbeat.Models.SongDto
import com.tavish.musicplayerbeat.R
import com.tavish.musicplayerbeat.Utils.Constants
import com.tavish.musicplayerbeat.Utils.MusicUtils

class SearchAdapter(
    context: Context
) : RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

   // private lateinit var mContent: List<Any>
    private  var mContent: ArrayList<Any> = arrayListOf()
    private var mContext: Context = context
    private var mApp: Common = mContext.applicationContext as Common


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view: View? = null
        when (viewType) {
            0 -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.placeholder_song_item, parent, false)
                return SongDtoViewHolder(view)
            }
            1 -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.placeholder_grid_item, parent, false)
                return AlbumDtoViewHolder(view)
            }
            2 -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.placeholder_searchitem, parent, false)
                return HeadViewHolder(view)
            }
            3 -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.placeholder_grid_item, parent, false)
                return ArtistsDtoViewHolder(view)
            }
            4 -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.placeholder_grid_item, parent, false)
                return GenreDtoViewHolder(view)
            }
        }

        return ViewHolder(view!!)
    }

    override fun getItemViewType(position: Int): Int {
        if (mContent[position] is SongDto)
            return 0
        if (mContent[position] is AlbumDto)
            return 1
        if (mContent[position] is String)
            return 2
        if (mContent[position] is ArtistDto)
            return 3
        if (mContent[position] is GenreDto)
            return 4

        return 5
    }

    fun update(song: ArrayList<Any>?) {
        mContent.clear()
        mContent.addAll(song!!)
       // mContent = song!!
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        when (getItemViewType(position)) {
            0 -> {
                val songDtoholder = holder as SongDtoViewHolder
                val song = mContent.get(position) as SongDto
                songDtoholder.run {
                    mSongTitle?.text = song._title
                    mSongArtist?.text = song._artist
                    mSongDuration?.text =
                        MusicUtils.makeShortTimeString(mContext, song._duration?.div(1000)!!)
                }
                Picasso.get()
                    .load(MusicUtils.getAlbumArtUri(song._albumId!!).toString())
                    .placeholder(R.drawable.ic_song_placeholder)
                    .into(songDtoholder.mSongAlbumArt)
            }
            1 -> {
                val albumHolder = holder as AlbumDtoViewHolder
                val album = mContent.get(position) as AlbumDto
                albumHolder.run {
                    mArtistTitle?.text = album._album
                    mArtistSubText?.text = album._artist
                }
                Picasso.get()
                    .load(MusicUtils.getAlbumArtUri(album._id!!).toString())
                    .placeholder(R.drawable.ic_album)
                    .into(albumHolder.mArtistAlbumArt)
            }
            2 -> {
                val headHolder = holder as HeadViewHolder
                headHolder.mHeader?.text = mContent.get(position).toString()
            }
            3 -> {
                val artistHolder = holder as ArtistsDtoViewHolder
                val artist = mContent.get(position) as ArtistDto
                artistHolder.mArtistTitle?.text = artist._artistName
                Picasso.get()
                    .load(MusicUtils.getAlbumArtUri(artist._artistId!!).toString())
                    .placeholder(R.drawable.ic_song_placeholder)
                    .into(artistHolder.mArtistAlbumArt)

                try {
                    val trackNumbers = MusicUtils.makeLabel(
                        (mContext as SearchActivity).applicationContext,
                        R.plurals.Nsongs,
                        artist._noOfTracksByArtist!!
                    )
                    val albumNumbers = MusicUtils.makeLabel(
                        (mContext as SearchActivity).applicationContext,
                        R.plurals.Nalbums,
                        artist._noOfAlbumsByArtist!!
                    )
                    artistHolder.mArtistTitle?.text = "$trackNumbers | $albumNumbers"

                } catch (ex: Exception) {
                    ex.printStackTrace()
                    artistHolder.mArtistSubText?.visibility = View.INVISIBLE
                }

            }
            4 -> {
                val genreHolder = holder as GenreDtoViewHolder
                val genre = mContent.get(position) as GenreDto
                Picasso.get()
                    .load(genre._genreAlbumArt)
                    .placeholder(R.drawable.ic_album)
                    .into(genreHolder.mArtistAlbumArt)
            }

        }

        /* val item = mValues[position]
         holder.mIdView.text = item.id
         holder.mContentView.text = item.content

         with(holder.mView) {
             tag = item
             // setOnClickListener(mOnClickListener)
         }*/
    }


    override fun getItemCount(): Int{
        return mContent.size
    }

    open inner class ViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
    }

    inner class SongDtoViewHolder(itemView: View?) : ViewHolder(itemView!!), View.OnClickListener {

        var mSongTitle: AppCompatTextView? = itemView?.findViewById(R.id.txt_title)
        var mSongArtist: AppCompatTextView? = itemView?.findViewById(R.id.txt_artist)
        var mSongAlbumArt: AppCompatImageView? = itemView?.findViewById(R.id.img_song)
        var mSongDuration: AppCompatTextView? = itemView?.findViewById(R.id.txt_duration)


        init {
            itemView?.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            // why used list instead of single DTO can suffice the same condition on below
            //val options = ActivityOptions.makeCustomAnimation(mContext,R.transition.slide_top,R.transition.slide_bottom)
            val songList = ArrayList<SongDto>()
            songList.add(mContent[adapterPosition] as SongDto)
            mApp.getPlayBackStarter().playSongs(songList, 0)
            mContext.startActivity(Intent(mContext, MPlayerActivity::class.java))

        }
    }

    open inner class HeadViewHolder(itemView: View?) : ViewHolder(itemView!!) {

        var mHeader: AppCompatTextView? = itemView?.findViewById(R.id.item_number)

    }

    inner class ArtistsDtoViewHolder(itemView: View?) : ViewHolder(itemView!!),
        View.OnClickListener {

        var mArtistAlbumArt: AppCompatImageView? = itemView?.findViewById(R.id.img_grid_Album)
        var mArtistTitle: AppCompatTextView? = itemView?.findViewById(R.id.tv_grid_title)
        var mArtistSubText: AppCompatTextView? = itemView?.findViewById(R.id.tv_grid_sub_title)

        init {
            itemView?.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val bundle = Bundle()
            val artist = mContent.get(adapterPosition) as ArtistDto
            bundle.run {
                putString(Constants.TRACK_HEADER_TITLE, artist._artistName)
                putInt(Constants.TRACK_HEADER_SUB_TITLE, artist._noOfAlbumsByArtist!!)
                putString(Constants.FROM_WHERE, "ARTISTS")
                putString(Constants.COVER_PATH, artist._artistAlbumArt)
                putLong(Constants.SELECTION_VALUE, artist._artistId!!)
            }
            val trackCardSubFragment = TrackCardSubFragment()
            trackCardSubFragment.arguments = bundle
            (mContext as SearchActivity).addFragment(trackCardSubFragment)
           // trackCardSubFragment.addFragment()

        }


    }

    inner class AlbumDtoViewHolder(itemView: View?) : ViewHolder(itemView!!), View.OnClickListener {

        var mArtistAlbumArt: AppCompatImageView? = itemView?.findViewById(R.id.img_grid_Album)
        var mArtistTitle: AppCompatTextView? = itemView?.findViewById(R.id.tv_grid_title)
        var mArtistSubText: AppCompatTextView? = itemView?.findViewById(R.id.tv_grid_sub_title)

        init {
            itemView?.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val bundle = Bundle()
            val album = mContent.get(adapterPosition) as AlbumDto
            bundle.run {
                putString(Constants.TRACK_HEADER_TITLE, album._album)
                putString(Constants.TRACK_HEADER_SUB_TITLE, album._artist)
                putString(Constants.FROM_WHERE, "ALBUMS")
                putLong(Constants.SELECTION_VALUE, album._id!!)
               // putString(Constants.COVER_PATH, album._albumart)

            }
            val trackSubFragment = TrackSubSongFragment()
            trackSubFragment.arguments = bundle
            (mContext as SearchActivity).addFragment(trackSubFragment)
          //  trackSubFragment.addFragment()

        }


    }

    inner class GenreDtoViewHolder(itemView: View?) : ViewHolder(itemView!!), View.OnClickListener {

        var mArtistAlbumArt: AppCompatImageView? = itemView?.findViewById(R.id.img_grid_Album)
        var mArtistTitle: AppCompatTextView? = itemView?.findViewById(R.id.tv_grid_title)
        var mArtistSubText: AppCompatTextView? = itemView?.findViewById(R.id.tv_grid_sub_title)
        init {
            itemView?.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val bundle = Bundle()
            val genre = mContent.get(adapterPosition) as GenreDto
            bundle.run {
                putString(Constants.TRACK_HEADER_SUB_TITLE, genre._genreName)
                putInt(Constants.TRACK_HEADER_SUB_TITLE, genre._noOfAlbumsInGenre!!)
                putString(Constants.FROM_WHERE, "GENRES")
                putLong(Constants.SELECTION_VALUE, genre._genreId!!)
                putString(Constants.COVER_PATH, genre._genreAlbumArt)

            }
            val trackCardSubFragment = TrackCardSubFragment()
            trackCardSubFragment.arguments = bundle

            (mContext as MainActivity).addFragment(trackCardSubFragment)
           // trackCardSubFragment.addFragment()

        }


    }


}
