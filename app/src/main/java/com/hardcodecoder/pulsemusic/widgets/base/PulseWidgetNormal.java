package com.hardcodecoder.pulsemusic.widgets.base;

import android.app.PendingIntent;
import android.content.Context;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;

import com.hardcodecoder.pulsemusic.R;

import static com.hardcodecoder.pulsemusic.widgets.PulseWidgetControlReceiver.ACTION_PLAY_PAUSE;
import static com.hardcodecoder.pulsemusic.widgets.PulseWidgetControlReceiver.ACTION_SKIP_NEXT;
import static com.hardcodecoder.pulsemusic.widgets.PulseWidgetControlReceiver.ACTION_SKIP_PREV;

public abstract class PulseWidgetNormal extends PulseWidgetBase {

    @Override
    public int getLayoutId() {
        return R.layout.pulse_widget_normal;
    }

    @Override
    public void setUpLayout(@NonNull Context context, @NonNull RemoteViews layoutView, @NonNull PendingIntent defaultClickAction) {
        layoutView.setOnClickPendingIntent(R.id.widget_media_art, defaultClickAction);
        layoutView.setImageViewBitmap(R.id.widget_media_art, getAlbumArt(context));
        layoutView.setImageViewResource(R.id.widget_previous_btn, R.drawable.ic_round_skip_previous);
        layoutView.setImageViewResource(R.id.widget_play_pause_btn, isPlaying() ? R.drawable.ic_round_pause : R.drawable.ic_round_play);
        layoutView.setImageViewResource(R.id.widget_next_btn, R.drawable.ic_round_skip_next);
        layoutView.setTextViewText(R.id.widget_track_title, getTitle(context));

        applyTheme(context, layoutView);

        layoutView.setOnClickPendingIntent(R.id.widget_previous_btn, buildPendingControlBroadcast(context, ACTION_SKIP_PREV));
        layoutView.setOnClickPendingIntent(R.id.widget_play_pause_btn, buildPendingControlBroadcast(context, ACTION_PLAY_PAUSE));
        layoutView.setOnClickPendingIntent(R.id.widget_next_btn, buildPendingControlBroadcast(context, ACTION_SKIP_NEXT));
    }

    public abstract void applyTheme(@NonNull Context context, @NonNull RemoteViews layoutView);
}