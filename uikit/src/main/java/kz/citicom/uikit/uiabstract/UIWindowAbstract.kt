package kz.citicom.uikit.uiabstract

import kz.citicom.uikit.UIActivity
import kz.citicom.uikit.controllers.UIViewController

abstract class UIWindowAbstract {
    protected abstract var key: String?
    protected abstract val weakActivity: UIActivity?
    abstract var rootViewController: UIViewController?
    abstract var isHidden: Boolean

    abstract fun makeKeyAndVisible()
    protected abstract fun resignKey()
    abstract fun onBackPressed() : Boolean
    abstract fun containerLayoutUpdated()
}