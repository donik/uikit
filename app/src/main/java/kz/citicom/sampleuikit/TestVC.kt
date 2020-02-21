package kz.citicom.sampleuikit

import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.widget.TextView
import kz.citicom.uikit.UIActivity
import kz.citicom.uikit.controllers.UIViewController
import kz.citicom.uikit.tools.LayoutHelper
import kz.citicom.uikit.tools.UIFonts
import kz.citicom.uikit.views.UIView
import kz.citicom.uikit.views.navigationBar.UINavigationBar

class TestVC(context: UIActivity, open val index: Int = 0) : UIViewController(context) {
    override fun loadView(): UIView? {
        val contentView = UIView(weakContext ?: return null)

        val navigationBar = UINavigationBar(weakContext)
        contentView.addView(
            navigationBar,
            LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT)
        )

        contentView.setBackgroundResource(R.color.divider)
        if (index % 2 == 0) {
            contentView.setBackgroundResource(R.drawable.first)
        } else {
            contentView.setBackgroundResource(R.drawable.second)
        }

        val textView = TextView(weakContext)
        textView.text = "INDEX OF PAGE CENTERED: $index"
        textView.typeface = UIFonts.robotoBold
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18.0f)
        contentView.addView(
            textView,
            LayoutHelper.createFrame(
                LayoutHelper.WRAP_CONTENT,
                LayoutHelper.WRAP_CONTENT,
                Gravity.CENTER
            )
        )

        return contentView
    }

    override fun toString(): String {
        return "Controller destroyed index: $index"
    }

    override fun viewDidLoad() {
        super.viewDidLoad()

//        Log.e("TestVC", "viewDidLoad")
    }

    override fun viewWillAppear() {
        super.viewWillAppear()

//        Log.e("TestVC", "viewWillAppear")
    }

    override fun viewDidAppear() {
        super.viewDidAppear()

//        Log.e("TestVC", "viewDidAppear")
    }

    override fun viewWillDisappear() {
        super.viewWillDisappear()

//        Log.e("TestVC", "viewWillDisappear")
    }

    override fun viewDidDisappear() {
        super.viewDidDisappear()

//        Log.e("TestVC", "viewDidDisappear")
    }

}