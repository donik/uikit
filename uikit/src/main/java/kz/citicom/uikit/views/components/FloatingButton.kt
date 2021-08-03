package kz.citicom.uikit.views.components

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Outline
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Build
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.FrameLayout
import android.widget.ImageView
import kz.citicom.uikit.tools.*

class FloatingButton(
    context: Context,
    resource: Int,
    private val size: Int = 56,
    clickAction: () -> Unit
) : FrameLayout(context) {

    private var floatingHidden = false
    private var floatingButton: ImageView = ImageView(context)

    private var defaultColor: Int = UIColor.black
    private var pressedColor: Int = Color.RED

    init {
        val ripple = UIImage.createSimpleSelectorCircleDrawable(
            UIScreen.dp(size),
            this.defaultColor,
            this.pressedColor
        )
        floatingButton.scaleType = ImageView.ScaleType.CENTER
        floatingButton.setBackgroundDrawable(ripple)
        floatingButton.colorFilter = PorterDuffColorFilter(UIColor.white, PorterDuff.Mode.SRC_ATOP)
        floatingButton.setImageResource(resource)
        if (Build.VERSION.SDK_INT >= 21) {
            floatingButton.outlineProvider = object : ViewOutlineProvider() {
                @SuppressLint("NewApi")
                override fun getOutline(view: View, outline: Outline) {
                    outline.setOval(0, 0, UIScreen.dp(size), UIScreen.dp(size))
                }
            }
        }
        addView(floatingButton, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT))

        this.setOnClickListener {
            clickAction()
        }
    }

    fun setBackgroundAlpha(value: Int) {
        val ripple = UIImage.createSimpleSelectorCircleDrawable(UIScreen.dp(this.size), this.defaultColor, this.pressedColor)
        ripple?.alpha = value
        floatingButton.setBackgroundDrawable(ripple)
    }

    fun setButtonColor(defaultColor: Int, pressedColor: Int) {
        if (this.defaultColor == defaultColor && this.pressedColor == pressedColor) {
            return
        }

        this.defaultColor = defaultColor
        this.pressedColor = pressedColor

        val ripple = UIImage.createSimpleSelectorCircleDrawable(UIScreen.dp(this.size), this.defaultColor, this.pressedColor)
        floatingButton.setBackgroundDrawable(ripple)
    }

    fun setImageResource(resource: Int) {
        floatingButton.setImageResource(resource)
    }

    fun showHide(hide: Boolean) {
        if (floatingHidden == hide) {
            return
        }
        this.floatingHidden = hide
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(ObjectAnimator.ofFloat<View>(this, View.TRANSLATION_Y, if (floatingHidden) UIScreen.dpf(100f) else 0f))
        animatorSet.duration = 300
        animatorSet.interpolator = UIAnimation.ACCELERATE_DECELERATE_INTERPOLATOR
        this.isClickable = !hide
        animatorSet.start()
    }


}