package kz.citicom.uikit.controllers

enum class UIModalPresentationStyle {
    FULL_SCREEN,
    CUSTOM;

    fun dismissPresentationStyle(): UIViewControllerPresentationType {
        return when (this) {
            FULL_SCREEN -> UIViewControllerPresentationType.POP_MODAL
            CUSTOM -> UIViewControllerPresentationType.POP_CUSTOM
        }
    }
}