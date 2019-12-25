package kz.citicom.sampleuikit.notifications

import android.app.Notification
import androidx.core.app.NotificationManagerCompat

class NotificationHolder(val id: Int, val notification: Notification) {
    fun call(notificationManager: NotificationManagerCompat) {
        notificationManager.notify(id, notification)
    }
}