package com.example.curamate;

import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import java.util.Calendar;
import java.util.List;

public class BootReceiver extends BroadcastReceiver {

    private static final String TAG = "BootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Log.d(TAG, "Device rebooted, rescheduling all reminders...");

            // ✅ Check exact alarm permission for Android 12+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                if (alarmManager != null && !alarmManager.canScheduleExactAlarms()) {
                    Log.w(TAG, "Exact alarms not allowed. Asking user to grant permission.");
                    Intent settingsIntent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                    settingsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(settingsIntent);
                    return; // Stop here until user grants permission
                }
            }

            // -------- Reschedule medicine alarms --------
            try {
                MedicineDbHelper medDbHelper = new MedicineDbHelper(context);
                List<MedicineModel> meds = medDbHelper.getAllMedicineModels();

                for (MedicineModel med : meds) {
                    for (String time : med.times) {
                        try {
                            String[] hm = time.split(":");
                            int hour = Integer.parseInt(hm[0]);
                            int minute = Integer.parseInt(hm[1]);

                            AddMedicineActivity.scheduleAlarm(
                                    context,
                                    med.name,
                                    hour,
                                    minute,
                                    med.days
                            );

                            Log.d(TAG, "Rescheduled medicine: " + med.name + " at " + time + " for " + med.days);
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing medicine time: " + time + " for " + med.name, e);
                        }
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error rescheduling medicine alarms", e);
            }

            // -------- Reschedule meal alarms --------
            try {
                MealDbHelper mealDbHelper = new MealDbHelper(context);
                Cursor cursor = mealDbHelper.getAllMealTimes();

                Calendar cal = Calendar.getInstance();
                String[] days = {"sun", "mon", "tue", "wed", "thu", "fri", "sat"};
                String today = days[cal.get(Calendar.DAY_OF_WEEK) - 1];

                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        try {
                            String mealName = cursor.getString(cursor.getColumnIndexOrThrow("meal"));
                            int hour = cursor.getInt(cursor.getColumnIndexOrThrow("hour"));
                            int minute = cursor.getInt(cursor.getColumnIndexOrThrow("minute"));

                            // Fetch today's menu
                            String menu = mealDbHelper.getMenu(mealName, today);

                            MealNotificationHelper.scheduleMealReminder(context, mealName + ": " + menu, hour, minute);

                            Log.d(TAG, "Rescheduled meal: " + mealName + " at " + hour + ":" + minute + " Menu: " + menu);

                        } catch (Exception e) {
                            Log.e(TAG, "Error rescheduling meal", e);
                        }
                    }
                    cursor.close();
                }
            } catch (Exception e) {
                Log.e(TAG, "Error handling meal rescheduling", e);
            }

            // -------- Reschedule exercise reminders --------
            try {
                ExerciseDatabaseHelper exerciseDbHelper = new ExerciseDatabaseHelper(context);
                Cursor exCursor = exerciseDbHelper.getAllExerciseTimes();

                if (exCursor != null) {
                    while (exCursor.moveToNext()) {
                        try {
                            String exerciseName = exCursor.getString(exCursor.getColumnIndexOrThrow("exercise"));
                            int hour = exCursor.getInt(exCursor.getColumnIndexOrThrow("hour"));
                            int minute = exCursor.getInt(exCursor.getColumnIndexOrThrow("minute"));

                            ExerciseNotificationHelper.scheduleExerciseReminder(context, exerciseName, hour, minute);

                            Log.d(TAG, "Rescheduled exercise: " + exerciseName + " at " + hour + ":" + minute);

                        } catch (Exception e) {
                            Log.e(TAG, "Error rescheduling exercise", e);
                        }
                    }
                    exCursor.close();
                }
            } catch (Exception e) {
                Log.e(TAG, "Error handling exercise rescheduling", e);
            }

            // -------- Reschedule sleep reminders --------
            try {
                SleepDatabaseHelper sleepDbHelper = new SleepDatabaseHelper(context);
                Cursor sleepCursor = sleepDbHelper.getReadableDatabase().query(
                        "sleepHistory",
                        null,
                        null,
                        null,
                        null,
                        null,
                        null
                );

                if (sleepCursor != null) {
                    while (sleepCursor.moveToNext()) {
                        try {
                            String date = sleepCursor.getString(sleepCursor.getColumnIndexOrThrow("date"));
                            String duration = sleepCursor.getString(sleepCursor.getColumnIndexOrThrow("duration"));

                            // Default sleep reminder time → 10 PM
                            int hour = 22;
                            int minute = 0;

                            SleepNotificationHelper.scheduleSleepReminder(
                                    context,
                                    "Sleep Reminder: " + duration,
                                    hour,
                                    minute
                            );

                            Log.d(TAG, "Rescheduled sleep reminder at " + hour + ":" + minute +
                                    " (" + date + " - " + duration + ")");

                        } catch (Exception e) {
                            Log.e(TAG, "Error rescheduling sleep reminder", e);
                        }
                    }
                    sleepCursor.close();
                }
            } catch (Exception e) {
                Log.e(TAG, "Error handling sleep rescheduling", e);
            }
        }
    }
}
