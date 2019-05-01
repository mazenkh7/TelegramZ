package com.maze.telegramz;

import java.util.Date;

public class ChatRecyclerItem {
    private int mImageResource;
    private String nameLine;
    private String lastMsgLine;
    private String date;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


    public ChatRecyclerItem(int img, String name, String lm, String d){
        mImageResource = img;
        nameLine = name;
        lastMsgLine = lm;
        date = d;
    }

    public int getmImageResource() {
        return mImageResource;
    }

    public String getNameLine() {
        return nameLine;
    }

    public String getLastMsgLine() {
        return lastMsgLine;
    }

}
