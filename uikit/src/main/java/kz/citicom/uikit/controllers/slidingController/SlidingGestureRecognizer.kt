package kz.citicom.uikit.controllers.slidingController

import android.content.Context
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import kz.citicom.uikit.controllers.UIViewController
import kz.citicom.uikit.tools.UIScreen

class SlidingGestureRecognizer(
    context: Context,
    private val slidingController: UISlidingController?,
    private val transitionCoordinator: UISlidingControllerTransitionCoordinator
) : GestureDetector.OnGestureListener {
    private var abortUp: Boolean = false
    private val gestureDetector: GestureDetector = GestureDetector(context, this)
    private var lastScrollX: Int = 0
    private var slidingDrawer: Boolean = false
    private var startX: Float = 0.0f

    val isDispatching: Boolean
        get() {
            return this.slidingDrawer
        }


    private fun clear() {
        this.startX = 0.0f
        this.slidingDrawer = false
        this.lastScrollX = -1
    }

    fun onTouchEvent(e: MotionEvent?): Boolean {
        val event = e ?: return false

        return when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                this.gestureDetector.onTouchEvent(event)
                return true
            }
            MotionEvent.ACTION_UP -> {
                this.gestureDetector.onTouchEvent(event)
                return onUp()
            }
            MotionEvent.ACTION_MOVE -> {
                this.gestureDetector.onTouchEvent(event)
                return onDrag(event)
            }
            else -> {
                return this.gestureDetector.onTouchEvent(event)
            }
        }
    }

    private fun onUp(): Boolean {
        if (this.abortUp) {
            clear()
            this.abortUp = false
        } else {
            if (this.slidingDrawer) {
                this.transitionCoordinator.drop()
            }
            clear()
        }
        return false
    }

    private fun onDrag(event: MotionEvent): Boolean {
        this.lastScrollX = (event.getX(0) - this.startX).toInt()

        if (this.slidingDrawer) {
            if (this.transitionCoordinator.isVisible) {
                this.lastScrollX = (event.getX(0) - this.startX).toInt()
            }
            this.transitionCoordinator.translate(this.lastScrollX.toFloat())
            return false
        } else {
            val diffX = event.getX(0) - this.startX
            this.startX += diffX

            if (!this.canShowSlidingControllerFrom(this.slidingController?.mainController)) {
                return false
            }

            if (!this.transitionCoordinator.prepare()) {
                return false;
            }

            this.slidingDrawer = true
            return false
        }
    }

    override fun onShowPress(p0: MotionEvent?) {

    }

    override fun onSingleTapUp(p0: MotionEvent?): Boolean {
        return false
    }

    override fun onDown(e: MotionEvent?): Boolean {
        val event = e ?: return false
        if (this.slidingController?.isDestroyed == true || this.slidingController?.isAnimating == true) {
            return false
        }
        this.startX = event.getX(0)

        return false
    }

    override fun onFling(
        e1: MotionEvent?,
        e2: MotionEvent?,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        val abs = Math.abs(velocityX)
        if (abs > UIScreen.dpf(250.0f, 1.0f)) {
            this.abortUp = true

            if (this.slidingDrawer) {
                if (velocityX >= 0.0f) {
                    this.transitionCoordinator.open(abs)
                } else {
                    this.transitionCoordinator.close(abs)
                }
            }

            clear()
        } else {
            this.abortUp = false
        }
        return this.abortUp
    }

    override fun onScroll(p0: MotionEvent?, p1: MotionEvent?, p2: Float, p3: Float): Boolean {
        return this.slidingDrawer
    }

    override fun onLongPress(p0: MotionEvent?) {

    }

    private fun canShowSlidingControllerFrom(viewController: UIViewController?): Boolean {
        if (viewController == null || !viewController.canShowSlidingControllerFrom() || !viewController.isSwipeNavigationEnabled) {
            return false
        }
        return true
    }

}