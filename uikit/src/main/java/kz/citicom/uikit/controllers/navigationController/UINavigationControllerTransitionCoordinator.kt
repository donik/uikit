package kz.citicom.uikit.controllers.navigationController

import android.view.View
import kz.citicom.uikit.controllers.UIViewController
import kz.citicom.uikit.tools.LayoutHelper
import kz.citicom.uikit.tools.UIAnimation
import kz.citicom.uikit.tools.weak
import kz.citicom.uikit.views.UIView
import kz.citicom.uikit.views.removeChildes
import kz.citicom.uikit.views.removeFromSuperview
import kotlin.math.max
import kotlin.math.roundToInt

class UINavigationControllerTransitionCoordinator(
    private val foregroundContentView: UIView,
    private val backgroundContentView: UIView
) {
    companion object {
        private const val TRANSLATION_FACTOR = 0.14f

        private fun calculateDropDuration(length: Float, velocity: Float, max: Int, min: Int): Int {
            return if (velocity <= 0.0f) max else (length / (velocity / 1000.0f)).roundToInt().coerceAtLeast(
                min
            ).coerceAtMost(max)
        }
    }

    var isAnimating: Boolean = false
        private set

    fun navigateForward(from: UIViewController?, to: UIViewController, animated: Boolean) {
        val isAnimated = from != null && animated
        from?.viewWillDisappear()

        val view = to.getWrap()
        to.viewWillAppear()
        prepareForwardAnimation()

        backgroundContentView.preventLayout()
        backgroundContentView.removeAllViews()
        backgroundContentView.addView(view)
        backgroundContentView.layoutIfRequested()

        preventLayout()

        if (isAnimated) {
            this.isAnimating = true
            val weakSelf by weak(this)
            UIView.animate(120, 400, UIAnimation.ACCELERATE_DECELERATE_INTERPOLATOR, {
                weakSelf?.factorForwardBackwardAnimation(true, it)
            }, {
                weakSelf?.finishForwardBackwardAnimation(view ?: return@animate)
                from?.viewDidDisappear()
                to.viewDidAppear()
                from?.getWrap()?.removeFromSuperview()
            })
        } else {
            finishForwardBackwardAnimation(view ?: return)
            from?.viewDidDisappear()
            to.viewDidAppear()
            from?.getWrap()?.removeFromSuperview()
        }
    }

    fun navigateBackward(from: UIViewController, to: UIViewController, animated: Boolean) {
        from.viewWillDisappear()

        val view = to.getWrap()
        to.viewWillAppear()
        prepareBackwardAnimation()

        backgroundContentView.preventLayout()
        backgroundContentView.removeAllViews()
        backgroundContentView.addView(view)
        backgroundContentView.layoutIfRequested()

        preventLayout()

        if (animated) {
            this.isAnimating = true
            val weakSelf by weak(this)

            UIView.animate(120, 300, UIAnimation.ACCELERATE_DECELERATE_INTERPOLATOR, {
                weakSelf?.factorForwardBackwardAnimation(false, it)
            }, {
                weakSelf?.finishForwardBackwardAnimation(view ?: return@animate)
                from.viewDidDisappear()
                to.viewDidAppear()
                from.getWrap()?.removeFromSuperview()
            })
        } else {
            finishForwardBackwardAnimation(view ?: return)
            from.viewDidDisappear()
            to.viewDidAppear()
            from.getWrap()?.removeFromSuperview()
        }
    }

    fun prepareBackwardTransition(from: UIViewController?, to: UIViewController?): Boolean {
        if (this.isAnimating || to == null) {
            return false
        }

        from?.viewWillDisappear()

        this.isAnimating = true
        val view = to.getWrap()
        to.viewWillAppear()
        prepareBackwardAnimation()

        backgroundContentView.preventLayout()
        backgroundContentView.removeAllViews()
        backgroundContentView.addView(view)
        backgroundContentView.layoutIfRequested()

        preventLayout()

        return true
    }

    fun translateBackward(px: Float) {
        if (!this.isAnimating) {
            return
        }

        val factor = px / this.foregroundContentView.measuredWidth
        this.factorForwardBackwardAnimation(false, factor)
    }

    fun cancelBackward(from: UIViewController?, to: UIViewController?, velocity: Float) {
        if (!this.isAnimating) {
            return
        }

        val view = to?.getWrap()
        if (this.backgroundContentView.translationX == 0.0f) {
            return
        }
    }

    fun applyBackward(from: UIViewController?, to: UIViewController?, velocity: Float) {
        if (!this.isAnimating) {
            return
        }

        val view = to?.getWrap()
        if (this.backgroundContentView.translationX == 0.0f) {
            finishForwardBackwardAnimation(view ?: return)
            from?.viewDidDisappear()
            to.viewDidAppear()
            from?.getWrap()?.removeFromSuperview()
            return
        }

        val factor =
            this.foregroundContentView.translationX / this.foregroundContentView.measuredWidth
        val diffFactor = 1.0f - factor
        val weakSelf by weak(this)
        UIView.animate(
            0L,
            calculateDropDuration(
                this.foregroundContentView.measuredWidth - this.foregroundContentView.translationX,
                velocity,
                200,
                60
            ).toLong(),
            UIAnimation.DECELERATE_INTERPOLATOR,
            {
                weakSelf?.factorForwardBackwardAnimation(false, it)
            }, {
                weakSelf?.finishForwardBackwardAnimation(view ?: return@animate)
                from?.viewDidDisappear()
                to?.viewDidAppear()
                from?.getWrap()?.removeFromSuperview()
            }
        )
    }

    private fun factorForwardBackwardAnimation(forward: Boolean, factor: Float) {
        val factor = factor.coerceAtLeast(0.0f)

        if (forward) {
            val shiftX = -(this.foregroundContentView.measuredWidth.toFloat() * TRANSLATION_FACTOR)

            this.backgroundContentView.translationX =
                (1 - factor) * this.backgroundContentView.measuredWidth.toFloat()
            this.foregroundContentView.translationX = shiftX * factor
        } else {
            val shiftX = -(this.backgroundContentView.measuredWidth.toFloat() * TRANSLATION_FACTOR)

            this.foregroundContentView.translationX =
                factor * this.foregroundContentView.measuredWidth.toFloat()
            this.backgroundContentView.translationX = (1 - factor) * shiftX
        }
    }

    private fun prepareForwardAnimation() {
        backgroundContentView.bringToFront()
        backgroundContentView.translationX = backgroundContentView.measuredWidth.toFloat()
        foregroundContentView.translationX = 0.0f
    }

    private fun prepareBackwardAnimation() {
        val shiftX = -(backgroundContentView.measuredWidth.toFloat() * TRANSLATION_FACTOR)
        foregroundContentView.bringToFront()
        foregroundContentView.translationX = 0.0f
        backgroundContentView.translationX = shiftX
    }

    private fun finishForwardBackwardAnimation(view: View) {
        view.removeFromSuperview()

        this.foregroundContentView.preventLayout()
        this.backgroundContentView.preventLayout()

        this.foregroundContentView.addView(
            view,
            LayoutHelper.createFrame(
                LayoutHelper.MATCH_PARENT,
                LayoutHelper.MATCH_PARENT
            )
        )
        this.backgroundContentView.removeChildes()

        this.foregroundContentView.translationX = 0.0f
        this.backgroundContentView.translationX = 0.0f
        this.foregroundContentView.bringToFront()

        this.foregroundContentView.layoutIfRequested()
        this.backgroundContentView.layoutIfRequested()

        this.isAnimating = false
        layoutIfRequested()
    }

    private fun preventLayout() {
        (this.foregroundContentView.parent as? UIView)?.preventLayout()
        (this.backgroundContentView.parent as? UIView)?.preventLayout()
        this.foregroundContentView.preventLayout()
        this.backgroundContentView.preventLayout()
    }

    private fun layoutIfRequested() {
        (this.foregroundContentView.parent as? UIView)?.layoutIfRequested()
        (this.backgroundContentView.parent as? UIView)?.layoutIfRequested()
        this.foregroundContentView.layoutIfRequested()
        this.backgroundContentView.layoutIfRequested()
    }
}