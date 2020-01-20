package kz.citicom.uikit.tools

import android.widget.FrameLayout

object LayoutHelper {
    const val MATCH_PARENT = -1
    const val WRAP_CONTENT = -2

    private fun getSize(size: Int): Int {
        return if (size < 0) size else UIScreen.dp(size.toFloat())
    }

    fun createFrame(
        width: Int,
        height: Int,
        gravity: Int,
        leftMargin: Float,
        topMargin: Float,
        rightMargin: Float,
        bottomMargin: Float
    ): FrameLayout.LayoutParams {
        val layoutParams = FrameLayout.LayoutParams(getSize(width), getSize(height), gravity)
        layoutParams.setMargins(
            UIScreen.dp(leftMargin),
            UIScreen.dp(topMargin),
            UIScreen.dp(rightMargin),
            UIScreen.dp(bottomMargin)
        )
        return layoutParams
    }

    fun createFrame(width: Int, height: Int, gravity: Int): FrameLayout.LayoutParams {
        return FrameLayout.LayoutParams(getSize(width), getSize(height), gravity)
    }

    fun createFrame(width: Int, height: Int): FrameLayout.LayoutParams {
        return FrameLayout.LayoutParams(getSize(width), getSize(height))
    }

}