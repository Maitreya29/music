package com.hardcodecoder.pulsemusic.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.themes.ColorUtil;

public class ImageUtil {

    public static Drawable generateTintedDefaultAlbumArt(Context context, @ColorInt int color) {
        Drawable drawable = ContextCompat.getDrawable(context, R.drawable.ic_audio_48dp);
        if (drawable != null) {
            drawable.mutate();
            drawable.setTint(color);
        }
        return drawable;
    }

    @NonNull
    public static Drawable getAccentTintedSelectedItemBackground(Context context) {
        GradientDrawable drawable = new GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                new int[]{
                        android.R.color.transparent,
                        ColorUtil.generatePrimaryTintedColorOverlay()});
        drawable.setCornerRadius(DimensionsUtil.getDimension(context, 8));
        drawable.setGradientCenter(34, 0);
        return drawable;
    }
}