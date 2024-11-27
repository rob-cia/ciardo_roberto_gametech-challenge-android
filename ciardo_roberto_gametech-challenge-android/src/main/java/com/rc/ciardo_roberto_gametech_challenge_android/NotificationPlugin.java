package com.rc.ciardo_roberto_gametech_challenge_android;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

public class NotificationPlugin {

    private static final String TAG = "NotificationPlugin";
    private static final int NOTIFICATION_ID_BASE = 1000;
    private static final int NOTIFICATION_COUNT = 5;
    private static final long INTERVAL_MS = 60 * 1000;
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

    // unity-list-scheduled-notification
    private static final String PREFS_NAME = "NotificationPrefs";
    private static final String SCHEDULED_NOTIFICATIONS_KEY = "ScheduledNotifications";


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
            intent.putExtra("icon", i);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context,
                    NOTIFICATION_ID_BASE + i,
                    intent,
                    PendingIntent.FLAG_IMMUTABLE // Android 12+
            );

            // unity-list-scheduled-notification: use the currentTimeMillis to schedule notification
            long triggerTimeU = System.currentTimeMillis() + ((i+1) * INTERVAL_MS);
            //long triggerTime = SystemClock.elapsedRealtime() + ((i+1) * INTERVAL_MS);

            // unity-list-scheduled-notification: allowed to execute even when the system is in low-power idle modes
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTimeU, pendingIntent);
            //alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerTime, pendingIntent);

            Log.d(TAG, "scheduleNotifications: Scheduled notification " + (i + 1) + " at " + triggerTimeU);

            // unity-list-scheduled-notification: save the scheduled notification in the SharedPreferences
            saveScheduledNotification(context, NOTIFICATION_ID_BASE + i, TITLES[i], DESCRIPTIONS[i], i, triggerTimeU, "running", i+1);
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
            /*PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context,
                    NOTIFICATION_ID_BASE + i,
                    new Intent(context, NotificationReceiver.class),
                    PendingIntent.FLAG_IMMUTABLE
            );

            alarmManager.cancel(pendingIntent);*/
            Log.d(TAG, "removeNotifications: Cancelled notification ID " + (NOTIFICATION_ID_BASE + i));

            saveRemoveNotifications(context, NOTIFICATION_ID_BASE + i, "cancelled");
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d(TAG, "removeNotifications: Deleting notification channel...");
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                notificationManager.deleteNotificationChannel(CHANNEL_ID);
                Log.d(TAG, "removeNotifications: Notification channel deleted.");
            } else {
                Log.e(TAG, "removeNotifications: NotificationManager is null!");
            }
        }
    }


    // unity-list-scheduled-notification : called by Unity when the app is opened
    private static void onStartup(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        String scheduledNotifications = sharedPreferences.getString(SCHEDULED_NOTIFICATIONS_KEY, "");

        if (!scheduledNotifications.isEmpty()) {
            String[] notifications = scheduledNotifications.split(";");

            StringBuilder updatedNotifications = new StringBuilder();

            Log.d(TAG, "startup: Searching notification to update..");
            for (String notification : notifications) {
                String[] parts = notification.split(":");

                long triggerTime = Long.parseLong(parts[4]);

                if (triggerTime <= System.currentTimeMillis()) {
                    parts[5] = "cancelled";
                    notification = String.join(":", parts);
                }

                if (updatedNotifications.length() > 0) {
                    updatedNotifications.append(";");
                }
                updatedNotifications.append(notification);
            }

            // Update notification list
            scheduledNotifications = updatedNotifications.toString();
        }

        // Save the new scheduled notification list
        editor.putString(SCHEDULED_NOTIFICATIONS_KEY, scheduledNotifications);
        editor.apply();
    }

    private static void saveScheduledNotification(Context context, int notificationId, String title, String description, int iconId, long triggerTime, String status, int order) {
        Log.d(TAG, "Preparing to save schedule notification..");

        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        String scheduledNotifications = sharedPreferences.getString(SCHEDULED_NOTIFICATIONS_KEY, "");

        if (!scheduledNotifications.isEmpty()) {
            String[] notifications = scheduledNotifications.split(";");
            boolean notificationFound = false;

            StringBuilder updatedNotifications = new StringBuilder();

            Log.d(TAG, "Searching notification to update..");
            for (String notification : notifications) {
                String[] parts = notification.split(":");
                try {
                    int storedNotificationId = Integer.parseInt(parts[0]);

                    if (storedNotificationId == notificationId) {
                        Log.d(TAG, "Notification " + notificationId + " already present, update starting");
                        parts[1] = title;
                        parts[2] = description;
                        parts[3] = String.valueOf(iconId);
                        parts[4] = String.valueOf(triggerTime);
                        parts[5] = status;
                        parts[6] = String.valueOf(order);
                        notification = String.join(":", parts);
                        notificationFound = true;
                    }
                } catch (Exception e) {
                    Log.e("NotificationManager", "Format Error with: " + notification, e);
                }


                if (updatedNotifications.length() > 0) {
                    updatedNotifications.append(";");
                }
                updatedNotifications.append(notification);
            }

            if (!notificationFound) {
                if (updatedNotifications.length() > 0) {
                    updatedNotifications.append(";");
                }
                Log.d(TAG, "Add notification " + notificationId + " - len (" + updatedNotifications.length() + ")");
                updatedNotifications.append(notificationId + ":" + title + ":" + description + ":" + iconId + ":" + triggerTime + ":" + status + ":" + order);
            }

            // Update notification list
            scheduledNotifications = updatedNotifications.toString();
        } else {
            Log.d(TAG, "Add first notification " + notificationId);
            scheduledNotifications = notificationId + ":" + title + ":" + description + ":" + iconId + ":" + triggerTime + ":" + status + ":" + order;
        }

        // Save the new scheduled notification list
        editor.putString(SCHEDULED_NOTIFICATIONS_KEY, scheduledNotifications);
        editor.apply();
    }

    public static void saveRemoveNotifications(Context context, int notificationId, String status) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        String scheduledNotifications = sharedPreferences.getString(SCHEDULED_NOTIFICATIONS_KEY, "");

        if (!scheduledNotifications.isEmpty()) {
            String[] notifications = scheduledNotifications.split(";");
            StringBuilder updatedNotifications = new StringBuilder();

            for (String notification : notifications) {
                String[] parts = notification.split(":");
                int storedNotificationId = Integer.parseInt(parts[0]);

                if (storedNotificationId == notificationId) {
                    parts[5] = status;
                    notification = String.join(":", parts);
                }

                if (updatedNotifications.length() > 0) {
                    updatedNotifications.append(";");
                }
                updatedNotifications.append(notification);
            }

            editor.putString(SCHEDULED_NOTIFICATIONS_KEY, updatedNotifications.toString());
            editor.apply();
        }
    }

    public static String[] getScheduledNotifications(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String scheduledNotifications = sharedPreferences.getString(SCHEDULED_NOTIFICATIONS_KEY, "");

        if (scheduledNotifications.isEmpty()) {
            return new String[0];
        }

        return scheduledNotifications.split(";");
    }

    // android-remove-scheduled-notification
    public static void removeNotificationById(Context context, int notificationId) {
        Log.d(TAG, "removeNotificationById: Attempting to remove notification with ID " + notificationId);

        updateOrderOnRemoveNotification(context, notificationId);
        updateScheduledNotifications(context);
    }

    private static void updateOrderOnRemoveNotification(Context context, int notificationId) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        String scheduledNotifications = sharedPreferences.getString(SCHEDULED_NOTIFICATIONS_KEY, "");

        if (scheduledNotifications.isEmpty()) {
            Log.e("NotificationManager", "No scheduled notifications found.");
            return;
        }


        String[] notifications = scheduledNotifications.split(";");

        StringBuilder updatedNotifications = new StringBuilder();

        boolean startDecrement = false;
        for (String notification : notifications) {

            String[] parts = notification.split(":");

            if (Integer.parseInt(parts[0]) == notificationId) {
                startDecrement = true;
                parts[5] = "cancelled";//parts[6] = String.valueOf(5);
            } else {
                if (startDecrement) {
                    parts[4] = String.valueOf(Long.parseLong(parts[4]) - INTERVAL_MS);
                    parts[6] = String.valueOf(Integer.parseInt(parts[6]) - 1);
                }
            }

            String updatedNotification = String.join(":", parts);

            if (updatedNotifications.length() > 0) {
                updatedNotifications.append(";");
            }
            updatedNotifications.append(updatedNotification);

        }

        editor.putString(SCHEDULED_NOTIFICATIONS_KEY, updatedNotifications.toString());
        editor.apply();
    }

    private static void updateScheduledNotifications(Context context) {
        Log.d(TAG, "updateScheduledNotifications: Scheduling " + NOTIFICATION_COUNT + " notifications...");

        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String scheduledNotifications = sharedPreferences.getString(SCHEDULED_NOTIFICATIONS_KEY, "");

        if (scheduledNotifications.isEmpty()) {
            Log.e("NotificationPlugin", "No scheduled notifications found.");
            return;
        }

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) {
            Log.e(TAG, "updateScheduledNotifications: AlarmManager is null!");
            return;
        }

        String[] notifications = scheduledNotifications.split(";");

        for (String notification : notifications) {

            String[] parts = notification.split(":");

            Intent intent = new Intent(context, NotificationReceiver.class);
            intent.putExtra("notificationId", parts[0]);
            intent.putExtra("title", parts[1]);
            intent.putExtra("description", parts[2]);
            intent.putExtra("icon", parts[3]);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context,
                    Integer.parseInt(parts[0]),
                    intent,
                    PendingIntent.FLAG_IMMUTABLE
            );

            if (parts[5].equals("running")) {
                long triggerTimeU = Long.parseLong(parts[4]);
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTimeU, pendingIntent);

                Log.d(TAG, "updateScheduledNotifications: Scheduled notification " + (Integer.parseInt(parts[6])) + " at " + triggerTimeU);

            } else {
                alarmManager.cancel(pendingIntent);
                Log.d(TAG, "updateScheduledNotifications: Canceled notification " + (Integer.parseInt(parts[6])));
            }
        }

    }
}
