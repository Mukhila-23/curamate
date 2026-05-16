package com.example.curamate;


import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class WakeAlarmActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wake_alarm);

        Button btnStop = findViewById(R.id.btnStopAlarm);
        Button btnSnooze = findViewById(R.id.btnSnoozeAlarm);

        // Play alarm sound
        mediaPlayer = MediaPlayer.create(this, android.provider.Settings.System.DEFAULT_ALARM_ALERT_URI);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

        // Stop
        btnStop.setOnClickListener(v -> {
            stopAlarm();
            finish();
        });

        // Snooze (add 5 mins)
        btnSnooze.setOnClickListener(v -> {
            stopAlarm();
            snoozeAlarm();
            finish();
        });
    }

    private void stopAlarm() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void snoozeAlarm() {
        long snoozeTime = System.currentTimeMillis() + (5 * 60 * 1000); // 5 minutes
        android.app.AlarmManager alarmManager = (android.app.AlarmManager) getSystemService(ALARM_SERVICE);

        if (alarmManager != null) {
            try {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                    // Check if app is allowed to schedule exact alarms
                    if (alarmManager.canScheduleExactAlarms()) {
                        setExactSnooze(alarmManager, snoozeTime);
                    } else {
                        // Ask user to grant "exact alarm" permission
                        Intent intent = new Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                        intent.setData(android.net.Uri.parse("package:" + getPackageName()));
                        startActivity(intent);
                    }
                } else {
                    setExactSnooze(alarmManager, snoozeTime);
                }
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }
    }

    private void setExactSnooze(android.app.AlarmManager alarmManager, long snoozeTime) {
        Intent intent = new Intent(this, WakeAlarmReceiver.class);
        android.app.PendingIntent pendingIntent = android.app.PendingIntent.getBroadcast(
                this,
                101,
                intent,
                android.app.PendingIntent.FLAG_UPDATE_CURRENT | android.app.PendingIntent.FLAG_IMMUTABLE
        );

        alarmManager.setExact(android.app.AlarmManager.RTC_WAKEUP, snoozeTime, pendingIntent);
    }


    @Override
    protected void onDestroy() {
        stopAlarm();
        super.onDestroy();
    }
}

