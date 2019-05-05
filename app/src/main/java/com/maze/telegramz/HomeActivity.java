package com.maze.telegramz;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MenuItem;

import com.maze.telegramz.ChatsFragment.OnFragmentInteractionListener;

import org.drinkless.td.libcore.telegram.TdApi;

import static com.maze.telegramz.Telegram.client;
import static com.maze.telegramz.Telegram.getChatList;


public class HomeActivity extends AppCompatActivity implements OnFragmentInteractionListener {
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            fragmentManager = getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            switch (item.getItemId()) {
                case R.id.navigation_chats:
                    fragmentTransaction.replace(R.id.homeFragmentFrame, new ChatsFragment(), "HELLO");
                    fragmentTransaction.commit();
                    setTitle(R.string.title_chats);
                    return true;
                case R.id.navigation_calls:
                    setTitle(R.string.title_calls);
                    return true;
                case R.id.navigation_profile:
                    setTitle(R.string.title_profile);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        client.send(new TdApi.RegisterDevice(new TdApi.DeviceTokenGoogleCloudMessaging(),null), null);
        SharedPreferences sp = getSharedPreferences("TZSP", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("Loggedin", true);
        editor.apply();
        getChatList(100);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.homeFragmentFrame, new ChatsFragment(), "Chats Fragment");
        fragmentTransaction.commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
