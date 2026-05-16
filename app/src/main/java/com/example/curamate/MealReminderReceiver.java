package com.example.curamate;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class MealReminderReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String mealWithMenu = intent.getStringExtra("mealWithMenu");

        if (mealWithMenu == null) {
            Log.d("MealReminder", "No meal info found in intent!");
            return;
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "mealChannel")
                .setSmallIcon(R.drawable.ic_food)
                .setContentTitle("Meal Reminder 🍽️")
                .setContentText(mealWithMenu)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(mealWithMenu)) // Show full text
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManagerCompat manager = NotificationManagerCompat.from(context);
        manager.notify(mealWithMenu.hashCode(), builder.build());
    }
}
