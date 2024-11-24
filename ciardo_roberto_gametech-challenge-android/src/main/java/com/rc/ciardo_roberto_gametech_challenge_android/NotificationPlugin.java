package com.rc.ciardo_roberto_gametech_challenge_android;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;

public class NotificationPlugin {

    private static final String TAG = "NotificationPlugin";
    private static final int NOTIFICATION_ID_BASE = 1000;
    private static final int NOTIFICATION_COUNT = 5;
    private static final long INTERVAL_MS = 10 * 1000;
    private static final String CHANNEL_ID = "NotificationChannel";
    private static final String CHANNEL_NAME = "Scheduled Notifications";

    private static final String[] TITLES = {
            "Notification 1",
            "Notification 2",
            "Notification 3",
            "Notification 4",
            "Notification 5"
    };

    private static final String[] DESCRIPTIONS = {
            "Content of the notification 1",
            "Content of the notification 2",
            "Content of the notification 3",
            "Content of the notification 4",
            "Content of the notification 5"
    };

    private static final int[] ICONS = {
            android.R.drawable.ic_notification_overlay,
            android.R.drawable.ic_menu_camera,
            android.R.drawable.ic_menu_compass,
            android.R.drawable.ic_menu_help,
            android.R.drawable.ic_menu_info_details
    };

    public static void scheduleNotifications(Context context) {
        Log.d(TAG, "scheduleNotifications: Scheduling " + NOTIFICATION_COUNT + " notifications...");

        createNotificationChannel(context);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) {
            Log.e(TAG, "scheduleNotifications: AlarmManager is null!");
            return;
        }

        for (int i = 0; i < NOTIFICATION_COUNT; i++) {
            Intent intent = new Intent(context, NotificationReceiver.class);
            intent.putExtra("notificationId", NOTIFICATION_ID_BASE + i);
            intent.putExtra("title", TITLES[i]);
            intent.putExtra("description", DESCRIPTIONS[i]);
            intent.putExtra("icon", ICONS[i]);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context,
                    NOTIFICATION_ID_BASE + i,
                    intent,
                    PendingIntent.FLAG_IMMUTABLE // Android 12+
            );

            long triggerTime = SystemClock.elapsedRealtime() + ((i+1) * INTERVAL_MS);
            alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerTime, pendingIntent);

            Log.d(TAG, "scheduleNotifications: Scheduled notification " + (i + 1) + " at " + triggerTime);
        }
    }

    private static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d(TAG, "createNotificationChannel: Creating notification channel...");

            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("Channel for scheduled notifications");

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
                Log.d(TAG, "createNotificationChannel: Notification channel created.");
            } else {
                Log.e(TAG, "createNotificationChannel: NotificationManager is null!");
            }
        }
    }

    public static void removeNotifications(Context context) {
        Log.d(TAG, "removeNotifications: Cancelling all notifications...");

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) {
            Log.e(TAG, "removeNotifications: AlarmManager is null!");
            return;
        }

        for (int i = 0; i < NOTIFICATION_COUNT; i++) {
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context,
                    NOTIFICATION_ID_BASE + i,
                    new Intent(context, NotificationReceiver.class),
                    PendingIntent.FLAG_IMMUTABLE
            );

            alarmManager.cancel(pendingIntent);
            Log.d(TAG, "removeNotifications: Cancelled notification ID " + (NOTIFICATION_ID_BASE + i));
        }
    }
}
