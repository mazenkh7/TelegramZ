package com.maze.telegramz;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.drinkless.td.libcore.telegram.Client;
import org.drinkless.td.libcore.telegram.TdApi;

import java.util.ArrayList;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import static com.maze.telegramz.HomeActivity.ic;
import static com.maze.telegramz.Telegram.client;

public class TZNotificationManager {
    public TZNotificationManager() {
    }

    public static void notify(int id, final TdApi.UpdateNotificationGroup notificationGroup) {
        long chatId = notificationGroup.chatId;
        client.send(new TdApi.GetChat(chatId), new Client.ResultHandler() {
            @Override
            public void onResult(TdApi.Object object) {
                TdApi.Chat chat = (TdApi.Chat) object;
                String title = chat.title;
                NotificationCompat.Builder builder = new NotificationCompat.Builder(ic.getContext());
                builder.setContentTitle(title)
                        .setSmallIcon(R.mipmap.app_notification)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setStyle(new NotificationCompat.InboxStyle())
                        .setLargeIcon(BitmapFactory.decodeFile(chat.photo.small.local.path));
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(ic.getContext());
                notificationManager.notify(notificationGroup.notificationGroupId, builder.build());
            }
        });

        for (int i = 0; i < notificationGroup.totalCount; i++) {

        }
    }
}
