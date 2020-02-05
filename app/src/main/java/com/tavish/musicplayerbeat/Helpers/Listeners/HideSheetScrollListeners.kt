package com.tavish.musicplayerbeat.Helpers.Listeners

import androidx.recyclerview.widget.RecyclerView
import kotlin.contracts.contract

abstract class HideSheetScrollListeners: RecyclerView.OnScrollListener(){

    companion object{
        const val THRESHOLD=20
    }

    private var scrollDistance = 0
    private var isControlVisible = true

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        if(scrollDistance> THRESHOLD && isControlVisible){
            onHide()
            isControlVisible=false
            scrollDistance=0
        }else if (scrollDistance<-THRESHOLD&&!isControlVisible){
            onShow()
            isControlVisible=true
            scrollDistance=0
        }

        if ((isControlVisible&&dy>0)or (!isControlVisible&&dy<0)){
            scrollDistance+=dy
        }
    }

    abstract fun onHide()

    abstract fun onShow()
}