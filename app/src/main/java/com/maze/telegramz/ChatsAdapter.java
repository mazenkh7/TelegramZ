package com.maze.telegramz;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.drinkless.td.libcore.telegram.TdApi;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static com.maze.telegramz.HomeActivity.ic;
import static com.maze.telegramz.Telegram.chatList;
import static com.maze.telegramz.Telegram.chats;
import static com.maze.telegramz.Telegram.client;
import static com.maze.telegramz.Telegram.getMe;


public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ChatListViewHolder> {
    private ArrayList<ChatsItem> cri;
    private OnChatClickListener mOccl;
    public ChatsAdapter(ArrayList<ChatsItem> i, OnChatClickListener mOccl) {
        cri = i;
        this.mOccl = mOccl;
    }

    @NonNull
    @Override
    public ChatListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_recycler_item, parent, false);
        ChatListViewHolder clvh = new ChatListViewHolder(v, mOccl);
        return clvh;
    }

    @Override
    public void onBindViewHolder(@NonNull ChatListViewHolder holder, int position) {
        ChatsItem currentItem = cri.get(position);
        holder.getName().setText(currentItem.getTitle());
        holder.getLastMsg().setText(currentItem.getLastMsg());
        holder.getLastMsgTime().setText(currentItem.getDate());
        if (currentItem.getDisplayPic() != null)
            holder.getDisplayPic().setImageBitmap(currentItem.getDisplayPic());
        else {
            holder.getDisplayPic().setImageDrawable(currentItem.getTextDrawable());
        }
    }

    @Override
    public int getItemCount() {
        return cri.size();
    }


    public static class ChatListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView displayPic;
        public TextView name;
        public TextView lastMsg;
        public TextView lastMsgTime;
        public OnChatClickListener occl;
        public TextView getLastMsgTime() {
            return lastMsgTime;
        }

        public ImageView getDisplayPic() {
            return displayPic;
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

        public ChatListViewHolder(@NonNull View itemView, OnChatClickListener occl) {
            super(itemView);
            displayPic = itemView.findViewById(R.id.user_photo);
            name = itemView.findViewById(R.id.UserName);
            lastMsg = itemView.findViewById(R.id.LastMessage);
            lastMsgTime = itemView.findViewById(R.id.LastMessageTime);
            this.occl = occl;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            occl.onClick(getAdapterPosition());
        }
    }


    public static void populateChatsArrayList(ArrayList<ChatsItem> list) {
        java.util.Iterator<Telegram.OrderedChat> iter = chatList.iterator();
        for (int i = 0; i < chatList.size(); i++) {
            long chatId = iter.next().chatId;
            TdApi.Chat chat = chats.get(chatId);
            long timeStamp = chat.lastMessage.date;
            String dateString = makeDateString(timeStamp);
            String lastMsg = makeLastMsgStr(chat);
            File f = null;
            ChatsItem ch = new ChatsItem(chat.id, f, chat.title, lastMsg, dateString, chat.order);
            if (chat.photo != null) {
                f = new File(chat.photo.small.local.path);
                ch.setDisplayPicID(chat.photo.small.id);
                if (!f.exists())
                    client.send(new TdApi.DownloadFile(chat.photo.small.id, 1, 0, 0, true), new Telegram.displayPicDownloadHandler());
                else
                    ch.setDisplayPic(f);
            }
            if (getMe() != null && chat.id == getMe().id) {
                ch.setTitle("Saved Messages");
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                bmOptions.inScaled = false;
                ch.setDisplayPic(BitmapFactory.decodeResource(ic.getContext().getResources(), R.drawable.saved_messages, bmOptions));
            }
            list.add(ch);
        }
        ic.refreshChatsRecycler();
    }

    private static char showDateNotTime(Date a, Date b) {
        long diff = a.getTime() - b.getTime();
        long diffHrs = TimeUnit.HOURS.convert(diff, TimeUnit.MILLISECONDS);
        long diffDays = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
        if (diffHrs >= 12 && diffDays < 7)
            return 'w';
        else if (diffDays >= 7)
            return 'd';
        else
            return 'h';
    }

    public static void prefetchAllChatsLastMessages(ArrayList<ChatsItem> list){
        for (ChatsItem i : list)
            client.send(new TdApi.GetChatHistory(i.getId(),0,0,100,false), null);
    }

    public static String makeDateString(long timeStamp) {
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

    public static String makeLastMsgStr(TdApi.Chat chat) {
        String lastMsg = "Message";
        if (chat.lastMessage != null)
            switch (chat.lastMessage.content.getConstructor()) {
                case TdApi.MessageText.CONSTRUCTOR:
                    TdApi.MessageText mt = (TdApi.MessageText) chat.lastMessage.content;
                    lastMsg = mt.text.text;
                    break;
//                case MessageAnimation.CONSTRUCTOR:
//                    break;
//                case MessageAudio.CONSTRUCTOR:
//                    break;
//                case MessageBasicGroupChatCreate.CONSTRUCTOR:
//                    break;
//                case MessageCall.CONSTRUCTOR:
//                    break;
//                case MessageChatAddMembers.CONSTRUCTOR:
//                    break;
//                case MessageChatChangePhoto.CONSTRUCTOR:
//                    break;
//                case MessageChatChangeTitle.CONSTRUCTOR:
//                    break;
//                case MessageChatDeleteMember.CONSTRUCTOR:
//                    break;
//                case MessageChatDeletePhoto.CONSTRUCTOR:
//                    break;
//                case MessageChatJoinByLink.CONSTRUCTOR:
//                    break;
//                case MessageChatSetTtl.CONSTRUCTOR:
//                    break;
//                case MessageChatUpgradeFrom.CONSTRUCTOR:
//                    break;
//                case MessageChatUpgradeTo.CONSTRUCTOR:
//                    break;
//                case MessageContact.CONSTRUCTOR:
//                    break;
//                case MessageContactRegistered.CONSTRUCTOR:
//                    break;
//                case MessageCustomServiceAction.CONSTRUCTOR:
//                    break;
//                case MessageDocument.CONSTRUCTOR:
//                    break;
//                case MessageExpiredPhoto.CONSTRUCTOR:
//                    break;
//                case MessageExpiredVideo.CONSTRUCTOR:
//                    break;
//                case MessageGame.CONSTRUCTOR:
//                    break;
//                case MessageGameScore.CONSTRUCTOR:
//                    break;
//                case MessageInvoice.CONSTRUCTOR:
//                    break;
//                case MessageLocation.CONSTRUCTOR:
//                    break;
//                case MessagePassportDataReceived.CONSTRUCTOR:
//                    break;
//                case MessagePassportDataSent.CONSTRUCTOR:
//                    break;
//                case MessagePaymentSuccessful.CONSTRUCTOR:
//                    break;
//                case MessagePaymentSuccessfulBot.CONSTRUCTOR:
//                    break;
//                case MessagePhoto.CONSTRUCTOR:
//                    break;
//                case MessagePinMessage.CONSTRUCTOR:
//                    break;
//                case MessageScreenshotTaken.CONSTRUCTOR:
//                    break;
                case TdApi.MessageSticker.CONSTRUCTOR:
                    TdApi.MessageSticker ms = (TdApi.MessageSticker) chat.lastMessage.content;
                    lastMsg = ms.sticker.emoji + " Sticker";
                    break;
//                case MessageSupergroupChatCreate.CONSTRUCTOR:
//                    break;
//                case MessageUnsupported.CONSTRUCTOR:
//                    break;
//                case MessageVenue.CONSTRUCTOR:
//                    break;
//                case MessageVideo.CONSTRUCTOR:
//                    break;
//                case MessageVideoNote.CONSTRUCTOR:
//                    break;
//                case MessageVoiceNote.CONSTRUCTOR:
//                    break;
//                case MessageWebsiteConnected.CONSTRUCTOR:
//                    break;
            }
//            Log.e("msg type class name", chat.lastMessage.content.getClass().getName());
        return lastMsg;
    }



    public interface OnChatClickListener {
        void onClick(int pos);
    }
}