package kz.citicom.sampleuikit

import android.os.Bundle
import android.util.Log
import kz.citicom.uikit.UIActivity
import kz.citicom.uikit.controllers.navigationController.UINavigationController
import kz.citicom.uikit.views.UIWindow

class MainActivity : UIActivity() {
    override var uiWindow: UIWindow? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val vc = UINavigationController(this)

        this.uiWindow = UIWindow(this)
        this.uiWindow?.rootViewController = vc
        this.uiWindow?.makeKeyAndVisible()

        var index = 0
        vc.getWrap()?.setOnClickListener {
            index++

            val root = TestVC(this, index)

            vc.push(root, true)
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        Log.e("ONDE", "ONDESTROY")
    }

}