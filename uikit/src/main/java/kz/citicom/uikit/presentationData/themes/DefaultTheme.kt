package kz.citicom.uikit.presentationData.themes

import androidx.annotation.ColorInt
import kz.citicom.uikit.presentationData.PresentationTheme
import kz.citicom.uikit.presentationData.PresentationThemeRootController
import kz.citicom.uikit.presentationData.PresentationThemeRootTabBar
import kz.citicom.uikit.tools.UIColor

fun makeDefaultPresentationTheme(@ColorInt accentColor: Int) : PresentationTheme {
    val tabBarTheme = PresentationThemeRootTabBar(
        backgroundColor =  UIColor.getColor("#3a3c58"),
        iconColor = UIColor.getColor("#959595"),
        selectedIconColor = UIColor.getColor("#ffffff"),
        textColor = UIColor.getColor("#959595"),
        selectedTextColor = UIColor.getColor("#ffffff"),
        badgeBackgroundColor = UIColor.getColor("#ff3b30"),
        badgeStrokeColor = UIColor.getColor("#ff3b30"),
        badgeTextColor = UIColor.getColor("#ffffff")
    )

    val rootControllerTheme = PresentationThemeRootController(
        UIColor.getColor("#ffffff"),
        tabBar = tabBarTheme
    )

    return PresentationTheme(
        "Light",
        rootController = rootControllerTheme
    )
}