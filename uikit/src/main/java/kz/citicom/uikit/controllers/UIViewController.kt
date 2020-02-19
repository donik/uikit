package kz.citicom.uikit.controllers

import android.content.pm.ActivityInfo
import android.util.Log
import androidx.annotation.CallSuper
import kz.citicom.uikit.UIActivity
import kz.citicom.uikit.tools.UIEdgeInsets
import kz.citicom.uikit.tools.UIScreen
import kz.citicom.uikit.tools.zero
import kz.citicom.uikit.views.UIView
import kz.citicom.uikit.views.removeChilds
import kz.citicom.uikit.views.removeFromSuperview
import java.lang.Exception
import java.lang.ref.WeakReference

abstract class UIViewController(context: UIActivity) {
    private var _view: UIView? = null
    protected val view: UIView
        get() {
            val v = this._view
            if (v == null) {
                assert(true) {
                    return@assert "Error View is null"
                }
            }
            return v!!
        }

    var isViewLoaded: Boolean = false
        private set
    internal var isDestroyed: Boolean = false
        private set
    internal var controllerInsets: UIEdgeInsets = UIEdgeInsets.zero()
        set(value) {
            field = value
            this.insetsUpdated()
        }
    private var _weakContext: WeakReference<UIActivity> = WeakReference(context)
    val weakContext: UIActivity? = _weakContext.get()
    protected var supportedOrientation: SupportedOrientation = SupportedOrientation.ALL
    private val activityOrientation = when (supportedOrientation) {
        SupportedOrientation.PORTRAIT -> ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        SupportedOrientation.ALL -> ActivityInfo.SCREEN_ORIENTATION_SENSOR
    }

    fun getWrap(): UIView {
        if (this._view != null) {
            return this._view!!
        }
        if (this._view == null && weakContext != null) {
            this._view = UIView(weakContext, this.javaClass.toString())
            loadView()
            viewDidLoad()
        }
        assert(this._view == null) { "View is null" }

        return this.view
    }

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
        weakContext?.requestedOrientation = activityOrientation
    }

    @CallSuper
    open fun viewWillDisappear() {
    }

    @CallSuper
    open fun viewDidDisappear() {
    }

    @CallSuper
    internal fun destroy() {
        this.isDestroyed = true
        this.view.removeFromSuperview()
        this._view = null
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

    protected fun finalize() {
        Log.e("FIN", "FINALIZE")
    }

    enum class SupportedOrientation {
        PORTRAIT,
        ALL;
    }

}