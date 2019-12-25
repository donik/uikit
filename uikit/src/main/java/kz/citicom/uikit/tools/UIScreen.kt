package kz.citicom.uikit.tools

import android.content.Context
import android.graphics.Point
import android.os.Build
import android.util.DisplayMetrics
import android.view.WindowManager
import kotlin.math.max
import kotlin.math.min

object UIScreen {
    private var appContext: Context? = null

    var density: Float = 0.0f
        private set
    val displaySize: Point = Point()
    val displayMetrics = DisplayMetrics()
    private var point: Point? = null
    private var screenPoint: Point? = null
    private var statusBarHeight: Int = 0

    fun init(context: Context) {
        this.appContext = context

        density = context.resources.displayMetrics.density
        val manager = context.getSystemService(Context.WINDOW_SERVICE) as? WindowManager
        manager?.defaultDisplay?.getMetrics(displayMetrics)
        manager?.defaultDisplay?.getSize(displaySize)
    }

    fun revertDp(size: Float): Int {
        return (size / density).toInt()
    }

    fun dp(size: Float): Int {
        return (density * size + 0.5f).toInt()
    }

    fun dp(size: Int): Int {
        return (density * size + 0.5f).toInt()
    }

    fun dpf(size: Float): Float {
        return density * size + 0.5f
    }

    fun dp(size: Float, maxDensity: Float): Int {
        return (Math.min(density, maxDensity) * size + 0.5f).toInt()
    }

    fun dpf(size: Float, maxDensity: Float): Int {
        return (Math.min(density, maxDensity) * size + 0.5f).toInt()
    }

    fun px(size: Float): Int {
        return (size / density - 0.5f).toInt()
    }
}
