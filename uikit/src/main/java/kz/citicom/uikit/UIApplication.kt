package kz.citicom.uikit

import android.app.Application
import android.content.Context
import android.os.Handler
import androidx.annotation.CallSuper
import kz.citicom.uikit.tools.UIFonts
import kz.citicom.uikit.tools.UIScreen
import kz.citicom.uikit.tools.UISize
import java.io.File

abstract class UIApplication : Application() {
    companion object {
        var shared: UIApplication? = null
            private set
        var applicationContext: Context? = null
            private set
        var applicationHandler: Handler? = null
    }

    abstract fun filesDirectory(): File
    abstract fun cacheDirectory(): File

    @CallSuper
    override fun onCreate() {
        super.onCreate()

        shared = this
        UIApplication.applicationContext = this.applicationContext
        applicationHandler = Handler(this.applicationContext.mainLooper)

        UIScreen.init(this)
        UISize.init(this)
        UIFonts.init(this)
    }

}