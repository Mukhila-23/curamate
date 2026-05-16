package com.example.curamate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AvatarSetupActivity extends AppCompatActivity {

    ImageButton img1, img2, img3;
    EditText editAvatarName, editUserName;
    Button btnSave;
    SharedPreferences prefs;
    int selectedAvatarResId = -1;
    ImageButton[] avatarButtons;

    // CheckBoxes
    CheckBox cbTime24, cbBackground, cbBattery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_avatar_setup);

        img1 = findViewById(R.id.imgAvatar1);
        img2 = findViewById(R.id.imgAvatar2);
        img3 = findViewById(R.id.imgAvatar3);

        editAvatarName = findViewById(R.id.editAvatarName);
        editUserName = findViewById(R.id.editUserName);
        btnSave = findViewById(R.id.btnSaveAvatar);

        cbTime24 = findViewById(R.id.cbTime24);
        cbBackground = findViewById(R.id.cbBackground);
        cbBattery = findViewById(R.id.cbBattery);

        prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        avatarButtons = new ImageButton[]{img1, img2, img3};

        // default selection
        selectAvatar(R.drawable.avatar1, 0);

        img1.setOnClickListener(v -> selectAvatar(R.drawable.avatar1, 0));
        img2.setOnClickListener(v -> selectAvatar(R.drawable.avatar2, 1));
        img3.setOnClickListener(v -> selectAvatar(R.drawable.avatar3, 2));

        btnSave.setOnClickListener(v -> {
            // Check all checkboxes
            if (!cbTime24.isChecked() || !cbBackground.isChecked() || !cbBattery.isChecked()) {
                Toast.makeText(AvatarSetupActivity.this,
                        "Please complete all instructions before saving", Toast.LENGTH_SHORT).show();
                return;
            }

            String avatarName = editAvatarName.getText().toString().trim();
            String userName = editUserName.getText().toString().trim();

            if (avatarName.isEmpty() || userName.isEmpty()) {
                Toast.makeText(AvatarSetupActivity.this, "Please fill both names", Toast.LENGTH_SHORT).show();
                return;
            }

            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("avatarName", avatarName);
            editor.putString("userName", userName);
            editor.putInt("avatarResId", selectedAvatarResId);
            editor.apply();

            Toast.makeText(AvatarSetupActivity.this, "Saved!", Toast.LENGTH_SHORT).show();

            startActivity(new Intent(AvatarSetupActivity.this, MainActivity.class));
            finish();
        });
    }

    private void selectAvatar(int resId, int index) {
        selectedAvatarResId = resId;
        // visually indicate selection by changing alpha
        for (int i = 0; i < avatarButtons.length; i++) {
            avatarButtons[i].setAlpha(i == index ? 1f : 0.5f);
        }
    }
}
