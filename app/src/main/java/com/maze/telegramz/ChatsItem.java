package com.maze.telegramz;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

import com.amulyakhare.textdrawable.TextDrawable;

import java.io.File;

import static com.maze.telegramz.HomeActivity.ic;

public class ChatsItem implements Comparable<ChatsItem> {
    private long id;

    public void setTitle(String title) {
        this.title = title;
    }

    public void setLastMsg(String lastMsg) {
        this.lastMsg = lastMsg.replaceAll("\n", " ");
    }

    private long displayPicID;

    public void setDisplayPic(Bitmap displayPic){
        this.displayPic = displayPic;
    }

    public void setDisplayPic(File displayPic) {
        if (displayPic != null)
            this.displayPic = BitmapFactory.decodeFile(displayPic.getAbsolutePath());
//        else
//            this.displayPic = BitmapFactory.decodeResource(ic.getContext().getResources(),R.mipmap.ic_default_dp);
    }

    private Bitmap displayPic;
    private String title;
    private String lastMsg;
    private String date;
    private long order;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


    public ChatsItem(long id, File f, String name, String lastMessage, String date, long order) {
//        if (displayPic != null)
//            this.displayPic = BitmapFactory.decodeFile(displayPic.getAbsolutePath());
//        else {
//            this.displayPic = BitmapFactory.decodeResource(AppContext.getAppContext().getResources(),R.drawable.default_profile);
//        }
        this.title = name;
        this.lastMsg = lastMessage.replaceAll("\n", " ");
        this.date = date;
        this.id = id;
        this.order = order;
        setDisplayPic(f);
    }

    public Bitmap getDisplayPic() {
        return displayPic;
    }

    public String getTitle() {
        return title;
    }

    public String getLastMsg() {
        return lastMsg;
    }

    public long getId() {
        return id;
    }

    public long getOrder() {
        return order;
    }

    public void setOrder(long order) {
        this.order = order;
    }

    @Override
    public int compareTo(ChatsItem o) {
        if (this.order != o.order) {
            return o.order < this.order ? -1 : 1;
        }
        if (this.id != o.id) {
            return o.id < this.id ? -1 : 1;
        }
        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        ChatsItem o = (ChatsItem) obj;
        return this.order == o.order && this.id == o.id;
    }

    public long getDisplayPicID() {
        return displayPicID;
    }

    public void setDisplayPicID(long displayPicID) {
        this.displayPicID = displayPicID;
    }
}
