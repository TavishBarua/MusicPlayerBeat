package com.tavish.musicplayerbeat.Utils.CarouselEffectViewPager

import android.content.Context
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.ViewCompat.setTranslationX
import androidx.core.view.ViewCompat.setScaleY
import androidx.core.view.ViewCompat.setScaleX
import androidx.viewpager.widget.ViewPager


class CarouselEffectTransformer(context: Context) : ViewPager.PageTransformer {

    private val maxTranslateOffsetX: Int
    private var viewPager: ViewPager? = null

    init {
        this.maxTranslateOffsetX = dp2px(context, 180f)
    }

    override fun transformPage(view: View, position: Float) {
        if (viewPager == null) {
            viewPager = view.parent as ViewPager
        }

        val leftInScreen = view.getLeft() - viewPager!!.scrollX
        val centerXInViewPager = leftInScreen + view.getMeasuredWidth() / 2
        val offsetX = centerXInViewPager - viewPager!!.measuredWidth / 2
        val offsetRate = offsetX.toFloat() * 0.38f / viewPager!!.measuredWidth
        val scaleFactor = 1 - Math.abs(offsetRate)

        if (scaleFactor > 0) {
            view.apply {
                scaleX = scaleFactor
                scaleY = scaleFactor
                translationX = -maxTranslateOffsetX * offsetRate
            }
            //ViewCompat.setElevation(view, 0.0f);
        }
        ViewCompat.setElevation(view, scaleFactor)

    }

    /**
     * Dp to pixel conversion
     */
    private fun dp2px(context: Context, dipValue: Float): Int {
        val m = context.getResources().getDisplayMetrics().density
        return (dipValue * m + 0.5f).toInt()
    }

}