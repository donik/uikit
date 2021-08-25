package kz.citicom.uikit.controllers

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ActivityInfo
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import butterknife.ButterKnife
import kz.citicom.uikit.UIActivity
import kz.citicom.uikit.controllers.modal.ModalWindow
import kz.citicom.uikit.controllers.navigationController.UINavigationController
import kz.citicom.uikit.controllers.slidingController.UISlidingController
import kz.citicom.uikit.presentationData.PresentationData
import kz.citicom.uikit.tools.LayoutHelper
import kz.citicom.uikit.tools.weak
import kz.citicom.uikit.uiabstract.UIWindowAbstract
import kz.citicom.uikit.views.UIView
import kz.citicom.uikit.views.removeFromSuperview
import java.lang.ref.WeakReference

abstract class UIViewController(
    context: Context,
    protected var presentationData: PresentationData
) : InternalViewController() {
    private var _view: UIView? = null
    val view: UIView
        get() {
            return getWrap()!!
        }
    lateinit var rootView: View

    protected var isViewLoaded: Boolean = false
        private set
    internal var isDestroyed: Boolean = false
        private set
    private var _weakContext: WeakReference<Context> = WeakReference(context)
    val context: Context = _weakContext.get()!!
    val uiWindow: UIWindowAbstract?
        get() = (this.context as? UIActivity)?.uiUIWindow

    private var _weakModalWindow: ModalWindow<*>? = null
    var modalWindow: ModalWindow<*>?
        get() {
            return _weakModalWindow
        }
        set(value) {
            _weakModalWindow = value
        }

    private var _weakNavigationController: UINavigationController? = null
    var navigationController: UINavigationController?
        get() {
            return _weakNavigationController //?: _weakTabBarController?.navigationController
        }
        set(value) {
            _weakNavigationController = value
        }
//    private var _weakTabBarController: UITabBarController? = null
//    var tabBarController: UITabBarController?
//        get() {
//            return _weakTabBarController ?: _weakNavigationController?.tabBarController
//        }
//        set(value) {
//            _weakTabBarController = value
//        }
    private var _weakSlidingController: UISlidingController? = null
    var slidingController: UISlidingController?
        get() {
            return _weakSlidingController ?: _weakNavigationController?.slidingController
            //?: _weakTabBarController?.slidingController
        }
        set(value) {
            _weakSlidingController = value
        }
    open var isSwipeNavigationEnabled: Boolean = true
    open var transitionType = TransitionType.PUSH

    protected open var supportedOrientation: SupportedOrientation = SupportedOrientation.ALL
    private val activityOrientation = when (supportedOrientation) {
        SupportedOrientation.PORTRAIT -> ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        SupportedOrientation.ALL -> ActivityInfo.SCREEN_ORIENTATION_SENSOR
    }

    var afterAppear: (() -> Unit)? = null
    var afterDisappear: (() -> Unit)? = null

    open fun isIntercepted(): Boolean {
        return false
    }

    private fun getWrap(): UIView? {
        if (this._view == null) {
            this.loadView()
            this.viewDidLoad()
            this.updateThemeAndStrings()
        }
        return this._view
    }

    @LayoutRes
    abstract fun getLayoutRes(): Int

    @SuppressLint("Assert")
    @CallSuper
    protected open fun loadView() {
        if (isDestroyed) {
            throw Exception("Controller destroyed. Initialize new controller for use")
        }

        val weakSelf by weak(this)
        val view = object : UIView(context) {
            override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
                return weakSelf?.onInterceptTouchEvent(ev) ?: false
            }

            override fun onTouchEvent(event: MotionEvent?): Boolean {
                return weakSelf?.onTouchEvent(event) ?: false
            }
        }
        view.setBackgroundColor(presentationData.theme.rootController.backgroundColor)
        this._view = view

        val layoutRes = this.getLayoutRes()
        if (layoutRes == -1) {
            return
        }

        val root = LayoutInflater.from(context).inflate(layoutRes, this._view, false)
        ButterKnife.bind(this, root)
        this.rootView = root

        this.view.addView(
            this.rootView,
            LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT)
        )

        Log.e("ROOT", rootView.toString())
    }

    @CallSuper
    open fun viewDidLoad() {
        this.isViewLoaded = true
    }

    @CallSuper
    open fun viewDidUnload() {
        this._view?.removeFromSuperview()
        this._view = null
        this.isViewLoaded = false
    }

    @CallSuper
    open fun viewWillAppear() {
    }

    @CallSuper
    open fun viewDidAppear() {
        (this.context as? UIActivity)?.requestedOrientation = activityOrientation
    }

    @CallSuper
    open fun viewWillDisappear() {
    }

    @CallSuper
    open fun viewDidDisappear() {
    }

    @CallSuper
    internal open fun destroy() {
        viewDidUnload()
        this.isDestroyed = true
        this._weakContext.clear()
        this.navigationController = null
//        this.tabBarController = null
        this.slidingController = null
    }

    open fun present(viewController: UIViewController) {
        Log.e("PRESENT", "PRESENT CONTROLLER ${viewController.controllerID}")
    }

    open fun containerLayoutUpdated() {
    }

    protected open fun updateThemeAndStrings() {
    }

    open fun scrollToTop() {

    }

    override fun deinit() {
        Log.e("DEINIT", "DEINIT CONTROLLER")
    }

    open fun canSlideBackFrom(x: Float, y: Float): Boolean {
        return this.transitionType == TransitionType.PUSH
    }

    open fun canShowSlidingControllerFrom(): Boolean {
        return true
    }

    open fun onInterceptTouchEvent(event: MotionEvent?): Boolean {
        return false
    }

    open fun onTouchEvent(event: MotionEvent?): Boolean {
        return false
    }

    open fun onBackPressed(): Boolean {
        return this.navigationController?.onBackPressed() ?: true
    }

    open fun leftButtonClicked() {
        onBackPressed()
    }

    enum class TransitionType {
        PUSH,
        FADE
    }

    enum class SupportedOrientation {
        PORTRAIT,
        ALL;
    }
}