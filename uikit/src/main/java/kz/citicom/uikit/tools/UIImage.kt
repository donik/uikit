package kz.citicom.uikit.tools

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.*
import android.graphics.drawable.shapes.OvalShape
import android.os.Build
import android.util.SparseArray
import android.util.StateSet
import androidx.annotation.DrawableRes
import kz.citicom.uikit.UIApplication

object UIImage {
    private var __sparseIcons: SparseArray<Bitmap>? = null
    private val simpleSelectorCircleDrawableCache = hashMapOf<String, Drawable?>()

    fun getSparseIcon(@DrawableRes iconResource: Int): Bitmap? {
        if (iconResource == 0) {
            return null
        }
        var cachedIcon: Bitmap?
        synchronized(UIImage::class.java) {
            if (__sparseIcons == null) {
                __sparseIcons = SparseArray(10)
                cachedIcon = null
            } else {
                cachedIcon = __sparseIcons?.get(iconResource)
            }
            if (cachedIcon == null) {
                cachedIcon = getBitmapFrom(UIApplication.shared?.resources?.getDrawable(iconResource))
                __sparseIcons?.put(iconResource, cachedIcon)
            }
        }
        return cachedIcon
    }

    private fun getBitmapFrom(d: Drawable?): Bitmap? {
        val drawable = d ?: return null
        if (drawable is BitmapDrawable) {
            return drawable.bitmap
        }

        val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    private var context: Context? = null

    fun init(context: Context) {
        this.context = context
    }

    public fun getDrawable(@DrawableRes res: Int): Drawable? {
        return context?.resources?.getDrawable(res)
    }

    fun createSimpleSelectorCircleDrawable(size: Int, defaultColor: Int, pressedColor: Int): Drawable? {
        val key = "$size-$defaultColor-$pressedColor"
        var drawable = simpleSelectorCircleDrawableCache[key]

        if (drawable == null) {
            val ovalShape = OvalShape()
            ovalShape.resize(size.toFloat(), size.toFloat())
            val defaultDrawable = ShapeDrawable(ovalShape)
            defaultDrawable.paint.color = defaultColor
            val pressedDrawable = ShapeDrawable(ovalShape)
            if (Build.VERSION.SDK_INT >= 21) {
                pressedDrawable.paint.color = UIColor.white
                val colorStateList = ColorStateList(
                    arrayOf(StateSet.WILD_CARD),
                    intArrayOf(pressedColor)
                )
                drawable = RippleDrawable(colorStateList, defaultDrawable, pressedDrawable)
            } else {
                pressedDrawable.paint.color = pressedColor
                val stateListDrawable = StateListDrawable()
                stateListDrawable.addState(intArrayOf(android.R.attr.state_pressed), pressedDrawable)
                stateListDrawable.addState(intArrayOf(android.R.attr.state_focused), pressedDrawable)
                stateListDrawable.addState(StateSet.WILD_CARD, defaultDrawable)
                drawable = stateListDrawable
            }
            simpleSelectorCircleDrawableCache[key] = drawable
        }

        return drawable
    }
}