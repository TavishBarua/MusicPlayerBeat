package com.tavish.musicplayerbeat.Activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Environment
import android.preference.PreferenceManager

import android.util.Log
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
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
import com.tavish.musicplayerbeat.Models.BeatDto
import com.tavish.musicplayerbeat.R
import java.lang.ref.WeakReference


class MainActivity : AppCompatActivity() {


    private val tabLayout by binder<TabLayout>(R.id.tabs)
    private val viewPager by binder<ViewPager>(R.id.viewpager)
    private val appBarLayout by binder<AppBarLayout>(R.id.appBarLayout)
    private lateinit var context:WeakReference<Context>
    private lateinit var mAdapter: TabFragmentAdapter
    private lateinit var mViewPager: ViewPager



    private val COMMON_TAG : String = "CombinedLifeCycle"
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
   // val context = this


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

        val params = appBarLayout.getLayoutParams() as LinearLayout.LayoutParams
        params.topMargin = Common.getStatusBarHeight(this)
        appBarLayout.setLayoutParams(params)







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
    fun addFragment(fragment: Fragment) {

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.viewpager, fragment) // to be added frag parent layout
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
