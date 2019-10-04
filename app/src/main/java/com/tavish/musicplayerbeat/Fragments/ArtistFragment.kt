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
import com.tavish.musicplayerbeat.Adapters.ArtistAdapter
import com.tavish.musicplayerbeat.Common
import com.tavish.musicplayerbeat.Helpers.MediaHelpers.MusicCursor
import com.tavish.musicplayerbeat.Models.AlbumDto
import com.tavish.musicplayerbeat.Models.ArtistDto

import com.tavish.musicplayerbeat.R
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers

class ArtistFragment : Fragment() {


    private var mArtistList: MutableList<ArtistDto>? = null
    lateinit var mArtistAdapter: ArtistAdapter
    private var mContext: Context? = null
    private lateinit var mRecyclerView: RecyclerView
    private var mApp:Common?=null
    private var compositeDisposable:CompositeDisposable?=null
    private lateinit var mView:View





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContext=context
        mApp=mContext?.applicationContext as Common
        setHasOptionsMenu(true)
        mArtistAdapter= ArtistAdapter(this)
        compositeDisposable= CompositeDisposable()



    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val numberColumns=Common.getNumberOfColumns
        mView = inflater.inflate(R.layout.fragment_artist, container, false)
        mRecyclerView= mView.findViewById(R.id.rr_artists)
        mRecyclerView.layoutManager = GridLayoutManager(mContext, numberColumns)
        mRecyclerView.adapter=mArtistAdapter
        return mView
    }

    override fun onResume() {
        loadArtists()
        super.onResume()
    }

    fun loadArtists(){
        compositeDisposable?.add(Observable.fromCallable { mApp?.getDBAccessHelper()?.getAllArtist() }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object:DisposableObserver<MutableList<ArtistDto>>(){
                override fun onComplete() {

                }

                override fun onNext(data: MutableList<ArtistDto>) {
                    mArtistList=data
                    mArtistAdapter.updateData(mArtistList)
                    mArtistAdapter.notifyDataSetChanged()
                }

                override fun onError(e: Throwable) {
                    Log.d("FAILED", "" + e.message)
                }
            })
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable?.dispose()
    }

    fun IsAlbumsEmpty(albums:MutableList<AlbumDto>, pos:Int):Boolean{
        if(albums.size==0){
            mArtistList?.removeAt(pos)
            mArtistAdapter.updateData(mArtistList)
            Toast.makeText(mContext, R.string.no_albums_by_this_artist, Toast.LENGTH_SHORT).show()
            return true
        }
        return false
    }




}
