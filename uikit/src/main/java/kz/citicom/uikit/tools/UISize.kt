package kz.citicom.uikit.tools

import android.content.Context
import android.view.ViewConfiguration

object UISize {
    private var appContext: Context? = null

    var TOUCH_SLOP: Float = 0.0f
        private set
    var TOUCH_SLOP_BIG: Float = 0.0f
        private set
    var TOUCH_SLOP_Y: Float = 0.0f
        private set

    fun init(context: Context) {
        this.appContext = context

        TOUCH_SLOP = ViewConfiguration.get(context).scaledTouchSlop.toFloat()
        TOUCH_SLOP_BIG = TOUCH_SLOP * 1.89f
        TOUCH_SLOP_Y = if (UIScreen.density >= 2.0f) TOUCH_SLOP_BIG else TOUCH_SLOP
    }
}