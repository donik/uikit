package kz.citicom.uikit.tools

import android.graphics.*
import android.util.SparseArray
import kz.citicom.uikit.R

object UIPaints {
    private var bitmap: Paint? = null
    private var porterDuffFilters: SparseArray<PorterDuffColorFilter>? = null
    private var porterDuffPaint: Paint? = null
    private var whitePDPaint: Paint? = null
    private var lastPorterDuffColor: Int = 0

    fun getBitmapPaint(): Paint? {
        if (bitmap == null) {
            bitmap = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG or Paint.DITHER_FLAG)
        }
        return bitmap
    }

    private fun createPorterDuffPaint(paint: Paint?, color: Int): Paint {
        var paint = paint
        if (paint == null) {
            paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG or Paint.DITHER_FLAG)
        }
        paint.colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN)
        return paint
    }

    private fun createPorterDuffPaint(color: Int): Paint {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG or Paint.DITHER_FLAG)
        paint.colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN)
        return paint
    }

    fun getPorterDuffPaint(color: Int): Paint? {
        if (color == Color.WHITE) {
            if (whitePDPaint == null) {
                whitePDPaint = createPorterDuffPaint(color)
            }
            return whitePDPaint
        }
        val filter = getColorFilter(color)
        if (porterDuffPaint == null) {
            porterDuffPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG or Paint.DITHER_FLAG)
            porterDuffPaint?.colorFilter = filter
            lastPorterDuffColor = color
        } else if (lastPorterDuffColor != color) {
            lastPorterDuffColor = color
            porterDuffPaint?.colorFilter = filter
        }
        return porterDuffPaint
    }

    fun getColorFilter(color: Int): PorterDuffColorFilter? {
        var filter: PorterDuffColorFilter?
        if (porterDuffFilters == null) {
            porterDuffFilters = SparseArray()
            filter = null
        } else {
            filter = porterDuffFilters?.get(color)
        }
        if (filter == null) {
            filter = createColorFilter(color)
            if (porterDuffFilters?.size() ?: 0 >= 100) {
                porterDuffFilters?.removeAt(0)
            }
            porterDuffFilters?.put(color, filter)
        }
        return filter
    }

    private fun createColorFilter(color: Int): PorterDuffColorFilter {
        return PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN)
    }
}