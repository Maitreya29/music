package com.hardcodecoder.pulsemusic.loaders;

import android.content.Context;
import android.os.Bundle;

import com.hardcodecoder.pulsemusic.helper.DataModelHelper;
import com.hardcodecoder.pulsemusic.playback.PlaybackManager;
import com.hardcodecoder.pulsemusic.utils.AppSettings;

import java.util.concurrent.Callable;

public class LastTrackLoader implements Callable<Bundle> {

    private final Context mContext;

    public LastTrackLoader(Context context) {
        mContext = context;
    }

    @Override
    public Bundle call() {
        final int id = AppSettings.getLastTrackId(mContext);
        final int position = (int) AppSettings.getLastTrackPosition(mContext);
        Bundle bundle = new Bundle();
        bundle.putSerializable(PlaybackManager.TRACK_ITEM, DataModelHelper.getModelFromId(id));
        bundle.putInt(PlaybackManager.PLAYBACK_POSITION, position);
        return bundle;
    }
}