package kz.citicom.uikit.overlay

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.FrameLayout
import androidx.annotation.ColorInt
import kz.citicom.uikit.R
import kz.citicom.uikit.tools.LayoutHelper

open class UIOverlayWindow(context: Context) { //todo add rootController: UIViewController<*>

    private val baseContent: Dialog = object : Dialog(context) {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            val currentWindow = this.window
            currentWindow?.setWindowAnimations(R.style.WindowNoAnimation)
            setContentView(
                contentView,
                LayoutHelper.createFrame(
                    LayoutHelper.MATCH_PARENT,
                    LayoutHelper.MATCH_PARENT
                )
            )
        }

        override fun show() {
            super.show()
        }

        override fun dismiss() {
            super.dismiss()
        }
    }

    @ColorInt
    var backgroundColor: Int = 0x00000000
        set(value) {
            field = value
            this.contentView.setBackgroundColor(value)
        }
    private var contentView: FrameLayout = object : FrameLayout(context) {
        init {
            setBackgroundColor(backgroundColor)
        }
    }
    var isHidden: Boolean
        get() {
            return this.baseContent.isShowing
        }
        set(value) {
            if (value) {
                this.baseContent.dismiss()
            } else {
                this.baseContent.show()
            }
        }

}