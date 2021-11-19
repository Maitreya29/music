package com.radiant.music.widgets;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.session.MediaController;
import android.media.session.PlaybackState;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.radiant.music.playback.RadiantController;
import com.radiant.music.playback.RadiantController.RadiantRemote;
import com.radiant.music.service.PMS;
import com.radiant.music.utils.AppSettings;
import com.radiant.music.utils.LogUtils;

public class RadiantWidgetControlReceiver extends BroadcastReceiver {

    public static final String TAG = RadiantWidgetControlReceiver.class.getSimpleName();
    public static final String ACTION_PLAY_PAUSE = "com.radiant.music.widgets:play_pause";
    public static final String ACTION_SKIP_NEXT = "com.radiant.music.widgets:skip_next";
    public static final String ACTION_SKIP_PREV = "com.radiant.music.widgets:skip_prev";

    @Override
    public void onReceive(@NonNull Context context, @NonNull Intent intent) {
        if (intent.getAction() == null) return;
        RadiantController radiantController = RadiantController.getInstance();
        RadiantRemote remote = radiantController.getRemote();
        switch (intent.getAction()) {
            case ACTION_SKIP_NEXT:
                remote.skipToNextTrack();
                break;
            case ACTION_SKIP_PREV:
                remote.skipToPreviousTrack();
                break;
            case ACTION_PLAY_PAUSE:
                MediaController controller = radiantController.getController();
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