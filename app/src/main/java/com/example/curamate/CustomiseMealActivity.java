package com.example.curamate;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class CustomiseMealActivity extends AppCompatActivity {

    EditText edtMon, edtTue, edtWed, edtThu, edtFri, edtSat, edtSun;
    MealDbHelper dbHelper;
    String mealType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customise_meal);

        mealType = getIntent().getStringExtra("mealType");
        dbHelper = new MealDbHelper(this);

        TextView title = findViewById(R.id.txtCustomiseTitle);
        edtMon = findViewById(R.id.edtMon);
        edtTue = findViewById(R.id.edtTue);
        edtWed = findViewById(R.id.edtWed);
        edtThu = findViewById(R.id.edtThu);
        edtFri = findViewById(R.id.edtFri);
        edtSat = findViewById(R.id.edtSat);
        edtSun = findViewById(R.id.edtSun);
        Button btnSave = findViewById(R.id.btnSaveMenu);

        title.setText("Customise " + mealType + " Menu");

        // Load existing values from DB
        edtMon.setText(dbHelper.getMenu(mealType, "mon"));
        edtTue.setText(dbHelper.getMenu(mealType, "tue"));
        edtWed.setText(dbHelper.getMenu(mealType, "wed"));
        edtThu.setText(dbHelper.getMenu(mealType, "thu"));
        edtFri.setText(dbHelper.getMenu(mealType, "fri"));
        edtSat.setText(dbHelper.getMenu(mealType, "sat"));
        edtSun.setText(dbHelper.getMenu(mealType, "sun"));

        btnSave.setOnClickListener(v -> {
            dbHelper.saveMenu(mealType, "mon", edtMon.getText().toString());
            dbHelper.saveMenu(mealType, "tue", edtTue.getText().toString());
            dbHelper.saveMenu(mealType, "wed", edtWed.getText().toString());
            dbHelper.saveMenu(mealType, "thu", edtThu.getText().toString());
            dbHelper.saveMenu(mealType, "fri", edtFri.getText().toString());
            dbHelper.saveMenu(mealType, "sat", edtSat.getText().toString());
            dbHelper.saveMenu(mealType, "sun", edtSun.getText().toString());
            finish();
        });
    }
}
