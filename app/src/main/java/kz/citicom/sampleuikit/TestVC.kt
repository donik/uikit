package kz.citicom.sampleuikit

import android.util.Log
import android.widget.FrameLayout
import android.widget.TextView
import butterknife.BindView
import kz.citicom.uikit.UIActivity
import kz.citicom.uikit.controllers.UIViewController
import kz.citicom.uikit.presentationData.themes.getPresentationData
import butterknife.OnClick
import kz.citicom.sampleuikit.calendar_list.DayListView
import kz.citicom.uikit.controllers.modal.ModalWindow
import kz.citicom.uikit.controllers.navigationController.UINavigationController

class TestVC(context: UIActivity, open val index: Int = 0) :
    UIViewController(context, presentationData = getPresentationData()) {

    @BindView(R.id.testBTN)
    lateinit var buttonTextView: TextView

    @BindView(R.id.dayContent)
    lateinit var dayContent: FrameLayout

    override fun loadView() {
        super.loadView()

        Log.e("LOAD", "LOAD TEST")

        this.buttonTextView.setText("HELLO ${index}")

        val dayView = DayListView(context)
        this.dayContent.addView(dayView)
    }

    override fun getLayoutRes(): Int {
        return R.layout.test_controller_layout
    }

    override fun toString(): String {
        return "Controller destroyed index: $index"
    }

    @OnClick(R.id.testBTN)
    fun clickTest() {
        Log.e("CLICKED", "TEST BTN CLICKED!!!")
//        navigationController?.push(TestVC(this.context as? UIActivity ?: return, index + 1))
//        present()
        val dialog = ModalWindow(
            context,
            UINavigationController(
                context as? UIActivity ?: return,
                arrayListOf(TestModalWithAnimationVC(context as? UIActivity ?: return)),
                presentationData
            )
        )
        dialog.show()
    }
}