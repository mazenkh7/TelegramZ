package com.maze.telegramz;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.View;
import android.widget.Button;

import static com.maze.telegramz.Telegram.startClient;

public class IntroActivity extends AppCompatActivity {
    private static final int NUM_PAGES = 5;
    private ViewPager mPager;
    private PagerAdapter pagerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        startClient();
        super.onCreate(savedInstanceState);
        SharedPreferences sp = getSharedPreferences("TZSP", Context.MODE_PRIVATE);
        if (sp.getBoolean("Loggedin", false)) {
            final Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
            finish();
        } else {
            setContentView(R.layout.activity_intro);
            Button startBu = findViewById(R.id.startBu);
            final Intent intent = new Intent(this, LoginActivity.class);
            startBu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(intent);
                }
            });
//        SharedPreferences sp = getSharedPreferences("TZFS", Context.MODE_PRIVATE);
//        if (!sp.getBoolean("first", false)) {
//            SharedPreferences.Editor editor = sp.edit();
//            editor.putBoolean("first", true);
//            editor.apply();
//        }


//        if (ContextCompat.checkSelfPermission(IntroActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) //check storage permission
//                != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(IntroActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1024);
            //take storage permission from user
//        }
        }
    }
}
