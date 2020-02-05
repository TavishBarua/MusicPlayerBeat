package com.tavish.musicplayerbeat.Fragments

import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.transition.TransitionInflater
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import com.tavish.musicplayerbeat.Activities.MPlayerActivity
import com.tavish.musicplayerbeat.Common
import com.tavish.musicplayerbeat.Models.SongDto

import com.tavish.musicplayerbeat.R
import com.tavish.musicplayerbeat.Utils.SongDataHelper
import java.lang.Exception


class PlayerPagerFragment : Fragment() {

    //private var listener: OnFragmentInteractionListener? = null
    private var mContext: Context? = null
    private var mPosition: Int = 0
    private var mApp: Common? = null
    private var mSongDataHelper: SongDataHelper? = null


    private var tSongName: TextView? = null
    private var tSongAlbum: TextView? = null
    private var iSongImage: ImageView? = null


  /*  companion object{
        fun newInstance(currentItem: Int, songs:MutableList<SongDto>):PlayerPagerFragment{
            val playerActivity= PlayerPagerFragment()
            val bundle=Bundle()
            bundle.putInt("image", currentItem)
            bundle.putParcelableArrayList("transition_name",ArrayList<Parcelable>(songs))
            playerActivity.arguments = bundle


        }
    }*/


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        postponeEnterTransition()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)

        }

    }

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
            .noFade()
            .placeholder(R.drawable.ic_song_placeholder)
            .into(iSongImage, object: Callback{
                override fun onSuccess() {
                    startPostponedEnterTransition();
                }

                override fun onError(e: Exception?) {
                    startPostponedEnterTransition();
                }

            })

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
