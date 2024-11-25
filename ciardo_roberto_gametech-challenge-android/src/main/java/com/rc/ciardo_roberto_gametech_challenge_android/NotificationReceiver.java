package com.rc.ciardo_roberto_gametech_challenge_android;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

public class NotificationReceiver extends BroadcastReceiver {
    private static final int NOTIFICATION_ID_BASE = 1000;
    private static final String CHANNEL_ID = "NotificationChannel";
    private static final String TAG_RECEIVER = "NotificationReceiver";

    private static final Map<Integer, Integer> CUSTOM_ICONS = new HashMap<Integer, Integer>() {{
        put(0, R.drawable.icon1);
        put(1, R.drawable.icon2);
        put(2, R.drawable.icon3);
        put(3, R.drawable.icon4);
        put(4, R.drawable.icon5);
    }};

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG_RECEIVER, "onReceive: Alarm triggered, preparing notification...");

        // Extract notification data from Intent
        int notificationId = intent.getIntExtra("notificationId", NOTIFICATION_ID_BASE);
        String title = intent.getStringExtra("title");
        String description = intent.getStringExtra("description");
        int icon = intent.getIntExtra("icon", 0);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager == null) {
            Log.e(TAG_RECEIVER, "onReceive: NotificationManager is null!");
            return;
        }

        Log.d(TAG_RECEIVER, "onReceive: icon... " + icon);

        Notification notification;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // For API >= 26, use NotificationChannel
            notification = new Notification.Builder(context, CHANNEL_ID)
                    .setContentTitle(title)
                    .setContentText(description)
                    .setSmallIcon(CUSTOM_ICONS.get(icon))
                    .setContentIntent(createContentIntent(context, notificationId, title, description, icon))
                    .setAutoCancel(true)
                    .build();
        } else {
            // For API < 26, use the old constructor
            notification = new Notification.Builder(context)
                    .setContentTitle(title)
                    .setContentText(description)
                    .setSmallIcon(CUSTOM_ICONS.get(icon))
                    .setContentIntent(createContentIntent(context, notificationId, title, description, icon))
                    .setPriority(Notification.PRIORITY_DEFAULT)
                    .setAutoCancel(true)
                    .build();
        }

        // Send notification
        notificationManager.notify(notificationId, notification);

        Log.d(TAG_RECEIVER, "onReceive: Notification sent successfully. ID: " + notificationId);
    }

    private PendingIntent createContentIntent(Context context, int notificationId, String title, String description, int icon) {
        Intent intent = new Intent();

        intent.setComponent(new ComponentName("com.Miniclip.ciardo_roberto_gametechchallengeunity", "com.unity3d.player.UnityPlayerActivity"));
        intent.putExtra("notificationId", String.valueOf(notificationId));
        intent.putExtra("title", title);
        intent.putExtra("description", description);
        intent.putExtra("icon", String.valueOf(icon));

        return PendingIntent.getActivity(
                context,
                notificationId,
                intent,
                PendingIntent.FLAG_IMMUTABLE
        );
    }
}
