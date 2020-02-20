package kz.citicom.uikit.controllers.navigationController

import android.util.Log
import kz.citicom.uikit.controllers.UIViewController
import kz.citicom.uikit.tools.weak

class UINavigationControllerStack {
    private var currentIndex: Int = -1
    private var stack: ArrayList<UIViewController?> = arrayListOf()
    private val lock: Any = Any()

    val isEmpty: Boolean
        get() = this.stack.isEmpty()
    val size: Int
        get() = this.stack.size
    val first: UIViewController?
        get() = try {
            this.stack.first()
        } catch (_: Exception) {
            null
        }
    val previous: UIViewController?
        get() = get(currentIndex - 1)
    val last: UIViewController?
        get() = try {
            this.stack.last()
        } catch (_: Exception) {
            null
        }

    fun get(index: Int): UIViewController? {
        if (index < 0 || index >= this.stack.size) {
            return null
        }
        return this.stack[index]
    }

    fun set(viewControllers: ArrayList<UIViewController>) {
        synchronized(lock) {
            val controllers = viewControllers.map {
                val controller by weak(it)
                return@map controller
            }

            this.stack.clear()
            this.stack.addAll(controllers)
            this.currentIndex = this.size - 1
        }
    }

    fun insert(viewController: UIViewController, index: Int) {
        if (index >= this.currentIndex) {
            return
        }

        synchronized(lock) {
            val controller by weak(viewController)
            this.stack.add(index, controller)
            this.currentIndex++
        }
    }

    fun push(viewController: UIViewController) {
        val controller by weak(viewController)
        this.stack.add(controller)
        this.currentIndex++
    }

    fun remove(index: Int): UIViewController? {
        synchronized(lock) {
            val controller = this.stack.removeAt(index)
            controller?.destroy()
            this.currentIndex--

            return controller
        }
    }

    fun removeLast(): UIViewController? {
        synchronized(lock) {
            val controller = this.stack.removeAt(this.currentIndex - 1)
            controller?.destroy()
            this.currentIndex--

            return controller
        }
    }

}