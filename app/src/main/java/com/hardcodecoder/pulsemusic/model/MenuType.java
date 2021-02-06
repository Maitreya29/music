package com.hardcodecoder.pulsemusic.model;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;

public class MenuType {

    private final int mType;
    private final String mTitle;
    @DrawableRes
    private final int mResId;

    public MenuType(int type, @NonNull String title, int resId) {
        mType = type;
        mTitle = title;
        mResId = resId;
    }

    public int getType() {
        return mType;
    }

    public String getTitle() {
        return mTitle;
    }

    public int getIconId() {
        return mResId;
    }
}