package com.nezukoos.music;

import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;

import java.util.Arrays;

public class MediaArtCache {

    // 0th index album art tinted with primary color
    // and rest is for 7 different colored album art
    private static final Drawable[] mMediaArtDefaultDrawables = new Drawable[8];

    public static void addDrawableIfNotPresent(@NonNull Drawable drawable, long albumId) {
        //Since 0th index is reserved
        int index = (int) (albumId % 7);
        mMediaArtDefaultDrawables[index + 1] = drawable;
    }

    public static Drawable getMediaArtDrawable(long albumId) {
        //Since 0th index is reserved
        int index = (int) (albumId % 7);
        return mMediaArtDefaultDrawables[index + 1];
    }

    public static void flushCache() {
        Arrays.fill(mMediaArtDefaultDrawables, null);
    }
}