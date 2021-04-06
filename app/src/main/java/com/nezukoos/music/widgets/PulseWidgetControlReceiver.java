package com.nezukoos.music.widgets;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.session.MediaController;
import android.media.session.PlaybackState;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.nezukoos.music.playback.PulseController;
import com.nezukoos.music.playback.PulseController.PulseRemote;
import com.nezukoos.music.service.PMS;
import com.nezukoos.music.utils.AppSettings;
import com.nezukoos.music.utils.LogUtils;

public class PulseWidgetControlReceiver extends BroadcastReceiver {

    public static final String TAG = PulseWidgetControlReceiver.class.getSimpleName();
    public static final String ACTION_PLAY_PAUSE = "com.nezukoos.music.widgets:play_pause";
    public static final String ACTION_SKIP_NEXT = "com.nezukoos.music.widgets:skip_next";
    public static final String ACTION_SKIP_PREV = "com.nezukoos.music.widgets:skip_prev";

    @Override
    public void onReceive(@NonNull Context context, @NonNull Intent intent) {
        if (intent.getAction() == null) return;
        PulseController pulseController = PulseController.getInstance();
        PulseRemote remote = pulseController.getRemote();
        switch (intent.getAction()) {
            case ACTION_SKIP_NEXT:
                remote.skipToNextTrack();
                break;
            case ACTION_SKIP_PREV:
                remote.skipToPreviousTrack();
                break;
            case ACTION_PLAY_PAUSE:
                MediaController controller = pulseController.getController();
                if (null == controller || null == controller.getPlaybackState()) {
                    Intent serviceIntent = new Intent(context, PMS.class);
                    serviceIntent.setAction(PMS.ACTION_PLAY_CONTINUE);
                    serviceIntent.putExtra(PMS.KEY_PLAY_CONTINUE, AppSettings.getWidgetPlayAction(context));
                    ContextCompat.startForegroundService(context, serviceIntent);
                    return;
                }
                PlaybackState state = controller.getPlaybackState();
                if (state.getState() == PlaybackState.STATE_PLAYING) remote.pause();
                else remote.play();
                break;
            default:
                LogUtils.logInfo(TAG, "onReceived: " + intent.getAction());
        }
    }
}