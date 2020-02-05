package com.tavish.musicplayerbeat.Adapters

import android.content.ContentUris
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.tavish.musicplayerbeat.Fragments.CurrentPlayingBottomBarFragment
import com.tavish.musicplayerbeat.Interfaces.SharedResourceOnItemClickFragment
import com.tavish.musicplayerbeat.Models.SongDto
import com.tavish.musicplayerbeat.R
import com.tavish.musicplayerbeat.Utils.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch




class CurrentPlayingBottomBarAdapter(songs:MutableList<SongDto>, sharedInterfaceFragment:SharedResourceOnItemClickFragment) :
    RecyclerView.Adapter<CurrentPlayingBottomBarAdapter.ItemViewHolder>() {


    var mFragment:CurrentPlayingBottomBarFragment?=null
    var mSongs: MutableList<SongDto>? = null
    var mSharedInterfaceFragment:SharedResourceOnItemClickFragment? = null
    init {
      //  mFragment=fragment
        mSongs = songs
        mSharedInterfaceFragment = sharedInterfaceFragment
    }

  //  var mContext: Context = context
    var songList: MutableList<SongDto> = mutableListOf()
   // private var mApp: Common? = mContext.applicationContext as? Common

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
            val view:View = LayoutInflater.from(parent.context).inflate(R.layout.current_playing_bottom_item, parent, false)
            return ItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return if(songList==null) 0 else songList.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {

        val uri = ContentUris.withAppendedId(
            Constants.sArtworkUri,
            songList[holder.adapterPosition]._albumId!!
        )
        Picasso.get()
            .load(uri)
            .placeholder(R.mipmap.ic_launcher_round)
            .into(holder.songImage)
        holder.title?.text = songList[holder.adapterPosition]._title
        holder.artist?.text = songList[holder.adapterPosition]._artist
       // holder.songImage?.transitionName+=position
      //  ViewCompat.setTransitionName(holder.songImage!!, holder.songImage?.transitionName)
        holder.songImage?.transitionName = "song_image$position"

        holder.itemView.setOnClickListener { mSharedInterfaceFragment?.onSongItemClickFragment(holder.adapterPosition, mSongs!!,holder.songImage!!) }
      //  holder.itemView.setOnClickListener{ mFragment?.startActivity(Intent(mFragment!!.activity, MPlayerActivity::class.java)) }


    }

    fun updateSongData(mSongList:MutableList<SongDto>?){
       songList = mSongList!!
     //   notifyDataSetChanged()
        CoroutineScope(Dispatchers.Main).launch {
            notifyDataSetChanged()
        }
    }


     inner class ItemViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!){

         var title:AppCompatTextView? = null
         var artist:AppCompatTextView? = null
         var songImage:AppCompatImageView? = null

        init {
            title = itemView?.findViewById(R.id.txt_bottom_title)
            artist = itemView?.findViewById(R.id.txt_bottom_artist)
            songImage=itemView?.findViewById(R.id.img_bottom_song)



          /*  itemView?.setOnClickListener{
                val intent = Intent(mFragment!!.activity, MPlayerActivity::class.java)
              //  val pairs = arrayOfNulls<androidx.core.util.Pair<View,String>>(3)
                val p1 = androidx.core.util.Pair.create<View,String>(title!!,"song_title")
            //    pairs[1] = Pair<View,String>(artist!!,"song_title")
                val p2 = androidx.core.util.Pair.create<View,String>(songImage!!,"song_image")
                //val options = ActivityOptions.makeSceneTransitionAnimation(mFragment!!.activity,p1, p2)
                val fragment = PlayerPagerFragment.newInstance(ContentUris.withAppendedId(
                    Constants.sArtworkUri,
                    songList[adapterPosition]._albumId!!),ViewCompat.getTransitionName(songImage!!)!!)

                mFragment?.fragmentManager
                    ?.beginTransaction()
                    ?.setReorderingAllowed(true)
                    ?.addSharedElement(songImage!!,songImage?.transitionName!!)
                    ?.replace(R.id.frag_cardview, fragment)
                    ?.addToBackStack(null)
                    ?.commit()

             //   mFragment?.startActivity(Intent(mFragment!!.activity, MPlayerActivity::class.java))
            }*/
        }


       /* fun bindItems(item: SongDto) {
            itemView.txt_bottom_title.text = item._title
            itemView.txt_bottom_artist.text = item._artist

            val uri = ContentUris.withAppendedId(
                Constants.sArtworkUri,
                item._albumId!!
            )

            Picasso.get()
                .load(uri)
                .placeholder(R.mipmap.ic_launcher_round)
                .into(itemView.img_bottom_song)


            itemView.setOnClickListener{ mFragment?.startActivity(Intent(mFragment!!.activity, MPlayerActivity::class.java)) }

        }*/





    }

}