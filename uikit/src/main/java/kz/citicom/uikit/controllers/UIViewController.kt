package kz.citicom.uikit.controllers

import android.content.Context
import androidx.annotation.CallSuper
import kz.citicom.uikit.tools.UIEdgeInsets
import kz.citicom.uikit.tools.UIScreen
import kz.citicom.uikit.tools.zero
import kz.citicom.uikit.views.UIView
import kz.citicom.uikit.views.removeChilds
import kz.citicom.uikit.views.removeFromSuperview

abstract class UIViewController(protected val context: Context) {
    private var _view: UIView? = null
    protected val view: UIView = UIView(context)

    var isViewLoaded: Boolean = false
        private set
    internal var isDestroyed: Boolean = false
        private set
    internal var controllerInsets: UIEdgeInsets = UIEdgeInsets.zero()
        set(value) {
            field = value
            this.insetsUpdated()
        }

    fun getWrap(): UIView {
        if (this.isDestroyed) {
            this.isDestroyed = false
        }
        if (this._view == null) {
            this.view.removeChilds()
            this._view = this.view
            loadView()
            viewDidLoad()
        }
        assert(this._view == null) { "View is null" }

        return this.view
    }

    private val internalContainer: UIView = UIView(context)

    abstract fun loadView()

    @CallSuper
    open fun viewDidLoad() {
        this.isViewLoaded = true
    }

    @CallSuper
    open fun viewWillAppear() {
    }

    @CallSuper
    open fun viewDidAppear() {
    }

    @CallSuper
    open fun viewWillDisappear() {
    }

    @CallSuper
    open fun viewDidDisappear() {
    }

    internal fun destroy() {
        this.isDestroyed = true
        this._view = null
        this.view.removeFromSuperview()
    }

    private fun insetsUpdated() {
        this.view.setPadding(
            UIScreen.dp(controllerInsets.left),
            UIScreen.dp(controllerInsets.top),
            UIScreen.dp(controllerInsets.right),
            UIScreen.dp(controllerInsets.bottom)
        )
    }

    // return true is need close
    open fun onBackPressed(): Boolean {
        return false
    }


}