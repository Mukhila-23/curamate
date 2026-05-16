package com.example.curamate;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ChatActivity extends AppCompatActivity {

    private TextView chatBox;
    private EditText userInput;
    private Button sendBtn;
    private String avatarName = "CuraMate";
    private String userName = "Friend";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatBox = findViewById(R.id.chatBox);
        userInput = findViewById(R.id.userInput);
        sendBtn = findViewById(R.id.sendBtn);

        chatBox.setMovementMethod(new ScrollingMovementMethod());

        // Load saved names from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        avatarName = prefs.getString("avatarName", "CuraMate");
        userName = prefs.getString("userName", "Friend");

        chatBox.setText(avatarName + ": Hello, " + userName + "! How can I help you today?");

        // Handle send button
        sendBtn.setOnClickListener(v -> {
            String message = userInput.getText().toString().trim();
            if (!message.isEmpty()) {
                appendChat("You: " + message);
                appendChat(avatarName + ": " + getReply(message));
                userInput.setText("");
            }
        });
    }

    private void appendChat(String text) {
        chatBox.append("\n" + text);
    }

    // Avatar’s simple AI replies
    private String getReply(String msg) {
        String m = msg.toLowerCase();
        if (m.contains("hello") || m.contains("hi")) return "Hi " + userName + "! How are you?";
        if (m.contains("joke")) return "Why did the phone go to school? Because it wanted to be a smart phone! 📱😂";
        if (m.contains("diet")) return "Eat more veggies and drink enough water, " + userName + ".";
        if (m.contains("exercise")) return "Let’s do some stretches today!";
        if (m.contains("sleep")) return "Try to sleep at the same time every night.";
        if (m.contains("medicine")) return "Don’t forget your prescription! Check the Medicine tab.";
        if (m.contains("bye")) return "Goodbye " + userName + "! See you later.";
        return "Hmm, I didn’t get that. You can ask me about medicine, diet, exercise, or sleep!";
    }
}
