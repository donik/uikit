package kz.citicom.sampleuikit

import android.os.Bundle
import android.util.Log
import kz.citicom.uikit.UIActivity
import kz.citicom.uikit.uiabstract.UIWindowAbstract
import kz.citicom.uikit.controllers.navigationController.UINavigationController
import kz.citicom.uikit.presentationData.themes.getPresentationData
import kz.citicom.uikit.views.UIWindow

class MainActivity : UIActivity() {
    override var uiUIWindow: UIWindowAbstract? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        val vc = TestVC(this, 0)
//        val vc = UITabBarController(this, getPresentationData())
        val vc = UINavigationController(this, getPresentationData())
        vc.setViewControllers(arrayOf(TestVC(this, 0)))

        this.uiUIWindow = UIWindow(this)
        this.uiUIWindow?.rootViewController = vc
        this.uiUIWindow?.makeKeyAndVisible()
    }

    override fun onDestroy() {
        super.onDestroy()

        Log.e("ONDE", "ONDESTROY")
    }
}