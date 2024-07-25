package com.interview.fcmpushnotification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.messaging.remoteMessage
import java.util.Random


class MyFirebaseMessagingService: FirebaseMessagingService() {
    private val channelId =
        "all_notifications" // You should create a String resource for this instead of storing in a variable

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        // Check if message contains a data payload.
        message.notification?.let { notification ->
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            createNotificationChannel(notificationManager)
            Log.d(TAG, "Message data payload: ${message.data}")

            val notificationId: Int = Random().nextInt(60000)

            val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val notificationBuilder: NotificationCompat.Builder =
                NotificationCompat.Builder(this, channelId)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle(
                        notification.title
                    ) //the "title" value you sent in your notification
                    .setContentText(notification.body) //ditto
                    .setAutoCancel(true) //dismisses the notification on click
                    .setSound(defaultSoundUri)



            notificationManager.notify(
                notificationId /* ID of notification */,
                notificationBuilder.build()
            )
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    fun createNotificationChannel(notificationManager: NotificationManager) {

        val mChannel = NotificationChannel(
            channelId,
            "General Notifications",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        mChannel.description = "This is default channel used for all other notifications"

        notificationManager.createNotificationChannel(mChannel)
    }
}