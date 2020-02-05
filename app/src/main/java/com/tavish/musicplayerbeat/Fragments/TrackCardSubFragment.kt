package com.tavish.musicplayerbeat.Fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.tavish.musicplayerbeat.Activities.MPlayerActivity
import com.tavish.musicplayerbeat.Adapters.TrackCardSubAdapter
import com.tavish.musicplayerbeat.Common
import com.tavish.musicplayerbeat.Helpers.MediaHelpers.MusicCursor
import com.tavish.musicplayerbeat.Models.AlbumDto
import com.tavish.musicplayerbeat.Models.SongDto
import com.tavish.musicplayerbeat.R
import com.tavish.musicplayerbeat.Utils.Constants
import com.tavish.musicplayerbeat.Utils.MusicUtils


class TrackCardSubFragment : Fragment() {

   // private var listener: OnFragmentInteractionListener? = null
    private var mContext: Context? = null
    private var mApp: Common? = null
    private var mTrackCardSubAdapter: TrackCardSubAdapter? = null
    private var mRelativeLayout: RelativeLayout? = null

    private var mTrackImage: AppCompatImageView? = null
    private var mBtnPlayAll: AppCompatImageButton? = null
    private var mTrackSubTextView: AppCompatTextView? = null
    private var mTrackHeaderTextView: AppCompatTextView? = null

    private var TRACK_HEADER_TITLE: String? = null
    private var TRACK_SUB_TITLE = 0


    private var FROM_WHERE: String? = null
    private var SELECTION_VALUE: String? = null
    private var COVER_ART_PATH: String? = null

    private var mAlbums: MutableList<AlbumDto>? = null


    private var mRecyclerView: RecyclerView? = null
    private var mFrameLayout: FrameLayout? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val mView = inflater.inflate(R.layout.fragment_track_sub, container, false)
        mContext = Common.getInstance()
        mApp = mContext?.applicationContext as Common
        val bundle = arguments
        val numberColumns=Common.getNumberOfColumns
       // mRelativeLayout = mView?.findViewById(R.id.gridAlbumItemLayout)
        mFrameLayout = mView?.findViewById(R.id.fl_track_card_sub)
        mTrackImage = mView?.findViewById(R.id.album_art_track_song_bg)
        mTrackSubTextView = mView?.findViewById(R.id.header_track_text)
        mTrackHeaderTextView = mView?.findViewById(R.id.sub_track_text)
        mBtnPlayAll = mView?.findViewById(R.id.btn_track)

        TRACK_HEADER_TITLE = bundle?.getString(Constants.TRACK_HEADER_TITLE)
        TRACK_SUB_TITLE = bundle?.getInt(Constants.TRACK_HEADER_SUB_TITLE)!!
        FROM_WHERE = bundle.getString(Constants.FROM_WHERE)
        SELECTION_VALUE = bundle.getLong(Constants.SELECTION_VALUE).toString()
        COVER_ART_PATH = bundle.getString(Constants.COVER_PATH)

        mTrackHeaderTextView?.text = TRACK_HEADER_TITLE



        mRecyclerView = mView?.findViewById(R.id.rv_sub_track_list)

       // mRecyclerView?.layoutManager = LinearLayoutManager(mContext, RecyclerView.HORIZONTAL, false)
        mRecyclerView?.layoutManager = GridLayoutManager(mContext, numberColumns)

        mTrackCardSubAdapter = TrackCardSubAdapter(this)
        mRecyclerView?.adapter = mTrackCardSubAdapter

        //pop menu TO-DO

        mBtnPlayAll?.setOnClickListener {
            val songs = MusicCursor.getSongsSelection(FROM_WHERE!!, SELECTION_VALUE!!)
            if (songs.size > 0) {
                mApp?.getPlayBackStarter()?.playSongs(songs, 0)
                startActivity(Intent(mContext, MPlayerActivity::class.java))
            } else {
                //removeFragment()
            }

        }

        Picasso.get().load(COVER_ART_PATH).placeholder(R.drawable.ic_song_placeholder)
            .into(mTrackImage)

        addFragment()

        return mView
    }



    override fun onResume() {
        super.onResume()
        mAlbums=MusicCursor.getAlbumsSelection(FROM_WHERE!!, SELECTION_VALUE!!)
        mTrackCardSubAdapter?.updateContent(mAlbums!!)
        if (mAlbums?.size!!>0){
            //removeFragment()
        }
    }

    fun removeFragment(){
        MusicUtils.slideInFragmentAnimation(mFrameLayout!!,activity!!, this)
    }

    fun addFragment(){
        MusicUtils.slideOutFragmentAnimation(mFrameLayout!!)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
       
    }

    override fun onDetach() {
        super.onDetach()
       // listener = null
    }


    fun checkSongsEmpty(songs:MutableList<SongDto>,position: Int):Boolean{
        if (songs.size == 0){
            mAlbums?.removeAt(position)
            mTrackCardSubAdapter?.updateContent(mAlbums!!)
            Toast.makeText(mContext, "NO Songs in this Album", Toast.LENGTH_SHORT).show()
            return true
        }
        return false
    }


}
