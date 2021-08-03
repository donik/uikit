package kz.citicom.uikit.presentationData

import kz.citicom.uikit.tools.Equtable

data class PresentationData(
    val strings: PresentationStrings,
    val theme: PresentationTheme,
    val fontSize: PresentationFontSize
) : Equtable()