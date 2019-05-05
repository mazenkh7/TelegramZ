package com.maze.telegramz;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;

public class ChatsItem {
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


    public ChatsItem(long id, File img, String name, String lm, String d){
        Bitmap m = null;
        if(img!=null)
            m = BitmapFactory.decodeFile(img.getAbsolutePath());
        displayPic = m;
        nameLine = name;
        lastMsgLine = lm;
        date = d;
        this.id = id;
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
}
