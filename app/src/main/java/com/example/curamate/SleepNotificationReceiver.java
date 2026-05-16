package com.example.curamate;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class SleepNotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        // Check if POST_NOTIFICATIONS permission is granted
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, skip sending the notification
            return;
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "sleep_channel")
                .setSmallIcon(R.drawable.ic_sleep)
                .setContentTitle("Sleep Reminder")
                .setContentText("It's time to go to sleep 😴")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManagerCompat manager = NotificationManagerCompat.from(context);
        manager.notify(200, builder.build());
    }
}
