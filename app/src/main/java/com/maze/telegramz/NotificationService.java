package com.maze.telegramz;


import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessagingService;

import org.drinkless.td.libcore.telegram.TdApi;

import androidx.annotation.NonNull;

import static com.maze.telegramz.Telegram.client;


public class NotificationService extends FirebaseMessagingService {
    private static String token;

    @Override
    public void onNewToken(String token) {
        Log.e("TOKEN", "GOT TOKEN YAY:\n" + token);
        this.token = token;
        client.send(new TdApi.RegisterDevice(new TdApi.DeviceTokenGoogleCloudMessaging(getToken()), null), null);
    }

    public static void updateToken() {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.e("token", "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        token = task.getResult().getToken();

                        // Log and toast
                        Log.e("TOKEN", "GOT TOKEN YAY:\n" + token);
//                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                        client.send(new TdApi.RegisterDevice(new TdApi.DeviceTokenGoogleCloudMessaging(getToken()), null), null);

                    }
                });
    }

    public static String getToken() {
        return token;
    }
}
