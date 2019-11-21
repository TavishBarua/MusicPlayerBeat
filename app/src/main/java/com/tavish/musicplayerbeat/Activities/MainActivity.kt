package com.tavish.musicplayerbeat.Activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Environment
import android.preference.PreferenceManager

import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import com.tavish.musicplayerbeat.Adapters.TabFragmentAdapter
import com.tavish.musicplayerbeat.Common
import com.tavish.musicplayerbeat.Fragments.*
import com.tavish.musicplayerbeat.Helpers.SharedPrefHelper
//import com.tavish.musicplayerbeat.Helpers.RequestPermissionHandler
import com.tavish.musicplayerbeat.Helpers.SongManager
import com.tavish.musicplayerbeat.Helpers.binder
import com.tavish.musicplayerbeat.Interfaces.IScrollListener
import com.tavish.musicplayerbeat.Models.BeatDto
import com.tavish.musicplayerbeat.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.current_playing_bottom_item.view.*
import kotlinx.android.synthetic.main.fragment_current_playing_bottom_bar.*
import kotlinx.android.synthetic.main.fragment_current_playing_bottom_bar.view.*
import java.lang.ref.WeakReference


class MainActivity : AppCompatActivity(), IScrollListener{
    private val tabLayout by binder<TabLayout>(R.id.tabs)


    private val appBarLayout by binder<AppBarLayout>(R.id.appBarLayout)
    private lateinit var context:WeakReference<Context>
    private lateinit var mAdapter: TabFragmentAdapter
    private lateinit var mViewPager: ViewPager
    private val COMMON_TAG : String = "CombinedLifeCycle"
    private var mView:View?=null



    private val ACTIVITY_NAME : String? = MainActivity::class.simpleName
    private val TAG : String = COMMON_TAG
    //lateinit var readWriteSongPermissionHandler : RequestPermissionHandler
    var songList:MutableList<BeatDto> = mutableListOf()


    private lateinit var mFragments:MutableList<Fragment>
    lateinit var songManager: SongManager



    var sharedPreferences: SharedPreferences? = null
    lateinit var editor: SharedPreferences.Editor
    var file= Environment.getDataDirectory()
    val storage_check_pref="intent_memory"
    val song_list_pref="intent_songs"
    var current_memory:Float=0.0f
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        context = WeakReference(this)
        mViewPager= findViewById(R.id.viewpager)
        mFragments=mutableListOf()

        val tabs=getTabs()

        mAdapter= TabFragmentAdapter(supportFragmentManager,tabs)

        mViewPager.adapter=mAdapter

        setDefaultTab(tabs)

        mViewPager.offscreenPageLimit = 5

        tabLayout.setupWithViewPager(mViewPager)

        val params = appBarLayout.layoutParams as RelativeLayout.LayoutParams
        params.topMargin = Common.getStatusBarHeight(this)
        appBarLayout.layoutParams = params







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
            val tabTitles = arrayOf("ALBUMS", "ARTISTS", "SONGS", "GENRES", "PLAYLISTS", "MYFILES")
           // CursorHelper.saveTabTitles(tabTitles)
            return tabTitles
        /*} else {
            val gson = Gson()
            return gson.fromJson<Array<String>>(titles, Array<String>::class.java)
        }*/
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




    fun addFragment(fragment: Fragment) {

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.main_parent, fragment) // to be added frag parent layout
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
}
