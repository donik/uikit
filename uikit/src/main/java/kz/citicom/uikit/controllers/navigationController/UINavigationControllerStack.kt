package kz.citicom.uikit.controllers.navigationController

import android.util.Log
import kz.citicom.uikit.controllers.UIViewController

class UINavigationControllerStack {
    private var currentIndex: Int = -1
    private var stack: ArrayList<UIViewController> = arrayListOf()
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
            this.stack.clear()
            this.stack.addAll(viewControllers)
            this.currentIndex = this.size - 1
        }
    }

    fun insert(viewController: UIViewController, index: Int) {
        if (index >= this.currentIndex) {
            return
        }

        synchronized(lock) {
            this.stack.add(index, viewController)
            this.currentIndex++
        }
    }

    fun push(viewController: UIViewController) {
        this.stack.add(viewController)
        this.currentIndex++
    }

    fun remove(index: Int): UIViewController? {
        synchronized(lock) {
            val controller = this.stack.removeAt(index)
            controller.destroy()
            this.currentIndex--

            return controller
        }
    }

    fun removeLast(): UIViewController? {
        synchronized(lock) {
            val controller = this.stack.removeAt(this.currentIndex - 1)
            controller.destroy()
            this.currentIndex--

            return controller
        }
    }

}