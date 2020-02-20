package kz.citicom.uikit.utils

public abstract class Wrapper<T> {

    var parentWrapper: Wrapper<*>? = null

    abstract fun getWrap(): T?
}