package com.maze.telegramz.holders;

import android.view.View;

import com.stfalcon.chatkit.messages.MessageHolders;

import org.drinkless.td.libcore.telegram.TdApi;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class IncomingTextMessageHolder extends MessageHolders.IncomingTextMessageViewHolder<TdApi.Message> {
    public IncomingTextMessageHolder(View itemView, Object payload) {
        super(itemView, payload);
    }
    @Override
    public void onBind(TdApi.Message message){
        super.onBind(message);
        time.setText(new SimpleDateFormat("h:mm a", Locale.getDefault()).format(message.getCreatedAt()));
    }
}
