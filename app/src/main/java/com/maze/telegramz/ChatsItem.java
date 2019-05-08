package com.maze.telegramz;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;

public class ChatsItem implements Comparable<ChatsItem>{
    private long id;

    public void setNameLine(String nameLine) {
        this.nameLine = nameLine;
    }

    public void setLastMsgLine(String lastMsgLine) {
        this.lastMsgLine = lastMsgLine;
    }

    private Bitmap displayPic;
    private String nameLine;
    private String lastMsgLine;
    private String date;
    private long order;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


    public ChatsItem(long id, File displayPic, String name, String lastMessage, String date, long order){
        Bitmap bm = null;
        if(displayPic!=null)
            bm = BitmapFactory.decodeFile(displayPic.getAbsolutePath());
        this.displayPic = bm;
        this.nameLine = name;
        this.lastMsgLine = lastMessage;
        this.date = date;
        this.id = id;
        this.order = order;
    }

    public Bitmap getDisplayPic() {
        return displayPic;
    }

    public String getNameLine() {
        return nameLine;
    }

    public String getLastMsgLine() {
        return lastMsgLine;
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
}
