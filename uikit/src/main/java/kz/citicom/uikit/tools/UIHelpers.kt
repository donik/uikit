package kz.citicom.uikit.tools

data class UIEdgeInsets(var top: Float, var left: Float, var bottom: Float, var right: Float) {
    companion object
}

fun UIEdgeInsets.Companion.zero(): UIEdgeInsets {
    return UIEdgeInsets(0.0f, 0.0f, 0.0f, 0.0f)
}

data class CGSize(var width: Float, var height: Float)

data class CGPoint(var x: Float, var y: Float)

data class CGRect(var x: Float, var y: Float, var width: Float, var height: Float) {
    constructor(origin: CGPoint, size: CGSize) : this(origin.x, origin.y, size.width, size.height)
}

