package kz.citicom.uikit.views

import android.annotation.SuppressLint
import android.content.Context
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import kz.citicom.uikit.tools.LayoutHelper
import kz.citicom.uikit.tools.UIColor
import kz.citicom.uikit.tools.UIScreen
import kz.citicom.uikit.views.components.RadialProgressView

class EmptyTextProgressView @SuppressLint("ClickableViewAccessibility")
constructor(context: Context) : FrameLayout(context) {

    private val textView: TextView
    private val progressBar: RadialProgressView = RadialProgressView(context)
    private var inLayout: Boolean = false
    private var showAtCenter = true

    init {
        progressBar.visibility = View.INVISIBLE
        addView(progressBar, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT))

        textView = TextView(context)
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16f)
        textView.setTextColor(UIColor.black)
        textView.gravity = Gravity.CENTER
        textView.visibility = View.INVISIBLE
        textView.setPadding(UIScreen.dp(20), 0, UIScreen.dp(20), 0)
        addView(
            textView,
            LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER)
        )

        setOnTouchListener { v, event -> true }
    }

    fun showProgress() {
        textView.visibility = View.INVISIBLE
        progressBar.visibility = View.VISIBLE
    }

    fun showTextView() {
        textView.visibility = View.VISIBLE
        progressBar.visibility = View.INVISIBLE
    }

    fun setText(text: String) {
        textView.text = text
    }

    fun setTextColor(color: Int) {
        textView.setTextColor(color)
    }

    fun setProgressBarColor(color: Int) {
        progressBar.setProgressColor(color)
    }

    fun setTextSize(size: Int) {
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size.toFloat())
    }

    fun setShowAtCenter(value: Boolean) {
        showAtCenter = value
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        inLayout = true
        val width = r - l
        val height = b - t
        val childCount = childCount
        for (i in 0 until childCount) {
            val child = getChildAt(i)

            if (child.visibility == View.GONE) {
                continue
            }

            val x = (width - child.measuredWidth) / 2
            val y: Int = if (showAtCenter) {
                (height / 2 - child.measuredHeight) / 2
            } else {
                (height - child.measuredHeight) / 2
            }
            child.layout(x, y, x + child.measuredWidth, y + child.measuredHeight)
        }
        inLayout = false
    }

    override fun requestLayout() {
        if (!inLayout) {
            super.requestLayout()
        }
    }

    override fun hasOverlappingRendering(): Boolean {
        return false
    }
}