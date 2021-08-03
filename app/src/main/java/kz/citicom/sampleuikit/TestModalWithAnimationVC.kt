package kz.citicom.sampleuikit

import android.content.Context
import android.graphics.Color
import butterknife.OnClick
import kz.citicom.uikit.controllers.UIViewController
import kz.citicom.uikit.presentationData.themes.getPresentationData

class TestModalWithAnimationVC(context: Context) :
    UIViewController(context, presentationData = getPresentationData()) {

    override fun loadView() {
        super.loadView()

        this.view.setBackgroundColor(Color.RED)
        this.rootView.setBackgroundColor(Color.GREEN)
    }

    override fun getLayoutRes(): Int {
        return R.layout.test_controller_layout
    }

    @OnClick(R.id.testBTN)
    fun onClickTest() {
        navigationController?.push(TestModalWithAnimationVC(context))
    }

}