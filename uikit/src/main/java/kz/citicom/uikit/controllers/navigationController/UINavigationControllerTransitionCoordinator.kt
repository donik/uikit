package kz.citicom.uikit.controllers.navigationController

import android.util.Log
import kz.citicom.uikit.controllers.UIViewController
import kz.citicom.uikit.tools.LayoutHelper
import kz.citicom.uikit.tools.UIAnimation
import kz.citicom.uikit.views.UIView
import kz.citicom.uikit.views.removeFromSuperview

class UINavigationControllerTransitionCoordinator(
    private val foregroundContentView: UIView,
    private val backgroundContentView: UIView
) {
    var isAnimated: Boolean = false
        private set

    fun navigateForward(from: UIViewController?, to: UIViewController, animated: Boolean) {
        val isAnimated = from != null && animated

        Log.e("PUSH", "PUSH CONTROLLER: $isAnimated")

        val view = to.getWrap()
        if (isAnimated) {
            from?.viewWillDisappear()
            to.viewWillAppear()
            prepareForwardAnimation()

            backgroundContentView.removeAllViews()
            backgroundContentView.addView(view)

            this.isAnimated = true
            UIView.animate(400, UIAnimation.ACCELERATE_DECELERATE_INTERPOLATOR, {
                backgroundContentView.translationX =
                    (1 - it) * backgroundContentView.measuredWidth.toFloat()
            }, {
                view.removeFromSuperview()
                foregroundContentView.addView(
                    view,
                    LayoutHelper.createFrame(
                        LayoutHelper.MATCH_PARENT,
                        LayoutHelper.MATCH_PARENT
                    )
                )
                foregroundContentView.bringToFront()

                this.isAnimated = false
            })
        } else {
            from?.viewWillDisappear()
            to.viewWillAppear()
            foregroundContentView.removeAllViews()
            foregroundContentView.addView(
                view,
                LayoutHelper.createFrame(
                    LayoutHelper.MATCH_PARENT,
                    LayoutHelper.MATCH_PARENT
                )
            )
            backgroundContentView.removeAllViews()
            foregroundContentView.bringToFront()
            from?.viewDidDisappear()
            to.viewDidAppear()
        }
    }

    fun navigateBackward(from: UIViewController, to: UIViewController, animated: Boolean) {

    }

    private fun prepareForwardAnimation() {
        backgroundContentView.bringToFront()
        backgroundContentView.translationX = backgroundContentView.measuredWidth.toFloat()
        foregroundContentView.translationX = 0.0f
    }


}