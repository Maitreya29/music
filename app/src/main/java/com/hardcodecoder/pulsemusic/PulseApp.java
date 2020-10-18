package com.hardcodecoder.pulsemusic;

import android.app.Application;
import android.os.Build;

import com.hardcodecoder.pulsemusic.shortcuts.AppShortcutsManager;

public class PulseApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            final AppShortcutsManager manager = new AppShortcutsManager(getApplicationContext());
            manager.initDynamicShortcuts(false);
        }
    }

    @Override
    public void onLowMemory() {
        MediaArtCache.flushCache();
        super.onLowMemory();
    }
}