package kz.citicom.uikit.views.components

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.Log
import android.view.View
import android.view.animation.DecelerateInterpolator
import kz.citicom.uikit.tools.UIScreen

class LineProgressView(context: Context) : View(context) {

    private var lastUpdateTime: Long = 0
    private var currentProgress = 0f
    private var animationProgressStart = 0f
    private var currentProgressTime: Long = 0
    private var animatedProgressValue = 0f
    private var animatedAlphaValue = 1.0f

    private var backColor: Int = 0
    private var progressColor: Int = 0

    init {
        if (decelerateInterpolator == null) {
            decelerateInterpolator = DecelerateInterpolator()
            progressPaint = Paint(Paint.ANTI_ALIAS_FLAG)
            progressPaint?.strokeCap = Paint.Cap.ROUND
            progressPaint?.strokeWidth = UIScreen.dpf(2.0f)
        }
    }

    private fun updateAnimation() {
        val newTime = System.currentTimeMillis()
        val dt = newTime - lastUpdateTime
        lastUpdateTime = newTime

        if (animatedProgressValue != 1f && animatedProgressValue != currentProgress) {
            val progressDiff = currentProgress - animationProgressStart
            if (progressDiff > 0) {
                currentProgressTime += dt
                if (currentProgressTime >= 300) {
                    animatedProgressValue = currentProgress
                    animationProgressStart = currentProgress
                    currentProgressTime = 0
                } else {
                    animatedProgressValue =
                        animationProgressStart + progressDiff * decelerateInterpolator!!.getInterpolation(
                            currentProgressTime / 300.0f
                        )
                }
            }
            invalidate()
        }
        if (animatedProgressValue >= 1 && animatedProgressValue == 1f && animatedAlphaValue != 0f) {
            animatedAlphaValue -= dt / 200.0f
            if (animatedAlphaValue <= 0) {
                animatedAlphaValue = 0.0f
            }
            invalidate()
        }
    }

    fun setProgressColor(color: Int) {
        progressColor = color
    }

    fun setBackColor(color: Int) {
        backColor = color
    }

    fun setProgress(value: Float, animated: Boolean) {
        Log.e("value", value.toString())
        if (!animated) {
            animatedProgressValue = value
            animationProgressStart = value
        } else {
            animationProgressStart = animatedProgressValue
        }
        if (value != 1f) {
            animatedAlphaValue = 1f
        }
        currentProgress = value
        currentProgressTime = 0

        lastUpdateTime = System.currentTimeMillis()
        invalidate()
    }

    public override fun onDraw(canvas: Canvas) {
        if (backColor != 0 && animatedProgressValue != 1f) {
            progressPaint!!.color = backColor
            progressPaint!!.alpha = (255 * animatedAlphaValue).toInt()
            val start = (width * animatedProgressValue).toInt()
            canvas.drawRect(start.toFloat(), 0f, width.toFloat(), height.toFloat(), progressPaint!!)
        }

        progressPaint?.color = progressColor
        progressPaint?.alpha = (255 * animatedAlphaValue).toInt()
        canvas.drawRect(0f, 0f, width * animatedProgressValue, height.toFloat(), progressPaint!!)
        updateAnimation()
    }

    companion object {
        private var decelerateInterpolator: DecelerateInterpolator? = null
        private var progressPaint: Paint? = null
    }
}