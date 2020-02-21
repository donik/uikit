package kz.citicom.uikit.views.navigationBar

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import kz.citicom.uikit.tools.LayoutHelper
import kz.citicom.uikit.tools.UIScreen
import kz.citicom.uikit.tools.UISize
import kz.citicom.uikit.views.UIView
import kz.citicom.uikit.views.removeChildes
import kz.citicom.uikit.views.removeFromSuperview

class UINavigationBar(context: Context) : UIView(context) {
    private val leftItemsLayout: LinearLayout = LinearLayout(context)
    private val rightItemsLayout: LinearLayout = LinearLayout(context)
    private val titleLayout: TitleContentView = TitleContentView(context)

    private val contentLayout: FrameLayout = FrameLayout(context)

    init {
        this.setPadding(0, UIScreen.statusBarHeight, 0, 0)
        this.setBackgroundColor(Color.RED)

        val verticalLayout = LinearLayout(context)
        verticalLayout.orientation = LinearLayout.VERTICAL
        this.addView(
            verticalLayout,
            LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT)
        )

        val topContentLayout = LinearLayout(context)
        topContentLayout.orientation = LinearLayout.HORIZONTAL
        topContentLayout.setBackgroundColor(Color.GREEN)
        topContentLayout.setPadding(UIScreen.dp(16), 0, UIScreen.dp(16), 0)
        verticalLayout.addView(
            topContentLayout,
            LayoutHelper.createLinear(
                LayoutHelper.MATCH_PARENT,
                UIScreen.revertDp(UISize.HEADER_PORTRAIT_SIZE.toFloat())
            )
        )

        this.leftItemsLayout.setBackgroundColor(Color.LTGRAY)
        topContentLayout.addView(
            this.leftItemsLayout,
            LayoutHelper.createLinear(
                LayoutHelper.WRAP_CONTENT,
                UIScreen.revertDp(UISize.HEADER_PORTRAIT_SIZE.toFloat()),
                0.0f,
                Gravity.START
            )
        )

        this.titleLayout.setBackgroundColor(Color.BLUE)
        topContentLayout.addView(
            this.titleLayout,
            LayoutHelper.createLinear(
                LayoutHelper.MATCH_PARENT,
                UIScreen.revertDp(UISize.HEADER_PORTRAIT_SIZE.toFloat()),
                1.0f,
                Gravity.CENTER
            )
        )

        this.rightItemsLayout.setBackgroundColor(Color.CYAN)
        topContentLayout.addView(
            this.rightItemsLayout,
            LayoutHelper.createLinear(
                LayoutHelper.WRAP_CONTENT,
                UIScreen.revertDp(UISize.HEADER_PORTRAIT_SIZE.toFloat()),
                0.0f,
                Gravity.END
            )
        )

        verticalLayout.addView(
            this.contentLayout,
            LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT)
        )
    }



    private class TitleContentView(context: Context) : FrameLayout(context) {
        private val mainContent: FrameLayout = FrameLayout(context)
        private val loadingContent: FrameLayout = FrameLayout(context)

        init {
            this.loadingContent.visibility = View.GONE

            this.addView(
                this.mainContent,
                LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT)
            )
            this.addView(
                this.loadingContent,
                LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT)
            )
        }

        fun setTitleView(view: View) {
            view.removeFromSuperview()
            this.mainContent.removeChildes()
            this.mainContent.addView(view)
        }

        fun setLoadingView(view: View?) {
            view?.removeFromSuperview()
            this.loadingContent.removeChildes()
            view?.let { this.loadingContent.addView(it) }
        }

        fun toggleTitleViews(loading: Boolean) {
            this.loadingContent.visibility = if (loading) {
                View.VISIBLE
            } else {
                View.GONE
            }
            this.mainContent.visibility = if (!loading) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }
    }
}