package com.tavish.musicplayerbeat.Helpers

import android.content.Context
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import kotlin.Exception

open class OnSwipeTouchListener(context: Context):View.OnTouchListener{

    var context:Context?=context
    private val gestureDetector= GestureDetector(context,GestureListener())
    open fun onSwipeTop() {

    }

    fun onTouch(event: MotionEvent?): Boolean {
       return gestureDetector.onTouchEvent(event)
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        return gestureDetector.onTouchEvent(event)
    }


    private inner class GestureListener: SimpleOnGestureListener() {

             val SWIPE_DISTANCE_THRESHOLD:Int=100
             val SWIPE_VELOCITY_THRESHOLD:Int=100

        override fun onDown(e: MotionEvent): Boolean {
            return true
        }

        override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
            var result:Boolean=false
            try {
                val diffY = e2!!.y - e1!!.y
              //  val diffX = e2.x - e1.x
                if (Math.abs(diffY) > SWIPE_DISTANCE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffY < 0)
                        onSwipeTop()
                    result = true
                }
            }catch (exception:Exception){
                exception.printStackTrace()
            }
            return result

        }




    }






}