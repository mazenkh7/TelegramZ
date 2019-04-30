package com.maze.telegramz;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import static com.maze.telegramz.Authentication.startClient;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        super.onCreate(savedInstanceState);
        Button startBu = findViewById(R.id.startBu);
        final Intent intent = new Intent(this, LoginActivity.class);
        startBu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startClient();
                startActivity(intent);
            }
        });
//        SharedPreferences sp = getSharedPreferences("TZFS", Context.MODE_PRIVATE);
//        if (!sp.getBoolean("first", false)) {
//            SharedPreferences.Editor editor = sp.edit();
//            editor.putBoolean("first", true);
//            editor.apply();
//        }


//        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) //check storage permission
//                != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1024);
            //take storage permission from user
//        }
    }

}
