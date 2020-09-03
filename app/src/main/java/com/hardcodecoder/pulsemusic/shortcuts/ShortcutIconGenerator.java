package com.hardcodecoder.pulsemusic.shortcuts;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;

import androidx.annotation.DrawableRes;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.themes.ThemeManagerUtils;

@RequiresApi(Build.VERSION_CODES.N_MR1)
public class ShortcutIconGenerator {

    public static Icon getThemedIcon(Context context, @DrawableRes int drawableRes) {
        Drawable background = ContextCompat.getDrawable(context, R.drawable.ic_app_shortcut_background);
        Drawable foreground = ContextCompat.getDrawable(context, drawableRes);
        if (null != foreground)
            foreground.setTint(ThemeManagerUtils.getSelectedAccentColor());

        LayerDrawable layerDrawable = new LayerDrawable(new Drawable[]{background, foreground});
        return Icon.createWithBitmap(createBitmap(layerDrawable));
    }

    private static Bitmap createBitmap(Drawable drawable) {
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bitmap);
        drawable.setBounds(0, 0, c.getWidth(), c.getHeight());
        drawable.draw(c);
        return bitmap;
    }
}
