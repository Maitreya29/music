package com.hardcodecoder.pulsemusic;

import android.app.Application;
import com.hardcodecoder.pulsemusic.utils.LogUtils;

public class PulseApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.init(this);
    }

    @Override
    public void onLowMemory() {
        MediaArtCache.flushCache();
        super.onLowMemory();
    }
}