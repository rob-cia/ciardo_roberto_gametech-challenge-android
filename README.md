# ciardo_roberto_gametech-challenge-android

## Description
This plugin enables the scheduling and delivery of push notifications on Android devices. It provides a mechanism to schedule **5 notifications** at one-minute intervals, using `AlarmManager` and `BroadcastReceiver`.

---

## Features

### *TAG: Android-Schedule-Notifications*

#### **Notification Scheduling**
- **Method**: `scheduleNotifications(Context context)`
- **Description**: 
  Schedules 5 notifications to be delivered at one-minute intervals.
- **Parameters**:
  - `Context context`: The application context for accessing system services.
- **Details**:
  - Uses `AlarmManager` to schedule notifications.
  - Each notification includes:
    - A unique ID.
    - Title, description, and icon data.
  - Notifications are sent using a `PendingIntent` and handled by `NotificationReceiver`.

#### **Notification Receiver: On Receive**
- **Method**: `onReceive(Context context, Intent intent)`
- **Description**: Processes incoming alarm intents and displays the corresponding notification.
- **Details**:
  - Retrieves notification ID, title, description, and icon from the `Intent`.
  - Uses `NotificationManager` to send the notification.
  - Supports Android API 26+ using `NotificationChannel`, while retaining backward compatibility for lower APIs.

### *TAG: Android-Remove-Notifications*

#### **Remove Notifications**
- **Method**: `removeNotifications(Context context)`
- **Description**: Cancels all scheduled notifications.
- **Details**:
  - Utilizes `AlarmManager` to cancel pending alarms.
  - Cancels notifications based on their unique IDs (`NOTIFICATION_ID_BASE` and increments).
  - Ensures clean removal of all pending intents to avoid unintended notifications.

### *TAG: Android-Touched-Notification-Data*

#### **Notification Receiver: On Receive**
- **Method**: `onReceive(Context context, Intent intent)`
- **Description**: Processes incoming alarm intents and displays the corresponding notification.
- **Details**:
  - `Notification.Builder` use `.setContentIntent()` to set a message for the unity app created by `PendingIntent createContentIntent(Context context, int notificationId, String title, String description, int icon)`.
  - Use `.setAutoCancel(true)` to delete the notification touched.

#### **Create Content Intent**
- **Method**: `PendingIntent createContentIntent(Context context, int notificationId, String title, String description, int icon)`
- **Description**: Create content intent to send title, description, and icon information in Unity app.
- **Details**:
  - Using `intent.setComponent(new ComponentName("com.Miniclip.ciardo_roberto_gametechchallengeunity", "com.unity3d.player.UnityPlayerActivity"))` to manage application opening when tapping notification.
 
#### **Remove Notifications**
- **Method**: `removeNotifications(Context context)`
- **Description**: Cancels all scheduled notifications.
- **Details**:
  - If the Android version is 8.0 (API level 26) or higher, it deletes the notification channel by calling `deleteNotificationChannel(CHANNEL_ID)` on the `NotificationManager`.
  - Logs the deletion of the notification channel or an error if `NotificationManager` is null.
 
#### **FIND BUG**:

#### *MISSING*: **Create Notification Channel**
- **Method**: `createNotificationChannel(Context context)`
- **Description**: Creates a notification channel for devices running Android 8.0 (API level 26) or higher.
- **Parameters**:
  - `Context context`: The application context for accessing system services.
- **Details**:
  - Checks if the Android version is 8.0 (API level 26) or higher before creating the notification channel.
  - The channel is created with a unique `CHANNEL_ID` and `CHANNEL_NAME`.
  - Sets a description for the channel: `"Channel for scheduled notifications"`.
  - Utilizes `NotificationManager` to create the channel.
  - If `NotificationManager` is null, logs an error message.
  - Ensures that the channel is properly created to handle notifications on compatible devices.


---

## Notification Example

- **Example Titles and Descriptions**:
  - Title: `"Notification 1"` - Description: `"Content of the notification 1"`
  - Title: `"Notification 2"` - Description: `"Content of the notification 2"`
- **Icons**:
  Utilizes system icons, e.g., `android.R.drawable.ic_menu_camera`.

### *TAG: Android-Touched-Notification-Data*
- **Icons**:
  Utilizes custom icons, e.g., `R.drawable.icon1`, located in `ciardo_roberto_gametech-challenge-android\src\main\res\drawable\icon1.png`

---

## Requirements
- **Minimum Android Version**: 7.0 (API Level 24)
- **Required Permissions**:
  - `android.permission.SCHEDULE_EXACT_ALARM`
  - `android.permission.SET_ALARM`
- **Custom AndroidManifest**:
  - Defines `BroadcastReceiver` for handling intents:
    - `android:enabled="true"`
    - `android:exported="false"`

---
