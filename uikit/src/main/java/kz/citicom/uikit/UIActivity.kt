package kz.citicom.uikit

import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.Window
import kz.citicom.uikit.uiabstract.UIWindowAbstract

import kz.citicom.uikit.tools.UIScreen
import kz.citicom.uikit.views.ActivityContentLayout

abstract class UIActivity : Activity() {
    internal lateinit var view: ActivityContentLayout
    abstract var uiUIWindow: UIWindowAbstract?
    val keyWindows: HashMap<String, UIWindowAbstract> = hashMapOf()

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setTheme(R.style.Theme_UIKit)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        super.onCreate(savedInstanceState)

        this.view = ActivityContentLayout(this)
        super.setContentView(this.view)
    }

    override fun setContentView(view: View?) {
        throw Exception("USE container addChild v")
    }

    override fun setContentView(layoutResID: Int) {
        throw Exception("USE container addChild")
    }

    override fun setContentView(view: View?, params: ViewGroup.LayoutParams?) {
        throw Exception("USE container addChild")
    }

    override fun onBackPressed() {
        val closeApp = this.uiUIWindow?.onBackPressed() ?: false
        if (!closeApp) {
            return
        }

        super.onBackPressed()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        UIScreen.onConfigurationChanged()
        this.uiUIWindow?.containerLayoutUpdated()
        this.keyWindows.forEach {
            it.value.containerLayoutUpdated()
        }
    }
}