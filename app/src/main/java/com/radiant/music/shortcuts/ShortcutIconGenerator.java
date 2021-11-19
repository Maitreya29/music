package com.radiant.music.shortcuts;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import com.radiant.music.R;
import com.radiant.music.utils.DimensionsUtil;

@RequiresApi(Build.VERSION_CODES.N_MR1)
public class ShortcutIconGenerator {

    @NonNull
    public static Icon getThemedIcon(@NonNull Context context, @DrawableRes int drawableRes) {
        Drawable background = ContextCompat.getDrawable(context, R.drawable.ic_app_shortcut_background);
        Drawable foreground = ContextCompat.getDrawable(context, drawableRes);
        if (null != background)
            background.setTint(ShortcutsThemeManager.getBackgroundColorForIcons());
        if (null != foreground)
            foreground.setTint(ShortcutsThemeManager.getAccentColorForIcons());

        LayerDrawable layerDrawable = new LayerDrawable(new Drawable[]{background, foreground});
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            final int padding = DimensionsUtil.getDimensionPixelSize(context, 16);
            layerDrawable.setLayerInset(1, padding, padding, padding, padding);
            return Icon.createWithAdaptiveBitmap(createBitmap(layerDrawable));
        }
        return Icon.createWithBitmap(createBitmap(layerDrawable));
    }

    @NonNull
    private static Bitmap createBitmap(@NonNull Drawable drawable) {
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bitmap);
        drawable.setBounds(0, 0, c.getWidth(), c.getHeight());
        drawable.draw(c);
        return bitmap;
    }
}