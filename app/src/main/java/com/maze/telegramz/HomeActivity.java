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
//        getChatList(100);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sp = getSharedPreferences("TZSP", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("Loggedin", true);
        editor.apply();
        client.send(new TdApi.GetMe(), new Client.ResultHandler() {
            @Override
            public void onResult(TdApi.Object object) {
                if(object.getConstructor() == TdApi.User.CONSTRUCTOR)
                    setMe((TdApi.User)object);
            }
        }, null);
        setContentView(R.layout.activity_home);
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.homeFragmentFrame, new ChatsFragment(), "Chats Fragment");
        fragmentTransaction.commit();
        ic = new HomeActivityIC();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public class HomeActivityIC {
        public void refreshChatsRecycler() {
            HomeActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    chatsAdapter.notifyDataSetChanged();
                }
            });
        }
        public Context getContext(){
            return HomeActivity.this.getApplicationContext();
        }
    }

}
