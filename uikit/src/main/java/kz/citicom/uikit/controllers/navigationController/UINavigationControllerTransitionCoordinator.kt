package kz.citicom.uikit.controllers.navigationController

import android.util.Log
import android.view.View
import kz.citicom.uikit.R

import kz.citicom.uikit.UIApplication
import kz.citicom.uikit.controllers.UIViewController
import kz.citicom.uikit.tools.LayoutHelper
import kz.citicom.uikit.tools.UIAnimation
import kz.citicom.uikit.tools.UIImage
import kz.citicom.uikit.tools.weak
import kz.citicom.uikit.views.UIView
import kz.citicom.uikit.views.removeChildes
import kz.citicom.uikit.views.removeFromSuperview
import kotlin.math.roundToInt

class UINavigationControllerTransitionCoordinator(
    private val foregroundContentView: UIView,
    private val backgroundContentView: UIView
) {
    companion object {
        private const val TRANSLATION_FACTOR = 0.14f

        private fun calculateDropDuration(
            length: Float,
            velocity: Float,
            maximum: Int,
            minimum: Int
        ): Int {
            return if (velocity <= 0.0f) maximum else (length / (velocity / 1000.0f)).roundToInt().coerceAtLeast(
                minimum
            ).coerceAtMost(maximum)
        }
    }

    var mainContainer: UIView? = null
        set(value) {
            field = value
            this.configureShadow()
        }

    var progressBlock: (() -> Unit)? = null
    var isAnimating: Boolean = false
        private set
    private var transitionType: UIViewController.TransitionType = UIViewController.TransitionType.PUSH
    private var runnable = Runnable {
        this.isAnimating = false
        this.layoutIfRequested()
    }

    private fun configureShadow() {
        val weakSelf by weak(this)
        val layerShadowDrawable = UIImage.getDrawable(R.drawable.layer_shadow)
        this.mainContainer?.drawBlock = { canvas ->
            val child = weakSelf?.foregroundContentView
            if (weakSelf != null && canvas != null) {
                val foregroundTransitionX = (weakSelf?.foregroundContentView?.translationX ?: 0.0f).toInt()
                val backgroundTransitionX = (weakSelf?.backgroundContentView?.translationX ?: 0.0f).toInt()

                val transitionX = foregroundTransitionX.coerceAtLeast(backgroundTransitionX)

                layerShadowDrawable?.setBounds(
                    transitionX - layerShadowDrawable.intrinsicWidth,
                    child?.top ?: 0,
                    transitionX,
                    child?.bottom ?: 0
                )
                layerShadowDrawable?.draw(canvas)
            }
        }
        this.progressBlock = {
            weakSelf?.mainContainer?.invalidate()
        }
    }

    fun navigateForward(from: UIViewController?, to: UIViewController, animated: Boolean, completion: (() -> Unit)? = null) {
        val isAnimated = from != null && animated
        from?.viewWillDisappear()

        val view = to.view
        to.viewWillAppear()
        prepareForwardAnimation(to)

        backgroundContentView.preventLayout()
        backgroundContentView.removeAllViews()
        backgroundContentView.addView(view)
        backgroundContentView.layoutIfRequested()

        preventLayout()

        if (isAnimated) {
            this.isAnimating = true
            val weakSelf by weak(this)
            UIView.animate(120, 300, UIAnimation.NAVIGATION_INTERPOLATOR, {
                weakSelf?.factorForwardBackwardAnimation(true, it)
            }, {
                weakSelf?.finishForwardBackwardAnimation(view)
                from?.viewDidDisappear()
                to.viewDidAppear()
                from?.view?.removeFromSuperview()
                completion?.let { it() }
            })
        } else {
            finishForwardBackwardAnimation(view)
            from?.viewDidDisappear()
            to.viewDidAppear()
            from?.view?.removeFromSuperview()
            completion?.let { it() }
        }
    }

    fun navigateBackward(from: UIViewController, to: UIViewController, animated: Boolean, completion: (() -> Unit)? = null) {
        from.viewWillDisappear()

        val view = to.view
        to.viewWillAppear()
        prepareBackwardAnimation(from)

        backgroundContentView.preventLayout()
        backgroundContentView.removeAllViews()
        backgroundContentView.addView(view)
        backgroundContentView.layoutIfRequested()

        preventLayout()

        if (animated) {
            this.isAnimating = true
            val weakSelf by weak(this)

            UIView.animate(120, 300, UIAnimation.DECELERATE_INTERPOLATOR, {
                weakSelf?.factorForwardBackwardAnimation(false, it)
            }, {
                weakSelf?.finishForwardBackwardAnimation(view)
                from.viewDidDisappear()
                to.viewDidAppear()
                from.view.removeFromSuperview()
                completion?.let { it() }
            })
        } else {
            finishForwardBackwardAnimation(view)
            from.viewDidDisappear()
            to.viewDidAppear()
            from.view.removeFromSuperview()
            completion?.let { it() }
        }
    }

    fun prepareBackwardTransition(from: UIViewController?, to: UIViewController?): Boolean {
        if (this.isAnimating || to == null) {
            return false
        }

        from?.viewWillDisappear()
        this.isAnimating = true
        val view = to.view
        to.viewWillAppear()
        prepareBackwardAnimation(from ?: return false)

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

        val factor = 1.0f.coerceAtMost(0.0f.coerceAtLeast(px / this.foregroundContentView.measuredWidth))

        this.factorForwardBackwardAnimation(false, factor)
    }

    fun cancelBackward(from: UIViewController?, to: UIViewController?, velocity: Float) {
        if (!this.isAnimating) {
            return
        }

        val view = from?.view
        if (this.backgroundContentView.translationX == 0.0f) {
            finishForwardBackwardAnimation(view ?: return)
            from.viewDidAppear()
            to?.viewDidDisappear()
            to?.view?.removeFromSuperview()
            return
        }

        val startFactor =
            this.foregroundContentView.translationX / this.foregroundContentView.measuredWidth
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
                val factor = startFactor - (startFactor * it)
                weakSelf?.factorForwardBackwardAnimation(false, factor)
            }, {
                weakSelf?.finishForwardBackwardAnimation(view ?: return@animate)
                from?.viewDidAppear()
                to?.viewDidDisappear()
                to?.view?.removeFromSuperview()
            }
        )
    }

    fun applyBackward(from: UIViewController?, to: UIViewController?, velocity: Float, completion: () -> Unit) {
        if (!this.isAnimating) {
            return
        }

        val view = to?.view
        if (this.backgroundContentView.translationX == 0.0f) {
            finishForwardBackwardAnimation(view ?: return)
            from?.viewDidDisappear()
            to.viewDidAppear()
            from?.view?.removeFromSuperview()
            completion()
            return
        }

        val startFactor =
            this.foregroundContentView.translationX / this.foregroundContentView.measuredWidth
        val diffFactor = 1.0f - startFactor
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
                val factor = startFactor + diffFactor * it
                weakSelf?.factorForwardBackwardAnimation(false, factor)
            }, {
                weakSelf?.finishForwardBackwardAnimation(view ?: return@animate)
                from?.viewDidDisappear()
                to?.viewDidAppear()
                from?.view?.removeFromSuperview()
                completion()
            }
        )
    }

    //called when user touch up
    fun backwardTranslationDone() {
        UIApplication.post(this.runnable, 1400)
    }

    private fun factorForwardBackwardAnimation(forward: Boolean, f: Float) {
        val factor = Math.min(f.coerceAtLeast(0.0f), 1.0f)

        if (this.transitionType == UIViewController.TransitionType.FADE) {
            if (forward) {
                this.backgroundContentView.alpha = factor * 1.0f
                this.backgroundContentView.translationX = 0.0f
                this.foregroundContentView.translationX = 0.0f
            } else {
                this.foregroundContentView.alpha = (1 - factor) * 1.0f
                this.foregroundContentView.translationX = 0.0f
                this.backgroundContentView.translationX = 0.0f
            }
        } else {
            if (forward) {
                val shiftX = -(this.foregroundContentView.measuredWidth.toFloat() * TRANSLATION_FACTOR)

                this.backgroundContentView.translationX = (1 - factor) * this.backgroundContentView.measuredWidth.toFloat()
                this.foregroundContentView.translationX = shiftX * factor
                this.foregroundContentView.scaleX = 0.95f + 0.05f * (1 - factor)
                this.foregroundContentView.scaleY = 0.95f + 0.05f * (1 - factor)
            } else {
                val shiftX = -(this.backgroundContentView.measuredWidth.toFloat() * TRANSLATION_FACTOR)

                this.foregroundContentView.translationX =
                    factor * this.foregroundContentView.measuredWidth.toFloat()
                this.backgroundContentView.translationX = (1 - factor) * shiftX

                this.backgroundContentView.scaleX = 0.95f + 0.05f * factor
                this.backgroundContentView.scaleY = 0.95f + 0.05f * factor
            }
        }

        this.progressBlock?.let { it() }
    }

    private fun prepareForwardAnimation(rightController: UIViewController) {
        this.transitionType = rightController.transitionType

        if (this.transitionType == UIViewController.TransitionType.FADE) {
            backgroundContentView.translationX = 0.0f
            foregroundContentView.translationX = 0.0f
            backgroundContentView.alpha = 0.0f
        } else {
            backgroundContentView.translationX = backgroundContentView.measuredWidth.toFloat()
            foregroundContentView.translationX = 0.0f
        }
        backgroundContentView.bringToFront()
    }

    private fun prepareBackwardAnimation(rightController: UIViewController) {
        this.transitionType = rightController.transitionType

        foregroundContentView.bringToFront()
        if (this.transitionType == UIViewController.TransitionType.FADE) {
            backgroundContentView.translationX = 0.0f
            foregroundContentView.translationX = 0.0f
            foregroundContentView.alpha = 1.0f
        } else {
            val shiftX = -(backgroundContentView.measuredWidth.toFloat() * TRANSLATION_FACTOR)
            foregroundContentView.translationX = 0.0f
            backgroundContentView.translationX = shiftX
        }
    }

    private fun finishForwardBackwardAnimation(view: View) {
        UIApplication.cancel(runnable)
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
        this.foregroundContentView.alpha = 1.0f
        this.backgroundContentView.alpha = 1.0f
        this.foregroundContentView.scaleX = 1.0f
        this.backgroundContentView.scaleX = 1.0f
        this.foregroundContentView.scaleY = 1.0f
        this.backgroundContentView.scaleX = 1.0f
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