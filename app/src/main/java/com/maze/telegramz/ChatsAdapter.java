package com.maze.telegramz;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.drinkless.td.libcore.telegram.TdApi;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static com.maze.telegramz.Telegram.chatList;
import static com.maze.telegramz.Telegram.chats;
import static com.maze.telegramz.Telegram.getChatList;


public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ChatListViewHolder> {
    private ArrayList<ChatsItem> cri;

    public ChatsAdapter(ArrayList<ChatsItem> i){
        cri = i;
    }

    @NonNull
    @Override
    public ChatListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_recycler_item,parent,false);
        ChatListViewHolder clvh = new ChatListViewHolder(v);
        return clvh;
    }

    @Override
    public void onBindViewHolder(@NonNull ChatListViewHolder holder, int position) {
        ChatsItem currentItem = cri.get(position);
        holder.getName().setText(currentItem.getNameLine());
        holder.getLastMsg().setText(currentItem.getLastMsgLine());
        holder.getLastMsgTime().setText(currentItem.getDate());
        holder.getDisplayPic().setImageBitmap(currentItem.getDisplayPic());
    }

    @Override
    public int getItemCount() {
        return cri.size();
    }


    public static class ChatListViewHolder extends RecyclerView.ViewHolder{
        public ImageView displayPic;
        public TextView name;
        public TextView lastMsg;
        public TextView lastMsgTime;

        public TextView getLastMsgTime() {
            return lastMsgTime;
        }

        public void setLastMsgTime(TextView lastMsgTime) {
            this.lastMsgTime = lastMsgTime;
        }

        public ImageView getDisplayPic() {
            return displayPic;
        }

        public void setDisplayPic(ImageView displayPic) {
            this.displayPic = displayPic;
        }

        public TextView getName() {
            return name;
        }

        public void setName(TextView name) {
            this.name = name;
        }

        public TextView getLastMsg() {
            return lastMsg;
        }

        public void setLastMsg(TextView lastMsg) {
            this.lastMsg = lastMsg;
        }

        public ChatListViewHolder(@NonNull View itemView) {
            super(itemView);
            displayPic = itemView.findViewById(R.id.user_photo);
            name= itemView.findViewById(R.id.UserName);
            lastMsg= itemView.findViewById(R.id.LastMessage);
            lastMsgTime = itemView.findViewById(R.id.LastMessageTime);
        }
    }
    public static ArrayList<ChatsItem> createChatsArrayList(){
        ArrayList<ChatsItem> list = new ArrayList<>();
        java.util.Iterator<Telegram.OrderedChat> iter = chatList.iterator();
        for (int i = 0; i < chatList.size(); i++) {
            long chatId = iter.next().chatId;
            TdApi.Chat chat = chats.get(chatId);
            synchronized (chat) {
                String lastMsg = makeLastMsgLine(chat);
                File f = null;
                long timeStamp = chat.lastMessage.date;
                String dateString = makeDateString(timeStamp);
                list.add(new ChatsItem(chat.id,f, chat.title, lastMsg, dateString,chat.order));
            }
        }
        return list;
    }
    private static char showDateNotTime(Date a, Date b){
        long diff = a.getTime() - b.getTime();
        long diffHrs = TimeUnit.HOURS.convert(diff, TimeUnit.MILLISECONDS);
        long diffDays = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
        if(diffHrs >= 12 && diffDays < 7)
            return 'w';
        else if (diffDays >= 7)
            return 'd';
        else
            return 'h';
    }

    public static String makeDateString(long timeStamp){
        String dateString;
        Date lastMsgDate = new java.util.Date(timeStamp * 1000L);
        Date now = new Date();
        char diff = showDateNotTime(now, lastMsgDate);
        if (diff == 'd')
            dateString = new SimpleDateFormat("d MMM", Locale.getDefault()).format(lastMsgDate);
        else if (diff == 'h')
            dateString = new SimpleDateFormat("h:mm a", Locale.getDefault()).format(lastMsgDate);
        else
            dateString = new SimpleDateFormat("EEE", Locale.getDefault()).format(lastMsgDate);

        return dateString;
    }

    public static String makeLastMsgLine(TdApi.Chat chat){
        String lastMsg = "Message";
        TdApi.MessageText m;
        if (chat.lastMessage != null && chat.lastMessage.content.getConstructor() == TdApi.MessageText.CONSTRUCTOR) {
            m = (TdApi.MessageText) chat.lastMessage.content;
            lastMsg = m.text.text;
        }
        return lastMsg;
    }
}
