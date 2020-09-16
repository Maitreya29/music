package com.hardcodecoder.pulsemusic.utils;

import android.content.Context;
import android.util.DisplayMetrics;

public class DimensionsUtil {

    public static float getDimension(Context context, float dimenDp) {
        float factor = (float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT;
        return factor * dimenDp;
    }

    public static int getDimensionPixelSize(Context context, float dimenDp) {
        float factor = (float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT;
        return Math.round(factor * dimenDp);
    }
}