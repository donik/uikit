package kz.citicom.uikit.tools

import android.graphics.Color
import androidx.core.view.ViewCompat
import kz.citicom.uikit.UIApplication

object UIColor {
    private val colors = arrayOf(
        "#FC5C51",
        "#FA790F",
        "#0FB297",
        "#3CA5EC",
        "#3D72ED",
        "#895DD5",
        "#00A1C4",
        "#FF1744",
        "#B71C1C",
        "#C51162",
        "#FF80AB",
        "#6200EA",
        "#8C9EFF",
        "#00E5FF",
        "#1DE9B6",
        "#AEEA00",
        "#FFD600",
        "#BF360C",
        "#3E2723",
        "#795548",
        "#1A237E"
    )

    fun getUserColor(userID: Int): Int {
        val index = Math.abs(userID % colors.size)
        val color = colors[index]
        return getColor(color)
    }

    fun getColor(color: String): Int {
        return Color.parseColor(color)
    }

    fun getColor(color: Int): Int {
        return UIApplication.shared?.resources?.getColor(color) ?: 0
    }

    fun color(alpha: Int, color: Int): Int {
        return alpha shl 24 or (ViewCompat.MEASURED_SIZE_MASK and color)
    }

    fun alphaColor(alpha: Float, color: Int): Int {
        return if (alpha == 1.0f) color else color((Color.alpha(color).toFloat() * alpha).toInt(), color)
    }

    fun compositeColor(color: Int, overlay: Int): Int {
        val alpha = Color.alpha(overlay).toFloat() / 255.0f
        val r = Color.red(color)
        val g = Color.green(color)
        val b = Color.blue(color)
        return Color.rgb(
            (Color.red(overlay).toFloat() * alpha).toInt() + (r.toFloat() * (1.0f - alpha)).toInt(),
            (Color.green(overlay).toFloat() * alpha).toInt() + (g.toFloat() * (1.0f - alpha)).toInt(),
            (Color.blue(overlay).toFloat() * alpha).toInt() + (b.toFloat() * (1.0f - alpha)).toInt()
        )
    }
}