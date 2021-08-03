package kz.citicom.uikit.controllers.slidingController

import android.view.Gravity
import android.view.View
import androidx.core.view.marginRight
import kz.citicom.uikit.UIApplication
import kz.citicom.uikit.controllers.UIViewController
import kz.citicom.uikit.tools.LayoutHelper
import kz.citicom.uikit.tools.UIAnimation
import kz.citicom.uikit.tools.UIScreen
import kz.citicom.uikit.tools.weak
import kz.citicom.uikit.views.UIView
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.roundToInt

class UISlidingControllerTransitionCoordinator(
    private val mainContentView: UIView,
    private val shadowView: UIView,
    private val slidingView: UIView
) {
    companion object {
        private fun calculateDropDuration(
            length: Float,
            velocity: Float,
            maximum: Int,
            minimum: Int
        ): Long {
            return if (velocity <= 0.0f) maximum.toLong() else (length / (velocity / 1000.0f)).roundToInt()
                .coerceAtLeast(
                    minimum
                ).coerceAtMost(maximum).toLong()
        }
    }

    private var currentScreenWidth: Int = 0
    private var currentWidth: Int = 0

    var isAnimating: Boolean = false
        private set
    private var factor: Float = 0.0f
    private val lastTranslation: Float
        get() = this.currentWidth.toFloat() * (1.0f - this.factor)

    var isVisible: Boolean = false
        private set

    init {
        this.currentWidth = Math.min(UIScreen.dp(300), UIScreen.smallestSide() - UIScreen.dp(56))
        this.slidingView.layoutParams = LayoutHelper.createFrame(
            UIScreen.revertDp(this.currentWidth.toFloat()),
            LayoutHelper.MATCH_PARENT,
            Gravity.LEFT
        )
    }

    private fun showView() {
        UIView.setLayerType(this.slidingView, View.LAYER_TYPE_HARDWARE)
        UIView.setLayerType(this.shadowView, View.LAYER_TYPE_HARDWARE)
        this.slidingView.visibility = View.VISIBLE
        this.shadowView.visibility = View.VISIBLE
    }

    private fun hideView() {
        UIView.setLayerType(this.slidingView, View.LAYER_TYPE_NONE)
        UIView.setLayerType(this.shadowView, View.LAYER_TYPE_NONE)
        this.slidingView.visibility = View.GONE
        this.shadowView.visibility = View.GONE
    }

    fun prepare(): Boolean {
        this.currentScreenWidth = UIScreen.currentWidth()
        this.showView()
        if (!this.isVisible) {
            this.slidingView.translationX = -this.currentWidth.toFloat()
        }
        return true
    }

    fun open() {
        this.prepare()
        this.open(0.0f)
    }

    fun open(velocity: Float) {
        if (this.isAnimating) {
            return
        }

        this.isAnimating = true
        val startFactor = this.factor
        val diffFactor = 1.0f - startFactor
        val weakSelf by weak(this)

        UIView.animate(
            10,
            calculateDropDuration(lastTranslation, velocity, 300, 180),
            UIAnimation.DECELERATE_INTERPOLATOR,
            progress = {
                val strongSelf = weakSelf ?: return@animate
                strongSelf.setFactor(startFactor + (diffFactor * it))
            },
            completion = {
                val strongSelf = weakSelf ?: return@animate
                strongSelf.factor = factor
                strongSelf.isAnimating = false
                strongSelf.isVisible = true
            }
        )
    }

    fun close(velocity: Float) {
        if (this.isAnimating) {
            return
        }

        this.isAnimating = true
        if (this.factor == 0.0f) {
            this.forceClose()
            return
        }

        val startFactor = this.factor
        this.isVisible = false
        val weakSelf by weak(this)

        UIView.animate(
            10,
            calculateDropDuration(
                this.currentWidth.toFloat() + this.lastTranslation,
                velocity,
                300,
                180
            ),
            UIAnimation.DECELERATE_INTERPOLATOR,
            progress = {
                val strongSelf = weakSelf ?: return@animate
                strongSelf.setFactor(startFactor - (startFactor * it))
            },
            completion = {
                val strongSelf = weakSelf ?: return@animate
                strongSelf.hideView()
                strongSelf.factor = 0.0f
                strongSelf.isAnimating = false
            }
        )
    }

    fun forceOpen() {
        this.isVisible = true
        this.isAnimating = false
        this.factor = 1.0f
        this.setFactor(1.0f)
    }

    fun forceClose() {
        this.isVisible = false
        this.isAnimating = false
        this.factor = 0.0f
        this.setFactor(0.0f)
        this.hideView()
    }

    fun drop() {
        if (this.factor < 0.4f) {
            this.isVisible = true
            this.close(0.0f)
            return
        }
        this.isVisible = false
        open(0.0f)
    }

    fun forceDrop() {
        if (this.factor < 0.4f) {
            forceClose()
        } else {
            forceOpen()
        }
    }

    fun translate(lastScrollX: Float) {
        val factor = 1.0f - Math.abs(
            (if (this.isVisible) {
                Math.min(0.0f, Math.max(lastScrollX, -this.currentWidth.toFloat()))
            } else {
                Math.min(0.0f, (lastScrollX - this.currentWidth))
            }) / this.currentWidth.toFloat()
        )
        this.setFactor(factor)
    }

    fun setFactor(factor: Float) {
        val translation = -this.currentWidth.toFloat() * (1.0f - factor)
        val oldTranslation = -this.currentWidth.toFloat() * (1.0f - this.factor)

        if (factor == 0.0f || factor == 1.0f || Math.abs(oldTranslation - translation) >= 1.0f) {
            this.factor = factor
            this.slidingView.translationX = translation
            this.shadowView.alpha = 0.6f * factor
        }
    }
}