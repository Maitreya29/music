package com.radiant.music;

import android.app.Application;
import com.radiant.music.utils.LogUtils;

public class RadiantApp extends Application {

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