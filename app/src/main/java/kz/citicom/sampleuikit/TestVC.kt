package kz.citicom.sampleuikit

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.Gravity
import kz.citicom.uikit.UIActivity
import kz.citicom.uikit.controllers.UIViewController
import kz.citicom.uikit.tools.LayoutHelper
import kz.citicom.uikit.views.navigationBar.UINavigationBar

class TestVC(context: UIActivity) : UIViewController(context) {
    override fun loadView() {
        val context = weakContext ?: return
//        this.view.setBackgroundColor(Color.GRAY)

        val navigationBar = UINavigationBar(context)
        this.view.addView(
            navigationBar,
            LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 44, Gravity.START)
        )
    }

    override fun viewDidLoad() {
        super.viewDidLoad()

        Log.e("TestVC", "viewDidLoad")
    }

    override fun viewWillAppear() {
        super.viewWillAppear()

        Log.e("TestVC", "viewWillAppear")
    }

    override fun viewDidAppear() {
        super.viewDidAppear()

        Log.e("TestVC", "viewDidAppear")
    }

    override fun viewWillDisappear() {
        super.viewWillDisappear()

        Log.e("TestVC", "viewWillDisappear")
    }

    override fun viewDidDisappear() {
        super.viewDidDisappear()

        Log.e("TestVC", "viewDidDisappear")
    }
}