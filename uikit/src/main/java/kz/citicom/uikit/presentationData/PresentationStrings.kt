package kz.citicom.uikit.presentationData

import android.content.Context
import kz.citicom.uikit.R

import kz.citicom.uikit.tools.Equtable

data class PresentationStrings(private val context: Context): Equtable() {
    val commonBack: String?
        get() = context.getString(R.string.commonBack)
    val commonClose: String?
        get() = context.getString(R.string.commonClose)
}