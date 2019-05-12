package com.maze.telegramz;


import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.drinkless.td.libcore.telegram.Client;
import org.drinkless.td.libcore.telegram.TdApi;
import org.json.JSONObject;

import androidx.annotation.NonNull;

import static com.maze.telegramz.Telegram.client;


public class NotificationService extends FirebaseMessagingService {
    private static String token = "";

    @Override
    public void onNewToken(String token) {
        NotificationService.token = token;
        if (getToken() != null && !getToken().isEmpty())
            client.send(new TdApi.RegisterDevice(new TdApi.DeviceTokenFirebaseCloudMessaging(getToken(), true), null), new Client.ResultHandler() {
                @Override
                public void onResult(TdApi.Object object) {
                    Log.e("token",object.toString());
                }
            });
    }

    public static void updateToken() {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            return;
                        }
                        // Get new Instance ID token
                        token = task.getResult().getToken();
                        if (getToken() != null && !getToken().isEmpty())
                            client.send(new TdApi.RegisterDevice(new TdApi.DeviceTokenFirebaseCloudMessaging(getToken(), true), null), new Client.ResultHandler() {
                                @Override
                                public void onResult(TdApi.Object object) {
                                    Log.e("token",object.toString());
                                }
                            });
                    }
                });
    }

    public static String getToken() {
        return token;
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        // TODO(developer): Handle FCM messages here.
        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            client.send(new TdApi.ProcessPushNotification(new JSONObject(remoteMessage.getData()).toString()), null, null);
        }
        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.e("notifizo", "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
    }
}
