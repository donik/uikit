package kz.citicom.uikit.controllers

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.view.LayoutInflater
import android.view.MotionEvent
import androidx.annotation.CallSuper
import androidx.annotation.IntegerRes
import androidx.annotation.LayoutRes
import kotlinx.android.synthetic.main.ui_view.view.*
import kz.citicom.uikit.R
import kz.citicom.uikit.UIActivity
import kz.citicom.uikit.controllers.navigationController.UINavigationController
import kz.citicom.uikit.tools.UIEdgeInsets
import kz.citicom.uikit.tools.UIScreen
import kz.citicom.uikit.tools.zero
import kz.citicom.uikit.utils.Wrapper
import kz.citicom.uikit.views.UIView
import kz.citicom.uikit.views.removeFromSuperview
import java.lang.ref.WeakReference

abstract class UIViewController(context: UIActivity) : Wrapper<UIView>() {
    protected var view: UIView? = null
        protected set

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

    private var _weakNavigationController: Wrapper<UIView>? = null
    var navigationController: UINavigationController?
        get() {
            return _weakNavigationController as? UINavigationController
        }
        set(value) {
            _weakNavigationController = value
        }

    @LayoutRes
    internal var viewResource: Int = R.layout.ui_view

    protected var supportedOrientation: SupportedOrientation = SupportedOrientation.ALL
    private val activityOrientation = when (supportedOrientation) {
        SupportedOrientation.PORTRAIT -> ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        SupportedOrientation.ALL -> ActivityInfo.SCREEN_ORIENTATION_SENSOR
    }
    open var isSwipeNavigationEnabled: Boolean = true

    open fun isIntercepted(): Boolean {
        return false
    }

    override fun getWrap(): UIView? {
        if (this.view == null) {
            this.loadView()
            this.viewDidLoad()
        }
        return this.view
    }

    @SuppressLint("Assert")
    @CallSuper
    protected open fun loadView() {
        if (this.weakContext == null) {
            assert(false) { "Error context null" }
        }

        val context = this.weakContext ?: return
        val view = LayoutInflater.from(context).inflate(this.viewResource, null) as? UIView//?: UIView(context)
        this.view = view
    }

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
        this.navigationController = null
    }

    private fun insetsUpdated() {
        this.view?.setPadding(
            UIScreen.dp(controllerInsets.left),
            UIScreen.dp(controllerInsets.top),
            UIScreen.dp(controllerInsets.right),
            UIScreen.dp(controllerInsets.bottom)
        )
    }

    open fun canSlideBackFrom(x: Float, y: Float): Boolean {
        return true
    }

    open fun onInterceptTouchEvent(event: MotionEvent?): Boolean {
        return false
    }

    open fun onTouchEvent(event: MotionEvent?): Boolean {
        return false
    }

    open fun onBackPressed(): Boolean {
        return true
    }

    enum class SupportedOrientation {
        PORTRAIT,
        ALL;
    }

}