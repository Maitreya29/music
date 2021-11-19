package com.radiant.music.helper;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.radiant.music.MediaArtCache;
import com.radiant.music.R;
import com.radiant.music.model.MusicModel;
import com.radiant.music.themes.ThemeColors;
import com.radiant.music.utils.ImageUtil;

public class MediaArtHelper {

    private static TypedArray mMediaArtColors;

    @Nullable
    public static Bitmap getAlbumArt(@NonNull Context context, @NonNull MusicModel data) {
        // We know that manually selected tracks have negative track id's
        if (data.getId() < 0)
            return getDefaultAlbumArtBitmap(context, data.getAlbumId());
        try {
            Uri uri = Uri.parse(data.getAlbumArtUrl());
            Bitmap bm = ImageUtil.getScaledBitmap(context.getContentResolver().openInputStream(uri), 512, 512);
            return bm == null ? getDefaultAlbumArtBitmap(context, data.getAlbumId()) : bm;
        } catch (Exception e) {
            return getDefaultAlbumArtBitmap(context, data.getAlbumId());
        }
    }

    @Nullable
    public static Drawable getDefaultAlbumArt(@NonNull Context context, long albumId) {
        // albumId = -1 represents album art tinted with the primary color
        // So, we need not convert it into positive int
        if (albumId != -1) albumId = Math.abs(albumId);

        if (null != MediaArtCache.getMediaArtDrawable(albumId))
            return MediaArtCache.getMediaArtDrawable(albumId);
        Drawable drawable = ImageUtil.generateTintedDefaultAlbumArt(context, getTintColor(context, albumId));
        if (null == drawable) return null;
        MediaArtCache.addDrawableIfNotPresent(drawable, albumId);
        return drawable;
    }

    @Nullable
    public static Bitmap getDefaultAlbumArtBitmap(@NonNull Context context, long albumId) {
        // -1 represents the primary color tinted album art
        // So, we need not convert it into positive int
        if (albumId != -1) albumId = Math.abs(albumId);

        Drawable drawable = ImageUtil.generateTintedDefaultAlbumArt(context, getTintColor(context, albumId));
        if (null == drawable) return null;
        return ImageUtil.getBitmapFromDrawable(drawable);
    }

    @ColorInt
    private static int getTintColor(@NonNull Context context, long albumId) {
        if (albumId == -1) return ThemeColors.getCurrentColorPrimary();
        if (null == mMediaArtColors)
            mMediaArtColors = context.getResources().obtainTypedArray(R.array.album_art_colors);
        return mMediaArtColors.getColor((int) (albumId % mMediaArtColors.length()), 0xFF0000);
    }
}