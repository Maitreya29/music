package com.hardcodecoder.pulsemusic;

import android.graphics.drawable.Drawable;

import java.util.Arrays;

public class MediaArtCache {

    private static Drawable[] mMediaArtDefaultDrawables = new Drawable[7];

    public static void addDrawableIfNotPresent(Drawable drawable, long albumId) {
        int index = (int) albumId % 7;
        mMediaArtDefaultDrawables[index] = drawable;
    }

    public static Drawable getMediaArtDrawable(long albumId) {
        int index = (int) albumId % 7;
        return mMediaArtDefaultDrawables[index];
    }

    public static void flushCache() {
        Arrays.fill(mMediaArtDefaultDrawables, null);
    }
}