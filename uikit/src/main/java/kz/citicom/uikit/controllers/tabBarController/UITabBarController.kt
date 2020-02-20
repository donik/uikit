package kz.citicom.uikit.controllers.tabBarController

import android.content.Context
import kz.citicom.uikit.R
import kz.citicom.uikit.UIActivity
import kz.citicom.uikit.controllers.UIViewController
import kz.citicom.uikit.views.UIView

class UITabBarController(context: UIActivity) : UIViewController(context) {

    override fun loadView(): UIView? {
        val contentView = UIView(this.weakContext ?: return null)
        contentView.setBackgroundResource(R.drawable.transparent)

        return contentView
    }

}