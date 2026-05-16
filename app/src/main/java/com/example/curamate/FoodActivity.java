package com.example.curamate;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class FoodActivity extends AppCompatActivity {

    private TextView txtBreakfastTime, txtBrunchTime, txtLunchTime, txtSnackTime, txtDinnerTime;
    private MealDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food);

        dbHelper = new MealDbHelper(this);

        // Buttons
        Button btnBreakfast = findViewById(R.id.btnBreakfast);
        Button btnBrunch = findViewById(R.id.btnBrunch);
        Button btnLunch = findViewById(R.id.btnLunch);
        Button btnSnack = findViewById(R.id.btnSnack);
        Button btnDinner = findViewById(R.id.btnDinner);

        Button btnBreakfastSet = findViewById(R.id.btnBreakfastSetTime);
        Button btnBrunchSet = findViewById(R.id.btnBrunchSetTime);
        Button btnLunchSet = findViewById(R.id.btnLunchSetTime);
        Button btnSnackSet = findViewById(R.id.btnSnackSetTime);
        Button btnDinnerSet = findViewById(R.id.btnDinnerSetTime);

        Button btnBreakfastCustom = findViewById(R.id.btnBreakfastCustomize);
        Button btnBrunchCustom = findViewById(R.id.btnBrunchCustomize);
        Button btnLunchCustom = findViewById(R.id.btnLunchCustomize);
        Button btnSnackCustom = findViewById(R.id.btnSnackCustomize);
        Button btnDinnerCustom = findViewById(R.id.btnDinnerCustomize);

        // TextViews
        txtBreakfastTime = findViewById(R.id.txtBreakfastTime);
        txtBrunchTime = findViewById(R.id.txtBrunchTime);
        txtLunchTime = findViewById(R.id.txtLunchTime);
        txtSnackTime = findViewById(R.id.txtSnackTime);
        txtDinnerTime = findViewById(R.id.txtDinnerTime);

        // View Menu
        btnBreakfast.setOnClickListener(v -> openMeal("Breakfast"));
        btnBrunch.setOnClickListener(v -> openMeal("Brunch"));
        btnLunch.setOnClickListener(v -> openMeal("Lunch"));
        btnSnack.setOnClickListener(v -> openMeal("Snack"));
        btnDinner.setOnClickListener(v -> openMeal("Dinner"));

        // Set Time
        btnBreakfastSet.setOnClickListener(v -> pickTime("Breakfast"));
        btnBrunchSet.setOnClickListener(v -> pickTime("Brunch"));
        btnLunchSet.setOnClickListener(v -> pickTime("Lunch"));
        btnSnackSet.setOnClickListener(v -> pickTime("Snack"));
        btnDinnerSet.setOnClickListener(v -> pickTime("Dinner"));

        // Customise Menu
        btnBreakfastCustom.setOnClickListener(v -> openCustomise("Breakfast"));
        btnBrunchCustom.setOnClickListener(v -> openCustomise("Brunch"));
        btnLunchCustom.setOnClickListener(v -> openCustomise("Lunch"));
        btnSnackCustom.setOnClickListener(v -> openCustomise("Snack"));
        btnDinnerCustom.setOnClickListener(v -> openCustomise("Dinner"));

        // Load saved times
        loadSavedTimes();
    }

    private void openMeal(String mealType) {
        Intent i = new Intent(this, MealActivity.class);
        i.putExtra("mealType", mealType);
        startActivity(i);
    }

    private void openCustomise(String mealType) {
        Intent i = new Intent(this, CustomiseMealActivity.class);
        i.putExtra("mealType", mealType);
        startActivity(i);
    }

    private void pickTime(String mealType) {
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        TimePickerDialog timePicker = new TimePickerDialog(this, (TimePicker view, int hourOfDay, int minuteOfHour) -> {
            // Save time to SQLite
            dbHelper.saveMealTime(mealType, hourOfDay, minuteOfHour);

            // Schedule notification
            MealNotificationHelper.scheduleMealReminder(this, mealType, hourOfDay, minuteOfHour);

            // Update UI
            updateTimeLabel(mealType, hourOfDay, minuteOfHour);
        }, hour, minute, true);

        timePicker.show();
    }

    private void loadSavedTimes() {
        String[] meals = {"Breakfast", "Brunch", "Lunch", "Snack", "Dinner"};
        for (String meal : meals) {
            int[] time = dbHelper.getMealTime(meal);
            updateTimeLabel(meal, time[0], time[1]);
        }
    }

    private void updateTimeLabel(String mealType, int hour, int minute) {
        String timeText = (hour == -1) ? "Time: Not set"
                : String.format("Time: %02d:%02d", hour, minute);

        switch (mealType) {
            case "Breakfast": txtBreakfastTime.setText(timeText); break;
            case "Brunch": txtBrunchTime.setText(timeText); break;
            case "Lunch": txtLunchTime.setText(timeText); break;
            case "Snack": txtSnackTime.setText(timeText); break;
            case "Dinner": txtDinnerTime.setText(timeText); break;
        }
    }
}
