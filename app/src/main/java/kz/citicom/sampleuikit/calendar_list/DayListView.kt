package kz.citicom.sampleuikit.calendar_list

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import android.widget.FrameLayout
import kz.citicom.uikit.tools.LayoutHelper
import kz.citicom.uikit.tools.UIScreen

class DayListView(context: Context) : FrameLayout(context) {

    private val hours = 24
    private val hourHeight = 120

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    init {
        setWillNotDraw(false)

        this.paint.color = Color.WHITE
        this.paint.style = Paint.Style.FILL
        this.paint.textSize = UIScreen.dpf(12.0f)

        val totalHeight = UIScreen.dp(hourHeight) * hours
        this.layoutParams = LayoutHelper.createFrame(
            LayoutHelper.MATCH_PARENT,
            UIScreen.revertDp(totalHeight.toFloat())
        )
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.drawText("hello", UIScreen.dpf(12.0f), 0.0f, paint)
        Log.e("DRRR", "DRAW CALLED ${this.measuredHeight}")
    }
}