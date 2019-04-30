package com.maze.telegramz;

public class ChatRecyclerItem {
    private int mImageResource;
    private String nameLine;
    private String lastMsgLine;

    public ChatRecyclerItem(int img, String L1, String L2){
        mImageResource = img;
        nameLine = L1;
        lastMsgLine = L2;
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
