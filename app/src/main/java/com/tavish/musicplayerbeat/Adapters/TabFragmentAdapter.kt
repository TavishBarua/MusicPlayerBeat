package com.tavish.musicplayerbeat.Adapters

import android.util.SparseArray
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.tavish.musicplayerbeat.Fragments.*
import java.util.ArrayList
import java.util.HashMap
import android.R
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getSystemService

class TabFragmentAdapter(fm: FragmentManager, pageTitles: Array<String>) : FragmentPagerAdapter(fm) {

    private lateinit var mFragmentTags: SparseArray<String>
    private  var mFragmentManager: FragmentManager = fm
    private var mPageTile: Array<String> = pageTitles
    private lateinit var fragments: ArrayList<Fragment>
    private lateinit var context: Context



    init {
        fragments= arrayListOf()
        mFragmentTags= SparseArray()
        for (tab in mPageTile) {
            if (tab.equals("Albums", ignoreCase = true)) {
                fragments.add(AlbumFragment())
            } else if (tab.equals("Artists", ignoreCase = true)) {
                fragments.add(ArtistFragment())
            } else if (tab.equals("Playlists", ignoreCase = true)) {
                fragments.add(PlaylistFragment())
            } else if (tab.equals("Songs", ignoreCase = true)) {
                fragments.add(SongFragment())
            } else if (tab.equals("MyFiles", ignoreCase = true)) {
                fragments.add(MyFileFragment())
            } else if (tab.equals("Genres", ignoreCase = true)) {
                fragments.add(GenreFragment())
            }
        }
    }

    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val obj = super.instantiateItem(container, position)
        if (obj is Fragment) {
            val tag = obj.tag
            mFragmentTags.put(position, tag)
        }
        return obj
    }

    fun getFragment(position: Int): Fragment? {
        val tag = mFragmentTags.get(position) ?: return null
        return mFragmentManager.findFragmentByTag(tag)
    }


    override fun getPageTitle(position: Int): CharSequence? {
        return mPageTile[position]
    }

    override fun getCount(): Int {
        return mPageTile.size
    }


}