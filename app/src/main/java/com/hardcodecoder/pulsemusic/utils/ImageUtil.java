package com.hardcodecoder.pulsemusic.utils;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.themes.ColorUtil;
import com.hardcodecoder.pulsemusic.themes.ThemeColors;
import com.hardcodecoder.pulsemusic.themes.TintHelper;

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

    @NonNull
    public static Drawable getHighlightedItemBackground(@NonNull Context context) {
        return TintHelper.setTintTo(
                ContextCompat.getDrawable(context, R.drawable.shape_rounded_rectangle),
                ThemeColors.getCurrentColorBackgroundHighlight(),
                false);
    }

    public static int calculateInSampleSize(@NonNull BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }
}