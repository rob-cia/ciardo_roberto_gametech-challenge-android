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

___

### *TAG: Android-Remove-Notifications*

#### **Remove Notifications**
- **Method**: `removeNotifications(Context context)`
- **Description**: Cancels all scheduled notifications.
- **Details**:
  - Utilizes `AlarmManager` to cancel pending alarms.
  - Cancels notifications based on their unique IDs (`NOTIFICATION_ID_BASE` and increments).
  - Ensures clean removal of all pending intents to avoid unintended notifications.

___

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

___

### *TAG: Android-List-Scheduled-Notifications*

#### **On Startup**
- **Method**: `onStartup(Context context)`
- **Description**: 
  Updates the state of scheduled notifications when the Unity app is opened or change application focus.
- **Details**:
  - Retrieves the notification list from `SharedPreferences` using the key `SCHEDULED_NOTIFICATIONS_KEY`.
  - Marks notifications with expired `triggerTime` as `"cancelled"`.
  - Preserves the state of notifications still valid.
  - Saves the updated notification list back to `SharedPreferences` for consistency.

___
 
### *TAG: Unity-Save-Scheduled-Notification*

#### **Save Scheduled Notification**
- **Method**: `saveScheduledNotification(Context context, int notificationId, String title, String description, int iconId, long triggerTime, String status)`
- **Description**: 
  Saves or updates a scheduled notification in `SharedPreferences`.
- **Parameters**:
  - `Context context`: The application context for accessing shared preferences.
  - `int notificationId`: The unique identifier for the notification.
  - `String title`: The title of the notification.
  - `String description`: The description of the notification.
  - `int iconId`: The ID of the notification icon.
  - `long triggerTime`: The time at which the notification is scheduled to trigger.
  - `String status`: The current status of the notification (e.g., `"running"` or `"cancelled"`).
- **Details**:
  - Retrieves the current list of scheduled notifications from `SharedPreferences` using the key `SCHEDULED_NOTIFICATIONS_KEY`.
  - If the notification already exists, updates its details (title, description, icon, trigger time, and status).
  - If the notification is new, appends it to the list of scheduled notifications.
  - Saves the updated list back into `SharedPreferences` for persistence.
  - Ensures proper formatting of the notification data, logging any errors encountered during parsing.

#### **Save Remove Notifications**
- **Method**: `saveRemoveNotifications(Context context, int notificationId, String status)`
- **Description**: 
  Updates the status of a specific notification in `SharedPreferences` to mark it as removed.
- **Parameters**:
  - `Context context`: The application context for accessing shared preferences.
  - `int notificationId`: The unique identifier for the notification to update.
  - `String status`: The new status of the notification (e.g., `"cancelled"`).
- **Details**:
  - Retrieves the list of scheduled notifications from `SharedPreferences` using the key `SCHEDULED_NOTIFICATIONS_KEY`.
  - Iterates through the notification list to find the notification with the matching `notificationId`.
  - Updates the `status` field of the matched notification.
  - Reconstructs the notification list with the updated status and saves it back into `SharedPreferences`.

#### **Check If App Is Running**
- **Method**: `isAppInForeground(Context context, String packageName)`
- **Description**: 
  Determines if a specific app, identified by its package name, is currently running in the foreground.
- **Parameters**:
  - `Context context`: The application context for accessing system services.
  - `String packageName`: The package name of the app to check.
- **Returns**: 
  - `boolean`: `true` if the app is running in the foreground, `false` otherwise.

#### **FIX: Notification Scheduling with RTC_WAKEUP and Current Time**
- **Details**:
  - Adjusted the scheduling logic to use `System.currentTimeMillis()` instead of `SystemClock.elapsedRealtime()` for determining the `triggerTime`.
    - **Previous Implementation**: Used `SystemClock.elapsedRealtime()` with `AlarmManager.ELAPSED_REALTIME_WAKEUP`.
    - **Updated Implementation**: Uses `System.currentTimeMillis()` with `AlarmManager.RTC_WAKEUP`.
  - Ensures the notification is scheduled based on real-world time rather than device uptime.
  - Enabled the `setExactAndAllowWhileIdle` method to ensure notifications are triggered even during low-power idle modes, enhancing reliability on modern Android devices.
 
#### **Fix: Avoid AlarmManager Cancellation**
- **Details**:
  - Commented out the logic to cancel scheduled notifications using `AlarmManager.cancel(PendingIntent)` for improved compatibility and reliability.
    - **Previous Implementation**: Used `PendingIntent.getBroadcast()` and `AlarmManager.cancel()` to remove scheduled alarms for notifications.
    - **Current Fix**: Instead of directly cancelling alarms, the system updates the notification status using the `saveRemoveNotifications` method, marking them as `"cancelled"` in the `SharedPreferences`.

___

### *TAG: Android-Remove-Scheduled-Notification*

#### **Remove Notification by ID**
- **Method**: `removeNotificationById(Context context, int notificationId)`
- **Description**: Removes a specific notification by its ID and updates the schedule of the remaining notifications.
- **Details**:
  - **Calls**:
    - `updateOrderOnRemoveNotification`: Adjusts the order and timing of notifications after marking the target notification as **cancelled**.
    - `updateScheduledNotifications`: Refreshes the system alarms for the updated notification list.

#### **Update Order on Remove Notification**
- **Method**: `updateOrderOnRemoveNotification(Context context, int notificationId)`
- **Description**: Updates the internal order and timing of notifications after removing one.
- **Details**:
  - Retrieves the current notification list from `SharedPreferences`.
  - Marks the notification matching the given ID as **cancelled**.
  - For subsequent notifications:
    - Decreases their `triggerTime` by the interval (`INTERVAL_MS`).
    - Adjusts their position index (`order`).
  - Saves the updated list back to `SharedPreferences`.
  - **Called By**: `removeNotificationById`.
 
#### **Update Scheduled Notifications**
- **Method**: `updateScheduledNotifications(Context context)`
- **Description**: Reschedules all active notifications after an update.
- **Details**:
  - Fetches the updated notification list from `SharedPreferences`.
  - For each notification:
    - **Active (`running`)**: Schedules the notification with `AlarmManager.setExactAndAllowWhileIdle`.
    - **Cancelled**: Cancels the pending alarm via `AlarmManager.cancel`.
  - **Called By**: `removeNotificationById`.

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
