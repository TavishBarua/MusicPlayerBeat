package com.tavish.musicplayerbeat.Fragments


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tavish.musicplayerbeat.Adapters.GenreAdapter
import com.tavish.musicplayerbeat.Common
import com.tavish.musicplayerbeat.Models.AlbumDto
import com.tavish.musicplayerbeat.Models.GenreDto

import com.tavish.musicplayerbeat.R
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import java.util.ArrayList

class GenreFragment : Fragment() {


    private var mGenres: MutableList<GenreDto>? = null
    lateinit var mRecyclerView: RecyclerView
    lateinit var mGenreAdapter: GenreAdapter
    private var mApp: Common? = null
    private var mContext: Context? = null
    private lateinit var mView: View
    private var mCompositeDisposable: CompositeDisposable? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContext = context
        setHasOptionsMenu(true)
        mApp = mContext?.getApplicationContext() as Common
        mCompositeDisposable = CompositeDisposable()
        mGenreAdapter = GenreAdapter(this)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val numberColumns=Common.getNumberOfColumns
        mView= inflater.inflate(R.layout.fragment_genre, container, false)

        mRecyclerView = mView.findViewById(R.id.rr_genres)
        mRecyclerView.layoutManager= GridLayoutManager(mContext, numberColumns)
        mRecyclerView.adapter=mGenreAdapter

        return mView
    }


    override fun onResume() {
        loadGenres()
        super.onResume()
    }

    fun loadGenres(){
        mCompositeDisposable?.add(Observable.fromCallable { mApp?.getDBAccessHelper()?.getAllGenre() }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object: DisposableObserver<MutableList<GenreDto>>(){
                override fun onComplete() {
                }

                override fun onNext(data: MutableList<GenreDto>) {
                    mGenres=data
                    mGenreAdapter.updateData(mGenres)
                    mGenreAdapter.notifyDataSetChanged()
                }

                override fun onError(e: Throwable) {
                }


            })
        )



    }

    fun IsAlbumsEmpty(albums: MutableList<AlbumDto>, pos: Int): Boolean? {
        if (albums.size == 0) {
            mGenres?.removeAt(pos)
            mGenreAdapter.updateData(mGenres)
            Toast.makeText(mContext, "No songs in this album", Toast.LENGTH_SHORT).show()
            return true
        }
        return false


    }


}
