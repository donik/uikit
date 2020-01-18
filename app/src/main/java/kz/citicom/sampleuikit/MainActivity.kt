package kz.citicom.sampleuikit

import android.os.Bundle
import kz.citicom.uikit.UIActivity
import kz.citicom.uikit.views.UIWindow

class MainActivity : UIActivity() {
    override var uiWindow: UIWindow? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.uiWindow = UIWindow(this)
        this.uiWindow?.rootViewController = TestVC(this)
        this.uiWindow?.makeKeyAndVisible()
    }

}