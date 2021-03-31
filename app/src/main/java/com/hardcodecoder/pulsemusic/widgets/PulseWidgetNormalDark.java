package com.hardcodecoder.pulsemusic.widgets;

import android.content.Context;
import android.content.res.Resources;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;

import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.themes.ColorUtil;
import com.hardcodecoder.pulsemusic.utils.AppSettings;
import com.hardcodecoder.pulsemusic.widgets.base.PulseWidgetNormal;

public class PulseWidgetNormalDark extends PulseWidgetNormal {

    public static final String TAG = PulseWidgetNormalDark.class.getSimpleName();
    private static PulseWidgetNormalDark mInstance;

    public PulseWidgetNormalDark() {
        mInstance = this;
    }

    @NonNull
    public static PulseWidgetNormalDark getInstance() {
        if (null == mInstance) new PulseWidgetNormalDark();
        return mInstance;
    }

    @Override
    public void applyTheme(@NonNull Context context, @NonNull RemoteViews layoutView) {
        final float alpha = (float) AppSettings.getWidgetBackgroundAlpha(context) / 100;
        final Resources res = context.getResources();
        int backgroundColor = res.getColor(R.color.widget_background_dark);
        int widgetBackgroundColor = ColorUtil.changeAlphaComponentTo(backgroundColor, alpha);
        float secondaryAlpha = (alpha <= 0.8f) ? alpha + 0.2f : 1.0f;
        int secondaryBackgroundColor = ColorUtil.changeAlphaComponentTo(backgroundColor, secondaryAlpha);
        int primaryTextColor = res.getColor(R.color.widget_primary_text_color_dark);
        int iconTintColor = res.getColor(R.color.widget_button_tint_dark);

        if (alpha == 1.0f) {
            primaryTextColor = ColorUtil.changeAlphaComponentTo(primaryTextColor, 0.84f);
            iconTintColor = ColorUtil.changeAlphaComponentTo(iconTintColor, 0.84f);
        }

        layoutView.setTextColor(R.id.widget_track_title, primaryTextColor);
        layoutView.setInt(R.id.widget_normal_background, "setBackgroundColor", widgetBackgroundColor);
        layoutView.setInt(R.id.widget_media_art, "setBackgroundColor", secondaryBackgroundColor);

        layoutView.setInt(R.id.widget_play_pause_btn, "setBackgroundResource", R.drawable.widget_button_selector_dark);
        layoutView.setInt(R.id.widget_next_btn, "setBackgroundResource", R.drawable.widget_button_selector_dark);
        layoutView.setInt(R.id.widget_previous_btn, "setBackgroundResource", R.drawable.widget_button_selector_dark);

        layoutView.setInt(R.id.widget_play_pause_btn, "setColorFilter", iconTintColor);
        layoutView.setInt(R.id.widget_next_btn, "setColorFilter", iconTintColor);
        layoutView.setInt(R.id.widget_previous_btn, "setColorFilter", iconTintColor);
    }
}