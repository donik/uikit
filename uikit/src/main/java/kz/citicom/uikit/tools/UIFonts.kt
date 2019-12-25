package kz.citicom.uikit.tools

import android.content.Context
import android.graphics.Typeface

object UIFonts {
    var robotoBold: Typeface? = null
        private set
    var robotoLight: Typeface? = null
        private set
    var robotoMedium: Typeface? = null
        private set
    var robotoRegular: Typeface? = null
        private set

    fun init(context: Context) {
        robotoRegular = Typeface.createFromAsset(context.resources.assets, "fonts/1.ttf")
        robotoBold = Typeface.createFromAsset(context.resources.assets, "fonts/2.ttf")
        robotoMedium = Typeface.createFromAsset(context.resources.assets, "fonts/3.ttf")
        robotoLight = Typeface.createFromAsset(context.resources?.assets, "fonts/4.ttf")
    }
}