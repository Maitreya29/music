package com.hardcodecoder.pulsemusic.widgets;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;

import androidx.annotation.NonNull;

import com.hardcodecoder.pulsemusic.Preferences;
import com.hardcodecoder.pulsemusic.utils.AppSettings;

public class PulseWidgetsHelper {

    public static void enableWidgets(@NonNull Context context, boolean enable) {
        if (enable) AppSettings.setWidgetEnabled(context, true);
        else {
            // Want to set widgets disable
            // Disable only if we doo not have any instance of any widget types
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            if (hasInstances(context, appWidgetManager, PulseWidgetNormalLight.class)) return;
            if (hasInstances(context, appWidgetManager, PulseWidgetNormalDark.class)) return;
            // No widget instance found we can disable widgets
            AppSettings.setWidgetEnabled(context, false);
        }
    }

    public static void notifyWidgets(@NonNull Context context) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        if (hasInstances(context, appWidgetManager, PulseWidgetNormalLight.class)) {
            PulseWidgetNormalLight.getInstance().updateWidget(context, appWidgetManager, null);
        }
        if (hasInstances(context, appWidgetManager, PulseWidgetNormalDark.class)) {
            PulseWidgetNormalDark.getInstance().updateWidget(context, appWidgetManager, null);
        }
    }

    public static void resetWidgets(@NonNull Context context) {
        if (AppSettings.isRememberPlaylistEnabled(context) &&
                AppSettings.getWidgetPlayAction(context) == Preferences.ACTION_PLAY_CONTINUE)
            return;
        notifyWidgets(context);
    }

    private static boolean hasInstances(@NonNull Context context, @NonNull AppWidgetManager appWidgetManager, @NonNull Class<?> name) {
        final int[] mAppWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, name));
        return mAppWidgetIds.length > 0;
    }
}