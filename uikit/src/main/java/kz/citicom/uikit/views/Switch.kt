package kz.citicom.uikit.views

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.*
import android.graphics.drawable.Drawable
import android.graphics.drawable.RippleDrawable
import android.os.Build
import android.util.StateSet
import android.view.View
import androidx.annotation.Keep
import kz.citicom.uikit.tools.UIColor
import kz.citicom.uikit.tools.UIScreen

class Switch(context: Context) : View(context) {

    private val rectF: RectF = RectF()

    @get:Keep
    var progress: Float = 0.toFloat()
        @Keep
        set(value) {
            if (this.progress == value) {
                return
            }
            field = value
            invalidate()
        }
    private var checkAnimator: ObjectAnimator? = null
    private var iconAnimator: ObjectAnimator? = null

    private var attachedToWindow: Boolean = false
    var isChecked: Boolean = false
        private set
    private val paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val paint2: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var drawIconType: Int = 0

    @get:Keep
    var iconProgress = 1.0f
        @Keep
        set(value) {
            if (this.iconProgress == value) {
                return
            }
            field = value
            invalidate()
        }

    private var onCheckedChangeListener: OnCheckedChangeListener? = null

    private var trackColorKey = UIColor.getColor("#ADADAD")
    private var trackCheckedColorKey = UIColor.getColor("#00CC99")
    private var thumbColorKey = UIColor.white
    private var thumbCheckedColorKey = UIColor.white

    private var iconDrawable: Drawable? = null
    private var lastIconColor: Int = 0

    private var drawRipple: Boolean = false
    private var rippleDrawable: RippleDrawable? = null
    private var ripplePaint: Paint? = null
    private val pressedState =
        intArrayOf(android.R.attr.state_enabled, android.R.attr.state_pressed)
    private var colorSet: Int = 0

    private var bitmapsCreated: Boolean = false
    private var overlayBitmap: Array<Bitmap?>? = null
    private var overlayCanvas: Array<Canvas?>? = null
    private var overlayMaskBitmap: Bitmap? = null
    private var overlayMaskCanvas: Canvas? = null
    private var overlayCx: Float = 0.toFloat()
    private var overlayCy: Float = 0.toFloat()
    private var overlayRad: Float = 0.toFloat()
    private var overlayEraserPaint: Paint? = null
    private var overlayMaskPaint: Paint? = null

    private var overrideColorProgress: Int = 0

    interface OnCheckedChangeListener {
        fun onCheckedChanged(view: Switch, isChecked: Boolean)
    }

    init {

        paint2.style = Paint.Style.STROKE
        paint2.strokeCap = Paint.Cap.ROUND
        paint2.strokeWidth = UIScreen.dpf(2.0f)
    }

    private fun cancelCheckAnimator() {
        if (checkAnimator != null) {
            checkAnimator!!.cancel()
            checkAnimator = null
        }
    }

    private fun cancelIconAnimator() {
        if (iconAnimator != null) {
            iconAnimator!!.cancel()
            iconAnimator = null
        }
    }

    fun setDrawIconType(type: Int) {
        drawIconType = type
    }

    fun setDrawRipple(value: Boolean) {
        if (Build.VERSION.SDK_INT < 21 || value == drawRipple) {
            return
        }
        drawRipple = value

        if (rippleDrawable == null) {
            ripplePaint = Paint(Paint.ANTI_ALIAS_FLAG)
            ripplePaint?.color = UIColor.white
            val maskDrawable = object : Drawable() {
                override fun draw(canvas: Canvas) {
                    val bounds = bounds
                    canvas.drawCircle(
                        bounds.centerX().toFloat(),
                        bounds.centerY().toFloat(),
                        UIScreen.dpf(18.0f),
                        ripplePaint!!
                    )
                }

                override fun setAlpha(alpha: Int) {

                }

                override fun setColorFilter(colorFilter: ColorFilter?) {

                }

                override fun getOpacity(): Int {
                    return PixelFormat.UNKNOWN
                }
            }
            val colorStateList = ColorStateList(
                arrayOf(StateSet.WILD_CARD),
                intArrayOf(0)
            )
            rippleDrawable = RippleDrawable(colorStateList, null, maskDrawable)
            rippleDrawable?.callback = this
        }
        if (isChecked && colorSet != 2 || !isChecked && colorSet != 1) {
            val color = if (isChecked) UIColor.black else UIColor.white //todo
            val colorStateList = ColorStateList(
                arrayOf(StateSet.WILD_CARD),
                intArrayOf(color)
            )
            rippleDrawable!!.setColor(colorStateList)
            colorSet = if (isChecked) 2 else 1
        }
        if (Build.VERSION.SDK_INT >= 28 && value) {
            rippleDrawable?.setHotspot(
                if (isChecked) 0.0f else UIScreen.dpf(100.0f),
                UIScreen.dpf(18.0f)
            )
        }
        rippleDrawable?.state = if (value) pressedState else StateSet.NOTHING
        invalidate()
    }

    override fun verifyDrawable(who: Drawable): Boolean {
        return super.verifyDrawable(who) || rippleDrawable != null && who == rippleDrawable
    }

    fun setColors(track: Int, trackChecked: Int, thumb: Int, thumbChecked: Int) {
        trackColorKey = track
        trackCheckedColorKey = trackChecked
        thumbColorKey = thumb
        thumbCheckedColorKey = thumbChecked
    }

    private fun animateToCheckedState(newCheckedState: Boolean) {
        checkAnimator =
            ObjectAnimator.ofFloat(this, "progress", if (newCheckedState) 1.0f else 0.0f)
        checkAnimator?.duration = 250
        checkAnimator?.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                checkAnimator = null
            }
        })
        checkAnimator?.start()
    }

    private fun animateIcon(newCheckedState: Boolean) {
        iconAnimator =
            ObjectAnimator.ofFloat(this, "iconProgress", if (newCheckedState) 1.0f else 0.0f)
        iconAnimator?.duration = 250
        iconAnimator?.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                iconAnimator = null
            }
        })
        iconAnimator?.start()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        attachedToWindow = true
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        attachedToWindow = false
    }

    fun setOnCheckedChangeListener(listener: OnCheckedChangeListener) {
        onCheckedChangeListener = listener
    }

    fun setChecked(checked: Boolean, animated: Boolean) {
        setChecked(checked, drawIconType, animated)
    }

    fun setChecked(checked: Boolean, iconType: Int, animated: Boolean) {
        if (checked != isChecked) {
            isChecked = checked
            if (attachedToWindow && animated) {
                animateToCheckedState(checked)
            } else {
                cancelCheckAnimator()
                progress = if (checked) 1.0f else 0.0f
            }
            if (onCheckedChangeListener != null) {
                onCheckedChangeListener!!.onCheckedChanged(this, checked)
            }
        }
        if (drawIconType != iconType) {
            drawIconType = iconType
            if (attachedToWindow && animated) {
                animateIcon(iconType == 0)
            } else {
                cancelIconAnimator()
                iconProgress = if (iconType == 0) 1.0f else 0.0f
            }
        }
    }

    fun setIcon(icon: Int) {
        if (icon != 0) {
            iconDrawable = resources.getDrawable(icon).mutate()
            if (iconDrawable != null) {
                iconDrawable?.colorFilter = PorterDuffColorFilter(
                    if (isChecked) trackCheckedColorKey else trackColorKey,
                    PorterDuff.Mode.MULTIPLY
                )
            }
        } else {
            iconDrawable = null
        }
    }

    fun hasIcon(): Boolean {
        return iconDrawable != null
    }

    fun setOverrideColor(override: Int) {
        if (overrideColorProgress == override) {
            return
        }
        if (overlayBitmap == null) {
            try {
                overlayBitmap = arrayOfNulls(2)
                overlayCanvas = arrayOfNulls(2)
                for (a in 0..1) {
                    overlayBitmap!![a] =
                        Bitmap.createBitmap(measuredWidth, measuredHeight, Bitmap.Config.ARGB_8888)
                    overlayCanvas!![a] = Canvas(overlayBitmap!![a]!!)
                }
                overlayMaskBitmap =
                    Bitmap.createBitmap(measuredWidth, measuredHeight, Bitmap.Config.ARGB_8888)
                overlayMaskCanvas = Canvas(overlayMaskBitmap!!)

                overlayEraserPaint = Paint(Paint.ANTI_ALIAS_FLAG)
                overlayEraserPaint?.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)

                overlayMaskPaint = Paint(Paint.ANTI_ALIAS_FLAG)
                overlayMaskPaint?.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)
                bitmapsCreated = true
            } catch (e: Throwable) {
                return
            }

        }
        if (!bitmapsCreated) {
            return
        }
        overrideColorProgress = override
        overlayCx = 0f
        overlayCy = 0f
        overlayRad = 0f
        invalidate()
    }

    fun setOverrideColorProgress(cx: Float, cy: Float, rad: Float) {
        overlayCx = cx
        overlayCy = cy
        overlayRad = rad
        invalidate()
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        if (visibility != View.VISIBLE) {
            return
        }

        val width = UIScreen.dp(31.0f)
        val thumb = UIScreen.dp(20.0f)
        val x = (measuredWidth - width) / 2
        val y = (measuredHeight - UIScreen.dp(14.0f)) / 2
        var tx = x + UIScreen.dp(7.0f) + (UIScreen.dp(17.0f) * this.progress)
        var ty = measuredHeight / 2.0f


        var color1: Int
        var color2: Int
        var colorProgress: Float
        var r1: Int
        var r2: Int
        var g1: Int
        var g2: Int
        var b1: Int
        var b2: Int
        var a1: Int
        var a2: Int
        var red: Int
        var green: Int
        var blue: Int
        var alpha: Int
        var color: Int

        for (a in 0..1) {
            if (a == 1 && overrideColorProgress == 0) {
                continue
            }
            val canvasToDraw = if (a == 0) canvas else overlayCanvas!![0]

            if (a == 1) {
                overlayBitmap?.get(0)?.eraseColor(0)
                paint.color = UIColor.black
                overlayMaskCanvas!!.drawRect(
                    0f,
                    0f,
                    overlayMaskBitmap?.width?.toFloat() ?: 0.0f,
                    overlayMaskBitmap?.height?.toFloat() ?: 0.0f,
                    paint
                )
                overlayMaskCanvas?.drawCircle(
                    overlayCx - getX(),
                    overlayCy - getY(),
                    overlayRad,
                    overlayEraserPaint!!
                )
            }
            colorProgress = when (overrideColorProgress) {
                1 -> (if (a == 0) 0 else 1).toFloat()
                2 -> (if (a == 0) 1 else 0).toFloat()
                else -> this.progress
            }

            color1 = trackColorKey
            color2 = trackCheckedColorKey
            if (a == 0 && iconDrawable != null && lastIconColor != (if (isChecked) color2 else color1)) {
                iconDrawable?.colorFilter =
                    PorterDuffColorFilter(
                        if (isChecked) color2 else color1,
                        PorterDuff.Mode.MULTIPLY
                    )
            }

            r1 = Color.red(color1)
            r2 = Color.red(color2)
            g1 = Color.green(color1)
            g2 = Color.green(color2)
            b1 = Color.blue(color1)
            b2 = Color.blue(color2)
            a1 = Color.alpha(color1)
            a2 = Color.alpha(color2)

            red = (r1 + (r2 - r1) * colorProgress).toInt()
            green = (g1 + (g2 - g1) * colorProgress).toInt()
            blue = (b1 + (b2 - b1) * colorProgress).toInt()
            alpha = (a1 + (a2 - a1) * colorProgress).toInt()
            color =
                alpha and 0xff shl 24 or (red and 0xff shl 16) or (green and 0xff shl 8) or (blue and 0xff)
            paint.color = color
            paint2.color = color

            rectF.set(x.toFloat(), y.toFloat(), (x + width).toFloat(), y + UIScreen.dpf(14.0f))
            canvasToDraw?.drawRoundRect(rectF, UIScreen.dpf(7.0f), UIScreen.dpf(7.0f), paint)
            canvasToDraw?.drawCircle(tx, ty, UIScreen.dpf(10.0f), paint)

            if (a == 0 && rippleDrawable != null) {
                rippleDrawable?.setBounds(
                    tx.toInt() - UIScreen.dp(18.0f),
                    ty.toInt() - UIScreen.dp(18.0f),
                    tx.toInt() + UIScreen.dp(18.0f),
                    ty.toInt() + UIScreen.dp(18.0f)
                )
                rippleDrawable?.draw(canvasToDraw!!)
            } else if (a == 1) {
                canvasToDraw?.drawBitmap(overlayMaskBitmap!!, 0f, 0f, overlayMaskPaint)
            }
        }
        if (overrideColorProgress != 0) {
            canvas.drawBitmap(overlayBitmap?.get(0)!!, 0f, 0f, null)
        }

        for (a in 0..1) {
            if (a == 1 && overrideColorProgress == 0) {
                continue
            }
            val canvasToDraw = if (a == 0) canvas else overlayCanvas!![1]

            if (a == 1) {
                overlayBitmap?.get(1)?.eraseColor(0)
            }
            if (overrideColorProgress == 1) {
                colorProgress = (if (a == 0) 0 else 1).toFloat()
            } else if (overrideColorProgress == 2) {
                colorProgress = (if (a == 0) 1 else 0).toFloat()
            } else {
                colorProgress = this.progress
            }

            color1 = thumbColorKey
            color2 = thumbCheckedColorKey
            r1 = Color.red(color1)
            r2 = Color.red(color2)
            g1 = Color.green(color1)
            g2 = Color.green(color2)
            b1 = Color.blue(color1)
            b2 = Color.blue(color2)
            a1 = Color.alpha(color1)
            a2 = Color.alpha(color2)

            red = (r1 + (r2 - r1) * colorProgress).toInt()
            green = (g1 + (g2 - g1) * colorProgress).toInt()
            blue = (b1 + (b2 - b1) * colorProgress).toInt()
            alpha = (a1 + (a2 - a1) * colorProgress).toInt()
            paint.color =
                alpha and 0xff shl 24 or (red and 0xff shl 16) or (green and 0xff shl 8) or (blue and 0xff)

            canvasToDraw?.drawCircle(tx, ty, UIScreen.dpf(8.0f), paint)

            if (a == 0) {
                if (iconDrawable != null) {
                    iconDrawable?.setBounds(
                        (tx - ((iconDrawable?.intrinsicWidth ?: 0) / 2)).toInt(),
                        (ty - (iconDrawable?.intrinsicHeight ?: 0) / 2).toInt(),
                        (tx + (iconDrawable?.intrinsicWidth ?: 0) / 2).toInt(),
                        (ty + (iconDrawable?.intrinsicHeight ?: 0) / 2).toInt()
                    )
                    iconDrawable!!.draw(canvasToDraw!!)
                } else if (drawIconType == 1) {
                    tx -= UIScreen.dp(10.8f) - UIScreen.dp(1.3f) * this.progress
                    ty -= UIScreen.dp(8.5f) - UIScreen.dp(0.5f) * this.progress.toInt()
                    val startX2 = UIScreen.dpf(4.6f) + tx
                    val startY2 = (UIScreen.dpf(9.5f) + ty)
                    val endX2 = startX2 + UIScreen.dpf(2.0f)
                    val endY2 = startY2 + UIScreen.dpf(2.0f)

                    var startX = UIScreen.dpf(7.5f) + tx
                    var startY = UIScreen.dpf(5.4f) + ty
                    var endX = startX + UIScreen.dpf(7.0f)
                    var endY = startY + UIScreen.dpf(7.0f)

                    startX += (startX2 - startX) * this.progress
                    startY += (startY2 - startY) * this.progress
                    endX += (endX2 - endX) * this.progress
                    endY += (endY2 - endY) * this.progress
                    canvasToDraw?.drawLine(startX, startY, endX, endY, paint2)

                    startX = UIScreen.dpf(7.5f) + tx
                    startY = UIScreen.dpf(12.5f) + ty
                    endX = startX + UIScreen.dpf(7.0f)
                    endY = startY - UIScreen.dpf(7.0f)
                    canvasToDraw?.drawLine(startX, startY, endX, endY, paint2)
                } else if (drawIconType == 2 || iconAnimator != null) {
                    paint2.alpha = (255 * (1.0f - this.iconProgress)).toInt()
                    canvasToDraw?.drawLine(tx, ty, tx, ty - UIScreen.dpf(5.0f), paint2)
                    canvasToDraw?.save()
                    canvasToDraw?.rotate(-90 * this.iconProgress, tx, ty)
                    canvasToDraw?.drawLine(tx, ty, tx + UIScreen.dp(4.0f), ty, paint2)
                    canvasToDraw?.restore()
                }
            }
            if (a == 1) {
                canvasToDraw?.drawBitmap(overlayMaskBitmap!!, 0f, 0f, overlayMaskPaint)
            }
        }
        if (overrideColorProgress != 0) {
            canvas.drawBitmap(overlayBitmap?.get(1)!!, 0f, 0f, null)
        }
    }
}