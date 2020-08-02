package kz.citicom.uikit.tools

import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator

object UIAnimation {
    val ACCELERATE_DECELERATE_INTERPOLATOR = AccelerateDecelerateInterpolator()
    val DECELERATE_INTERPOLATOR = DecelerateInterpolator()
    val NAVIGATION_INTERPOLATOR = DecelerateInterpolator(1.5f)
    val LINEAR_INTERPOLATOR = LinearInterpolator()
}