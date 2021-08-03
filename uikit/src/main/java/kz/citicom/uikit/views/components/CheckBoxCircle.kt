package kz.citicom.uikit.views.components

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.annotation.Keep
import kz.citicom.uikit.R

import kz.citicom.uikit.tools.ColorChanger
import kz.citicom.uikit.tools.UIColor
import kz.citicom.uikit.tools.UIPaints
import kz.citicom.uikit.tools.UIScreen


class CheckBoxCircle(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {
    private val rectF: RectF = RectF()
    private val drawBitmap: Bitmap =
        Bitmap.createBitmap(UIScreen.dp(24), UIScreen.dp(24), Bitmap.Config.ARGB_4444)
    private val drawCanvas: Canvas
    private var colorChanger: ColorChanger? = null

    private var checkBoxDisabledColor: Int = UIColor.getColor("#eeeeee")
    private var uncheckedColor: Int = UIColor.getColor("#AEAEB2")
    private var checkedColor: Int = UIColor.getColor("#20BF55")
    private var checkBoxCornerColor: Int = UIColor.getColor("#ffffff")

    var progress: Float = 0.toFloat()
        @Keep
        set(value) {
            if (this.progress == value) {
                return
            }
            field = value
            invalidate()
        }
    var isChecked: Boolean = false
        private set
    private var checkAnimator: ObjectAnimator? = null
    private var attachedToWindow: Boolean = false
    private var isDisabled: Boolean = false

    init {
        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.CheckBoxCircle, 0, 0)
            val checkBoxDisabledColor = a.getColor(R.styleable.CheckBoxCircle_checkBoxDisabledColor, 0)
            val checkBoxCheckedColor = a.getColor(R.styleable.CheckBoxCircle_checkedColor, 0)
            val checkBoxUncheckedColor = a.getColor(R.styleable.CheckBoxCircle_uncheckedColor, 0)
            a.recycle()

            if (checkBoxDisabledColor != 0) {
                this.checkBoxDisabledColor = checkBoxDisabledColor
            }
            if (checkBoxCheckedColor != 0) {
                this.checkedColor = checkBoxCheckedColor
            }
            if (checkBoxUncheckedColor != 0) {
                this.uncheckedColor = checkBoxUncheckedColor
            }
        }

        drawCanvas = Canvas(drawBitmap)
    }

    private fun cancelCheckAnimator() {
        if (checkAnimator != null) {
            checkAnimator!!.cancel()
        }
    }

    private fun animateToCheckedState(newCheckedState: Boolean) {
        checkAnimator =
            ObjectAnimator.ofFloat(this, "progress", if (newCheckedState) 1.0f else 0.0f)
        checkAnimator!!.duration = 300
        checkAnimator!!.start()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        attachedToWindow = true
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        attachedToWindow = false
    }

    fun setChecked(checked: Boolean, animated: Boolean) {
        if (checked == isChecked) {
            return
        }
        isChecked = checked
        if (attachedToWindow && animated) {
            animateToCheckedState(checked)
        } else {
            cancelCheckAnimator()
            progress = if (checked) 1.0f else 0.0f
        }
    }

    fun setDisabled(disabled: Boolean) {
        isDisabled = disabled
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(
            MeasureSpec.makeMeasureSpec(UIScreen.dp(24), MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(UIScreen.dp(24), MeasureSpec.EXACTLY)
        )
    }

    override fun onDraw(canvas: Canvas) {
        if (visibility != View.VISIBLE) {
            return
        }

        val checkProgress: Float
        val bounceProgress: Float

        if (this.progress <= 0.5f) {
            checkProgress = this.progress / 0.5f
            bounceProgress = checkProgress

            if (this.colorChanger == null) {
                this.colorChanger = ColorChanger(this.uncheckedColor, this.checkedColor)
            } else {
                this.colorChanger?.setFromTo(this.uncheckedColor, this.checkedColor)
            }

            UIPaints.getCheckboxSquareBackgroundPaint().color =
                this.colorChanger?.changeColor(checkProgress) ?: UIColor.clear
        } else {
            bounceProgress = 2.0f - this.progress / 0.5f
            checkProgress = 1.0f
            UIPaints.getCheckboxSquareBackgroundPaint().color = this.checkedColor
        }
        if (isDisabled) {
            UIPaints.getCheckboxSquareBackgroundPaint().color = this.checkBoxDisabledColor
        }
        val bounce = UIScreen.dp(1) * bounceProgress
        rectF.set(bounce, bounce, UIScreen.dp(22) - bounce, UIScreen.dp(22) - bounce)

        drawBitmap.eraseColor(0)
        drawCanvas.drawRoundRect(
            rectF,
            UIScreen.dpf(200.0f),
            UIScreen.dpf(200.0f),
            UIPaints.getCheckboxSquareBackgroundPaint()
        )

        if (checkProgress != 1f) {
            val rad = Math.min(UIScreen.dpf(7.0f), UIScreen.dp(7) * checkProgress + bounce)
            rectF.set(
                UIScreen.dp(2) + rad,
                UIScreen.dp(2) + rad,
                UIScreen.dp(20) - rad,
                UIScreen.dp(20) - rad
            )
            drawCanvas.drawRoundRect(
                rectF,
                UIScreen.dpf(200.0f),
                UIScreen.dpf(200.0f),
                UIPaints.getCheckboxSquareEraserPaint()
            )
        }

        if (this.progress > 0.5f) {
            UIPaints.getCheckboxSquareCheckPaint().color = checkBoxCornerColor
            var endX = UIScreen.dp(9.5f) - UIScreen.dp(5) * (1.0f - bounceProgress)
            var endY = UIScreen.dpf(16.0f) - UIScreen.dp(5) * (1.0f - bounceProgress)
            drawCanvas.drawLine(
                UIScreen.dpf(9.5f),
                UIScreen.dpf(16.0f),
                endX,
                endY,
                UIPaints.getCheckboxSquareCheckPaint()
            )
            endX = UIScreen.dpf(8.5f) + UIScreen.dpf(8.5f) * (1.0f - bounceProgress)
            endY = UIScreen.dpf(16.0f) - UIScreen.dpf(8.5f) * (1.0f - bounceProgress)
            drawCanvas.drawLine(
                UIScreen.dpf(8.5f),
                UIScreen.dpf(16.0f),
                endX,
                endY,
                UIPaints.getCheckboxSquareCheckPaint()
            )
        }

        canvas.drawBitmap(drawBitmap, 0f, 0f, null)
    }
}