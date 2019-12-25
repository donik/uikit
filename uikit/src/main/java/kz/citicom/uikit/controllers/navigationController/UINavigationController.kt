package kz.citicom.uikit.controllers.navigationController

import android.content.Context
import android.view.View
import kz.citicom.uikit.controllers.UIViewController
import kz.citicom.uikit.tools.LayoutHelper
import kz.citicom.uikit.tools.weak
import kz.citicom.uikit.views.UIView

open class UINavigationController(context: Context) : UIViewController(context) {
    private val processor: UINavigationControllerProcessor = UINavigationControllerProcessor()
    private var foregroundContentView: UIView = UIView(context)
    private var backgroundContentView: UIView = UIView(context)

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

    fun push(viewController: UIViewController, animated: Boolean = true) {
        //todo check push allow !!!
        val weakSelf by weak(this)
        this.processor.push(viewController, animated)
    }

    fun popViewController(animated: Boolean) {
        //todo check pop allow !!!
        this.processor.pop(animated)
    }

    override fun onBackPressed(): Boolean {
        return super.onBackPressed()
    }

}