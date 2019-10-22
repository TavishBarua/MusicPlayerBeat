package com.tavish.musicplayerbeat.Fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import com.tavish.musicplayerbeat.Activities.MPlayerActivity
import com.tavish.musicplayerbeat.Common

import com.tavish.musicplayerbeat.R
import com.tavish.musicplayerbeat.Utils.SongDataHelper


class PlayerPagerFragment : Fragment() {

    //private var listener: OnFragmentInteractionListener? = null
    private var mContext: Context? = null
    private var mPosition: Int = 0
    private var mApp: Common? = null
    private var mSongDataHelper: SongDataHelper? = null


    private var tSongName: TextView? = null
    private var tSongAlbum: TextView? = null
    private var iSongImage: ImageView? = null



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View? = inflater.inflate(R.layout.fragment_player_pager, container, false)
        mContext = activity?.applicationContext
        mPosition = arguments?.getInt("POSITION")!!

      /*  tSongName = view?.findViewById(R.id.txt_song_name)*/
      /*tSongAlbum = view?.findViewById(R.id.txt_song_album)*/
        iSongImage = view?.findViewById(R.id.img_cover_art)

        mApp = activity?.applicationContext!! as Common
        mSongDataHelper = SongDataHelper()
        mSongDataHelper!!.populateSongData(context!!, (activity as MPlayerActivity).mSongs, mPosition)

        Picasso.get()
            .load(mSongDataHelper?.mAlbumArtPath)
            .fit()
            .centerCrop()
            .placeholder(R.drawable.ic_song_placeholder)
            .into(iSongImage)

      /*  tSongName?.apply {

            text = mSongDataHelper?.mTitle
            isSelected = true
        }

        tSongAlbum?.apply {

            text = mSongDataHelper?.mAlbum + " - " + mSongDataHelper?.mArtist

            isSelected = true
        }*/
        return view
    }



    /*interface OnFragmentInteractionListener {
        fun onFragmentInteraction(uri: Uri)
    }*/

}
