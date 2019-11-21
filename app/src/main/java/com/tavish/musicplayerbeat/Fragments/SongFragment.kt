package com.tavish.musicplayerbeat.Fragments


import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CursorAdapter
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tavish.musicplayerbeat.Adapters.SongAdapter
import com.tavish.musicplayerbeat.Helpers.RequestPermissionHandler
import com.tavish.musicplayerbeat.Helpers.SongManager
import com.tavish.musicplayerbeat.Models.BeatDto


import com.tavish.musicplayerbeat.Helpers.SpacesItemDecoration
import com.tavish.musicplayerbeat.Activities.MainActivity
import com.tavish.musicplayerbeat.Common
import com.tavish.musicplayerbeat.Helpers.Listeners.HideSheetScrollListeners
import com.tavish.musicplayerbeat.Helpers.MediaHelpers.MusicCursor
import com.tavish.musicplayerbeat.Interfaces.IScrollListener
import com.tavish.musicplayerbeat.Models.SongDto
import com.tavish.musicplayerbeat.R
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import java.util.*


class SongFragment : Fragment(){



 //   lateinit var mRecyclerView:RecyclerView
    lateinit var songManager: SongManager
    var file=Environment.getDataDirectory()

    lateinit var readWriteSongPermissionHandler : RequestPermissionHandler
    var sharedPreferences:SharedPreferences? = null
    lateinit var editor:SharedPreferences.Editor
    val storage_check_pref="intent_memory"
    val song_list_pref="intent_songs"
    var current_memory:Float=0.0f

    private val COMMON_TAG : String = "CombinedLifeCycle"
    private val FRAGMENT_NAME : String? = SongFragment::class.simpleName
    private val TAG : String = COMMON_TAG
    private var mOnScrollListener:IScrollListener?=null

    private var mCompositeDisposable: CompositeDisposable? = null
    private lateinit var mContext: Context
    private lateinit var mCommon: Common
   private lateinit var songList:MutableList<SongDto>
    private lateinit var recyclerView: RecyclerView
    private lateinit var songAdapter: SongAdapter



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view:View = inflater.inflate(R.layout.fragment_song, container, false)
        recyclerView= view.findViewById(R.id.rr_songs)
        recyclerView.layoutManager= LinearLayoutManager(activity,RecyclerView.VERTICAL,false)
        songAdapter = SongAdapter(mContext)
        recyclerView.adapter=songAdapter

        recyclerView.run {
            recyclerView= findViewById(R.id.rr_songs)
            layoutManager = LinearLayoutManager(activity,RecyclerView.VERTICAL,false)
            songAdapter = SongAdapter(mContext)
            adapter =songAdapter

            addOnScrollListener(object:HideSheetScrollListeners(){
                override fun onHide() {
                    mOnScrollListener?.scrollUp()
                }

                override fun onShow() {
                    mOnScrollListener?.scrollDown()
                }
            })
        }

        Log.i(TAG, "$FRAGMENT_NAME onCreateView")
        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
       super.onCreate(savedInstanceState)
        mContext = context!!
        setHasOptionsMenu(true)
        mCompositeDisposable= CompositeDisposable()
        mCommon = mContext.applicationContext as Common
        songList= mutableListOf()


    }

    fun loadData(){

        mCompositeDisposable?.add(Observable.fromCallable {MusicCursor.getSongsSelection("SONGS","")} // to do something with context parameter in getsongscollection method
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableObserver<MutableList<SongDto>>(){
                override fun onComplete() {
                }

                override fun onNext(songDto: MutableList<SongDto>) {
                   songList.clear()
                    songList.addAll(songDto)
                    songAdapter.update(songList)

                }

                override fun onError(e: Throwable) {
                }

            })
        )
    }

    override fun onResume() {
        loadData()
        super.onResume()
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is MainActivity){
            mOnScrollListener = context
        }
    }

    override fun onDetach() {
        super.onDetach()
        mCompositeDisposable?.dispose()
        mOnScrollListener=null
    }

/*

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        readWriteSongPermissionHandler.onActivityResult(requestCode)
        Log.i(TAG,FRAGMENT_NAME+" onActivityResult")
    }
*/

    /*
        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            val mActivity = activity as MainActivity
            // mActivity.setAboutDataListener(this)
            mActivity.setAboutDataListener(object : MainActivity.OnSongReceivedListener {
                override fun onPassSongs(listSongs: MutableList<BeatDto>) {
                    songList=listSongs
                }
            })
            songAdapter= SongAdapter(songList)
            mRecyclerView= view.findViewById(R.id.rr_songs)
            mRecyclerView.layoutManager= LinearLayoutManager(activity,RecyclerView.VERTICAL,false)
            val spacingInPixels = resources.getDimensionPixelSize(R.dimen.mtrl_card_spacing)
            mRecyclerView.addItemDecoration(SpacesItemDecoration(spacingInPixels))
            mRecyclerView.adapter=songAdapter
            mRecyclerView.setHasFixedSize(true)
            Log.i(TAG,FRAGMENT_NAME+" onViewCreated")
        }*/




}
