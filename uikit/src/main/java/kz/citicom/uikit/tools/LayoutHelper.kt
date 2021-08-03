package kz.citicom.uikit.tools

import android.graphics.PixelFormat
import android.os.Build
import android.util.Log
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.LinearLayout
import java.lang.Exception

object LayoutHelper {
    const val MATCH_PARENT = -1
    const val WRAP_CONTENT = -2

    private fun windowManagerType(): Int {
        if (Build.VERSION.SDK_INT >= 26) {
            return WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        }
        return WindowManager.LayoutParams.TYPE_SYSTEM_ERROR
    }

    private fun getSize(size: Int): Int {
        return if (size < 0) size else UIScreen.dp(size.toFloat())
    }

    fun createWindowManager(
        width: Int,
        height: Int
    ): WindowManager.LayoutParams {
        val lp = WindowManager.LayoutParams(
            width,
            height,
            this.windowManagerType(),
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    or WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                    or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
//                    or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                    or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSPARENT
        )
        val className = "android.view.WindowManager\$LayoutParams"
        try {
            val layoutParamsClass = Class.forName(className)
            val privateFlags = layoutParamsClass.getField("privateFlags")
            val noAnim = layoutParamsClass.getField("PRIVATE_FLAG_NO_MOVE_ANIMATION")

            var privateFlagsValue = privateFlags.getInt(lp)
            val noAnimFlag = noAnim.getInt(lp)
            privateFlagsValue = privateFlagsValue or noAnimFlag
            privateFlags.setInt(lp, privateFlagsValue)
        } catch (e: Exception) {
            Log.e("EXCEPT", "EXCEPTION: ${e.localizedMessage}")
        }

        return lp
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

    fun createLinear(
        width: Int,
        height: Int,
        weight: Float,
        gravity: Int
    ): LinearLayout.LayoutParams {
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