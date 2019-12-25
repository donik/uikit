package kz.citicom.sampleuikit.notifications

import android.app.NotificationManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat

open class NotificationGroup(
    val id: Long,
    val photo: Bitmap? = null,
    val isSilent: Boolean = false,
    val priority: Priority = Priority.DEFAULT
) {
    val items = arrayListOf<NotificationItem>()
    val count: Int
        get() = items.size
    val title: String
        get() = "GROUP TITLE"
    val lastNotificationObject: NotificationItem?
        get() = items.maxBy { it.date }
    val date: Long
        get() {
            return this.lastNotificationObject?.date ?: return 0L
        }
    val soundUri: Uri?
        get() {
            return if (isSilent) {
                null
            } else {
                Settings.System.DEFAULT_NOTIFICATION_URI
            }
        }
    val vibrationPattern: LongArray
        get() {
            return if (isSilent) {
                longArrayOf(0, 0)
            } else {
                longArrayOf(0, 400, 0, 400)
            }
        }

    fun addItem(item: NotificationItem) {
        items.add(item)
        items.sortBy { it.date }
    }

    open class NotificationItem(
        val groupID: Long,
        val title: String,
        val message: String,
        val date: Long,
        val photo: Bitmap? = null,
        val isSilent: Boolean = false,
        val priority: Priority = Priority.DEFAULT
    )

    enum class Priority(val value: Int) {
        DEFAULT(0),
        HIGHT(1),
        MIN(2),
        LOW(3);

        @RequiresApi(Build.VERSION_CODES.N)
        fun getImportance(): Int {
            return when (this) {
                DEFAULT -> NotificationManager.IMPORTANCE_DEFAULT
                HIGHT -> NotificationManager.IMPORTANCE_HIGH
                MIN -> NotificationManager.IMPORTANCE_MIN
                LOW -> NotificationManager.IMPORTANCE_LOW
            }
        }

        fun getPriority(): Int {
            return when (this) {
                DEFAULT -> NotificationCompat.PRIORITY_DEFAULT
                HIGHT -> NotificationCompat.PRIORITY_HIGH
                MIN -> NotificationCompat.PRIORITY_MIN
                LOW -> NotificationCompat.PRIORITY_LOW
            }
        }
    }

}