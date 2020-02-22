package kz.citicom.uikit.views.components

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import kz.citicom.uikit.tools.UIScreen

class RadialProgressView(context: Context) : View(context) {

    private var lastUpdateTime: Long = 0
    private var radOffset: Float = 0.0f
    private var currentCircleLength: Float = 0.0f
    private var risingCircleLength: Boolean = false
    private var currentProgressTime: Float = 0.0f
    private val circleRect = RectF()

    private var progressColor: Int = 0

    private val decelerateInterpolator: DecelerateInterpolator
    private val accelerateInterpolator: AccelerateInterpolator
    private val progressPaint: Paint
    private val rotationTime = 2000.0f
    private val risingTime = 500.0f
    private var size: Int = 0

    constructor(context: Context, color: Int = Color.BLACK):this(context) {
        progressColor = color
    }

    init {
        size = UIScreen.dp(40.0f)

        decelerateInterpolator = DecelerateInterpolator()
        accelerateInterpolator = AccelerateInterpolator()
        progressPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        progressPaint.style = Paint.Style.STROKE
        progressPaint.strokeCap = Paint.Cap.ROUND
        progressPaint.strokeWidth = UIScreen.dpf(3.0f)
        progressPaint.color = progressColor
    }

    private fun updateAnimation() {
        val newTime = System.currentTimeMillis()
        var dt = newTime - lastUpdateTime
        if (dt > 17) {
            dt = 17
        }
        lastUpdateTime = newTime

        radOffset += 360 * dt / rotationTime
        val count = (radOffset / 360).toInt()
        radOffset -= (count * 360).toFloat()

        currentProgressTime += dt.toFloat()
        if (currentProgressTime >= risingTime) {
            currentProgressTime = risingTime
        }
        if (risingCircleLength) {
            currentCircleLength = 4 + 266 * accelerateInterpolator.getInterpolation(currentProgressTime / risingTime)
        } else {
            currentCircleLength =
                4 - 270 * (1.0f - decelerateInterpolator.getInterpolation(currentProgressTime / risingTime))
        }
        if (currentProgressTime == risingTime) {
            if (risingCircleLength) {
                radOffset += 270f
                currentCircleLength = -266f
            }
            risingCircleLength = !risingCircleLength
            currentProgressTime = 0f
        }
        invalidate()
    }

    fun setSize(value: Int) {
        size = UIScreen.dp(value.toFloat())
        invalidate()
    }

    fun setProgressColor(color: Int) {
        progressColor = color
        progressPaint.color = progressColor
    }

    override fun onDraw(canvas: Canvas) {
        val x = (measuredWidth - size) / 2
        val y = (measuredHeight - size) / 2
        circleRect.set(x.toFloat(), y.toFloat(), (x + size).toFloat(), (y + size).toFloat())
        canvas.drawArc(circleRect, radOffset, currentCircleLength, false, progressPaint)
        updateAnimation()
    }
}