package kz.citicom.uikit

import android.content.Context
import kz.citicom.uikit.presentationData.themes.initPresentationData
import kz.citicom.uikit.tools.*

object UIKit {
    private var appContext: Context? = null

    fun init(context: Context) {
        appContext = appContext

        UIScreen.init(context)
        UISize.init(context)
        UIFonts.init(context)
        UIImage.init(context)
        UILocalization.init(context)
        initPresentationData(context)
    }
}