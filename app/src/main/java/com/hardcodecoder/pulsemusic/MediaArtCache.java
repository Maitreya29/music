package com.hardcodecoder.pulsemusic;

import android.graphics.drawable.Drawable;

import java.util.Arrays;

public class MediaArtCache {

    // 0th index stores primary color tinted album art
    // and rest is for 7 different color tinted album art
    private static final Drawable[] mMediaArtDefaultDrawables = new Drawable[8];

    public static void addDrawableIfNotPresent(Drawable drawable, long albumId) {
        //Since 0th index is reserved
        int index = (int) albumId % 7;
        mMediaArtDefaultDrawables[index + 1] = drawable;
    }

    public static Drawable getMediaArtDrawable(long albumId) {
        //Since 0th index is reserved
        int index = (int) albumId % 7;
        return mMediaArtDefaultDrawables[index + 1];
    }

    public static void flushCache() {
        Arrays.fill(mMediaArtDefaultDrawables, null);
    }
}