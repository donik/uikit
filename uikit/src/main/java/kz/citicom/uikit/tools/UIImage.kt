package kz.citicom.uikit.tools

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.SparseArray
import androidx.annotation.DrawableRes
import androidx.annotation.IntegerRes
import kz.citicom.uikit.UIApplication

object UIImage {
    private var __sparseIcons: SparseArray<Bitmap>? = null

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
}