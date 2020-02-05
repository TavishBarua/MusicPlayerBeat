package com.tavish.musicplayerbeat.Activities

import android.content.Context
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jakewharton.rxbinding3.widget.TextViewTextChangeEvent
import com.jakewharton.rxbinding3.widget.textChangeEvents
import com.mancj.materialsearchbar.MaterialSearchBar
import com.tavish.musicplayerbeat.Common
import com.tavish.musicplayerbeat.Fragments.SearchAdapter
import com.tavish.musicplayerbeat.Fragments.TrackCardSubFragment
import com.tavish.musicplayerbeat.Fragments.TrackSubSongFragment
import com.tavish.musicplayerbeat.R
import com.tavish.musicplayerbeat.Utils.MusicUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import java.util.concurrent.TimeUnit


class SearchActivity : AppCompatActivity() {

    private var mQueryText: String? = null
    private var lastSearches: List<String>? = null
    // private var searchBar: MaterialSearchBar? = null
    private var mSearchBar: AppCompatEditText? = null
    private var mContext: Context? = null
    private var mApp: Common? = null
    private var mSearchResults: ArrayList<Any>? = null
    private val mMainParent: RelativeLayout? = null
    private var mSelectedPosition: Int? = null
    // private val mEtSearch: EditText? = null

    private var mRecyclerView: RecyclerView? = null
    private var mInpMgr: InputMethodManager? = null

    private var mSearchAdapter: SearchAdapter? = null

    private lateinit var mFragments: MutableList<Fragment>




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        mSearchBar = findViewById(R.id.searchBar)
        mRecyclerView = findViewById(R.id.rv_search_result_list)
        //searchBar?.setOnClickListener(this)
        // searchBar?.setOnSearchActionListener(this)
        mContext = applicationContext
        mApp = applicationContext as Common
        mFragments = mutableListOf()


        mInpMgr = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?

       // mRecyclerView?.layoutManager = LinearLayoutManager(this)

        val gridLayoutManager = GridLayoutManager(this, Common.getNumberOfColumns)
        gridLayoutManager.spanSizeLookup = object: GridLayoutManager.SpanSizeLookup(){
            override fun getSpanSize(position: Int): Int {
                if (mSearchAdapter?.getItemViewType(position)==0 ||  mSearchAdapter?.getItemViewType(position)==2)
                    return Common.getNumberOfColumns
                else
                    return 1
            }
        }

        mRecyclerView?.layoutManager = gridLayoutManager

        mSearchResults = arrayListOf()

        mSearchAdapter = SearchAdapter(this)
        mRecyclerView?.adapter = mSearchAdapter

        mSearchBar?.textChangeEvents()
            ?.debounce(175, TimeUnit.MILLISECONDS)
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribeWith(searchObserver())


    }

    override fun onBackPressed() {
        if (mFragments.size>0){
            val fragment = mFragments[mFragments.size-1]
            if(fragment is TrackSubSongFragment)
                fragment.removeFragment()
            if(fragment is TrackCardSubFragment)
                fragment.removeFragment()

            mFragments.remove(fragment)
            return
        }
        super.onBackPressed()
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        hideInputManager()
        return super.dispatchTouchEvent(ev)
    }

    private fun hideInputManager() {
        if (mSearchBar != null) {
            if (mInpMgr != null) {
                mInpMgr?.hideSoftInputFromWindow(mSearchBar?.getWindowToken(), 0)
            }
            mSearchBar?.clearFocus()
        }
    }


    fun searchObserver(): DisposableObserver<TextViewTextChangeEvent> {
        return object : DisposableObserver<TextViewTextChangeEvent>() {
            override fun onComplete() {

            }

            override fun onNext(onTextChange: TextViewTextChangeEvent) {
                var query = onTextChange.text.toString().trim()
                /*var query = onTextChange.text.toString().trim()
                if(query.isEmpty()){

                }else{

                }*/
                onSearchQueryTextChange(query)
            }

            override fun onError(e: Throwable) {

            }
        }
    }

    fun addFragment(fragment: Fragment) {

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.rl_search, fragment) // to be added frag parent layout
        fragmentTransaction.commitAllowingStateLoss()

        mFragments.add(fragment)
    }


    fun onSearchQueryTextChange(queryText: String?): Boolean {
        if (queryText.equals(mQueryText)) {
            return true
        }
        mQueryText = queryText
        if (mQueryText?.trim { it <= ' ' } != "") {
            this.mSearchResults?.clear()
            val songs = MusicUtils.searchSongs(this, mQueryText!!)
            val albums = MusicUtils.searchAlbums(mContext!!, mQueryText!!)
            val artists = mApp?.getDBAccessHelper()?.searchArtists(mQueryText!!)!!
            val genres = mApp?.getDBAccessHelper()?.searchGenre(mQueryText!!)!!

            if (songs.isNotEmpty()) {
                mSearchResults?.add("Songs")
                mSearchResults?.addAll(songs)
            }
            if (albums.isNotEmpty()) {
                mSearchResults?.add("Albums")
                mSearchResults?.addAll(albums)
            }
            if (artists.isNotEmpty()) {
                mSearchResults?.add("Artists")
                mSearchResults?.addAll(artists)
            }
            if(genres.isNotEmpty()){
               mSearchResults?.add("Genres")
                mSearchResults?.addAll(genres)
            }

        } else {
            mSearchResults?.clear()

        }
        mSearchAdapter?.update(mSearchResults)
        mSearchAdapter?.notifyDataSetChanged()
        return true
    }


}