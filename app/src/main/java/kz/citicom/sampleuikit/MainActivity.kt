package kz.citicom.sampleuikit

import android.app.Activity
import android.app.ActivityManager.TaskDescription
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.widget.FrameLayout
import kz.citicom.uikit.UIActivity
import kz.citicom.uikit.tools.LayoutHelper

class MainActivity : UIActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setTheme(R.style.Theme_TMessages)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setTaskDescription(
                TaskDescription(
                    null,
                    null,
                    Color.RED
                )
            )
            Log.e("SET", "SET TASK DESCR")
        }

        window.setBackgroundDrawableResource(R.drawable.transparent)

        super.onCreate(savedInstanceState)

        val frameLayout = object : FrameLayout(this) {
            init {
                setBackgroundColor(Color.GREEN)
            }
        }


        setContentView(
            frameLayout,
            LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT)
        )
    }

}