package com.radiant.music.widgets;

import android.content.Context;
import android.content.res.Resources;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;

import com.radiant.music.R;
import com.radiant.music.themes.ColorUtil;
import com.radiant.music.utils.AppSettings;
import com.radiant.music.widgets.base.RadiantWidgetNormal;

public class RadiantWidgetNormalLight extends RadiantWidgetNormal {

    public static final String TAG = RadiantWidgetNormalLight.class.getSimpleName();
    private static RadiantWidgetNormalLight mInstance;

    public RadiantWidgetNormalLight() {
        mInstance = this;
    }

    @NonNull
    public static RadiantWidgetNormalLight getInstance() {
        if (null == mInstance) new RadiantWidgetNormalLight();
        return mInstance;
    }

    @Override
    public void applyTheme(@NonNull Context context, @NonNull RemoteViews layoutView) {
        final float alpha = (float) AppSettings.getWidgetBackgroundAlpha(context) / 100;
        final Resources res = context.getResources();
        int backgroundColor = res.getColor(R.color.widget_background_light);
        int widgetBackgroundColor = ColorUtil.changeAlphaComponentTo(backgroundColor, alpha);
        float secondaryAlpha = (alpha <= 0.8f) ? alpha + 0.2f : 1.0f;
        int secondaryBackgroundColor = ColorUtil.changeAlphaComponentTo(backgroundColor, secondaryAlpha);
        int primaryTextColor = res.getColor(R.color.widget_primary_text_color_light);
        int iconTintColor = res.getColor(R.color.widget_button_tint_light);

        if (alpha == 1.0f) {
            primaryTextColor = ColorUtil.changeAlphaComponentTo(primaryTextColor, 0.84f);
            iconTintColor = ColorUtil.changeAlphaComponentTo(iconTintColor, 0.84f);
        }

        layoutView.setTextColor(R.id.widget_track_title, primaryTextColor);
        layoutView.setInt(R.id.widget_normal_background, "setBackgroundColor", widgetBackgroundColor);
        layoutView.setInt(R.id.widget_media_art, "setBackgroundColor", secondaryBackgroundColor);

        layoutView.setInt(R.id.widget_play_pause_btn, "setBackgroundResource", R.drawable.widget_button_selector_light);
        layoutView.setInt(R.id.widget_next_btn, "setBackgroundResource", R.drawable.widget_button_selector_light);
        layoutView.setInt(R.id.widget_previous_btn, "setBackgroundResource", R.drawable.widget_button_selector_light);

        layoutView.setInt(R.id.widget_play_pause_btn, "setColorFilter", iconTintColor);
        layoutView.setInt(R.id.widget_next_btn, "setColorFilter", iconTintColor);
        layoutView.setInt(R.id.widget_previous_btn, "setColorFilter", iconTintColor);
    }
}