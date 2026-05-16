package com.example.curamate;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class MealActivity extends AppCompatActivity {

    private MealDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal);

        dbHelper = new MealDbHelper(this);

        String mealType = getIntent().getStringExtra("mealType");

        TextView txtMealTitle = findViewById(R.id.txtMealTitle);
        TextView txtMenu = findViewById(R.id.txtMenu);
        Button btnCustomise = findViewById(R.id.btnCustomise);

        txtMealTitle.setText(mealType);

        Calendar cal = Calendar.getInstance();
        String[] days = {"sun","mon","tue","wed","thu","fri","sat"};
        String today = days[cal.get(Calendar.DAY_OF_WEEK) - 1];

        // Fetch menu from SQLite
        String menu = dbHelper.getMenu(mealType, today);
        txtMenu.setText(menu);

        btnCustomise.setOnClickListener(v -> {
            Intent i = new Intent(this, CustomiseMealActivity.class);
            i.putExtra("mealType", mealType);
            startActivity(i);
        });
    }
}
