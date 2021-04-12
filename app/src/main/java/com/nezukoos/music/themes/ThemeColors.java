package com.nezukoos.music.themes;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;

import com.nezukoos.music.R;

public class ThemeColors {

    @ColorInt
    private static int mCurrentColorWindowBackground;
    @ColorInt
    private static int mCurrentColorSurface;
    @ColorInt
    private static int mCurrentColorOnSurface;
    /**
     * This is used both as the accent and primary color of the app
     * This represents the actual color value the user selected
     * for accent color using the accent picker
     */
    @ColorInt
    private static int mPrimarySelectedColor;
    /**
     * This is used both as the accent and primary color of the app
     * This represents the desaturated mPrimarySelectedColor (if dark theme)
     * or the mPrimarySelectedColor (if light theme) that will be used in UI
     */
    @ColorInt
    private static int mCurrentColorPrimary;
    @ColorInt
    private static int mCurrentColorOnPrimary;
    @ColorInt
    private static int mCurrentColorGenericRipple;
    @ColorInt
    private static int mCurrentColorControlNormal;
    @ColorInt
    private static int mCurrentColorControlHighlight;
    @ColorInt
    private static int mCurrentColorBackgroundHighlight;
    @ColorInt
    private static int mCurrentPrimaryTextColor;
    @ColorInt
    private static int mCurrentSecondaryTextColor;
    private static boolean mInitialized = false;

    /**
     * @param context used to retrieve theme attributes
     */
    public static void initColors(Context context) {
        if (mInitialized) return;
        mPrimarySelectedColor = ThemeManagerUtils.getStoredAccentColor();
        mCurrentColorPrimary = ThemeManagerUtils.isAccentsDesaturated() ?
                ColorUtil.makeColorDesaturated(mPrimarySelectedColor) :
                mPrimarySelectedColor;
        mCurrentColorGenericRipple = ColorUtil.changeAlphaComponentTo(getCurrentColorPrimary(), 0.20f);

        int[] attrs = new int[]{
                R.attr.colorControlNormal,           // [0]
                R.attr.colorControlHighlight,        // [1]
                R.attr.colorSurface,                 // [2]
                R.attr.colorOnSurface,               // [3]
                R.attr.windowBackgroundColor,        // [4]
                R.attr.colorOnPrimary,               // [5]
                android.R.attr.textColorPrimary,     // [6]
                android.R.attr.textColorSecondary,   // [7]
        };
        TypedArray array = context.obtainStyledAttributes(ThemeManagerUtils.getThemeToApply(), attrs);
        mCurrentColorControlNormal = array.getColor(0, 0xFFF2F2F2);
        mCurrentColorControlHighlight = array.getColor(1, 0x0DFFFFFF);
        mCurrentColorSurface = array.getColor(2, 0xFFFFFFFF);
        mCurrentColorOnSurface = array.getColor(3, 0xFF000000);
        mCurrentColorWindowBackground = array.getColor(4, 0xFFFFFFFF);
        mCurrentColorOnPrimary = array.getColor(5, 0xFFFFFFFF);
        mCurrentPrimaryTextColor = array.getColor(6, 0xFF000000);
        mCurrentSecondaryTextColor = array.getColor(7, 0xFF000000);
        array.recycle();
        mCurrentColorBackgroundHighlight = ThemeManagerUtils.isDarkModeEnabled() ?
                mCurrentColorSurface : 0xFFF2F2F2;
        mInitialized = true;
    }

    /**
     * Follows user preference to whether use a desaturated color or regular color
     *
     * @return color int that is already desaturated based on user preferences
     */
    public static int getCurrentColorPrimary() {
        return mCurrentColorPrimary;
    }

    /**
     * @return the stored accent color selected by the user
     */
    public static int getSelectedAccentColor() {
        return mPrimarySelectedColor;
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
    public static int getCurrentColorGenericRipple() {
        return mCurrentColorGenericRipple;
    }

    /**
     * @return color int
     */
    @ColorInt
    public static int getCurrentColorControlHighlight() {
        return mCurrentColorControlHighlight;
    }

    /**
     * @return color int
     */
    @ColorInt
    public static int getCurrentColorBackgroundHighlight() {
        return mCurrentColorBackgroundHighlight;
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
     * @return the current theme color applied to window background
     */
    public static int getCurrentColorWindowBackground() {
        return mCurrentColorWindowBackground;
    }

    /**
     * @return the color used on top of primary/accent color
     */
    public static int getCurrentColorOnPrimary() {
        return mCurrentColorOnPrimary;
    }

    /**
     * @return color int
     */
    public static int getCurrentPrimaryTextColor() {
        return mCurrentPrimaryTextColor;
    }

    /**
     * @return color int
     */
    public static int getCurrentSecondaryTextColor() {
        return mCurrentSecondaryTextColor;
    }

    /**
     * Generates ColorStateList from mCurrentColorPrimary
     *
     * @return ColorStateLList
     */
    @NonNull
    public static ColorStateList getPrimaryColorStateList() {
        return ColorStateList.valueOf(mCurrentColorPrimary);
    }

    /**
     * Generates ColorStateList from  mColorControlNormal
     *
     * @return ColorStateList
     */
    @NonNull
    public static ColorStateList getColorControlNormalTintList() {
        return ColorStateList.valueOf(mCurrentColorControlNormal);
    }


    /**
     * Helper method to generate a ColorStateList that represents
     * the ripple color of BottomNavigationView
     *
     * @return ColorStateList
     */
    @NonNull
    public static ColorStateList getBottomNavigationViewRippleColor() {
        int[][] states = new int[][]{
                // selected
                new int[]{android.R.attr.state_selected, android.R.attr.state_pressed},
                new int[]{android.R.attr.state_selected, android.R.attr.state_focused, android.R.attr.state_hovered},
                new int[]{android.R.attr.state_selected, android.R.attr.state_focused},
                new int[]{android.R.attr.state_selected, android.R.attr.state_hovered},
                new int[]{android.R.attr.state_selected},

                // unselected
                new int[]{android.R.attr.state_pressed},
                new int[]{android.R.attr.state_focused, android.R.attr.state_hovered},
                new int[]{android.R.attr.state_focused},
                new int[]{android.R.attr.state_hovered},
                new int[]{},
        };

        int[] colors = new int[]{
                // Selected
                ColorUtil.changeAlphaComponentTo(mCurrentColorPrimary, 0.08f),
                ColorUtil.changeAlphaComponentTo(mCurrentColorPrimary, 0.16f),
                ColorUtil.changeAlphaComponentTo(mCurrentColorPrimary, 0.12f),
                ColorUtil.changeAlphaComponentTo(mCurrentColorPrimary, 0.04f),
                ColorUtil.changeAlphaComponentTo(mCurrentColorPrimary, 0.00f),

                // Unselected
                ColorUtil.changeAlphaComponentTo(mCurrentColorOnSurface, 0.08f),
                ColorUtil.changeAlphaComponentTo(mCurrentColorOnSurface, 0.16f),
                ColorUtil.changeAlphaComponentTo(mCurrentColorOnSurface, 0.12f),
                ColorUtil.changeAlphaComponentTo(mCurrentColorOnSurface, 0.04f),
                ColorUtil.changeAlphaComponentTo(mCurrentColorOnSurface, 0.00f),
        };
        return new ColorStateList(states, colors);
    }

    /**
     * Helper method to generate a ColorStateList that represents
     * the ripple color of default styled MaterialButton
     *
     * @return ColorStateList
     */
    @NonNull
    public static ColorStateList getMaterialButtonRippleColor() {
        int[][] states = new int[][]{
                new int[]{android.R.attr.state_pressed},
                new int[]{android.R.attr.state_focused, android.R.attr.state_hovered},
                new int[]{android.R.attr.state_focused},
                new int[]{android.R.attr.state_hovered},
                new int[]{},
        };

        final int colorFocusedHovered = ColorUtil.changeAlphaComponentTo(mCurrentColorOnPrimary, 0.40f);
        final int colorPressedDefault = ColorUtil.changeAlphaComponentTo(mCurrentColorOnPrimary, 0.24f);

        int[] colors = new int[]{
                colorPressedDefault,
                colorFocusedHovered,
                colorFocusedHovered,
                colorFocusedHovered,
                colorPressedDefault,
        };
        return new ColorStateList(states, colors);
    }

    /**
     * Helper method to generate a ColorStateList that represents
     * the ripple color of text and outline styled MaterialButton
     *
     * @return ColorStateList
     */
    @NonNull
    public static ColorStateList getMaterialButtonTextRippleColor() {
        int[][] states = new int[][]{
                new int[]{android.R.attr.state_pressed},
                new int[]{android.R.attr.state_focused, android.R.attr.state_hovered},
                new int[]{android.R.attr.state_focused},
                new int[]{android.R.attr.state_hovered},
                new int[]{},
        };

        final int colorFocusedHovered = ColorUtil.changeAlphaComponentTo(mCurrentColorPrimary, 0.12f);
        final int colorPressedDefault = ColorUtil.changeAlphaComponentTo(mCurrentColorPrimary, 0.20f);

        int[] colors = new int[]{
                colorPressedDefault,
                colorFocusedHovered,
                colorFocusedHovered,
                colorFocusedHovered,
                colorPressedDefault
        };
        return new ColorStateList(states, colors);
    }

    /**
     * Helper method to generate a simple color state list that represents
     * Two states: Enabled,Selected & Enabled, Unselected
     *
     * @return ColorStateList object
     */
    @NonNull
    public static ColorStateList getEnabledSelectedColorStateList() {
        int[][] enabledSelectedStates = new int[][]{
                new int[]{android.R.attr.state_enabled, android.R.attr.state_selected}, // [0]
                new int[]{android.R.attr.state_enabled, -android.R.attr.state_selected} // [1]
        };

        final int[] colors = new int[enabledSelectedStates.length];

        colors[0] = R.color.card;
        colors[1] = mCurrentColorControlNormal;
        return new ColorStateList(enabledSelectedStates, colors);
    }

    /**
     * Helper method to generate a simple color state list that represents
     * Two states: Enabled,Selected & Enabled, Unselected
     *
     * @return ColorStateList object
     */
    @NonNull
    public static ColorStateList getEnabledSelectedColorStateList1() {
        int[][] enabledSelectedStates = new int[][]{
                new int[]{android.R.attr.state_enabled, android.R.attr.state_selected}, // [0]
                new int[]{android.R.attr.state_enabled, -android.R.attr.state_selected} // [1]
        };

        final int[] colors = new int[enabledSelectedStates.length];

        colors[0] = mCurrentColorPrimary;
        colors[1] = mCurrentColorPrimary;
        return new ColorStateList(enabledSelectedStates, colors);
    }

    /**
     * Helper method to generate a simple color state list that represents
     * Two states: Enabled,Selected & Enabled, Unselected
     *
     * @return ColorStateList object
     */
    @NonNull
    public static ColorStateList getEnabledSelectedColorStateList1() {
        int[][] enabledSelectedStates = new int[][]{
                new int[]{android.R.attr.state_enabled, android.R.attr.state_selected}, // [0]
                new int[]{android.R.attr.state_enabled, -android.R.attr.state_selected} // [1]
        };

        final int[] colors = new int[enabledSelectedStates.length];

        colors[0] = mCurrentColorPrimary;
        colors[1] = mCurrentColorControlNormal;
        return new ColorStateList(enabledSelectedStates, colors);
    }

    /**
     * Helper method to generate a ColorStateList that represents
     * the selected an unselected state for view outline decorations
     * cuh as those seen in outline styled MaterialButton
     *
     * @return ColorStateList
     */
    @NonNull
    public static ColorStateList getMaterialOutlineColorSelector() {
        int[][] states = new int[][]{
                new int[]{android.R.attr.state_checked},
                new int[]{-android.R.attr.state_checked},
        };

        int[] colors = new int[]{
                mCurrentColorPrimary,
                ColorUtil.changeAlphaComponentTo(mCurrentColorOnSurface, 0.12f),
        };
        return new ColorStateList(states, colors);
    }

    public static void reset() {
        mInitialized = false;
    }
}