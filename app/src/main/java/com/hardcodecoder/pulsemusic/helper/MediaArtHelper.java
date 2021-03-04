package com.hardcodecoder.pulsemusic.helper;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;

import com.hardcodecoder.pulsemusic.MediaArtCache;
import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.model.MusicModel;
import com.hardcodecoder.pulsemusic.themes.ThemeColors;
import com.hardcodecoder.pulsemusic.utils.ImageUtil;

public class MediaArtHelper {

    private static TypedArray mMediaArtColors;

    @NonNull
    public static Bitmap getAlbumArt(@NonNull Context context, @NonNull MusicModel data) {
        // We know that manually selected tracks have negative track id's
        if (data.getId() < 0)
            return getDefaultAlbumArtBitmap(context, data.getAlbumId());
        try {
            Uri uri = Uri.parse(data.getAlbumArtUrl());
            return ImageUtil.getScaledBitmap(context.getContentResolver().openInputStream(uri), 512, 512);
        } catch (Exception e) {
            return getDefaultAlbumArtBitmap(context, data.getAlbumId());
        }
    }

    @NonNull
    public static Drawable getDefaultAlbumArt(@NonNull Context context, long albumId) {
        // albumId = -1 represents album art tinted with the primary color
        // So, we need not convert it into positive int
        if (albumId != -1) albumId = Math.abs(albumId);

        if (null != MediaArtCache.getMediaArtDrawable(albumId))
            return MediaArtCache.getMediaArtDrawable(albumId);
        Drawable drawable = ImageUtil.generateTintedDefaultAlbumArt(context, getTintColor(context, albumId));
        MediaArtCache.addDrawableIfNotPresent(drawable, albumId);
        return drawable;
    }

    @NonNull
    public static Bitmap getDefaultAlbumArtBitmap(@NonNull Context context, long albumId) {
        // -1 represents the primary color tinted album art
        // So, we need not convert it into positive int
        if (albumId != -1) albumId = Math.abs(albumId);

        Drawable drawable = ImageUtil.generateTintedDefaultAlbumArt(context, getTintColor(context, albumId));
        Bitmap bitmap = Bitmap.createBitmap(400, 400, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    @ColorInt
    private static int getTintColor(@NonNull Context context, long albumId) {
        if (albumId == -1) return ThemeColors.getCurrentColorPrimary();
        if (null == mMediaArtColors)
            mMediaArtColors = context.getResources().obtainTypedArray(R.array.album_art_colors);
        return mMediaArtColors.getColor((int) (albumId % mMediaArtColors.length()), 0xFF0000);
    }
}