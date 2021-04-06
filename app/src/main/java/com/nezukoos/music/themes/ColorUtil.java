package com.nezukoos.music.themes;

import android.graphics.Color;

import androidx.annotation.ColorInt;
import androidx.annotation.FloatRange;
import androidx.core.graphics.ColorUtils;

public class ColorUtil {

    @ColorInt
    public static int mixColors(@ColorInt int color1, @ColorInt int color2, float ratio) {
        float inverseRatio = 1f - ratio;
        float r = Color.red(color1) * ratio + Color.red(color2) * inverseRatio;
        float g = Color.green(color1) * ratio + Color.green(color2) * inverseRatio;
        float b = Color.blue(color1) * ratio + Color.blue(color2) * inverseRatio;
        return Color.rgb((int) r, (int) g, (int) b);
    }

    @ColorInt
    public static int changeColorAlphaTo20(@ColorInt int color1) {
         /*First byte in the color int is responsible for the transparency: 0 - completely transparent, 255 (0xFF) – opaque.
           In the first part ("&" operation) we set the first byte to 0 and left other bytes untouched.
           In the second part we set the first byte to 0x33 which is the 20% of 0xFF (255 * 0.2 ≈ 51).
         */
        return (color1 & 0x00FFFFFF) | 0x33000000;
    }

    @ColorInt
    public static int generatePrimaryTintedColorOverlay() {
        int color = ThemeColors.getCurrentColorPrimary();
         /*First byte in the color int is responsible for the transparency: 0 - completely transparent, 255 (0xFF) – opaque.
           In the first part ("&" operation) we set the first byte to 0 and left other bytes untouched.
           In the second part we set the first byte to 0x0D which is the 5% of 0xFF (255 * 0.05 ≈ 13).
         */
        return (color & 0x00FFFFFF) | 0x40000000;
    }

    @ColorInt
    public static int makeColorDesaturated(@ColorInt int color) {
        return mixColors(color, Color.WHITE, 0.6f);
    }

    /**
     * Calculates a new color by replacing the alpha component of the color by the passed alpha value
     *
     * @param color The original color.
     * @param alpha The additional alpha [0-255].
     * @return The new color.
     */
    @ColorInt
    public static int changeAlphaComponentTo(@ColorInt int color, @FloatRange(from = 0.0f, to = 1.0f) float alpha) {
        int intAlpha = (int) (alpha * 255);
        return ColorUtils.setAlphaComponent(color, intAlpha);
    }
}