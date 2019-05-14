package com.maze.telegramz;


import android.graphics.BitmapFactory;
import android.graphics.Color;
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
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.RemoteInput;

import static com.maze.telegramz.HomeActivity.ic;
import static com.maze.telegramz.Telegram.chats;
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
        if (remoteMessage.getData().size() > 0) {
            client.send(new TdApi.ProcessPushNotification(new JSONObject(remoteMessage.getData()).toString()), null, null);
            client.send(new TdApi.SetOption("online", new TdApi.OptionValueBoolean(true)), null, null);
        }
    }

    public static void notify(final TdApi.UpdateNotificationGroup notificationGroup) {
        long chatId = notificationGroup.chatId;
        TdApi.Chat chat = chats.get(chatId);
        String title = chat.title;
        NotificationCompat.Builder builder = new NotificationCompat.Builder(ic.getContext());
        builder.setContentTitle(title)
                .setSmallIcon(R.mipmap.app_notification)
                .setColor(Color.RED)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setLargeIcon(BitmapFactory.decodeFile(chat.photo.small.local.path));
        NotificationCompat.InboxStyle notStyle = new NotificationCompat.InboxStyle();
        for (int i = 0; i < notificationGroup.addedNotifications.length; i++) {
            TdApi.Notification addedNotification = notificationGroup.addedNotifications[i];
            switch (addedNotification.type.getConstructor()) {
                case TdApi.NotificationTypeNewMessage.CONSTRUCTOR:
                    TdApi.NotificationTypeNewMessage newMsg = (TdApi.NotificationTypeNewMessage) addedNotification.type;
                    if (i == 0)
                        builder.setContentText(((TdApi.MessageText) newMsg.message.content).text.text);
                    notStyle.addLine(((TdApi.MessageText) newMsg.message.content).text.text);
            }
        }

        builder.setStyle(notStyle);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(ic.getContext());
        notificationManager.notify(1, builder.build());
    }
}
