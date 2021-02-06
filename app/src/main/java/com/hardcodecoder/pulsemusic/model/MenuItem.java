package com.hardcodecoder.pulsemusic.model;

import androidx.annotation.NonNull;

public class MenuItem {

    private final int mId;
    private final String mTitle;

    public MenuItem(int id, @NonNull String title) {
        mId = id;
        mTitle = title;
    }

    public int getId() {
        return mId;
    }

    @NonNull
    public String getTitle() {
        return mTitle;
    }
}