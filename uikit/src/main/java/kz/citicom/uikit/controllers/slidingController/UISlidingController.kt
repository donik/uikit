package kz.citicom.uikit.controllers.slidingController

import android.graphics.Color
import android.opengl.Visibility
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View

import kz.citicom.uikit.UIActivity
import kz.citicom.uikit.controllers.UIViewController
import kz.citicom.uikit.controllers.navigationController.NavigationGestureRecognizer
import kz.citicom.uikit.presentationData.PresentationData
import kz.citicom.uikit.tools.LayoutHelper
import kz.citicom.uikit.tools.UIColor
import kz.citicom.uikit.tools.WeakRef
import kz.citicom.uikit.tools.weak
import kz.citicom.uikit.views.UIView

class UISlidingController(
    context: UIActivity,
    mainController: UIViewController,
    slideController: UIViewController,
    presentationData: PresentationData
) :
    UIViewController(context, presentationData) {

    private var mainContainerView: UIView = UIView(context)
    private var shadowView: UIView = UIView(context)
    private var slidingContainerView: UIView = UIView(context)

    private val interactiveGestureRecognizer: SlidingGestureRecognizer
    private val transitionCoordinator: UISlidingControllerTransitionCoordinator =
        UISlidingControllerTransitionCoordinator(
            mainContainerView,
            shadowView,
            slidingContainerView
        )

    var mainController: UIViewController? = null
        private set
    var slideController: UIViewController? = null
        private set

    val isAnimating: Boolean
        get() {
            return this.transitionCoordinator.isAnimating
        }

    init {
        val weakSelf by weak(this)

        mainController.slidingController = this
        slideController.slidingController = this

        this.mainController = mainController
        this.slideController = slideController

        this.updateSlidingController(null, this.slideController)
        this.updateMainController(null, this.mainController)

        this.interactiveGestureRecognizer = SlidingGestureRecognizer(
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

        this.view.setBackgroundColor(Color.RED)

        this.mainContainerView.setBackgroundColor(UIColor.white)
        this.view.addView(
            this.mainContainerView,
            LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT)
        )

        this.shadowView.setBackgroundColor(UIColor.black)
        this.shadowView.alpha = 0.0f
        this.shadowView.visibility = View.INVISIBLE
        this.shadowView.setOnClickListener {
            this.closeSlidingMenu()
        }
        this.view.addView(
            this.shadowView,
            LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT)
        )

        UIView.setClickable(this.slidingContainerView)
        this.slidingContainerView.setBackgroundColor(UIColor.getColor("#ff0000"))
        this.slidingContainerView.visibility = View.INVISIBLE
        this.view.addView(
            this.slidingContainerView,
            LayoutHelper.createFrame(
                LayoutHelper.MATCH_PARENT,
                LayoutHelper.MATCH_PARENT,
                Gravity.LEFT,
                0.0f,
                0.0f,
                64.0f,
                0.0f
            )
        )
    }

    fun setSlidingViewController(viewController: UIViewController) {
        viewController.slidingController = this
        this.updateSlidingController(this.slideController, viewController)
    }

    fun setMainViewController(viewController: UIViewController) {
        viewController.slidingController = this
        this.updateMainController(this.mainController, viewController)
    }

    private fun updateSlidingController(
        oldSlidingController: UIViewController?,
        newSlidingController: UIViewController?
    ) {
        if (oldSlidingController == newSlidingController) {
            return
        }
        this.slideController = newSlidingController

        oldSlidingController?.viewWillDisappear()

        val view = newSlidingController?.view
        newSlidingController?.viewWillAppear()

        view?.let {
            this.slidingContainerView.addView(
                it,
                LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT)
            )
        }

        oldSlidingController?.viewDidDisappear()
        newSlidingController?.viewDidAppear()
    }

    private fun updateMainController(
        oldMainController: UIViewController?,
        newMainController: UIViewController?
    ) {
        if (oldMainController == newMainController) {
            return
        }

        oldMainController?.viewWillDisappear()

        val view = newMainController?.view
        newMainController?.viewWillAppear()

        view?.let {
            this.mainContainerView.addView(
                it,
                LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT)
            )
        }

        oldMainController?.viewDidDisappear()
        newMainController?.viewDidAppear()
        this.mainController = newMainController
    }

    fun showSlidingMenu() {
        if (this.isAnimating) {
            return
        }

        this.transitionCoordinator.open()
    }

    fun closeSlidingMenu() {
        if (this.isAnimating) {
            return
        }

        this.transitionCoordinator.close(0.0f)
    }

    private fun isBlocked(): Boolean {
        return interactiveGestureRecognizer.isDispatching
    }

    override fun onBackPressed(): Boolean {
        if (this.transitionCoordinator.isVisible) {
            this.closeSlidingMenu()
            return false
        }

        return this.mainController?.onBackPressed() ?: true
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

    override fun destroy() {
        super.destroy()

        this.slideController?.destroy()
        this.mainController?.destroy()
    }

    override fun containerLayoutUpdated() {
        super.containerLayoutUpdated()

        this.mainController?.containerLayoutUpdated()
        this.slideController?.containerLayoutUpdated()
    }
}