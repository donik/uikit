package kz.citicom.sampleuikit

import kz.citicom.uikit.UIApplication
import java.io.File

class Application : UIApplication() {
    override fun filesDirectory(): File {
        return File("files") //todo
    }

    override fun cacheDirectory(): File {
        return File("cache") //todo
    }
}