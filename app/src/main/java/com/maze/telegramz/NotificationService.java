package com.maze.telegramz;


import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;
import android.util.SparseArray;


import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.drinkless.td.libcore.telegram.Client;
import org.drinkless.td.libcore.telegram.TdApi;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import static com.maze.telegramz.HomeActivity.ic;
import static com.maze.telegramz.Telegram.chats;
import static com.maze.telegramz.Telegram.client;
import static com.maze.telegramz.Telegram.startClient;


public class NotificationService extends FirebaseMessagingService {
    private static String token = "";
    static SparseArray<ArrayList<TdApi.Notification>> globalNotifications = new SparseArray<>();

    @Override
    public void onNewToken(String token) {
        NotificationService.token = token;
        if (getToken() != null && !getToken().isEmpty())
            client.send(new TdApi.RegisterDevice(new TdApi.DeviceTokenFirebaseCloudMessaging(getToken(), true), null), null);
    }

    public static void updateToken() {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        return;
                    }
                    // Get new Instance ID token
                    token = task.getResult().getToken();
                    if (getToken() != null && !getToken().isEmpty())
                        client.send(new TdApi.RegisterDevice(new TdApi.DeviceTokenFirebaseCloudMessaging(getToken(), true), null), null);
                });
    }

    public static String getToken() {
        return token;
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getData().size() > 0) {
            startClient();
            client.send(new TdApi.ProcessPushNotification(new JSONObject(remoteMessage.getData()).toString()), null, null);
        }
    }

    public static void notify(final TdApi.UpdateNotificationGroup notificationGroup) {
        ArrayList<TdApi.Notification> notList;
        if (globalNotifications.get(notificationGroup.notificationGroupId) == null) {
            notList = new ArrayList<>();
            globalNotifications.put(notificationGroup.notificationGroupId, notList);
        } else {
            notList = globalNotifications.get(notificationGroup.notificationGroupId);
        }

        if (notificationGroup.removedNotificationIds.length > 0)
            for (int removedNotificationId : notificationGroup.removedNotificationIds) {
                for (TdApi.Notification n : notList) {
                    if (n.id == removedNotificationId)
                        notList.remove(n);
                }
            }
        if (notificationGroup.addedNotifications.length > 0)
            notList.addAll(Arrays.asList(notificationGroup.addedNotifications));

        NotificationManagerCompat nm = NotificationManagerCompat.from(ic.getContext());
        if (notList.isEmpty())
            nm.cancel(notificationGroup.notificationGroupId);

        else {
            long chatId = notificationGroup.chatId;
            TdApi.Chat chat = chats.get(chatId);
            String title = chat.title;
            NotificationCompat.Builder builder = new NotificationCompat.Builder(ic.getContext(), "Messages");
            builder.setContentTitle(title)
                    .setSmallIcon(R.mipmap.app_notification)
                    .setColor(Color.RED)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setGroup("yo")
                    .setGroupSummary(true)
                    .setLargeIcon(BitmapFactory.decodeFile(chat.photo.small.local.path));
            TdApi.NotificationTypeNewMessage contentMinimized = (TdApi.NotificationTypeNewMessage)notList.get(0).type;
            builder.setContentText(((TdApi.MessageText)contentMinimized.message.content).text.text);
            NotificationCompat.Style notStyle = null;

            if (notList.size() == 1) {
                notStyle = new NotificationCompat.BigTextStyle();
                TdApi.NotificationTypeNewMessage newMsg = (TdApi.NotificationTypeNewMessage)notList.get(0).type;
                ((NotificationCompat.BigTextStyle) notStyle).bigText(((TdApi.MessageText) newMsg.message.content).text.text);
            }

            else if (notList.size() > 1) {
                notStyle = new NotificationCompat.InboxStyle();
                builder.setStyle(new NotificationCompat.InboxStyle());
                for (TdApi.Notification n : notList) {
                    switch (n.type.getConstructor()) {
                        case TdApi.NotificationTypeNewMessage.CONSTRUCTOR:
                            TdApi.NotificationTypeNewMessage newMsg = (TdApi.NotificationTypeNewMessage) n.type;
                            ((NotificationCompat.InboxStyle)notStyle).addLine(((TdApi.MessageText) newMsg.message.content).text.text);
                    }
                }
            }
//            TdApi.NotificationTypeNewMessage lastMsg = (TdApi.NotificationTypeNewMessage) notList.get(notList.size()-1).type;
            builder.setStyle(notStyle);
            nm.notify(notificationGroup.notificationGroupId,builder.build());

        }
    }
}
