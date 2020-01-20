package kz.citicom.uikit.views.navigationBar

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import kz.citicom.uikit.tools.UIScreen

class UINavigationBar(context: Context) : FrameLayout(context) {

    init {
        setPadding(0, 20, 0, 0)
        setBackgroundColor(Color.RED)
    }

}