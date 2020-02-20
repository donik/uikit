package kz.citicom.sampleuikit

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.Gravity
import kz.citicom.uikit.UIActivity
import kz.citicom.uikit.controllers.UIViewController
import kz.citicom.uikit.tools.LayoutHelper
import kz.citicom.uikit.tools.weak
import kz.citicom.uikit.views.UIView
import kz.citicom.uikit.views.navigationBar.UINavigationBar
import java.util.*
import kotlin.concurrent.schedule

class TestVC(context: UIActivity, private val index: Int = 0) : UIViewController(context) {
    override fun loadView(): UIView? {
        val contentView = UIView(weakContext ?: return null)
        contentView.setBackgroundResource(R.drawable.first)

        return contentView
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

    protected fun finalize() {
        Log.e("FIN", "FINALIZE")
    }
}