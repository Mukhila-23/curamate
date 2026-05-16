package com.example.curamate;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class CustomiseExerciseActivity extends AppCompatActivity {

    private LinearLayout inputLayout;
    private Button btnAddRow, btnSave, btnClear;
    private ExerciseDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customise_exercise);

        inputLayout = findViewById(R.id.inputLayout);
        btnAddRow = findViewById(R.id.btnAddRow);
        btnSave = findViewById(R.id.btnSave);
        btnClear = findViewById(R.id.btnClear);

        dbHelper = new ExerciseDatabaseHelper(this);

        // Load existing exercises
        loadExistingExercises();

        btnAddRow.setOnClickListener(v -> addRow());

        btnClear.setOnClickListener(v -> {
            inputLayout.removeAllViews();
            dbHelper.resetExercises(); // Clear database
            addRow(); // Add one empty row for new input
        });

        btnSave.setOnClickListener(v -> {
            saveExercises(); // Save user-added exercises
            finish();
        });
    }

    private void addRow() {
        EditText et = new EditText(this);
        et.setHint("Enter exercise (e.g., 20 Jumping Jacks)");
        inputLayout.addView(et);
    }

    private void saveExercises() {
        dbHelper.resetExercises(); // Clear old exercises

        for (int i = 0; i < inputLayout.getChildCount(); i++) {
            EditText et = (EditText) inputLayout.getChildAt(i);
            String text = et.getText().toString().trim();
            if (!text.isEmpty()) {
                dbHelper.addExercise(text);
            }
        }
    }

    private void loadExistingExercises() {
        List<String> exercises = dbHelper.getAllExercises();

        if (exercises.isEmpty()) {
            addRow(); // Empty row if nothing exists
        } else {
            for (String exercise : exercises) {
                EditText et = new EditText(this);
                et.setText(exercise);
                inputLayout.addView(et);
            }
        }
    }
}
