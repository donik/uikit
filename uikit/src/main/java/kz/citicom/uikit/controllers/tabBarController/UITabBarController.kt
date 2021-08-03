package kz.citicom.uikit.controllers.tabBarController

import android.graphics.drawable.Drawable
import android.util.Log
import android.view.Gravity
import com.google.android.material.tabs.TabLayout
import kz.citicom.uikit.R
import kz.citicom.uikit.UIActivity
import kz.citicom.uikit.controllers.UIViewController
import kz.citicom.uikit.presentationData.PresentationData
import kz.citicom.uikit.tools.*
import kz.citicom.uikit.views.UIView

public class UITabBarController(
    context: UIActivity,
    private var viewControllers: Array<UIViewController>,
    private var selectedIndex: Int = 0,
    presentationData: PresentationData
) : UIViewController(context, presentationData) {

    private var tabLayout: TabLayout = UIView.getLayout(context, R.layout.tab_bar_layout)

    override fun getLayoutRes(): Int {
        return -1
    }

    override fun loadView() {
        super.loadView()

        val weakSelf by weak(this)
        this.tabLayout.tabMode = TabLayout.MODE_FIXED
        this.tabLayout.setSelectedTabIndicator(null)
        this.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
                weakSelf?.scrollToTop()
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                weakSelf?.setSelectedIndex(tab?.position ?: return, true)
            }
        })

        val contacts = this.newTab("Contacts", UIImage.getDrawable(R.drawable.abc_vector_test))
        val calls = this.newTab("Calls", UIImage.getDrawable(R.drawable.abc_vector_test))
        val chats = this.newTab("Chats", UIImage.getDrawable(R.drawable.abc_vector_test))
        val settings = this.newTab("Settings", UIImage.getDrawable(R.drawable.abc_vector_test))
        this.tabLayout.removeAllTabs()

        this.tabLayout.addTab(contacts)
        this.tabLayout.addTab(calls)
        this.tabLayout.addTab(chats)
        this.tabLayout.addTab(settings)

        this.view.addView(
            this.tabLayout,
            LayoutHelper.createFrame(
                LayoutHelper.MATCH_PARENT,
                LayoutHelper.WRAP_CONTENT,
                Gravity.BOTTOM or Gravity.LEFT
            )
        )
    }

    override fun updateThemeAndStrings() {
        this.tabLayout.tabIconTint = this.presentationData.theme.rootController.tabBar.getIconTint()
        this.tabLayout.tabTextColors = this.presentationData.theme.rootController.tabBar.getTitleTint()
        this.tabLayout.setBackgroundColor(this.presentationData.theme.rootController.tabBar.backgroundColor)
    }

    fun setSelectedIndex(index: Int, animated: Boolean) {
        Log.e("SELECT", "INDEX: $index")
    }

    fun setViewControllers(viewControllers: Array<UIViewController>) {
//        val contacts = this.newTab("Contacts", UIImage.getDrawable(R.drawable.abc_vector_test))
//        val calls = this.newTab("Calls", UIImage.getDrawable(R.drawable.abc_vector_test))
//        val chats = this.newTab("Chats", UIImage.getDrawable(R.drawable.abc_vector_test))
//        val settings = this.newTab("Settings", UIImage.getDrawable(R.drawable.abc_vector_test))
//        this.tabLayout.removeAllTabs()
//
//        this.tabLayout.addTab(contacts)
//        this.tabLayout.addTab(calls)
//        this.tabLayout.addTab(chats)
//        this.tabLayout.addTab(settings)
    }

    private fun newTab(title: String, icon: Drawable?): TabLayout.Tab {
        val tab = this.tabLayout.newTab()
        tab.text = title
        tab.icon = icon
        return tab
    }
}