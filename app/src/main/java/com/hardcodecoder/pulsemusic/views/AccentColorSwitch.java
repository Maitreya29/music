package com.hardcodecoder.pulsemusic.views;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Build;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.widget.TextViewCompat;

import com.google.android.material.color.MaterialColors;
import com.google.android.material.elevation.ElevationOverlayProvider;
import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.themes.ThemeColors;

public class AccentColorSwitch extends SwitchCompat {

    private static final int[][] ENABLED_CHECKED_STATES = new int[][]{
            new int[]{android.R.attr.state_enabled, android.R.attr.state_checked}, // [0]
            new int[]{android.R.attr.state_enabled, -android.R.attr.state_checked}, // [1]
            new int[]{-android.R.attr.state_enabled, android.R.attr.state_checked}, // [2]
            new int[]{-android.R.attr.state_enabled, -android.R.attr.state_checked} // [3]
    };

    @Nullable
    private ColorStateList materialThemeColorsThumbTintList;
    @Nullable
    private ColorStateList materialThemeColorsTrackTintList;

    public AccentColorSwitch(@NonNull Context context) {
        super(context);
    }

    public AccentColorSwitch(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AccentColorSwitch(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        if (getThumbTintList() == null) {
            setThumbTintList(getMaterialThemeColorsThumbTintList());
        }
        if (getTrackTintList() == null) {
            setTrackTintList(getMaterialThemeColorsTrackTintList());
        }
    }

    // Hijacked SwitchMaterial code for thumb tint
    // Instead of using R.attr.colorControlActivated, use custom accent color
    private ColorStateList getMaterialThemeColorsThumbTintList() {
        if (materialThemeColorsThumbTintList == null) {
            int colorSurface = ThemeColors.getCurrentColorSurface();
            int colorControlActivated = ThemeColors.getAccentColorForCurrentTheme();
            int[] switchThumbColorsList = new int[ENABLED_CHECKED_STATES.length];

            float thumbElevation = getResources().getDimension(R.dimen.rounding_radius_4dp);
            ElevationOverlayProvider elevationOverlayProvider = new ElevationOverlayProvider(getContext());
            int colorThumbOff = elevationOverlayProvider.compositeOverlayIfNeeded(colorSurface, thumbElevation);

            switchThumbColorsList[0] = MaterialColors.layer(colorSurface, colorControlActivated, MaterialColors.ALPHA_FULL);
            switchThumbColorsList[1] = colorThumbOff;
            switchThumbColorsList[2] = MaterialColors.layer(colorSurface, colorControlActivated, MaterialColors.ALPHA_DISABLED);
            switchThumbColorsList[3] = colorThumbOff;

            materialThemeColorsThumbTintList = new ColorStateList(ENABLED_CHECKED_STATES, switchThumbColorsList);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                TextViewCompat.setCompoundDrawableTintList(this, materialThemeColorsThumbTintList);
            }
        }
        return materialThemeColorsThumbTintList;
    }

    // Hijacked SwitchMaterial code for track tint
    // Instead of using R.attr.colorControlActivated, use custom accent color
    private ColorStateList getMaterialThemeColorsTrackTintList() {
        if (materialThemeColorsTrackTintList == null) {
            int[] switchTrackColorsList = new int[ENABLED_CHECKED_STATES.length];
            int colorSurface = ThemeColors.getCurrentColorSurface();
            int colorControlActivated = ThemeColors.getAccentColorForCurrentTheme();
            int colorOnSurface = ThemeColors.getCurrentColorOnSurface();

            switchTrackColorsList[0] = MaterialColors.layer(colorSurface, colorControlActivated, MaterialColors.ALPHA_MEDIUM);
            switchTrackColorsList[1] = MaterialColors.layer(colorSurface, colorOnSurface, MaterialColors.ALPHA_LOW);
            switchTrackColorsList[2] = MaterialColors.layer(colorSurface, colorControlActivated, MaterialColors.ALPHA_DISABLED_LOW);
            switchTrackColorsList[3] = MaterialColors.layer(colorSurface, colorOnSurface, MaterialColors.ALPHA_DISABLED_LOW);
            materialThemeColorsTrackTintList = new ColorStateList(ENABLED_CHECKED_STATES, switchTrackColorsList);
        }
        return materialThemeColorsTrackTintList;
    }
}