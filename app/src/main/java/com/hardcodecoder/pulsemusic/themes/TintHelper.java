package com.hardcodecoder.pulsemusic.themes;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class TintHelper {

    public static void setAccentTintTo(ImageView imageView) {
        imageView.setImageTintList(ColorStateList.valueOf(ThemeColors.getAccentColorForCurrentTheme()));
    }

    public static Drawable setAccentTintTo(Drawable drawable, boolean mutate) {
        if (mutate) drawable.mutate();
        drawable.setTint(ThemeColors.getAccentColorForCurrentTheme());
        return drawable;
    }

    public static void setAccentTintTo(FloatingActionButton fab) {
        fab.setBackgroundTintList(ThemeColors.getAccentColorStateList());
    }

    public static void setAccentTintToMaterialButton(MaterialButton materialButton) {
        materialButton.setBackgroundTintList(ColorStateList.valueOf(ThemeColors.getAccentColorForCurrentTheme()));
    }

    public static void setAccentTintToMaterialOutlineButton(MaterialButton materialButton) {
        ColorStateList stateList = ColorStateList.valueOf(ThemeColors.getAccentColorForCurrentTheme());
        materialButton.setRippleColor(ColorStateList.valueOf(
                ColorUtil.changeAlphaComponentTo(ThemeColors.getCurrentAccentColor(), 0.26f)));
        materialButton.setTextColor(stateList);
        materialButton.setIconTint(stateList);
        materialButton.setStrokeColor(stateList);
    }
}