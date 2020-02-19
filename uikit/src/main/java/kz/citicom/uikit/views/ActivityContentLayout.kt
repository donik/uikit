package kz.citicom.uikit.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Build
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.widget.FrameLayout
import kz.citicom.uikit.tools.UIScreen

class ActivityContentLayout(context: Context) : FrameLayout(context) {

    private var lastInsets: WindowInsets? = null
    private var hasCutout: Boolean = false

    private val backgroundPaint = Paint()
    var behindKeyboardColor: Int = 0

    init {
        descendantFocusability = ViewGroup.FOCUS_AFTER_DESCENDANTS
        isFocusableInTouchMode = true

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            fitsSystemWindows = true
            setOnApplyWindowInsetsListener { v, insets ->
                UIScreen.statusBarHeight = insets.systemWindowInsetTop
                this.lastInsets = insets
                (v as? ActivityContentLayout)?.setWillNotDraw(
                    insets.systemWindowInsetTop <= 0 && background == null
                )

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    val cutout = insets.displayCutout
                    hasCutout = cutout != null && cutout.boundingRects.size != 0
                }

                invalidate()
                return@setOnApplyWindowInsetsListener insets.consumeSystemWindowInsets()
            }
            systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }
    }

    override fun onDraw(canvas: Canvas?) {
        val insets = this.lastInsets ?: return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val bottomInset = insets.systemWindowInsetBottom
            if (bottomInset > 0) {
                backgroundPaint.color = behindKeyboardColor
                canvas?.drawRect(
                    0.0f,
                    measuredHeight - bottomInset.toFloat(),
                    measuredWidth.toFloat(),
                    measuredHeight.toFloat(),
                    backgroundPaint
                )
            }

            if (hasCutout) {
                backgroundPaint.color = Color.GREEN//-0x1000000
                val left = insets.systemWindowInsetLeft
                if (left != 0) {
                    canvas?.drawRect(
                        0.0f,
                        0.0f,
                        left.toFloat(),
                        measuredHeight.toFloat(),
                        backgroundPaint
                    )
                }
                val right = insets.systemWindowInsetRight
                if (right != 0) {
                    canvas?.drawRect(
                        right.toFloat(),
                        0.0f,
                        measuredWidth.toFloat(),
                        measuredHeight.toFloat(),
                        backgroundPaint
                    )
                }
            }
        }
    }
}