package com.tavish.musicplayerbeat.Adapters

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.tavish.musicplayerbeat.Activities.MPlayerActivity
import com.tavish.musicplayerbeat.Common
import com.tavish.musicplayerbeat.Fragments.PlayerPagerFragment
import kotlin.NullPointerException


class PlayerPagerAdapter(playerActivity: MPlayerActivity, fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

    private val mApp: Common = Common.getInstance() as Common
    private val mNowPlayingActivity: MPlayerActivity = playerActivity


    override fun getItem(position: Int): Fragment {
        val fragment: Fragment = PlayerPagerFragment()
        val bundle = Bundle()
        bundle.putInt("POSITION", position)
        fragment.arguments = bundle
        return fragment
    }

    override fun getCount(): Int {
        return try {
            if (mApp.isServiceRunning())
                mApp.mService?.getSongList()?.size!!
            else
                mNowPlayingActivity.mSongs?.size!!
        } catch (ex: NullPointerException) {
            0
        }
    }

    override fun destroyItem(container: View, position: Int, `object`: Any) {
        super.destroyItem(container, position, `object`)
    }
}