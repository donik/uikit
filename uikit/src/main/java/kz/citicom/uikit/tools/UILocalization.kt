package kz.citicom.uikit.tools

import android.content.Context
import android.content.res.Configuration
import androidx.annotation.StringRes
import java.util.*

object UILocalization {
    private var appContext: Context? = null

    fun init(context: Context) {
        this.appContext = context
    }

    fun getLocalizationContext(value: String): Context? {
        return this.appContext?.let {
            val configuration = Configuration(it.resources.configuration)
            val locale = Locale(value)
            Locale.setDefault(locale)
            configuration.setLocale(locale)
            return it.createConfigurationContext(configuration)
        }
    }

    fun getLocalizedString(@StringRes resource: Int): String {
        return this.appContext?.resources?.getString(resource) ?: ""
    }
}