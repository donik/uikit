package kz.citicom.sampleuikit

import android.content.Context
import android.graphics.Color
import android.util.Log
import kz.citicom.uikit.UIActivity
import kz.citicom.uikit.controllers.UIViewController

class TestVC(context: UIActivity) : UIViewController(context) {
    override fun loadView() {
        this.view.setBackgroundColor(Color.GRAY)
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