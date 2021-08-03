package kz.citicom.uikit.tools

import android.content.res.ColorStateList
import android.graphics.Color
import androidx.core.view.ViewCompat
import kz.citicom.uikit.R

import kz.citicom.uikit.UIApplication


object UIColor {

    val white = getColor("#ffffff")
    val black = getColor("#000000")
    val red = getColor("#ff0000")
    val clear = getColor(R.color.clear)

    fun getColor(color: String, alpha: Float = 1.0f): Int {
        val color = Color.parseColor(color)

        return if (alpha == 1.0f) {
            color
        } else {
            (Color.alpha(color).toFloat() * alpha).toInt() shl 24 or (ViewCompat.MEASURED_SIZE_MASK and color)
        }
    }

    fun getColor(color: Int): Int {
        return UIApplication.shared?.resources?.getColor(color) ?: 0
    }

    fun color(alpha: Int, color: Int): Int {
        return alpha shl 24 or (ViewCompat.MEASURED_SIZE_MASK and color)
    }

    fun alphaColor(alpha: Float, color: Int): Int {
        return if (alpha == 1.0f) color else color(
            (Color.alpha(color).toFloat() * alpha).toInt(),
            color
        )
    }

    fun compositeColor(color: Int, overlay: Int): Int {
        val alpha = Color.alpha(overlay).toFloat() / 255.0f
        val r = Color.red(color)
        val g = Color.green(color)
        val b = Color.blue(color)
        return Color.rgb(
            (Color.red(overlay).toFloat() * alpha).toInt() + (r.toFloat() * (1.0f - alpha)).toInt(),
            (Color.green(overlay)
                .toFloat() * alpha).toInt() + (g.toFloat() * (1.0f - alpha)).toInt(),
            (Color.blue(overlay).toFloat() * alpha).toInt() + (b.toFloat() * (1.0f - alpha)).toInt()
        )
    }

    fun selectedColorStateList(selectedColor: Int, defaultColor: Int): ColorStateList {
        val states = arrayOf(
            intArrayOf(android.R.attr.state_selected),
            intArrayOf()
        )

        val colors = intArrayOf(
            selectedColor,
            defaultColor
        )

        return ColorStateList(states, colors)
    }

    fun isColorDark(color: Int): Boolean {
        val darkness =
            1 - (0.299 * Color.red(color) + 0.587 * Color.green(
                color
            ) + 0.114 * Color.blue(color)) / 255
        return darkness >= 0.5
    }
}