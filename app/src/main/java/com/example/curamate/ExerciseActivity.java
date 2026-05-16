package com.example.curamate;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.List;

public class ExerciseActivity extends AppCompatActivity {

    private ProgressBar exerciseProgress;
    private TextView avatarMessage, reminderText;
    private LinearLayout exerciseListLayout;
    private Button btnCustomise, btnSetTime;

    private int totalExercises = 0;
    private int completedExercises = 0;
    private ExerciseDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);

        exerciseProgress = findViewById(R.id.exerciseProgress);
        avatarMessage = findViewById(R.id.avatarMessage);
        exerciseListLayout = findViewById(R.id.exerciseListLayout);
        btnCustomise = findViewById(R.id.btnCustomiseExercise);
        reminderText = findViewById(R.id.reminderText);
        btnSetTime = findViewById(R.id.btnSetTime);

        dbHelper = new ExerciseDatabaseHelper(this);

        btnCustomise.setOnClickListener(v -> {
            Intent i = new Intent(ExerciseActivity.this, CustomiseExerciseActivity.class);
            startActivity(i);
        });

        // Load saved reminder time
        SharedPreferences prefs = getSharedPreferences("ExercisePrefs", MODE_PRIVATE);
        int savedHour = prefs.getInt("reminder_hour", -1);
        int savedMinute = prefs.getInt("reminder_minute", -1);
        if (savedHour != -1 && savedMinute != -1) {
            String timeText = String.format("Remind me at: %02d:%02d", savedHour, savedMinute);
            reminderText.setText(timeText);
        }

        // Time Picker to set reminder
        btnSetTime.setOnClickListener(v -> openTimePicker());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadExercises(); // Reload exercises from database
    }

    private void loadExercises() {
        List<String> exercises = dbHelper.getAllExercises();

        exerciseListLayout.removeAllViews();
        totalExercises = exercises.size();
        completedExercises = 0;

        for (String ex : exercises) {
            CheckBox chk = new CheckBox(this);
            chk.setText(ex);
            chk.setTextSize(16);
            chk.setOnCheckedChangeListener((buttonView, isChecked) -> updateProgress());
            exerciseListLayout.addView(chk);
        }

        updateProgress();
    }

    private void updateProgress() {
        completedExercises = 0;

        for (int i = 0; i < exerciseListLayout.getChildCount(); i++) {
            CheckBox chk = (CheckBox) exerciseListLayout.getChildAt(i);
            if (chk.isChecked()) {
                completedExercises++;
            }
        }

        int progress = (totalExercises == 0) ? 0 : (completedExercises * 100) / totalExercises;
        exerciseProgress.setProgress(progress);

        if (completedExercises == 0) {
            avatarMessage.setText("Let’s start with the first exercise 💪");
        } else if (completedExercises < totalExercises) {
            avatarMessage.setText("Great! Keep going, you’re doing awesome 🚀");
        } else {
            avatarMessage.setText("🎉 Congratulations! You completed today’s workout! 🎉");
        }
    }

    private void openTimePicker() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePicker = new TimePickerDialog(
                this,
                (view, selectedHour, selectedMinute) -> setExerciseReminder(selectedHour, selectedMinute),
                hour, minute, true
        );
        timePicker.show();
    }

    private void setExerciseReminder(int hour, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DAY_OF_MONTH, 1); // Next day if time passed
        }

        Intent intent = new Intent(this, ExerciseReminderReceiver.class);
        intent.putExtra("exerciseName", "Daily Exercise Reminder 🏃‍♀️");

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                1001,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        if (alarmManager != null) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    if (alarmManager.canScheduleExactAlarms()) {
                        alarmManager.setRepeating(
                                AlarmManager.RTC_WAKEUP,
                                calendar.getTimeInMillis(),
                                AlarmManager.INTERVAL_DAY,
                                pendingIntent
                        );
                    } else {
                        Intent settingsIntent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                        settingsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(settingsIntent);
                    }
                } else {
                    alarmManager.setRepeating(
                            AlarmManager.RTC_WAKEUP,
                            calendar.getTimeInMillis(),
                            AlarmManager.INTERVAL_DAY,
                            pendingIntent
                    );
                }
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }

        // Save reminder time
        getSharedPreferences("ExercisePrefs", MODE_PRIVATE)
                .edit()
                .putInt("reminder_hour", hour)
                .putInt("reminder_minute", minute)
                .apply();

        // Update UI
        String timeText = String.format("Remind me at: %02d:%02d", hour, minute);
        reminderText.setText(timeText);
    }
}
