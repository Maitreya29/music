package com.nezukoos.music.model;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;

public class AccentsModel {

    private final String mTitle;
    private final int mAccentId;
    @ColorInt
    private final int mColor;

    public AccentsModel(int id, @NonNull String title, int color) {
        mAccentId = id;
        mTitle = title;
        mColor = color;
    }

    public int getId() {
        return mAccentId;
    }

    @NonNull
    public String getTitle() {
        return mTitle;
    }

    @ColorInt
    public int getColor() {
        return mColor;
    }
}