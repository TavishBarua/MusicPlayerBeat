package com.tavish.musicplayerbeat.Adapters

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.content.res.Resources
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import com.tavish.musicplayerbeat.Activities.MainActivity
import com.tavish.musicplayerbeat.Fragments.*
import com.tavish.musicplayerbeat.Models.AlbumDto
import com.tavish.musicplayerbeat.Models.SongDto
import com.tavish.musicplayerbeat.R
import com.tavish.musicplayerbeat.Utils.Constants
import com.tavish.musicplayerbeat.Utils.MusicUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*


class MenuCardAdapter(context: Context,tabs: Array<String>): RecyclerView.Adapter<MenuCardAdapter.MenuViewHolder>()  {


    private val mContext= context as MainActivity
    private var menuList: MutableList<SongDto>? = null
    private lateinit var mFragments: ArrayList<Fragment>
    private val mtabs= tabs

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.placeholder_frag_item, parent, false)
        return MenuViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mtabs.size
    }

    fun updateData(data: MutableList<SongDto>?) {
        menuList?.clear()
        menuList?.addAll(data!!)
        CoroutineScope(Dispatchers.Main).launch {
            notifyDataSetChanged()
        }
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {

       /* val uri = ContentUris.withAppendedId(
            Constants.sArtworkUri,
            menuList?.get(position)?._albumId!!
        )*/
        Picasso.get()
            .load("content://media/external/audio/albumart/$position")
            .placeholder(R.drawable.ic_album)
            .into(holder.image, object :Callback {
                override fun onSuccess() {
                  //  Log.d("success","YO YO")
                }

                override fun onError(e: Exception?) {
                   // Log.d("failure",e.toString())
                }

            })
        holder.text.text = mtabs[position]
        holder.itemView.setOnClickListener { fragTransation(mtabs[position], holder.image)  }
    }

    @SuppressLint("ResourceType")
    fun fragTransation(menuTitle:String, imageView: AppCompatImageView){
        var fragment:Fragment? = null
        when(menuTitle){

            "Albums"->fragment = AlbumFragment()
            "Artists"->fragment = ArtistFragment()
            "Playlists"->fragment = PlaylistFragment()
            "MyFiles"->fragment = MyFileFragment()
            "Genres"->fragment = GenreFragment()


        }

      /*  val fragmentManager = mContext.supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.rl_main,fragment!!)
        fragmentTransaction.setCustomAnimations(R.transition.change_clip_bounds,R.transition.auto_transition)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()*/
        mContext.addFragment(fragment!!)
    }



    inner class MenuViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!){


        val image: AppCompatImageView = itemView?.findViewById(R.id.img_frag_art)!!
        val text: AppCompatTextView = itemView?.findViewById(R.id.txt_frag)!!





    }



}