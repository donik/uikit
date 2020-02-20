package kz.citicom.uikit.controllers.navigationController

import android.view.MotionEvent
import kz.citicom.uikit.UIActivity
import kz.citicom.uikit.controllers.UIViewController
import kz.citicom.uikit.gestures.NavigationGestureRecognizer
import kz.citicom.uikit.tools.LayoutHelper
import kz.citicom.uikit.tools.weak
import kz.citicom.uikit.views.UIView

open class UINavigationController(context: UIActivity) : UIViewController(context) {
    private var foregroundContentView: UIView = UIView(context)
    private var backgroundContentView: UIView = UIView(context)
    private val interactiveGestureRecognizer: NavigationGestureRecognizer
    private val transitionCoordinator: UINavigationControllerTransitionCoordinator =
        UINavigationControllerTransitionCoordinator(
            foregroundContentView,
            backgroundContentView
        )
    private val processor: UINavigationControllerProcessor
    val stackSize: Int
        get() {
            return this.processor.stackSize
        }
    val isAnimating: Boolean
        get() {
            return this.transitionCoordinator.isAnimating
        }
    val previousController: UIViewController?
        get() {
            return this.processor.previous
        }
    val lastVisibleController: UIViewController?
        get() {
            return this.processor.current
        }

    init {
        val weakSelf by weak(this)
        this.processor = UINavigationControllerProcessor(
            weakSelf,
            this.transitionCoordinator
        )
        this.interactiveGestureRecognizer = NavigationGestureRecognizer(
            context,
            weakSelf,
            this.transitionCoordinator
        )
    }

    override fun loadView(): UIView? {
        val contentView = UIView(this.weakContext ?: return null)

        contentView.addView(
            this.backgroundContentView,
            LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT)
        )

        contentView.addView(
            this.foregroundContentView,
            LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT)
        )

        return contentView
    }

    fun setViewControllers(viewControllers: UIViewController, animated: Boolean = true) {

    }

    fun push(viewController: UIViewController, animated: Boolean = true) {
        this.processor.push(viewController, animated)
    }

    fun popViewController(animated: Boolean) {
        this.processor.pop(animated)
    }

    fun popToRoot(viewController: UIViewController, animated: Boolean = true) {

    }

    private fun isBlocked(): Boolean {
        return interactiveGestureRecognizer.isDispatching
    }

    override fun onInterceptTouchEvent(event: MotionEvent?): Boolean {
        if (this.isBlocked() || this.transitionCoordinator.isAnimating) {
            return true
        }
        if (!this.onTouchEvent(event) || event?.action == MotionEvent.ACTION_DOWN) {
            return false
        }

        return super.onInterceptTouchEvent(event)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return this.interactiveGestureRecognizer.onTouchEvent(event)
    }

    override fun onBackPressed(): Boolean {
        if (this.processor.stackSize > 1) {
            this.popViewController(true)
            return false
        }

        return super.onBackPressed()
    }
}