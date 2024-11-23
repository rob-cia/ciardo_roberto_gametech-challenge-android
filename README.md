# ciardo_roberto_gametech-challenge-android

## Description
This plugin enables the scheduling and delivery of push notifications on Android devices. It provides a mechanism to schedule **5 notifications** at one-minute intervals, using `AlarmManager` and `BroadcastReceiver`.

---

## Features
### **Notification Scheduling**
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

### **Notification Receiver: On Receive**
- **Method**: `onReceive(Context context, Intent intent)`
- **Description**: Processes incoming alarm intents and displays the corresponding notification.
- **Details**:
  - Retrieves notification ID, title, description, and icon from the `Intent`.
  - Uses `NotificationManager` to send the notification.
  - Supports Android API 26+ using `NotificationChannel`, while retaining backward compatibility for lower APIs.

---

## Notification Example

- **Example Titles and Descriptions**:
  - Title: `"Notification 1"` - Description: `"Content of the notification 1"`
  - Title: `"Notification 2"` - Description: `"Content of the notification 2"`
- **Icons**:
  Utilizes system icons, e.g., `android.R.drawable.ic_menu_camera`.

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
