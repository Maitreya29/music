package com.radiant.music.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.radiant.music.R;
import com.radiant.music.themes.ColorUtil;
import com.radiant.music.themes.ThemeColors;
import com.radiant.music.themes.TintHelper;

import java.io.BufferedInputStream;
import java.io.InputStream;

public class ImageUtil {

    @Nullable
    public static Drawable generateTintedDefaultAlbumArt(@NonNull Context context, @ColorInt int color) {
        Drawable drawable = ContextCompat.getDrawable(context, R.drawable.ic_audio_48dp);
        if (drawable != null) {
            drawable.mutate();
            drawable.setTint(color);
        }
        return drawable;
    }

    @Nullable
    public static Drawable getHighlightedItemBackground(@NonNull Context context) {
        Drawable drawable = getRoundedRectangle(context);
        if (null == drawable) return null;
        return TintHelper.setTintTo(
                drawable,
                ThemeColors.getCurrentColorBackgroundHighlight(),
                false);
    }

    @Nullable
    public static Drawable getSelectedItemDrawable(@NonNull Context context) {
        Drawable drawable = getRoundedRectangle(context);
        if (null == drawable) return null;
        return TintHelper.setTintTo(
                drawable,
                ColorUtil.changeAlphaComponentTo(ThemeColors.getCurrentColorPrimary(), 0.26f),
                false);
    }

    @Nullable
    public static Drawable getRoundedRectangle(@NonNull Context context) {
        return ContextCompat.getDrawable(context, R.drawable.shape_rounded_rectangle);
    }

    @NonNull
    public static Bitmap getBitmapFromDrawable(@NonNull Drawable drawable) {
        Bitmap bitmap = Bitmap.createBitmap(400, 400, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    @Nullable
    public static Bitmap getScaledBitmap(@Nullable final InputStream stream, int width, int height) {
        try {
            if (null == stream) {
                return null;
            }

            BufferedInputStream bufferedInputStream = new BufferedInputStream(stream);
            bufferedInputStream.mark(Integer.MAX_VALUE);

            // First decode with inJustDecodeBounds=true to check dimensions
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(bufferedInputStream, null, options);

            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, width, height);

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            bufferedInputStream.reset();
            return Bitmap.createScaledBitmap(
                    BitmapFactory.decodeStream(bufferedInputStream, null, options),
                    width,
                    height, true);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
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