package kz.citicom.uikit.controllers.navigationController

import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import kz.citicom.uikit.controllers.UIViewController
import kz.citicom.uikit.tools.weak
import java.lang.Exception

class UINavigationControllerProcessor(
    private val navigationController: UINavigationController?,
    private val transitionCoordinator: UINavigationControllerTransitionCoordinator
) : Handler() {
    companion object {
        private var flag = 0
        private val PUSH_CONTROLLER = flag++
        private val POP_CONTROLLER = flag++
        private val PRESENT_CONTROLLER = flag++
        private val DISMISS_CONTROLLER = flag++
    }

    private val stack: UINavigationControllerStack = UINavigationControllerStack()
    val stackSize: Int
        get() = stack.size
    val previous: UIViewController?
        get() = this.stack.previous
    val current: UIViewController?
        get() = this.stack.last

    private fun checkUiThread(): Boolean {
        return Looper.myLooper() == Looper.getMainLooper()
    }

    fun push(
        viewController: UIViewController,
        animated: Boolean
    ) {
        if (transitionCoordinator.isAnimating) {
            return
        }

        if (!checkUiThread()) {
            sendMessage(
                Message.obtain(
                    this,
                    PUSH_CONTROLLER,
                    if (animated) 1 else 0,
                    0,
                    viewController
                )
            )
            return
        }

        if (viewController is UINavigationController) {
            throw Exception("UINavigationController not allowed")
        }

        viewController.navigationController = navigationController
        val weakController by weak(viewController)
        this.stack.push(weakController ?: return)

        val previousController = this.stack.previous
        Log.e("TRY", "STACK SIZE: ${this.stack.size}")

//        if (this.stack.size > 10) {
//            this.stack.remove(5)
//        }

        val nextController = this.stack.last ?: return

        transitionCoordinator.navigateForward(previousController, nextController, animated)
    }

    fun pop(animated: Boolean) {
        if (transitionCoordinator.isAnimating) {
            return
        }

        if (!checkUiThread()) {
            sendMessage(
                Message.obtain(
                    this,
                    POP_CONTROLLER,
                    if (animated) 1 else 0,
                    0,
                    null
                )
            )
            return
        }

        val toController = this.stack.previous ?: return
        val fromController = this.stack.removeLast() ?: return

        Log.e("TRY", "STACK SIZE: ${this.stack.size}")

        transitionCoordinator.navigateBackward(fromController, toController, animated)
    }

    override fun handleMessage(msg: Message) {
        when (msg.what) {
            PUSH_CONTROLLER -> {
                val isAnimated = msg.arg1 == 1
                val controller = msg.obj as? UIViewController ?: return
                this.push(controller, isAnimated)
            }
            POP_CONTROLLER -> {
                val isAnimated = msg.arg1 == 1
                this.pop(isAnimated)
            }
        }
    }

}