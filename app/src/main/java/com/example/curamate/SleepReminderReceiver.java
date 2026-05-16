package com.example.curamate;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class SleepReminderReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String message = intent.getStringExtra("message");

        // Check notification permission for Android 13+
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission not granted, skip notification
            return;
        }

        try {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "sleep_channel")
                    .setSmallIcon(R.drawable.ic_sleep) // make sure you have an icon
                    .setContentTitle("Sleep Reminder")
                    .setContentText(message != null ? message : "Time to sleep!")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true);

            NotificationManagerCompat manager = NotificationManagerCompat.from(context);
            manager.notify((int) System.currentTimeMillis(), builder.build());
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }
}
