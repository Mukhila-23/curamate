package com.example.curamate;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.app.TimePickerDialog;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class SleepActivity extends AppCompatActivity {

    private TextView txtHistory;
    private SleepDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep);

        Button btnSetSleep = findViewById(R.id.btnSetSleepTime);
        Button btnSetWake = findViewById(R.id.btnSetWakeTime);
        txtHistory = findViewById(R.id.txtHistory);

        dbHelper = new SleepDatabaseHelper(this);

        // Sleep reminder → custom time picker
        btnSetSleep.setOnClickListener(v -> pickTime(true));

        // Wake alarm → custom time picker (instead of system alarm app)
        btnSetWake.setOnClickListener(v -> pickTime(false));

        // Load history
        loadHistory();
    }

    // Show time picker for sleep or wake
    private void pickTime(boolean isSleepTime) {
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        TimePickerDialog tpd = new TimePickerDialog(this, (TimePicker view, int h, int m) -> {
            if (isSleepTime) {
                // Schedule sleep reminder
                scheduleSleepNotification(h, m);
                dbHelper.addSleepRecord("Sleep Time", String.format(Locale.getDefault(), "%02d:%02d", h, m));
                Toast.makeText(this, "Daily Sleep Reminder Set at " + h + ":" + m, Toast.LENGTH_SHORT).show();
            } else {
                // Schedule wake alarm
                scheduleWakeAlarm(h, m);
                dbHelper.addSleepRecord("Wake Time", String.format(Locale.getDefault(), "%02d:%02d", h, m));
                Toast.makeText(this, "Wake Alarm Set at " + h + ":" + m, Toast.LENGTH_SHORT).show();
            }
            loadHistory();
        }, hour, minute, true);

        tpd.show();
    }

    // Schedule daily sleep reminder notification
    private void scheduleSleepNotification(int hour, int minute) {
        android.app.AlarmManager alarmManager = (android.app.AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(this, SleepNotificationReceiver.class);
        android.app.PendingIntent pendingIntent = android.app.PendingIntent.getBroadcast(
                this,
                (hour * 100 + minute),
                intent,
                android.app.PendingIntent.FLAG_UPDATE_CURRENT | android.app.PendingIntent.FLAG_IMMUTABLE
        );

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DATE, 1);
        }

        alarmManager.setRepeating(
                android.app.AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                android.app.AlarmManager.INTERVAL_DAY,
                pendingIntent
        );
    }

    // Schedule a real wake alarm (with stop/snooze UI)
    private void scheduleWakeAlarm(int hour, int minute) {
        android.app.AlarmManager alarmManager = (android.app.AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(this, WakeAlarmReceiver.class);
        android.app.PendingIntent pendingIntent = android.app.PendingIntent.getBroadcast(
                this,
                101,
                intent,
                android.app.PendingIntent.FLAG_UPDATE_CURRENT | android.app.PendingIntent.FLAG_IMMUTABLE
        );

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DATE, 1);
        }

        alarmManager.setExact(android.app.AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    // Load history
    private void loadHistory() {
        ArrayList<String> history = dbHelper.getSleepHistory();

        if (history.isEmpty()) {
            txtHistory.setText("Sleep/Wake History:\nNo records yet.");
        } else {
            StringBuilder sb = new StringBuilder("Sleep/Wake History:\n");
            for (String entry : history) {
                sb.append(entry).append("\n");
            }
            txtHistory.setText(sb.toString());
        }
    }
}
