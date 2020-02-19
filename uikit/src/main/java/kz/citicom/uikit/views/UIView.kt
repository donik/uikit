package kz.citicom.uikit.views

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.content.Context
import android.os.Build
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import kz.citicom.uikit.tools.UIAnimation

open class UIView(context: Context, private var name: String = this.javaClass.toString()) : FrameLayout(context) {
    companion object {
        fun animate(
            duration: Long,
            interpolator: TimeInterpolator = UIAnimation.ACCELERATE_DECELERATE_INTERPOLATOR,
            progress: ((Float) -> Unit)? = null,
            completion: (() -> Unit)? = null
        ) {
            val animator = simpleValueAnimator()
            animator.duration = duration
            animator.interpolator = interpolator
            animator.addUpdateListener { currentAnimator ->
                progress?.let { it(getFraction(currentAnimator)) }
            }
            animator.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    completion?.let { it() }
                }
            })
            animator.start()
        }

        private fun simpleValueAnimator(): ValueAnimator {
            return ValueAnimator.ofFloat(0.0f, 1.0f)
        }

        private fun getFraction(animator: ValueAnimator): Float {
            return animator.animatedFraction
        }
    }

    private var layoutComplete = 0
    private var layoutLimit = -1
    private var layoutRequested = false
    private var preventLayout = false
        set(value) {
            field = value
            setWillNotDraw(!value)
        }

    fun preventLayout() {
        this.preventLayout = true
    }

    fun layoutIfRequested() {
        this.preventLayout = false
        if (this.layoutRequested) {
            this.layoutRequested = false
            this.requestLayout()
        }
    }

    fun cancelLayout() {
        this.preventLayout = false
        this.layoutRequested = false
    }

    override fun isLayoutRequested(): Boolean {
        return this.layoutRequested
    }

    override fun requestLayout() {
        when {
            this.preventLayout -> {
                this.layoutRequested = true
            }
            this.layoutLimit == -1 -> {
                Log.e("REQ", "REQUEST LAYOUT " + toString())
                super.requestLayout()
            }
            this.layoutComplete < this.layoutLimit -> {
                Log.e("REQ", "REQUEST LAYOUT " + toString())
                this.layoutComplete++
                super.requestLayout()
            }
        }
    }

    fun preventNextLayouts(limit: Int) {
        this.layoutLimit = limit
        this.layoutComplete = 0
    }

    fun completeNextLayout() {
        this.layoutLimit = -1
        this.layoutComplete = 0
    }

    override fun toString(): String {
        return name
    }
}

fun View.removeChilds() {
    (this as? ViewGroup)?.removeAllViews()
}

fun View.removeFromSuperview(): View? {
    val view = this
    view.parent?.let {
        (it as? ViewGroup)?.removeView(view)
    }
    return view
}