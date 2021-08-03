package kz.citicom.uikit.presentationData.themes

import android.content.Context
import kz.citicom.uikit.presentationData.PresentationData
import kz.citicom.uikit.presentationData.PresentationFontSize
import kz.citicom.uikit.presentationData.PresentationStrings
import kz.citicom.uikit.presentationData.PresentationTheme
import kz.citicom.uikit.tools.UIColor

val darkTheme: PresentationTheme = makeDefaultDarkPresentationTheme()
val lightTheme: PresentationTheme = makeDefaultPresentationTheme(
    accentColor = UIColor.getColor("#007ee5")
)

private var presentationData: PresentationData? = null

fun initPresentationData(context: Context) {
    presentationData = PresentationData(
        PresentationStrings(context),
        lightTheme,
        PresentationFontSize.EXTRA_LARGE_X2
    )
}
fun getPresentationData(): PresentationData {
    return presentationData ?: throw Error("Initialize Presentation Data Before call this method use setPresentationData")
}