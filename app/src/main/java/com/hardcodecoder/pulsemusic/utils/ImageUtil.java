package com.hardcodecoder.pulsemusic.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;

import androidx.annotation.ColorInt;
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

    public static Drawable getTintedGradientOverlay(Context context) {
        GradientDrawable drawable = new GradientDrawable(
                GradientDrawable.Orientation.RIGHT_LEFT,
                new int[]{ColorUtil.generatePrimaryTintedColorOverlay(context), android.R.color.transparent});
        drawable.setCornerRadius(DimensionsUtil.getDimension(context, 16));
        drawable.setStroke((int) DimensionsUtil.getDimension(context, 2), ColorUtil.generatePrimaryTintedColorOverlay(context));
        return drawable;
    }
}
