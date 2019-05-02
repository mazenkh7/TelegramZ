package com.maze.telegramz;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;


public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatListViewHolder> {
    private ArrayList<ChatRecyclerItem> cri;

    public ChatListAdapter(ArrayList<ChatRecyclerItem> i){
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
        ChatRecyclerItem currentItem = cri.get(position);
        holder.getName().setText(currentItem.getNameLine());
        holder.getLastMsg().setText(currentItem.getLastMsgLine());
        holder.getLastMsgTime().setText(currentItem.getDate());
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
}
