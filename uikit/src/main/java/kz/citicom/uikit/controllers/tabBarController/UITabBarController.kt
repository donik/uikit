package kz.citicom.uikit.controllers.tabBarController

import kz.citicom.uikit.R
import kz.citicom.uikit.UIActivity
import kz.citicom.uikit.controllers.UIViewController

class UITabBarController(context: UIActivity) : UIViewController(context) {

    override fun loadView() {
        super.loadView()

        this.view?.setBackgroundResource(R.drawable.transparent)
    }

}