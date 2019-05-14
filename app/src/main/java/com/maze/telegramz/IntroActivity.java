package com.maze.telegramz;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import static com.maze.telegramz.Telegram.startClient;

public class IntroActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sp = getSharedPreferences("TZSP", Context.MODE_PRIVATE);
        if (sp.getBoolean("Loggedin", false)) {
            final Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
            startClient();
            finish();
        } else {
            setContentView(R.layout.activity_intro);
        }
    }

    public void startBuAction(View view) {
        final Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
