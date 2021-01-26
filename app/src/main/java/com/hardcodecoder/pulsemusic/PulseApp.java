package com.hardcodecoder.pulsemusic;

import android.app.Application;
import android.os.Build;

import com.hardcodecoder.pulsemusic.shortcuts.AppShortcutsManager;
import com.hardcodecoder.pulsemusic.utils.AppSettings;
import com.hardcodecoder.pulsemusic.utils.LogUtils;

public class PulseApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.init(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            final AppShortcutsManager manager = new AppShortcutsManager(getApplicationContext());
            manager.initDynamicShortcuts(false);
        }

        if (AppSettings.isFirstRun(this)) {
            AppSettings.setPlaylistSectionEnabled(this, Preferences.HOME_PLAYLIST_TOP_ALBUMS, true);
            AppSettings.setPlaylistSectionEnabled(this, Preferences.HOME_PLAYLIST_FOR_YOU, true);
            AppSettings.setPlaylistSectionEnabled(this, Preferences.HOME_PLAYLIST_NEW_IN_LIBRARY, true);
            AppSettings.setFirstRun(this, false);
        }
    }

    @Override
    public void onLowMemory() {
        MediaArtCache.flushCache();
        super.onLowMemory();
    }
}