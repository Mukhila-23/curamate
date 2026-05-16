package com.example.curamate;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class MedicineActivity extends AppCompatActivity {

    private TextView txtPrescription;
    private MedicineDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine);

        txtPrescription = findViewById(R.id.txtPrescription);
        Button btnDelete = findViewById(R.id.btnDeletePrescription);
        Button btnAdd = findViewById(R.id.btnAddMedicine);

        dbHelper = new MedicineDbHelper(this);

        loadPrescription();

        btnDelete.setOnClickListener(v -> {
            dbHelper.deleteAllMedicines();
            txtPrescription.setText("No prescription yet.");
            // TODO: Optionally cancel all alarms here
        });

        btnAdd.setOnClickListener(v -> startActivity(new Intent(this, AddMedicineActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPrescription();
    }

    private void loadPrescription() {
        List<String> medicines = dbHelper.getAllMedicines();
        if (medicines.isEmpty()) {
            txtPrescription.setText("No prescription yet.");
        } else {
            StringBuilder builder = new StringBuilder();
            for (String med : medicines) {
                builder.append(med).append("\n");
            }
            txtPrescription.setText(builder.toString());
        }
    }
}
