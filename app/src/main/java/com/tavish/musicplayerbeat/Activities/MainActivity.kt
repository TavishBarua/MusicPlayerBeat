package com.tavish.musicplayerbeat.Activities

//import com.tavish.musicplayerbeat.Helpers.RequestPermissionHandler
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Parcelable
import android.preference.PreferenceManager
import android.transition.TransitionInflater
import android.util.Log
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.tavish.musicplayerbeat.Adapters.MenuCardAdapter
import com.tavish.musicplayerbeat.Adapters.SongAdapter
import com.tavish.musicplayerbeat.Common
import com.tavish.musicplayerbeat.Fragments.*
import com.tavish.musicplayerbeat.Helpers.Listeners.HideSheetScrollListeners
import com.tavish.musicplayerbeat.Helpers.MediaHelpers.MusicCursor
import com.tavish.musicplayerbeat.Helpers.SongManager
import com.tavish.musicplayerbeat.Interfaces.IScrollListener
import com.tavish.musicplayerbeat.Interfaces.SharedResourceOnItemClickActivity
import com.tavish.musicplayerbeat.Models.AlbumDto
import com.tavish.musicplayerbeat.Models.SongDto
import com.tavish.musicplayerbeat.R
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import java.lang.ref.WeakReference


class MainActivity : AppCompatActivity(), IScrollListener, SharedResourceOnItemClickActivity{
   // private val tabLayout by binder<BeatCustomTabLayout>(R.id.tabs)


  //  private val appBarLayout by binder<AppBarLayout>(R.id.appBarLayout)
    private lateinit var context:WeakReference<Context>
    // lateinit var mAdapter: TabFragmentAdapter
    private lateinit var mMenuAdapter: MenuCardAdapter
    private lateinit var mViewPager: ViewPager
    private val COMMON_TAG : String = "CombinedLifeCycle"
    private var mView:View?=null
    private var mBtnMainSearch:AppCompatImageButton?=null

    lateinit var mRecyclerView: RecyclerView
    lateinit var mRecyclerViewSong: RecyclerView


    private var mMenuItems: MutableList<SongDto>? = null
    private lateinit var mSongAdapter: SongAdapter


    private var mCurrentPlayingBottomBarFragment:CurrentPlayingBottomBarFragment?=null
    private var mMPlayerFragment:MPlayerFragment?=null



    private val ACTIVITY_NAME : String? = MainActivity::class.simpleName
    private val TAG : String = COMMON_TAG
    //lateinit var readWriteSongPermissionHandler : RequestPermissionHandler
    private lateinit var songList:MutableList<SongDto>


    private lateinit var mFragments:MutableList<Fragment>
    lateinit var songManager: SongManager
    private var mOnScrollListener:IScrollListener?=null




    var sharedPreferences: SharedPreferences? = null
    lateinit var editor: SharedPreferences.Editor
    var file= Environment.getDataDirectory()
    val storage_check_pref="intent_memory"
    val song_list_pref="intent_songs"
    var current_memory:Float=0.0f
    private var mCompositeDisposable: CompositeDisposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        context = WeakReference(this)
       // mViewPager= findViewById(R.id.viewpager)
        mFragments=mutableListOf()
        songList= mutableListOf()
        mBtnMainSearch = findViewById(R.id.btn_main_search)

       // infiniteCardView = findViewById(R.id.view)


        mCompositeDisposable= CompositeDisposable()

        val tabs=getTabs()

       /* mCurrentPlayingBottomBarFragment = CurrentPlayingBottomBarFragment.newInstance(this)
      *//*  mMPlayerFragment = MPlayerFragment.newInstance(this)*//*

        supportFragmentManager
            .beginTransaction()
            .add(R.id.container, mCurrentPlayingBottomBarFragment!!)
            .commit()*/

       // mAdapter= TabFragmentAdapter(supportFragmentManager,tabs)


        mRecyclerView  =  findViewById(R.id.rv_frag)
        mRecyclerViewSong  = findViewById(R.id.rv_frag_song)

        mRecyclerView.layoutManager= LinearLayoutManager(this,RecyclerView.HORIZONTAL,false)
        this.mMenuAdapter  = MenuCardAdapter(this,tabs)

        mRecyclerView.adapter = mMenuAdapter


        mRecyclerViewSong.layoutManager = LinearLayoutManager(this,RecyclerView.VERTICAL,false)
        this.mSongAdapter = SongAdapter(this)
        mRecyclerViewSong.adapter  = mSongAdapter
        loadData()
        mOnScrollListener=this
        mRecyclerViewSong.addOnScrollListener(object: HideSheetScrollListeners(){
            override fun onHide() {
                mOnScrollListener?.scrollUp()
            }

            override fun onShow() {
                mOnScrollListener?.scrollDown()
            }
        })

        mBtnMainSearch?.setOnClickListener {
           val intent = Intent(this,SearchActivity::class.java)
            startActivity(intent)
        }


        //  mViewPager.adapter=mAdapter

     //   setDefaultTab(tabs)

      //  mViewPager.offscreenPageLimit = 5



      /*  tabLayout.setupWithViewPager(mViewPager,autoRefresh = false)
        tabLayout.setTitlesAtTabs(tabs)*/
      //  setDefaultTab(tabs)

       /* supportFragmentManager
            .beginTransaction()
            .add(R.id.content, CurrentPlayingBottomBarFragment.newInstance())
            .commit()*/
        /*tabLayout.addOnTabSelectedListener(object:OnTabSelectedListener{
            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
              *//*  mViewPager.setCurrentItem(tab?.position!!)
                val text = tab.customView as TextView
                text.textSize=8F*//*
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
              *//*  val title=(((tabLayout.getChildAt(0) as LinearLayout).getChildAt(tabLayout.selectedTabPosition))as LinearLayout).getChildAt(1) as AppCompatTextView
                title.textSize = 10F*//*

            }


        })*/

        /*val params = appBarLayout.layoutParams as RelativeLayout.LayoutParams
        params.topMargin = Common.getStatusBarHeight(this)
        appBarLayout.layoutParams = params*/

     /*   val params = appBarLayout.layoutParams as CoordinatorLayout.LayoutParams
        params.topMargin = Common.getStatusBarHeight(this)
        appBarLayout.layoutParams = params*/







      /*  sharedPreferences = context.getSharedPreferences("beat_drop", Context.MODE_PRIVATE);

        if(sharedPreferences?.getString(storage_check_pref,"").equals("success")){
            current_memory=SongManager.megaBytesAvailable(file)
        }

       // myMethod()
     //   setPermissionsandGetSongDetails()
        setupViewPager(viewPager)
        changeSelectedTabSize(tabLayout,viewPager,this)
        tabLayout.setupWithViewPager(viewPager)
        Log.i(TAG,ACTIVITY_NAME+" onCreate")*/

    }

    override fun onResume() {
       // loadMenuImages()
        super.onResume()
    }

    /*
  *//*  interface OnSongReceivedListener {

    // val context = this

        fun onPassSongs(listSongs: MutableList<BeatDto>)
    }*//*

    fun setAboutDataListener(listener: OnSongReceivedListener) {
        msongListener = listener
        msongListener?.onPassSongs(songList)
        Log.i(TAG,ACTIVITY_NAME+" onCreate")
    }*/

    private fun setDefaultTab(tabs: Array<String>) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(Common.getInstance())
        val startupScreen = sharedPreferences.getString("preference_key_startup_screen", "SONGS")
        for (i in tabs.indices) {
            if (tabs[i].equals(startupScreen, ignoreCase = true)) {
                mViewPager.currentItem = i
                break
            }
        }
    }


    private fun getTabs(): Array<String> {
       /* val titles = SharedPrefHelper.getInstance().getString(SharedPrefHelper.Key.TITLES)
        if (titles == null) {*/
            val tabTitles = arrayOf("Albums", "Artists", "Genres", "Playlists", "MyFiles")
           // CursorHelper.saveTabTitles(tabTitles)
            return tabTitles
        /*} else {
            val gson = Gson()
            return gson.fromJson<Array<String>>(titles, Array<String>::class.java)
        }*/
    }

    /*fun loadMenuImages(){
        mCompositeDisposable?.add(Observable.fromCallable { MusicCursor.getMenuImages() }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object: DisposableObserver<MutableList<AlbumDto>>(){
                override fun onComplete() {
                }

                override fun onNext(data: MutableList<AlbumDto>) {
                    mMenuItems?.clear()
                    mMenuItems?.addAll(data)
                    mMenuAdapter.updateData(mMenuItems)
                    //mMenuAdapter.notifyDataSetChanged()
                }

                override fun onError(e: Throwable) {
                    Log.d("FAILED", "" + e.message)
                }

            })
        )
    }*/

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
                    mSongAdapter.update(songList)

                }

                override fun onError(e: Throwable) {
                }

            })
        )
    }

    override fun scrollUp() {

   //    val view= LayoutInflater.from(this).inflate(R.layout.activity_main,null)
        val lp = supportFragmentManager.findFragmentById(R.id.bottom_bar)?.view?.layoutParams as RelativeLayout.LayoutParams
        val i =lp.bottomMargin+150
        val abc=supportFragmentManager.findFragmentById(R.id.bottom_bar)?.view


        abc?.animate()?.apply {
            translationY((supportFragmentManager.findFragmentById(R.id.bottom_bar)?.view?.height!!+i).toFloat())
            interpolator=AccelerateInterpolator(2F)
            start()

        }

    }

    override fun scrollDown() {

        val abc=supportFragmentManager.findFragmentById(R.id.bottom_bar)?.view
        abc?.animate()?.apply {
            translationY(0F)
            interpolator=DecelerateInterpolator(2F)
            start()
        }

    }

    override fun onBackPressed() {
        if (mFragments.size>0){
            val fragment = mFragments[mFragments.size-1]
            if(fragment is TrackSubSongFragment)
                fragment.removeFragment()
            if(fragment is TrackCardSubFragment)
                fragment.removeFragment()
            if (fragment is ArtistFragment)
                fragment.removeFragment()
            if (fragment is AlbumFragment)
                fragment.removeFragment()
            if (fragment is GenreFragment)
                fragment.removeFragment()

            mFragments.remove(fragment)
            return
        }
        super.onBackPressed()
    }

    fun addFragment(fragment: Fragment) {

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.rl_main, fragment) // to be added frag parent layout
        fragmentTransaction.commitAllowingStateLoss()

        mFragments.add(fragment)
    }

    internal inner class ViewPagerAdapter(manager: FragmentManager): FragmentPagerAdapter(manager){

        private val mFragmentList=ArrayList<Fragment>()
        private val mFragmentTitleList=ArrayList<String>()

        override fun getItem(p0: Int): Fragment {
            return mFragmentList[p0]
        }

        override fun getCount(): Int {
          return mFragmentList.size
        }

        fun addFragment(fragment:Fragment,title:String){
            mFragmentList.add(fragment)
            mFragmentTitleList.add(title)
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return mFragmentTitleList[position]
        }


    }

    override fun onSongItemClickActivity(pos: Int, songItems: MutableList<SongDto>, shareImageView: ImageView) {
        val intent = Intent(this, MPlayerActivity::class.java)
       // val currentPlayingBottomBarFragment = fragmentManager.findFragmentById(R.id.current_playing_btm_bar_frag)
        val pair = androidx.core.util.Pair<View,String>(shareImageView, shareImageView.transitionName)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window?.sharedElementEnterTransition = TransitionInflater.from(this).inflateTransition(
                android.R.transition.move
            )
            window?.sharedElementEnterTransition?.duration=300
        }


        val options =
            ActivityOptionsCompat.makeSceneTransitionAnimation(this, pair)
        intent.putParcelableArrayListExtra("data",songItems as java.util.ArrayList<out Parcelable>)
        startActivity(intent, options.toBundle())
    }
}
