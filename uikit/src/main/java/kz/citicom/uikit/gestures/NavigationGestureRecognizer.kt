package kz.citicom.uikit.gestures

import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import kz.citicom.uikit.controllers.UIViewController
import kz.citicom.uikit.controllers.navigationController.UINavigationController
import kz.citicom.uikit.controllers.navigationController.UINavigationControllerTransitionCoordinator
import kz.citicom.uikit.tools.UIScreen
import kz.citicom.uikit.tools.UISize
import kotlin.math.abs

public class NavigationGestureRecognizer(
    context: Context,
    private val navigationController: UINavigationController?,
    private val transitionCoordinator: UINavigationControllerTransitionCoordinator
) : GestureDetector.OnGestureListener {
    private var abortUp: Boolean = false
    private val gestureDetector: GestureDetector = GestureDetector(context, this)
    private var lastScrollX: Int = 0
    private var slidingBack: Boolean = false
    private var listenSlidingBack: Boolean = false
    private var startX: Float = 0.0f
    private var startY: Float = 0.0f

    val isDispatching: Boolean
        get() {
            return this.slidingBack
        }


    private fun clear() {
        this.startX = 0.0f
        this.startY = 0.0f
        this.slidingBack = false
        this.lastScrollX = -1
    }

    fun onTouchEvent(e: MotionEvent?): Boolean {
        val event = e ?: return false

        return when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                this.gestureDetector.onTouchEvent(event)
            }
            MotionEvent.ACTION_UP -> {
                this.gestureDetector.onTouchEvent(event)
                onUp()
            }
            MotionEvent.ACTION_MOVE -> {
                this.gestureDetector.onTouchEvent(event)
                onDrag(event)
            }
            else -> this.gestureDetector.onTouchEvent(event)
        }
    }

    private fun onUp(): Boolean {
        if (this.abortUp) {
            clear()
            this.abortUp = false
        } else {
            if (this.slidingBack) {
                val isClose = this.lastScrollX.toFloat() < UIScreen.currentWidth() * 0.67f
                if (isClose) {
//                    this.delegate.closePreview(0.0f)
                } else {
                    this.transitionCoordinator.applyBackward(
                        this.navigationController?.lastVisibleController,
                        this.navigationController?.previousController,
                        0.0f
                    )
                }
            }
            clear()
        }
        return false
    }

    private fun onDrag(event: MotionEvent): Boolean {
        this.lastScrollX = (event.getX(0) - this.startX).toInt()
        if (this.slidingBack) {
            this.transitionCoordinator.translateBackward(this.lastScrollX.toFloat())
            return this.slidingBack
        } else {
            val diffX = event.getX(0) - this.startX
            val diffY = event.getY(0) - this.startY
            val approve = diffX >= UISize.TOUCH_SLOP_BIG && Math.abs(diffY) <= UISize.TOUCH_SLOP_Y

            if (!approve) {
                return false
            }
            if (canSlideBack()) {
                this.startX += diffX
                this.startY += diffY
                this.listenSlidingBack = false
                this.slidingBack = (this.navigationController?.stackSize ?: 0) > 1
                if (this.slidingBack) {
                    if (!this.transitionCoordinator.prepareBackwardTransition(
                            this.navigationController?.lastVisibleController,
                            this.navigationController?.previousController
                        )) {
                        this.slidingBack = false
                        this.listenSlidingBack = false
                        return false
                    }
                }
                onDrag(event)
                return true
            }
            this.listenSlidingBack = false
            return false
        }
    }

    private fun canSlideBack(): Boolean {
        return true
    }

    private fun canSlideBack(viewController: UIViewController?, x: Float, y: Float): Boolean {
        val stackSize = this.navigationController?.stackSize ?: 0
        if (stackSize <= 0 || viewController == null || !viewController.canSlideBackFrom(
                x,
                y
            ) || !viewController.isSwipeNavigationEnabled
        ) {
            return false
        }
        return true
    }

    override fun onDown(e: MotionEvent?): Boolean {
        val event = e ?: return false
        if (this.navigationController?.isDestroyed == true || this.navigationController?.isAnimating == true) {
            return false
        }
        this.startX = event.getX(0)
        this.startY = event.getY(0)
        if (this.startY <= 0.0f) {
            return false
        }
        val viewController = this.navigationController?.lastVisibleController
        if (viewController == null || viewController.isIntercepted) {
            return false
        }
        this.listenSlidingBack = canSlideBack(viewController, this.startX, this.startY)

        return this.listenSlidingBack
    }

    override fun onFling(
        e1: MotionEvent?,
        e2: MotionEvent?,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        val abs = abs(velocityX)
        if (abs > UIScreen.dpf(250.0f, 1.0f)) {
            this.abortUp = true

            if (slidingBack) {
                if (velocityX >= 0.0f) {
                    this.transitionCoordinator.applyBackward(
                        this.navigationController?.lastVisibleController,
                        this.navigationController?.previousController,
                        abs
                    )
                } else {
//                    this.delegate.closePreview(abs)
                }
            }

            clear()
        } else {
            this.abortUp = false
        }
        return this.abortUp
    }

    override fun onScroll(
        e1: MotionEvent?,
        e2: MotionEvent?,
        distanceX: Float,
        distanceY: Float
    ): Boolean {
        return true
    }

    override fun onShowPress(e: MotionEvent?) {}

    override fun onSingleTapUp(e: MotionEvent?): Boolean {
        return false
    }

    override fun onLongPress(e: MotionEvent?) {
    }

}