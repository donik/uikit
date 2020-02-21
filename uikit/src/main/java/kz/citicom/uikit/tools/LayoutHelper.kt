package kz.citicom.uikit.tools

import android.widget.FrameLayout
import android.widget.LinearLayout

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
        val layoutParams = createFrame(width, height, gravity)
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

    fun createLinear(
        width: Int,
        height: Int,
        weight: Float,
        gravity: Int,
        leftMargin: Float,
        topMargin: Float,
        rightMargin: Float,
        bottomMargin: Float
    ): LinearLayout.LayoutParams {
        val layoutParams = LinearLayout.LayoutParams(getSize(width), getSize(height), weight)
        layoutParams.setMargins(
            UIScreen.dp(leftMargin.toFloat()),
            UIScreen.dp(topMargin.toFloat()),
            UIScreen.dp(rightMargin.toFloat()),
            UIScreen.dp(bottomMargin.toFloat())
        )
        layoutParams.gravity = gravity
        return layoutParams
    }

    fun createLinear(
        width: Int,
        height: Int,
        weight: Float,
        leftMargin: Float,
        topMargin: Float,
        rightMargin: Float,
        bottomMargin: Float
    ): LinearLayout.LayoutParams {
        val layoutParams = LinearLayout.LayoutParams(getSize(width), getSize(height), weight)
        layoutParams.setMargins(
            UIScreen.dp(leftMargin),
            UIScreen.dp(topMargin),
            UIScreen.dp(rightMargin),
            UIScreen.dp(bottomMargin)
        )
        return layoutParams
    }

    fun createLinear(
        width: Int,
        height: Int,
        gravity: Int,
        leftMargin: Float,
        topMargin: Float,
        rightMargin: Float,
        bottomMargin: Float
    ): LinearLayout.LayoutParams {
        val layoutParams = LinearLayout.LayoutParams(getSize(width), getSize(height))
        layoutParams.setMargins(
            UIScreen.dp(leftMargin),
            UIScreen.dp(topMargin),
            UIScreen.dp(rightMargin),
            UIScreen.dp(bottomMargin)
        )
        layoutParams.gravity = gravity
        return layoutParams
    }

    fun createLinear(
        width: Int,
        height: Int,
        leftMargin: Float,
        topMargin: Float,
        rightMargin: Float,
        bottomMargin: Float
    ): LinearLayout.LayoutParams {
        val layoutParams = LinearLayout.LayoutParams(getSize(width), getSize(height))
        layoutParams.setMargins(
            UIScreen.dp(leftMargin),
            UIScreen.dp(topMargin),
            UIScreen.dp(rightMargin),
            UIScreen.dp(bottomMargin)
        )
        return layoutParams
    }

    fun createLinear(width: Int, height: Int, weight: Float, gravity: Int): LinearLayout.LayoutParams {
        val layoutParams = LinearLayout.LayoutParams(getSize(width), getSize(height), weight)
        layoutParams.gravity = gravity
        return layoutParams
    }

    fun createLinear(width: Int, height: Int, gravity: Int): LinearLayout.LayoutParams {
        val layoutParams = LinearLayout.LayoutParams(getSize(width), getSize(height))
        layoutParams.gravity = gravity
        return layoutParams
    }

    fun createLinear(width: Int, height: Int, weight: Float): LinearLayout.LayoutParams {
        return LinearLayout.LayoutParams(getSize(width), getSize(height), weight)
    }

    fun createLinear(width: Int, height: Int): LinearLayout.LayoutParams {
        return LinearLayout.LayoutParams(getSize(width), getSize(height))
    }
}