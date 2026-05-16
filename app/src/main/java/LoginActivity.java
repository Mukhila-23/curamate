package com.example.curamate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    EditText editPhone, editPassword;
    Button btnLogin, btnRegister;
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editPhone = findViewById(R.id.editphone);
        editPassword = findViewById(R.id.editpassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        btnRegister.setOnClickListener(v -> {
            String phone = editPhone.getText().toString();
            String password = editPassword.getText().toString();

            if (phone.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Enter both phone and password", Toast.LENGTH_SHORT).show();
                return;


            }

            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("phone", phone);
            editor.putString("password", password);
            editor.apply();
            // after prefs editor.apply() in Register
            startActivity(new Intent(this, AvatarSetupActivity.class));
            finish();


            Toast.makeText(this, "Registered successfully!", Toast.LENGTH_SHORT).show();
        });

        btnLogin.setOnClickListener(v -> {
            String phone = editPhone.getText().toString();
            String password = editPassword.getText().toString();

            String savedPhone = prefs.getString("phone", "");
            String savedPassword = prefs.getString("password", "");

            if (phone.equals(savedPhone) && password.equals(savedPassword)) {
                Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, MainActivity.class)); // your main app screen
                String avatarName = prefs.getString("avatarName", "");
                if (avatarName.isEmpty()) {
                    startActivity(new Intent(LoginActivity.this, AvatarSetupActivity.class));
                } else {
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                }
                finish();

            } else {
                Toast.makeText(this, "Invalid phone or password", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
