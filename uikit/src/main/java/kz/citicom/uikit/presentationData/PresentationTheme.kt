package kz.citicom.uikit.presentationData

import android.R
import android.content.res.ColorStateList
import androidx.annotation.ColorInt
import kz.citicom.uikit.tools.Equtable

data class PresentationThemeRootTabBar(
    @ColorInt val backgroundColor: Int,
    @ColorInt val iconColor: Int,
    @ColorInt val selectedIconColor: Int,
    @ColorInt val textColor: Int,
    @ColorInt val selectedTextColor: Int,
    @ColorInt val badgeBackgroundColor: Int,
    @ColorInt val badgeStrokeColor: Int,
    @ColorInt val badgeTextColor: Int
) : Equtable() {
    fun getIconTint(): ColorStateList {
        val states = arrayOf(
            intArrayOf(R.attr.state_selected),
            intArrayOf()
        )
        val colors = intArrayOf(
            this.selectedIconColor,
            this.iconColor
        )

        return ColorStateList(
            states,
            colors
        )
    }

    fun getTitleTint(): ColorStateList {
        val states = arrayOf(
            intArrayOf(R.attr.state_selected),
            intArrayOf()
        )
        val colors = intArrayOf(
            this.selectedTextColor,
            this.textColor
        )

        return ColorStateList(
            states,
            colors
        )
    }
}

data class PresentationThemeRootController(
    @ColorInt val backgroundColor: Int,
    val tabBar: PresentationThemeRootTabBar
) : Equtable()

data class PresentationTheme(
    val name: String,
    val rootController: PresentationThemeRootController
) : Equtable() {

}