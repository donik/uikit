package kz.citicom.uikit.controllers.navigationController

import android.content.Context
import android.view.View
import kz.citicom.uikit.UIActivity
import kz.citicom.uikit.controllers.UIViewController
import kz.citicom.uikit.tools.LayoutHelper
import kz.citicom.uikit.tools.weak
import kz.citicom.uikit.views.UIView

open class UINavigationController(context: UIActivity) : UIViewController(context) {
    private var foregroundContentView: UIView = UIView(context)
    private var backgroundContentView: UIView = UIView(context)
    private val processor: UINavigationControllerProcessor = UINavigationControllerProcessor(
        UINavigationControllerTransitionCoordinator(
            foregroundContentView,
            backgroundContentView
        )
    )

    override fun loadView() {
        this.view.addView(
            this.backgroundContentView,
            LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT)
        )

        this.view.addView(
            this.foregroundContentView,
            LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT)
        )
    }

    fun setViewControllers(viewControllers: UIViewController, animated: Boolean = true) {

    }

    fun push(viewController: UIViewController, animated: Boolean = true) {
        this.processor.push(viewController, animated)
    }

    fun popViewController(animated: Boolean) {
        //todo check pop allow !!!
//        this.processor.pop(animated)
    }

    fun popToRoot(viewController: UIViewController, animated: Boolean = true) {

    }

    override fun onBackPressed(): Boolean {
        return super.onBackPressed()
    }

}