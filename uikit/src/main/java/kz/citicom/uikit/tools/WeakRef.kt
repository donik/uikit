package kz.citicom.uikit.tools

import java.lang.ref.WeakReference
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class WeakRef<T>(obj: T? = null): ReadWriteProperty<Any?, T?> {
    private var weakRef : WeakReference<T>?

    init {
        this.weakRef = obj?.let { WeakReference(it) }
    }

    override fun getValue(thisRef:Any? , property: KProperty<*>): T? {
        return weakRef?.get()
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T?) {
        weakRef = value?.let { WeakReference(it) }
    }
}

fun <T> weak(obj: T? = null) = WeakRef(obj)