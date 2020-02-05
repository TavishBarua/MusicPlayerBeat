package com.tavish.musicplayerbeat.Fragments

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.VectorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.tavish.musicplayerbeat.Adapters.TrackCardSubAdapter
import com.tavish.musicplayerbeat.Adapters.TrackSubSongAdapter
import com.tavish.musicplayerbeat.Common
import com.tavish.musicplayerbeat.Helpers.MediaHelpers.MusicCursor
import com.tavish.musicplayerbeat.Models.SongDto
import com.tavish.musicplayerbeat.R
import com.tavish.musicplayerbeat.Utils.Constants
import com.tavish.musicplayerbeat.Utils.MusicUtils


class TrackSubSongFragment : Fragment() {

    private var mContext: Context? = null
    private var mApp: Common? = null
    private var mTrackCardSubAdapter: TrackCardSubAdapter? = null


    private var mBtnPlayAll: AppCompatImageButton? = null
    private var mTrackSubTextView: AppCompatTextView? = null
    private var mTrackHeaderTextView: AppCompatTextView? = null
    private var mSubTrackHeaderTextView: AppCompatTextView? = null
    private var mSubTrackHeaderImageView: AppCompatImageView? = null

    private var TRACK_HEADER_TITLE: String? = null
    private var TRACK_SUB_TITLE: String? = null
    private var mView: View? = null


    private var FROM_WHERE: String? = null
    private var SELECTION_VALUE: String? = null
    private var COVER_ART_PATH: String? = null

    private var mSongList: MutableList<SongDto>? = null
    private var mTrackSubSongAdapter: TrackSubSongAdapter? = null


    private var mRecyclerView: RecyclerView? = null
    private var mFrameLayout: FrameLayout? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_track_sub_song, container, false)
        mContext = Common.getInstance()
        mApp = mContext?.applicationContext as Common
        val bundle = arguments

        mFrameLayout = mView?.findViewById(R.id.fl_track_sub_song)
        //  mTrackSubTextView = mView?.findViewById(R.id.album_art_track_song_bg)
        mRecyclerView = mView?.findViewById(R.id.rv_sub_track_song_list)
        mTrackHeaderTextView = mView?.findViewById(R.id.header_track_song_text)
        mSubTrackHeaderTextView = mView?.findViewById(R.id.sub_track_song_text)
        mSubTrackHeaderImageView = mView?.findViewById(R.id.album_art_track_bg)

        TRACK_HEADER_TITLE = bundle?.getString(Constants.TRACK_HEADER_TITLE)
        TRACK_SUB_TITLE = bundle?.getString(Constants.TRACK_HEADER_SUB_TITLE)
        FROM_WHERE = bundle?.getString(Constants.FROM_WHERE)
        SELECTION_VALUE = "" + bundle?.getLong(Constants.SELECTION_VALUE)


        mSongList = MusicCursor.getSongsSelection(FROM_WHERE!!, SELECTION_VALUE!!)
        Picasso.get()
            .load(MusicUtils.getAlbumArtUri(mSongList?.get(0)?._albumId!!))
            .placeholder(R.drawable.ic_song_placeholder)
            .into(mSubTrackHeaderImageView)

        var color: Int? = null
        if (mSubTrackHeaderImageView?.drawable is VectorDrawable) {
            val bitmap = Bitmap.createBitmap(
                mSubTrackHeaderImageView?.drawable?.intrinsicWidth!!,
                mSubTrackHeaderImageView?.drawable?.intrinsicHeight!!,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            mSubTrackHeaderImageView?.drawable?.run {
                setBounds(0, 0, canvas.width, canvas.height)
                draw(canvas)
            }

            color = MusicUtils.getDominantColor(bitmap)
        } else {
            color =
                MusicUtils.getDominantColor((mSubTrackHeaderImageView?.drawable as BitmapDrawable).bitmap)
        }

        val colorDense = MusicUtils.getDarkOrLight(color, mContext?.applicationContext!!)
        mTrackHeaderTextView?.let {
            it.text = TRACK_HEADER_TITLE
            it.setTextColor(colorDense)
        }
        // should get activity context
        mTrackSubSongAdapter = TrackSubSongAdapter(activity!!, colorDense)

        mTrackSubSongAdapter?.updateContent(mSongList!!)


        mSubTrackHeaderTextView?.let {
            it.text = TRACK_SUB_TITLE
            it.setTextColor(colorDense)
        }




        mRecyclerView?.layoutManager = LinearLayoutManager(mContext)
        mRecyclerView?.adapter = mTrackSubSongAdapter

        addFragment()
        return mView
    }


    fun removeFragment() {
        MusicUtils.slideInFragmentAnimation(mFrameLayout!!, activity!!, this)
    }

    fun addFragment() {
        MusicUtils.slideOutFragmentAnimation(mFrameLayout!!)
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)

    }

    override fun onDetach() {
        super.onDetach()
    }


}
