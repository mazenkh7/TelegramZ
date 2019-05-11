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
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.e("notifizo", "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.e("notifizo", "Message data payload: " + new JSONObject(remoteMessage.getData()).toString());
            client.send(new TdApi.ProcessPushNotification(new JSONObject(remoteMessage.getData()).toString()), null, null);

//            if (/* Check if data needs to be processed by long running job */ true) {
//                // For long-running tasks (10 seconds or more) use WorkManager.
//                scheduleJob();
//            } else {
//                // Handle message within 10 seconds
//                handleNow();
//            }

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.e("notifizo", "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
}
