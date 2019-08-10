package com.tavish.musicplayerbeat.Fragments


import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tavish.musicplayerbeat.Adapters.AlbumAdapter
import com.tavish.musicplayerbeat.Activities.MainActivity
import com.tavish.musicplayerbeat.Common
import com.tavish.musicplayerbeat.Helpers.MediaHelpers.MusicCursor
import com.tavish.musicplayerbeat.Models.AlbumDto
import com.tavish.musicplayerbeat.Models.BeatDto
import com.tavish.musicplayerbeat.Models.SongDto

import com.tavish.musicplayerbeat.R
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import java.util.*


class AlbumFragment : Fragment() {


    var mAlbumList:MutableList<BeatDto> = mutableListOf()
    lateinit var mRecyclerView: RecyclerView
    lateinit var mAlbumAdapter: AlbumAdapter
    private var mAlbums: MutableList<AlbumDto>? = null
    private lateinit var mView: View
    private lateinit var mContext: Context
    private var mCompositeDisposable: CompositeDisposable?=null
    private lateinit var mCommon: Common



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContext=context!!
        mAlbumAdapter= AlbumAdapter(this)
        mCompositeDisposable= CompositeDisposable()

        mCommon=mContext.applicationContext!! as Common


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val numberColumns=Common.getNumberOfColumns
        mView = inflater.inflate(R.layout.fragment_album, container, false)
        mRecyclerView = mView.findViewById(R.id.rr_albums)
        mRecyclerView.layoutManager=GridLayoutManager(mContext, numberColumns)
        mRecyclerView.adapter=mAlbumAdapter



        return mView
    }

    override fun onResume() {
        loadAlbums()
        super.onResume()
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
    }

    override fun onDetach() {
        super.onDetach()
        mCompositeDisposable?.clear()
        mCompositeDisposable?.dispose()
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val mActivity = activity as MainActivity
        // mActivity.setAboutDataListener(this)
       /* mActivity.setAboutDataListener(object : MainActivity.OnSongReceivedListener {
            override fun onPassSongs(listSongs: MutableList<BeatDto>) {
                mAlbumList=listSongs
            }
        })
        mAlbumAdapter= AlbumAdapter(mAlbumList)
        mRecyclerView= view.findViewById(R.id.rr_albums)
        mRecyclerView.layoutManager= GridLayoutManager(activity, 2)
*//*        val spacingInPixels = resources.getDimensionPixelSize(R.dimen.mtrl_card_spacing)
        mRecyclerView.addItemDecoration(SpacesItemDecoration(spacingInPixels))*//*
        mRecyclerView.adapter=mAlbumAdapter
        mRecyclerView.setHasFixedSize(true)*/

    }

    fun loadAlbums(){
        mCompositeDisposable?.add(Observable.fromCallable { MusicCursor.getAlbumsList() }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object: DisposableObserver<MutableList<AlbumDto>>(){
                override fun onComplete() {

                }

                override fun onNext(data: MutableList<AlbumDto>) {
                    mAlbums=data
                    mAlbumAdapter.updateData(mAlbums)
                    mAlbumAdapter.notifyDataSetChanged()
                }

                override fun onError(e: Throwable) {
                    Log.d("FAILED", "" + e.message)
                }


            })
        )

    }






    fun IsAlbumEmpty(songs: MutableList<SongDto> , pos:Int):Boolean{
        if(songs.size==0){
            mAlbums?.removeAt(pos)
            mAlbumAdapter.updateData(mAlbums)
            Toast.makeText(mContext,"No songs in this album",Toast.LENGTH_SHORT).show()
            return true
        }
        return false
    }





}
