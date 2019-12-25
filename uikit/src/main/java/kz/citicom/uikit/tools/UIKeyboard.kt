package kz.citicom.uikit.tools

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

object UIKeyboard {
    @SuppressLint("WrongConstant")
    fun show(context: Context, view: View?) {
        if (view != null) {
            try {
                view.requestFocus()
                (context.getSystemService("input_method") as InputMethodManager).showSoftInput(
                    view,
                    0
                )
            } catch (throwable: Throwable) {
            }
        }
    }

    fun hide(context: Context, view: View? = null) {
        val v = view ?: return
        (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
            v.windowToken,
            0
        )
    }
}