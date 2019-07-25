package com.maze.telegramz.holders;

import android.view.View;

import com.stfalcon.chatkit.messages.MessageHolders;

import org.drinkless.td.libcore.telegram.TdApi;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class OutcomingTextMessageHolder extends MessageHolders.OutcomingTextMessageViewHolder<TdApi.Message> {
    public OutcomingTextMessageHolder(View itemView, Object payload) {
        super(itemView, payload);
    }
    @Override
    public void onBind(TdApi.Message meessage){
        super.onBind(meessage);
        time.setText(new SimpleDateFormat("h:mm a", Locale.getDefault()).format(meessage.getCreatedAt()));
    }
}
