package kz.citicom.uikit.tools

import android.content.Context
import android.view.ViewConfiguration

object UISize {
    private var appContext: Context? = null

    var HEADER_PORTRAIT_SIZE: Int = 0
        private set
    var HEADER_SIZE_DIFFERENCE = 0
        private set
    var HEADER_BIG_PORTRAIT_SIZE = 0
        private set
    var TAB_BAR_PORTRAIT_SIZE: Int = 0
        private set
    var TOUCH_SLOP: Float = 0.0f
        private set
    var TOUCH_SLOP_BIG: Float = 0.0f
        private set
    var TOUCH_SLOP_Y: Float = 0.0f
        private set
    var _18: Int = 0
        private set
    var _21: Int = 0
        private set
    var _42: Int = 0
        private set
    var _68: Int = 0
        private set

    fun init(context: Context) {
        this.appContext = context

        HEADER_PORTRAIT_SIZE = UIScreen.dp(56)
        HEADER_BIG_PORTRAIT_SIZE = UIScreen.dp(144)
        HEADER_SIZE_DIFFERENCE = HEADER_BIG_PORTRAIT_SIZE - HEADER_PORTRAIT_SIZE
        TAB_BAR_PORTRAIT_SIZE = UIScreen.dp(56)
        TOUCH_SLOP = ViewConfiguration.get(context).scaledTouchSlop.toFloat()
        TOUCH_SLOP_BIG = TOUCH_SLOP * 1.89f
        TOUCH_SLOP_Y = if (UIScreen.density >= 2.0f) TOUCH_SLOP_BIG else TOUCH_SLOP

        _18 = UIScreen.dp(18.0f)
        _21 = UIScreen.dp(21.0f)
        _42 = UIScreen.dp(42.0f)
        _68 = UIScreen.dp(68.0f)
    }
}