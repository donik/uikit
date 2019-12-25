package kz.citicom.uikit.controllers.tabBarController

import android.os.Handler
import android.os.Looper
import android.os.Message

class UITabBarControllerProcessor : Handler() {

    companion object {
        private var flag = 0
        private val SHOW_CONTROLLER = flag++
    }

    private fun checkUiThread(): Boolean {
        return Looper.myLooper() == Looper.getMainLooper()
    }

    override fun handleMessage(msg: Message) {
        when (msg.what) {
            SHOW_CONTROLLER -> {

            }
        }
    }
}