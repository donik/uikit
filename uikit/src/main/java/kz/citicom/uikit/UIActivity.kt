package kz.citicom.uikit

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.FrameLayout
import kz.citicom.uikit.views.ActivityContentLayout
import kz.citicom.uikit.views.UIWindow

open abstract class UIActivity : Activity() {
    internal lateinit var view: ActivityContentLayout
    abstract var uiWindow: UIWindow?
    val keyWindows: HashMap<String, UIWindow> = hashMapOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setTheme(R.style.Theme_UIKit)

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
        super.onBackPressed()
    }
}