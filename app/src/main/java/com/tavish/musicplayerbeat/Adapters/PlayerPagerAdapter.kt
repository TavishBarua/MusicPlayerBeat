package com.tavish.musicplayerbeat.Adapters

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.app.ActivityCompat.startIntentSenderForResult
import androidx.core.app.ActivityCompat.startPostponedEnterTransition
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import com.tavish.musicplayerbeat.Activities.MPlayerActivity
import com.tavish.musicplayerbeat.Activities.MainActivity
import com.tavish.musicplayerbeat.Common
import com.tavish.musicplayerbeat.Fragments.PlayerPagerFragment
import com.tavish.musicplayerbeat.Models.SongDto
import com.tavish.musicplayerbeat.R
import com.tavish.musicplayerbeat.Utils.MusicUtils
import com.tavish.musicplayerbeat.Utils.SongDataHelper
import kotlinx.android.synthetic.main.placeholder_song_item.view.*
import java.lang.Exception
import kotlin.NullPointerException



class PlayerPagerAdapter(context: Context):RecyclerView.Adapter<PlayerPagerAdapter.SongPickerViewHolder>(){


   private val mContext = context
    private var mContent:MutableList<SongDto>?=null
  //  private var mApp: Common? = Common.getInstance() as Common //activity?.applicationContext!! as Common
    private var mApp: Common? = Common.getInstance() as Common
   // private var mSongDtos: ArrayList<SongDto>? = songDtos
    private var mSongDataHelper: SongDataHelper? = SongDataHelper()



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongPickerViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.fragment_player_pager,parent,false)
        mSongDataHelper = SongDataHelper()
        return SongPickerViewHolder(view)
    }



    override fun getItemCount(): Int {
        return try {
            if (mApp?.isServiceRunning()!!)
                mApp?.mService?.getSongList()?.size!!
            else{
              //  mApp?.mService?.getSongList()?.size!!
                // ((mContext)as MPlayerActivity ).mSongs?.size!!
                 ((mContext)as MPlayerActivity ).mSongs?.size!!
            }

        } catch (ex: NullPointerException) {
            0
        }
    }

    fun updateContent(content:MutableList<SongDto>){
        mContent=content
        notifyDataSetChanged()

    }




    override fun onBindViewHolder(holder: SongPickerViewHolder, position: Int) {
        mSongDataHelper?.populateSongData(mContext.applicationContext!!, (mContext as MPlayerActivity).mSongs, position)
            holder.image.transitionName = "song_image$position"
            Picasso.get()
                .load(mSongDataHelper?.mAlbumArtPath)  //mSongDataHelper?.mAlbumArtPath  MusicUtils.getAlbumArtUri(mSongDto?._albumId!!).toString()
                .fit()
                .centerCrop()
                .placeholder(R.drawable.ic_song_placeholder)
                .into(holder.image, object:Callback{
                    override fun onSuccess() {
                    //    Log.println(1, "TRANS NAME",holder.image.transitionName)
                        Log.d("TRANS NAME1",holder.image.transitionName)
                        startPostponedEnterTransition(mContext as MPlayerActivity)
                    }

                    override fun onError(e: Exception?) {

                    }
                })






       /* holder.txt_song_name.apply {

            text = mSongDataHelper?.mTitle
            isSelected = true
        }

        holder.txt_song_album.apply {

            text = mSongDataHelper?.mAlbum + " - " + mSongDataHelper?.mArtist

            isSelected = true
        }*/
    }


    inner class SongPickerViewHolder(itemView: View?): RecyclerView.ViewHolder(itemView!!){

         val image:AppCompatImageView = itemView?.findViewById(R.id.img_cover_art)!!
       /*  val txt_song_name:AppCompatTextView = itemView?.findViewById(R.id.txt_song_name)!!
         val txt_song_album:AppCompatTextView = itemView?.findViewById(R.id.txt_song_album)!!*/


    }

}
/*
class PlayerPagerAdapter(playerActivity: MPlayerActivity, fm: FragmentManager) : FragmentStatePagerAdapter(fm), ViewPager.OnPageChangeListener {
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



    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

    }

    override fun onPageScrollStateChanged(state: Int) {

    }

    override fun onPageSelected(position: Int) {

    }
}
*/
