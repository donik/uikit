package kz.citicom.uikit.views

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.content.Context
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import kz.citicom.uikit.tools.UIAnimation

open class UIView(context: Context) : FrameLayout(context) {

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