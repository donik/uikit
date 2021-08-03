package kz.citicom.uikit.controllers.navigationController

import android.view.Gravity
import android.view.MotionEvent
import kz.citicom.uikit.UIActivity
import kz.citicom.uikit.controllers.UIViewController
import kz.citicom.uikit.presentationData.PresentationData
import kz.citicom.uikit.tools.LayoutHelper
import kz.citicom.uikit.tools.weak
import kz.citicom.uikit.views.UIView

open class UINavigationController(
    context: UIActivity,
    presentationData: PresentationData
) : UIViewController(context, presentationData) {

    private var mainContainerView: UIView = UIView(context)
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

    constructor(
        context: UIActivity,
        viewControllers: List<UIViewController>,
        presentationData: PresentationData
    ) : this(context, presentationData) {
        this.setViewControllers(viewControllers.toTypedArray(), false)
    }

    constructor(
        context: UIActivity,
        rootViewController: UIViewController,
        presentationData: PresentationData
    ) : this(context, presentationData) {
        this.setViewControllers(arrayOf(rootViewController), false)
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

    override fun getLayoutRes(): Int {
        return -1
    }

    override fun loadView() {
        super.loadView()

        this.transitionCoordinator.mainContainer = this.view

        this.view.addView(
            this.mainContainerView,
            LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT)
        )

        this.mainContainerView.addView(
            this.backgroundContentView,
            LayoutHelper.createFrame(
                LayoutHelper.MATCH_PARENT,
                LayoutHelper.MATCH_PARENT,
                Gravity.TOP,
                0.0f,
                0.0f,
                0.0f,
                0.0f
            )
        )

        this.mainContainerView.addView(
            this.foregroundContentView,
            LayoutHelper.createFrame(
                LayoutHelper.MATCH_PARENT,
                LayoutHelper.MATCH_PARENT,
                Gravity.TOP,
                0.0f,
                0.0f,
                0.0f,
                0.0f
            )
        )
    }

    override fun leftButtonClicked() {
        this.lastVisibleController?.leftButtonClicked()
    }

    fun setViewControllers(viewControllers: Array<UIViewController>, animated: Boolean = true) {
        this.processor.setViewControllers(viewControllers, animated)
    }

    fun push(viewController: UIViewController, animated: Boolean = true) {
        this.processor.push(viewController, animated)
    }

    fun popViewController(animated: Boolean) {
        this.processor.pop(animated)
    }

    fun popToRoot(animated: Boolean = true) {
        this.processor.popToRoot(animated)
    }

    internal fun popInternal() {
        this.processor.popInternal()
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

        return true
    }

    override fun canShowSlidingControllerFrom(): Boolean {
        return this.stackSize <= 1 && !this.isAnimating
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

    override fun destroy() {
        super.destroy()

        this.processor.clear()
    }

    override fun containerLayoutUpdated() {
        super.containerLayoutUpdated()

        this.processor.forEach {
            it.containerLayoutUpdated()
        }
    }
}