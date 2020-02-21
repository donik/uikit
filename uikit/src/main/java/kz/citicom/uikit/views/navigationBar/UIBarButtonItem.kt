package kz.citicom.uikit.views.navigationBar

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.MotionEvent
import android.widget.FrameLayout
import kz.citicom.uikit.tools.UIIcons
import kz.citicom.uikit.tools.UIPaints
import kz.citicom.uikit.tools.UIScreen
import kz.citicom.uikit.tools.UISize
import kz.citicom.uikit.views.UIView

open class UIBarButtonItem(context: Context) : FrameLayout(context) {
    companion object {
        private var separatorSize = UIScreen.dp(1.0f)
    }

    private var colorFilter: Int = -1
    private var currentResource: Int = -1
    private var drawable: Drawable? = null
    private var icon: Bitmap? = null
    private var lastBgResource: Int = 0

    var iconShowFactor = 1.0f
        set(value) {
            field = value
            invalidate()
        }

    init {
        UIView.setClickable(this)
    }

    open fun setColor(color: Int) {
        this.setColorFilter(color)
    }

    private fun setColorFilter(filter: Int): UIBarButtonItem {
        if (this.colorFilter != filter) {
            this.colorFilter = filter
            invalidate()
        }
        return this
    }

    override fun onTouchEvent(e: MotionEvent): Boolean {
        return (e.action != MotionEvent.ACTION_DOWN || visibility == VISIBLE) && super.onTouchEvent(e)
    }

    fun setImageResource(resource: Int) {
        if (this.currentResource != resource) {
            this.currentResource = resource
            if (this.icon != null) {

                this.icon = UIIcons.getSparseIcon(resource)
                invalidate()
                return
            }
            this.icon = UIIcons.getSparseIcon(resource)
        }
    }

    fun setImageDrawable(drawable: Drawable?) {
        this.drawable = drawable
        drawable?.setBounds(0, 0, layoutParams.width, UISize.HEADER_PORTRAIT_SIZE)
    }

    fun getDrawable(): Drawable? {
        return this.drawable
    }

    fun setButtonBackground(resource: Int) {
        if (this.lastBgResource != resource) {
            this.lastBgResource = resource
            setBackgroundResource(resource)
        }
    }

    override fun onDraw(canvas: Canvas) {
        val icon = this.icon
        if (icon != null) {
            val width = icon.width * iconShowFactor
            val height = icon.height * iconShowFactor

            val paint = if (this.colorFilter == 0) UIPaints.getBitmapPaint() else UIPaints.getPorterDuffPaint(this.colorFilter)
            paint?.alpha = (iconShowFactor * 255.0f).toInt()

            canvas.drawBitmap(icon, null, Rect(
                (measuredWidth.toFloat() * 0.5f - width * 0.5f).toInt(),
                (separatorSize.toFloat() + (measuredHeight.toFloat() - separatorSize.toFloat()) * 0.5f - height * 0.5f).toInt(),
                (measuredWidth.toFloat() * 0.5f - width * 0.5f).toInt() + width.toInt(),
                (separatorSize.toFloat() + (measuredHeight.toFloat() - separatorSize.toFloat()) * 0.5f - height * 0.5f).toInt() + height.toInt()
            ), paint)
        } else if (this.drawable != null) {
            this.drawable?.draw(canvas)
        }
    }
}