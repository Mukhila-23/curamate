package com.example.curamate;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private TextView quoteTextView;

    // Health quotes
    private final String[] quotes = {
            "💚 Health is the greatest wealth – cherish it daily!",
            "🏃‍♀️ A little progress each day adds up to big results.",
            "🌿 Take care of your body, it’s the only place you have to live.",
            "💧 Drink water like it’s magic – because it is.",
            "🧘‍♂️ Rest is just as important as hard work.",
            "🍎 Nourish to flourish – eat well, live well.",
            "💪 Strong body, stronger mind.",
            "🌞 Wellness is a journey, not a destination."
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Notification channel for Android O and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager = getSystemService(NotificationManager.class);

            NotificationChannel medicineChannel = new NotificationChannel(
                    "medicineChannel",
                    "Medicine Reminders",
                    NotificationManager.IMPORTANCE_HIGH
            );
            manager.createNotificationChannel(medicineChannel);

            NotificationChannel mealChannel = new NotificationChannel(
                    "mealChannel",
                    "Meal Reminders",
                    NotificationManager.IMPORTANCE_HIGH
            );
            mealChannel.setDescription("Notifications for meal reminders");
            manager.createNotificationChannel(mealChannel);

            NotificationChannel sleepChannel = new NotificationChannel(
                    "sleep_channel",
                    "Sleep Reminders",
                    NotificationManager.IMPORTANCE_HIGH
            );
            sleepChannel.setDescription("Notifications to remind you to sleep");
            manager.createNotificationChannel(sleepChannel);

            NotificationChannel exerciseChannel = new NotificationChannel(
                    "exercise_channel",               // Channel ID
                    "Exercise Reminders",             // Channel Name
                    NotificationManager.IMPORTANCE_HIGH // Importance level
            );
            exerciseChannel.setDescription("Notifications to remind you to exercise");
            manager.createNotificationChannel(exerciseChannel);

        }

        // Initialize UI elements
        ImageView avatarImageView = findViewById(R.id.avatarImageView);
        TextView chatBox = findViewById(R.id.chatBox);
        Button btnMedicine = findViewById(R.id.btnMedicine);
        Button btnFood = findViewById(R.id.btnFood);
        Button btnExercise = findViewById(R.id.btnExercise);
        Button btnSleep = findViewById(R.id.btnSleep);
        quoteTextView = findViewById(R.id.quoteTextView);

        // Load avatar from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        int avatarResId = prefs.getInt("avatarResId", R.drawable.avatar1);
        avatarImageView.setImageResource(avatarResId);

        // Show random quote
        Random random = new Random();
        String randomQuote = quotes[random.nextInt(quotes.length)];
        quoteTextView.setText(randomQuote);

        // Chat box click → ChatActivity
        chatBox.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, ChatActivity.class)));

        // Buttons navigation
        btnMedicine.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, MedicineActivity.class)));
        btnFood.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, FoodActivity.class)));
        btnExercise.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, ExerciseActivity.class)));
        btnSleep.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, SleepActivity.class)));
        Button btnLogout = findViewById(R.id.btnLogout);

        btnLogout.setOnClickListener(v -> {
            // Clear saved login details
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.apply();

            // Navigate back to LoginActivity
            Intent intent = new Intent(MainActivity.this, com.example.curamate.LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

    }
}
