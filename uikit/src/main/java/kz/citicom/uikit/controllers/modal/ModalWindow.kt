package kz.citicom.uikit.controllers.modal

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import kz.citicom.uikit.R
import kz.citicom.uikit.UIActivity
import kz.citicom.uikit.controllers.UIViewController
import kz.citicom.uikit.tools.*
import kz.citicom.uikit.tools.UIAnimation.NAVIGATION_INTERPOLATOR
import kz.citicom.uikit.uiabstract.UIWindowAbstract
import kz.citicom.uikit.views.ActivityContentLayout
import kz.citicom.uikit.views.UIView
import kz.citicom.uikit.views.UIWindow
import java.lang.ref.WeakReference

class ModalWindow<T : UIViewController>(context: Context, controller: T) :
    Dialog(context, R.style.TransparentDialog) {

    private var view: ActivityContentLayout = ActivityContentLayout(context)
    private var weakController: T? = WeakReference(controller).get()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.weakController?.modalWindow = this
        this.setContentView(
            this.view,
            LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT)
        )
        this.view.addView(
            weakController?.view,
            LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT)
        )
    }

    override fun dismiss() {
        this.weakController?.viewWillDisappear()
        this.view.setBackgroundColor(UIColor.getColor("#000000", 0.3f))
        this.weakController?.view?.translationY = 0.0f
        this.weakController?.view?.alpha = 1.0f

        UIView.animate(0, 300, progress = {
            this.weakController?.view?.translationY = it * UIScreen.displaySize.y.toFloat()
            this.weakController?.view?.alpha = (1 - it) * 1.0f
            this.view.setBackgroundColor(UIColor.getColor("#000000", (1 - it) * 1.0f))
        }, completion = {
            this.weakController?.viewDidDisappear()
            this.weakController?.destroy()
            super.dismiss()
        })
    }

    override fun show() {
        super.show()

        this.weakController?.viewWillAppear()
        this.view.setBackgroundColor(UIColor.getColor("#000000", 0.3f))
        this.weakController?.view?.translationY = UIScreen.displaySize.y.toFloat() / 3.5f
        this.weakController?.view?.alpha = 0.3f

        UIView.animate(0, 120, NAVIGATION_INTERPOLATOR, progress = {
            this.weakController?.view?.translationY = (1 - it) * (UIScreen.displaySize.y.toFloat() / 3.5f)
            this.weakController?.view?.alpha = 0.3f + it * 0.7f
        }, completion = {
            this.weakController?.viewDidAppear()
            this.weakController?.modalWindow = null
        })
    }

    override fun onBackPressed() {
        val closeApp = this.weakController?.onBackPressed() ?: false
        if (!closeApp) {
            return
        }

        super.onBackPressed()
    }
}