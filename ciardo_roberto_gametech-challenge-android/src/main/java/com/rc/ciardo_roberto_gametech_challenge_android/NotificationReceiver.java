package com.rc.ciardo_roberto_gametech_challenge_android;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

public class NotificationReceiver extends BroadcastReceiver {
    private static final int NOTIFICATION_ID_BASE = 1000;
    private static final String CHANNEL_ID = "NotificationChannel";
    private static final String TAG_RECEIVER = "NotificationReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG_RECEIVER, "onReceive: Alarm triggered, preparing notification...");

        // Extract notification data from Intent
        int notificationId = intent.getIntExtra("notificationId", NOTIFICATION_ID_BASE);
        String title = intent.getStringExtra("title");
        String description = intent.getStringExtra("description");
        int icon = intent.getIntExtra("icon", android.R.drawable.ic_notification_overlay);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager == null) {
            Log.e(TAG_RECEIVER, "onReceive: NotificationManager is null!");
            return;
        }

        Notification notification;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // For API >= 26, use NotificationChannel
            notification = new Notification.Builder(context, CHANNEL_ID)
                    .setContentTitle(title)
                    .setContentText(description)
                    .setSmallIcon(icon)
                    .build();
        } else {
            // For API < 26, use the old constructor
            notification = new Notification.Builder(context)
                    .setContentTitle(title)
                    .setContentText(description)
                    .setSmallIcon(icon)
                    .setPriority(Notification.PRIORITY_DEFAULT)
                    .build();
        }

        // Send notification
        notificationManager.notify(notificationId, notification);

        Log.d(TAG_RECEIVER, "onReceive: Notification sent successfully. ID: " + notificationId);
    }
}
