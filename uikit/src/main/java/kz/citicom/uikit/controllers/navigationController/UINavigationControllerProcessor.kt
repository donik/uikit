package kz.citicom.uikit.controllers.navigationController

import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import kz.citicom.uikit.UIApplication
import kz.citicom.uikit.UIKit
import kz.citicom.uikit.controllers.UIViewController
import kz.citicom.uikit.tools.weak

class UINavigationControllerProcessor(
    private val navigationController: UINavigationController?,
    private val transitionCoordinator: UINavigationControllerTransitionCoordinator
) : Handler() {
    companion object {
        private var flag = 0
        private val SET_CONTROLLERS = flag++
        private val PUSH_CONTROLLER = flag++
        private val POP_CONTROLLER = flag++
        private val POP_TO_ROOT_CONTROLLER = flag++
        private val POP_INTERNAL_CONTROLLER = flag++
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

    fun forEach(block: (UIViewController) -> Unit) {
        for (index in 0 until this.stack.size) {
            this.stack.get(index)?.let { block(it) }
        }
    }

    fun setViewControllers(
        viewControllers: Array<UIViewController>,
        animated: Boolean
    ) {
        if (transitionCoordinator.isAnimating) {
            return
        }

        if (!checkUiThread()) {
            sendMessage(
                Message.obtain(
                    this,
                    SET_CONTROLLERS,
                    if (animated) 1 else 0,
                    0,
                    viewControllers
                )
            )
            return
        }
        val previousController = this.stack.last
        val nextController = viewControllers.lastOrNull()
            ?: throw Exception("setViewControllers view controllers empty!!!")
        for (viewController in viewControllers) {
            viewController.navigationController = navigationController
        }
        transitionCoordinator.navigateForward(previousController, nextController, animated) {
            this.stack.clearNotInControllers(viewControllers)
        }
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
        val nextController = this.stack.last ?: return

        transitionCoordinator.navigateForward(previousController, nextController, animated)
    }

    fun pop(animated: Boolean) {
        if (this.stackSize <= 1) {
            return
        }
        Log.e("STACK_SIZE", this.stackSize.toString());

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
        val fromController = this.stack.last ?: return

        transitionCoordinator.navigateBackward(fromController, toController, animated) {
            this.stack.removeLast()
        }
    }

    fun popToRoot(animated: Boolean) {
        if (this.stackSize <= 1) {
            return
        }

        if (transitionCoordinator.isAnimating) {
            return
        }

        if (!checkUiThread()) {
            sendMessage(
                Message.obtain(
                    this,
                    POP_TO_ROOT_CONTROLLER,
                    if (animated) 1 else 0,
                    0,
                    null
                )
            )
            return
        }

        val toController = this.stack.first ?: return
        val fromController = this.stack.last ?: return

        transitionCoordinator.navigateBackward(fromController, toController, animated) {
            this.stack.clearNotInControllers(arrayOf(toController))
        }
    }

    fun popInternal() {
        if (!checkUiThread()) {
            sendMessage(
                Message.obtain(
                    this,
                    POP_INTERNAL_CONTROLLER,
                    0,
                    0,
                    null
                )
            )
            return
        }

        this.stack.removeLast() ?: return
    }

    fun clear() {
        this.stack.clearNotInControllers(arrayOf())
    }

    override fun handleMessage(msg: Message) {
        when (msg.what) {
            SET_CONTROLLERS -> {
                val isAnimated = msg.arg1 == 1
                val controllers = msg.obj as? Array<UIViewController> ?: return
                this.setViewControllers(controllers, isAnimated)
            }
            PUSH_CONTROLLER -> {
                val isAnimated = msg.arg1 == 1
                val controller = msg.obj as? UIViewController ?: return
                this.push(controller, isAnimated)
            }
            POP_CONTROLLER -> {
                val isAnimated = msg.arg1 == 1
                this.pop(isAnimated)
            }
            POP_TO_ROOT_CONTROLLER -> {
                val isAnimated = msg.arg1 == 1
                this.popToRoot(isAnimated)
            }
            POP_INTERNAL_CONTROLLER -> {
                this.popInternal()
            }
        }
    }

}