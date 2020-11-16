package com.hardcodecoder.pulsemusic.themes;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;

import androidx.annotation.ColorInt;

import com.hardcodecoder.pulsemusic.R;

public class ThemeColors {

    private final static int[][] ENABLED_SELECTED_STATES = new int[][]{
            new int[]{android.R.attr.state_enabled, android.R.attr.state_selected}, // [0]
            new int[]{android.R.attr.state_enabled, -android.R.attr.state_selected} // [1]
    };
    @ColorInt
    private static int mCurrentAccentColor;
    @ColorInt
    private static int mAccentColorForCurrentTheme;
    @ColorInt
    private static int mCurrentColorRipple;
    @ColorInt
    private static int mCurrentColorControlNormal;
    @ColorInt
    private static int mCurrentColorOverlay;
    @ColorInt
    private static int mCurrentColorSurface;
    @ColorInt
    private static int mCurrentColorOnSurface;
    @ColorInt
    private static int mCurrentSecondaryTextColor;
    private static boolean mInitialized = false;

    /**
     * @param context used to retrieve theme attributes
     */
    public static void initColors(Context context) {
        if (mInitialized) return;
        mCurrentAccentColor = ThemeManagerUtils.getStoredAccentColor();
        mAccentColorForCurrentTheme = ThemeManagerUtils.isAccentsDesaturated() ?
                ColorUtil.makeColorDesaturated(mCurrentAccentColor) :
                mCurrentAccentColor;
        mCurrentColorRipple = ColorUtil.changeAlphaComponentTo(getAccentColorForCurrentTheme(), 0.2f);

        int[] attrs = new int[]{
                R.attr.colorControlNormal,           // [0]
                R.attr.overlayColor,                 // [1]
                R.attr.colorSurface,                 // [2]
                R.attr.colorOnSurface,               // [3]
                android.R.attr.textColorSecondary,   // [4]
        };
        TypedArray array = context.obtainStyledAttributes(ThemeManagerUtils.getThemeToApply(), attrs);
        mCurrentColorControlNormal = array.getColor(0, 0x8A000000);
        mCurrentColorOverlay = array.getColor(1, 0x0DFFFFFF);
        mCurrentColorSurface = array.getColor(2, 0xFFFFFFFF);
        mCurrentColorOnSurface = array.getColor(3, 0xFF000000);
        mCurrentSecondaryTextColor = array.getColor(4, 0xFF000000);
        array.recycle();
        mInitialized = true;
    }

    /**
     * Follows user preference to whether use a desaturated color or regular color
     *
     * @return color int that is already desaturated based on user preferences
     */
    public static int getAccentColorForCurrentTheme() {
        return mAccentColorForCurrentTheme;
    }

    /**
     * @return the stored accent color selected by the user
     */
    public static int getCurrentAccentColor() {
        return mCurrentAccentColor;
    }

    /**
     * @return the colorControlNormal for current theme
     */
    public static int getCurrentColorControlNormal() {
        return mCurrentColorControlNormal;
    }

    /**
     * @return the ripple color for ui component that has focused/pressed states
     */
    public static int getCurrentColorRipple() {
        return mCurrentColorRipple;
    }

    /**
     * @return color int
     */
    @ColorInt
    public static int getCurrentColorOverlay() {
        return mCurrentColorOverlay;
    }

    /**
     * @return color int
     */
    @ColorInt
    public static int getCurrentColorSurface() {
        return mCurrentColorSurface;
    }

    /**
     * @return color int
     */
    @ColorInt
    public static int getCurrentColorOnSurface() {
        return mCurrentColorOnSurface;
    }

    /**
     * @return color int
     */
    public static int getCurrentSecondaryTextColor() {
        return mCurrentSecondaryTextColor;
    }

    /**
     * Generates ColorStateList from mAccentColorForCurrentTheme
     *
     * @return ColorStateLList
     */
    public static ColorStateList getAccentColorStateList() {
        return ColorStateList.valueOf(mAccentColorForCurrentTheme);
    }

    /**
     * Generates ColorStateList from  mColorControlNormal
     *
     * @return ColorStateList
     */
    public static ColorStateList getColorControlNormalTintList() {
        return ColorStateList.valueOf(mCurrentColorControlNormal);
    }

    /**
     * Helper method to generate a simple color state list that represents
     * Two states: Enabled,Selected & Enabled, Unselected
     *
     * @return ColorStateList object
     */
    public static ColorStateList getEnabledSelectedColorStateList() {
        final int[] colors = new int[ENABLED_SELECTED_STATES.length];
        colors[0] = mAccentColorForCurrentTheme;
        colors[1] = mCurrentColorControlNormal;
        return new ColorStateList(ENABLED_SELECTED_STATES, colors);
    }

    public static void reset() {
        mInitialized = false;
    }
}