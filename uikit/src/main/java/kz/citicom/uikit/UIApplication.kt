package kz.citicom.uikit

import android.app.Application
import android.content.Context
import android.os.Handler
import androidx.annotation.CallSuper
import java.io.File

abstract class UIApplication : Application() {
    companion object {
        var shared: UIApplication? = null
            private set
        var applicationContext: Context? = null
            private set
        var applicationHandler: Handler? = null

        fun post(r: Runnable, delay: Long? = null) {
            if (delay == null) {
                applicationHandler?.post(r)
            } else {
                applicationHandler?.postDelayed(r, delay)
            }
        }

        fun cancel(r: Runnable) {
            applicationHandler?.removeCallbacks(r)
        }
    }

    abstract fun filesDirectory(): File
    abstract fun cacheDirectory(): File

    @CallSuper
    override fun onCreate() {
        super.onCreate()

        shared = this
        Companion.applicationContext = this.applicationContext
        applicationHandler = Handler(this.applicationContext.mainLooper)

        UIKit.init(this)
    }

}