package com.tavish.musicplayerbeat.Utils

import android.view.View
import androidx.core.view.ViewCompat.setScaleY
import androidx.core.view.ViewCompat.setElevation
import androidx.viewpager.widget.ViewPager


class CardsPagerTransformerShift(
    private val baseElevation: Int,
    private val raisingElevation: Int,
    private val smallerScale: Float,
    private val startOffset: Float
) : ViewPager.PageTransformer {

    override fun transformPage(page: View, position: Float) {
        val absPosition = Math.abs(position - startOffset)

        if (absPosition >= 1) {
            page.apply {
                elevation=baseElevation.toFloat()
                scaleY = smallerScale
            }
        } else {
            // This will be during transformation
            page.apply {
                elevation=(1 - absPosition) * raisingElevation + baseElevation
                scaleY = (smallerScale - 1) * absPosition + 1
            }
        }
    }

}