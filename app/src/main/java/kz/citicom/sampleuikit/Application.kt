package kz.citicom.sampleuikit

import android.content.Context
import android.os.Handler
import android.util.Log
import kz.citicom.uikit.UIApplication
import java.io.File

open class Application : UIApplication() {

    override fun filesDirectory(): File {
        return File("files") //todo
    }

    override fun cacheDirectory(): File {
        return File("cache") //todo
    }

    override fun onCreate() {
        super.onCreate()
    }
}
