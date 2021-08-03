package kz.citicom.uikit.presentationData.themes

import kz.citicom.uikit.presentationData.PresentationTheme
import kz.citicom.uikit.presentationData.PresentationThemeRootController
import kz.citicom.uikit.presentationData.PresentationThemeRootTabBar
import kz.citicom.uikit.tools.UIColor

fun makeDefaultDarkPresentationTheme() : PresentationTheme {
    val tabBarTheme = PresentationThemeRootTabBar(
        backgroundColor = UIColor.getColor("#1c1c1d"),
        iconColor = UIColor.getColor("#828282"),
        selectedIconColor = UIColor.getColor("#ffffff"),
        textColor = UIColor.getColor("#828282"),
        selectedTextColor = UIColor.getColor("#ffffff"),
        badgeBackgroundColor = UIColor.getColor("#ff3b30"),
        badgeStrokeColor = UIColor.getColor("#ff3b30"),
        badgeTextColor = UIColor.getColor("#ffffff")
    )

    val rootControllerTheme = PresentationThemeRootController(
        UIColor.getColor("#000000"),
        tabBar = tabBarTheme
    )

    return PresentationTheme(
        "Dark",
        rootController = rootControllerTheme
    )
}