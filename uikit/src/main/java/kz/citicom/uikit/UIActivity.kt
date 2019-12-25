package kz.citicom.uikit

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout

open class UIActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        super.setContentView(FrameLayout(this))
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
}