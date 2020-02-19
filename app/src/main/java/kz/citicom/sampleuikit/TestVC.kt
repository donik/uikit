package kz.citicom.sampleuikit

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.Gravity
import kz.citicom.uikit.UIActivity
import kz.citicom.uikit.controllers.UIViewController
import kz.citicom.uikit.tools.LayoutHelper
import kz.citicom.uikit.views.navigationBar.UINavigationBar

class TestVC(context: UIActivity, private val index: Int = 0) : UIViewController(context) {
    override fun loadView() {
        val context = weakContext ?: return

//        if (index % 2 == 0) {
            this.view.setBackgroundResource(R.drawable.first)
//        } else {
//            this.view.setBackgroundResource(R.drawable.second)
//        }
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