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

import android.util.Log;
import android.view.MenuItem;

import com.maze.telegramz.ChatsFragment.OnFragmentInteractionListener;

import org.drinkless.td.libcore.telegram.Client;
import org.drinkless.td.libcore.telegram.TdApi;

import static com.maze.telegramz.ChatsFragment.chatsAdapter;
import static com.maze.telegramz.Telegram.client;
import static com.maze.telegramz.Telegram.getChatList;
import static com.maze.telegramz.Telegram.setMe;
import static com.maze.telegramz.Telegram.startClient;


public class HomeActivity extends AppCompatActivity implements OnFragmentInteractionListener {
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    public static HomeActivityIC ic;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            fragmentManager = getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            switch (item.getItemId()) {
                case R.id.navigation_chats:
                    fragmentTransaction.replace(R.id.homeFragmentFrame, fragmentManager.findFragmentByTag("Chats Fragment"), "Chats Fragment");
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
    public void onResume(){
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }

    @Override
    protected void onStart() {
        super.onStart();
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.homeFragmentFrame, new ChatsFragment(), "Chats Fragment");
        fragmentTransaction.commit();
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        ic = new HomeActivityIC();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onBackPressed(){
        moveTaskToBack(true);
    }

    public class HomeActivityIC {
        public void refreshChatsRecycler() {
            HomeActivity.this.runOnUiThread(() -> chatsAdapter.notifyDataSetChanged());
        }
        public Context getContext(){
            return HomeActivity.this.getApplicationContext();
        }
    }

}
