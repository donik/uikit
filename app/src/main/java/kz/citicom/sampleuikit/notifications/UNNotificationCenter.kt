package kz.citicom.sampleuikit.notifications

import android.annotation.TargetApi
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.AudioAttributes
import android.media.AudioManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.Person
import androidx.core.graphics.drawable.IconCompat
import kz.citicom.sampleuikit.R
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom

class UNNotificationCenter(
    private var context: Context,
    private val channelName: String
) {
    private val notificationManager = NotificationManagerCompat.from(context)
    private val systemNotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private val notificationID = 1
    private val notificationGroup = "messages_notification_group"

    private val notificationGroups = arrayListOf<NotificationGroup>()
    private val notificationItems: List<NotificationGroup.NotificationItem>
        get() {
            return this.notificationGroups.map { it.items }.flatten()
        }
    private val unreadCount: Int
        get() {
            val unread = notificationGroups.map { it.count }
            return unread.sum()
        }
    private var OTHER_NOTIFICATIONS_CHANNEL: String? = null

    init {
        checkOtherNotificationsChannel()
    }

    private fun checkOtherNotificationsChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return
        }

        val preferences: SharedPreferences =
            context.getSharedPreferences("Notifications", Activity.MODE_PRIVATE)
        if (this.OTHER_NOTIFICATIONS_CHANNEL == null) {
            this.OTHER_NOTIFICATIONS_CHANNEL = preferences?.getString("OtherKey", "Other3")
        }

        var notificationChannel =
            this.systemNotificationManager.getNotificationChannel(OTHER_NOTIFICATIONS_CHANNEL)
        if (notificationChannel != null && notificationChannel.importance == NotificationManager.IMPORTANCE_NONE) {
            systemNotificationManager.deleteNotificationChannel(OTHER_NOTIFICATIONS_CHANNEL)
            OTHER_NOTIFICATIONS_CHANNEL = null
            notificationChannel = null
        }

        if (OTHER_NOTIFICATIONS_CHANNEL == null) {
            OTHER_NOTIFICATIONS_CHANNEL = "Other" + random.nextLong()
            preferences.edit().putString("OtherKey", OTHER_NOTIFICATIONS_CHANNEL).commit()
        }

        if (notificationChannel == null) {
            notificationChannel = NotificationChannel(
                OTHER_NOTIFICATIONS_CHANNEL,
                "Other",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationChannel.enableLights(false)
            notificationChannel.enableVibration(false)
            notificationChannel.setSound(null, null)
            systemNotificationManager.createNotificationChannel(notificationChannel)
        }
    }

    private fun useSummaryNotification(messagesSize: Int): Boolean {
        return Build.VERSION.SDK_INT <= Build.VERSION_CODES.O_MR1 ||
                Build.VERSION.SDK_INT > Build.VERSION_CODES.O_MR1 &&
                messagesSize > 1
    }

    fun showNotifications(vararg items: NotificationGroup.NotificationItem) {
        for (item in items) {
            var group = this.notificationGroups.find {
                it.id == item.groupID
            }
            if (group != null) {
                group.addItem(item)
            } else {
                group = NotificationGroup(item.groupID, item.photo, item.isSilent, item.priority)
                group.addItem(item)
                this.notificationGroups.add(group)
            }
        }

        this.notificationGroups.sortBy { it.date }
        this.showNotification(*items)
    }

    private fun showNotification(vararg pushNotificationItems: NotificationGroup.NotificationItem) {
        val group = this.notificationGroups.firstOrNull() ?: return
        var name = "chat name"
        var detailText: String = "test detail text"

//        if (this.notificationGroups.count() > 1) {
//            detailText = ""
//        } else {
//            detailText = ""
//        }


        val contentIntent = PendingIntent.getActivity(
            context,
            0,
            Intent(),
            PendingIntent.FLAG_ONE_SHOT
        ) //todo content intent

        val mBuilder = NotificationCompat.Builder(context)
            .setContentTitle(group.title)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setAutoCancel(true)
            .setNumber(this.unreadCount)
            .setContentIntent(contentIntent)
            .setGroup(this.notificationGroup)
            .setGroupSummary(true)
            .setShowWhen(true)
            .setWhen(group.date)
            .setColor(-0xee5306) //todo set color


        mBuilder.setCategory(NotificationCompat.CATEGORY_MESSAGE)
        mBuilder.priority = group.priority.getPriority()
        mBuilder.setVibrate(group.vibrationPattern)
        mBuilder.setSound(
            group.soundUri,
            AudioManager.STREAM_NOTIFICATION
        )

        Log.e("pushNotificationItems", pushNotificationItems.size.toString())
        Log.e("this.notificationGroups", this.notificationGroups.size.toString())

        if (pushNotificationItems.count() == 1) {
            val notificationItem = pushNotificationItems[0]
            mBuilder.setContentText(notificationItem.message)
            mBuilder.setStyle(NotificationCompat.BigTextStyle().bigText(notificationItem.message))
        } else {
            mBuilder.setContentText(detailText)
            val inboxStyle = NotificationCompat.InboxStyle()
            inboxStyle.setBigContentTitle(name)
            val count = Math.min(10, pushNotificationItems.count())
            for (i in 0 until count) {
                inboxStyle.addLine(pushNotificationItems[i].message)
            }
            inboxStyle.setSummaryText(detailText)
            mBuilder.setStyle(inboxStyle)
        }

        group.photo?.let {
            mBuilder.setLargeIcon(it)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mBuilder.setChannelId(
                validateChannelId(
                    channelName,
                    group.vibrationPattern,
                    -0xffff01,
                    group.soundUri,
                    group.priority.getImportance()
                )
            )
        }

        showExtraNotifications(mBuilder, detailText)
    }

    private fun showExtraNotifications(
        notificationBuilder: NotificationCompat.Builder,
        summary: String
    ) {
        val mainNotification = notificationBuilder.build()
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            this.notificationManager.notify(this.notificationID, mainNotification)
            return
        }

        val holders = arrayListOf<NotificationHolder>()

        val useSummaryNotification = this.useSummaryNotification(this.notificationItems.size)
        if (useSummaryNotification && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            checkOtherNotificationsChannel()
        }

        for (group in this.notificationGroups) {
            val messagingStyle = NotificationCompat.MessagingStyle("")
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
                messagingStyle.conversationTitle = ""//todo
            }
            messagingStyle.isGroupConversation = Build.VERSION.SDK_INT <= Build.VERSION_CODES.P

            val name = group.title
            val text = StringBuilder()
            val senderName = group.title
            val contentIntent = PendingIntent.getActivity(
                context,
                0,
                Intent(),
                PendingIntent.FLAG_ONE_SHOT
            ) //todo content intent

            for (notificationItem in group.items) {
                text.append(notificationItem.message)
                text.append("\n")

                val personBuilder = Person.Builder().setName(notificationItem.title)
                notificationItem.photo?.let {
                    personBuilder.setIcon(IconCompat.createWithBitmap(it))
                }

                messagingStyle.addMessage(
                    notificationItem.message,
                    notificationItem.date,
                    personBuilder.build()
                )
            }

            val builder = NotificationCompat.Builder(context)
                .setContentTitle(name)
                .setSmallIcon(R.drawable.ic_launcher_background) //todo
                .setContentText(text.toString())
                .setAutoCancel(true)
                .setNumber(group.count)
                .setColor(-0xee5306) //todo set color
                .setGroupSummary(false)
                .setWhen(group.date)
                .setShowWhen(true)
                .setShortcutId("sdid_${group.id}")
                .setStyle(messagingStyle)
                .setContentIntent(contentIntent)
                .setSortKey("${Long.MAX_VALUE - group.date}")
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)

            if (useSummaryNotification) {
                builder.setGroup(this.notificationGroup)
                builder.setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_SUMMARY)
            }

            if (this.notificationGroups.size == 1 && summary.isNotEmpty()) {
                builder.setSubText(summary)
            }

            group.photo?.let {
                builder.setLargeIcon(it)
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = OTHER_NOTIFICATIONS_CHANNEL
                if (useSummaryNotification && channel != null) {
                    builder.setChannelId(channel)
                } else {
                    builder.setChannelId(mainNotification.channelId)
                }
            }
            holders.add(NotificationHolder(group.id.toInt(), builder.build()))
        }

        if (useSummaryNotification) {
            this.notificationManager.notify(this.notificationID, mainNotification)
        } else {
            this.notificationManager.cancel(this.notificationID)
        }

        for (holder in holders) {
            holder.call(this.notificationManager)
        }

        //todo remove viewed notifications
        Log.e("SHOW", "SHOW NOTIFICATIONS $useSummaryNotification")
    }

    @TargetApi(26)
    private fun validateChannelId(
        name: String,
        vibrationPattern: LongArray,
        ledColor: Int,
        sound: Uri?,
        importance: Int
    ): String {
        val preferences = context.getSharedPreferences("Notifications", Activity.MODE_PRIVATE)
        val key = "kz.citicom.arnapp.$name"
        var channelID = preferences.getString(key, null)
        val settings = preferences.getString("${key}_s", null)
        val newSettings = StringBuilder()
        var newSettingsHash: String

        for (pattern in vibrationPattern) {
            newSettings.append(pattern)
        }
        newSettings.append(ledColor)
        sound?.let {
            newSettings.append(it)
        }
        newSettings.append(importance)

        newSettingsHash = MD5(newSettings.toString()) ?: newSettings.toString()
        if (channelID != null && settings != newSettingsHash) {
            this.systemNotificationManager.deleteNotificationChannel(channelID)
            channelID = null
        }

        if (channelID == null) {
            channelID = "channel_${name}_${random.nextLong()}"
            val notificationChannel = NotificationChannel(channelID, name, importance)
            if (ledColor != 0) {
                notificationChannel.enableLights(true)
                notificationChannel.lightColor = ledColor
            }
            if (!isEmptyVibration(vibrationPattern)) {
                notificationChannel.enableVibration(true)
                if (vibrationPattern.count() > 0) {
                    notificationChannel.vibrationPattern = vibrationPattern
                }
            } else {
                notificationChannel.enableVibration(false)
            }
            val builder = AudioAttributes.Builder()
            builder.setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            builder.setUsage(AudioAttributes.USAGE_NOTIFICATION)
            if (sound != null) {
                notificationChannel.setSound(sound, builder.build())
            } else {
                notificationChannel.setSound(null, builder.build())
            }
            systemNotificationManager.createNotificationChannel(notificationChannel)
            preferences.edit()
                .putString(key, channelID)
                .putString("${key}_s", newSettingsHash)
                .commit()
        }

        return channelID
    }

    private fun isEmptyVibration(pattern: LongArray): Boolean {
        if (pattern.isEmpty()) {
            return false
        }
        for (a in pattern.indices) {
            if (pattern[a] != 0L) {
                return false
            }
        }
        return true
    }

    //todo move to utils

    private var random: SecureRandom = SecureRandom()

    private fun MD5(md5: String?): String? {
        if (md5 == null) {
            return null
        }
        try {
            val md = MessageDigest.getInstance("MD5")
            val array = md.digest(getStringBytes(md5))
            val sb = java.lang.StringBuilder()
            for (a in array.indices) {
                sb.append(
                    Integer.toHexString(array[a].toInt() and 0xFF or 0x100).substring(
                        1,
                        3
                    )
                )
            }
            return sb.toString()
        } catch (e: NoSuchAlgorithmException) {
        }
        return null
    }

    private fun getStringBytes(src: String): ByteArray? {
        try {
            return src.toByteArray(charset("UTF-8"))
        } catch (ignore: Exception) {
        }
        return ByteArray(0)
    }
}