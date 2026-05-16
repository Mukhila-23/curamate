package com.example.curamate;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import java.util.Calendar;

public class ExerciseNotificationHelper {

    private static final String TAG = "ExerciseNotification";

    public static void scheduleExerciseReminder(Context context, String exerciseName, int hour, int minute) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, ExerciseReminderReceiver.class);
        intent.putExtra("exerciseName", exerciseName);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                (exerciseName + hour + minute).hashCode(), // unique ID
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Set reminder time
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, 0);

        // If time already passed today → schedule for tomorrow
        if (cal.getTimeInMillis() < System.currentTimeMillis()) {
            cal.add(Calendar.DAY_OF_YEAR, 1);
        }

        if (alarmManager != null) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    // On Android 12+ → must check permission
                    if (alarmManager.canScheduleExactAlarms()) {
                        alarmManager.setExactAndAllowWhileIdle(
                                AlarmManager.RTC_WAKEUP,
                                cal.getTimeInMillis(),
                                pendingIntent
                        );
                        Log.d(TAG, "Exercise reminder scheduled (API 31+): " + exerciseName + " at " + hour + ":" + minute);
                    } else {
                        // Ask user to allow exact alarms
                        Intent settingsIntent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                        settingsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(settingsIntent);

                        Log.w(TAG, "Exact alarm permission not granted. Requested user to allow it in settings.");
                    }
                } else {
                    // For Android < 12 → no permission needed
                    alarmManager.setExactAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            cal.getTimeInMillis(),
                            pendingIntent
                    );
                    Log.d(TAG, "Exercise reminder scheduled (API < 31): " + exerciseName + " at " + hour + ":" + minute);
                }
            } catch (SecurityException e) {
                Log.e(TAG, "SecurityException: Exact alarm not allowed", e);
            }
        }
    }
}
