package com.nezukoos.music;

import android.app.Application;
import com.nezukoos.music.utils.LogUtils;

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