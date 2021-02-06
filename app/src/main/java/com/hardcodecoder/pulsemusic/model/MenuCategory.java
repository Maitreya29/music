package com.hardcodecoder.pulsemusic.model;

import androidx.annotation.NonNull;

public class MenuCategory {

    private final String mCategoryTitle;
    private final MenuItem[] mItems;

    public MenuCategory(@NonNull String categoryTitle, @NonNull MenuItem[] items) {
        mCategoryTitle = categoryTitle;
        mItems = items;
    }

    @NonNull
    public String getCategoryTitle() {
        return mCategoryTitle;
    }

    @NonNull
    public MenuItem[] getItems() {
        return mItems;
    }
}