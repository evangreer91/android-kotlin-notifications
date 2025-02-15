/*
 * Copyright (C) 2019 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.eggtimernotifications.util

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.core.app.NotificationCompat
import com.example.android.eggtimernotifications.MainActivity
import com.example.android.eggtimernotifications.R
import com.example.android.eggtimernotifications.receiver.SnoozeReceiver

// Notification ID.
private val NOTIFICATION_ID = 0
private val REQUEST_CODE = 0
private val FLAGS = 0

/**
 * Builds and delivers the notification.
 *
 * @param context, activity context.
 */

//extension function to send notifications
fun NotificationManager.sendNotification(messageBody: String, applicationContext: Context) {
    // Create the content intent for the notification, which launches
    // this activity

    // create intent with application context and activity to be launched
    val contentIntent = Intent(applicationContext, MainActivity::class.java)

    // create a pending intent with application context, notification id, content intent, and pending intent flag
    // pending intent flag specifies the option to create a new pending intent or use an existing one
    // set PendingIntent.FLAG_UPDATE_CURRENT as flag since you want to update an existing one
    val contentPendingIntent = PendingIntent.getActivity(
        applicationContext,
        NOTIFICATION_ID,
        contentIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    // load image from resources using the BitmapFactory
    val eggImage = BitmapFactory.decodeResource(
        applicationContext.resources,
        R.drawable.cooked_egg
    )

    // create a new big picture style and set image
    val bigPicStyle = NotificationCompat.BigPictureStyle()
        .bigPicture(eggImage)
        .bigLargeIcon(null)

    // create a snooze intent and pending intent
    // pass in application context, request code for this pending intent, snooze intent, and appropriate flag
    val snoozeIntent = Intent(applicationContext, SnoozeReceiver::class.java)
    val snoozePendingIntent: PendingIntent = PendingIntent.getBroadcast(
        applicationContext,
        REQUEST_CODE,
        snoozeIntent,
        PendingIntent.FLAG_ONE_SHOT
    )

    // get an instance of the NotificationCompat builder, pass in app context and channel id
    // channel id is a string value from string resources which uses the matching channel
    // starting from API level 26, all notifications must be assigned to a channel
    val builder = NotificationCompat.Builder(
        applicationContext,
        applicationContext.getString(R.string.egg_notification_channel_id)
    )

    // set title, text and icon to builder
        .setSmallIcon(R.drawable.cooked_egg)
        .setContentTitle(applicationContext.getString(R.string.notification_title))
        .setContentText(messageBody)

    // pass pending intent to your notification
    // now when you click notification, the pending intent will be triggered opening up main activity
    // setting auto cancel to true, notification dismisses itself on click
        .setContentIntent(contentPendingIntent)
        .setAutoCancel(true)

    // set the style to notificationBuilder by calling setStyle
    // set image using setLargeIcon
        .setStyle(bigPicStyle)
        .setLargeIcon(eggImage)

    // call add action
    // pass in icon, text to describe your action, and the pending intent
    // the pending intent will be used to trigger the right broadcast receiver
        .addAction(
            R.drawable.egg_icon,
            applicationContext.getString(R.string.snooze),
            snoozePendingIntent
        )

    // to support API level 25 or lower, set priority to high using setPriority method
        .setPriority(NotificationCompat.PRIORITY_HIGH)

    // call notify with a unique id for your notification and with the notification object from your builder
    // we can use the same id for all notifications since there will be only one active notification
    notify(NOTIFICATION_ID, builder.build())
}

// create an extension function that cancels all notifications
fun NotificationManager.cancelNotifications() {
    cancelAll()
}
