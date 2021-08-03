package kz.citicom.uikit.views

import android.view.View
import android.widget.FrameLayout
import kz.citicom.uikit.UIActivity
import kz.citicom.uikit.uiabstract.UIWindowAbstract
import kz.citicom.uikit.controllers.UIViewController
import kz.citicom.uikit.tools.LayoutHelper
import java.lang.ref.WeakReference
import java.util.*

open class UIWindow(
    activity: UIActivity
) : UIWindowAbstract() {
    override var key: String? = null
    private var view: FrameLayout = FrameLayout(activity)

    private var _weakActivity: WeakReference<UIActivity> = WeakReference(activity)
    override val weakActivity: UIActivity? = _weakActivity.get()

    override var rootViewController: UIViewController? = null
        set(value) {
            if (field == value) {
                return
            }

            val tempRootController = field
            field = value
            this.updateRootController(tempRootController)
        }
    override var isHidden: Boolean
        get() = this.view.visibility != View.VISIBLE
        set(hidden) {
            this.view.visibility = if (hidden) {
                View.GONE
            } else {
                View.VISIBLE
            }

            if (hidden) {
                resignKey()
            } else {
                makeKeyAndVisible()
            }
        }

    init {
        this.view.visibility = View.GONE
    }

    override fun makeKeyAndVisible() {
        if (this.key != null) {
            return
        }

        val windowKey = UUID.randomUUID().toString()
        this.key = windowKey
        this.weakActivity?.keyWindows?.set(windowKey, this)
        this.view.removeFromSuperview()
        this.view.visibility = View.VISIBLE

        this.rootViewController?.viewWillAppear()
        this.weakActivity?.view?.addView(
            this.view,
            LayoutHelper.createFrame(
                LayoutHelper.MATCH_PARENT,
                LayoutHelper.MATCH_PARENT
            )
        )
        this.rootViewController?.viewDidAppear()
    }

    override fun resignKey() {
        val windowKey = this.key ?: return
        this.view.removeFromSuperview()
        this.weakActivity?.keyWindows?.remove(windowKey)
        this.key = null
    }

    private fun updateRootController(oldController: UIViewController?) {
        oldController?.viewWillDisappear()
        oldController?.view?.removeFromSuperview()
        oldController?.viewDidDisappear()
        oldController?.destroy()

        val rootController = this.rootViewController ?: return
        val view = rootController.view

        if (this.key != null) {
            rootController.viewWillAppear()
        }
        this.view.addView(
            view,
            LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT)
        )
        if (this.key != null) {
            rootController.viewDidAppear()
        }
    }

    override fun onBackPressed() : Boolean {
        return this.rootViewController?.onBackPressed() ?: false
    }

    override fun containerLayoutUpdated() {
        this.rootViewController?.containerLayoutUpdated()
    }

}