package kz.citicom.uikit.tools

abstract class Equtable {
    override fun equals(other: Any?): Boolean {
        return this.hashCode() == other.hashCode()
    }
}