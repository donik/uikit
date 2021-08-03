package kz.citicom.uikit.tools

import android.animation.ArgbEvaluator

class ColorChanger(private var fromColor: Int, private var toColor: Int) {
    private val argbEvaluator: ArgbEvaluator = ArgbEvaluator()

    fun setFromTo(fromColor: Int, toColor: Int) {
        this.fromColor = fromColor
        this.toColor = toColor
    }

    fun changeColor(rate: Float): Int {
        return argbEvaluator.evaluate(rate, fromColor, toColor) as Int
    }
}