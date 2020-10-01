package com.hardcodecoder.pulsemusic;

import android.app.Application;
import android.content.Intent;
import android.os.Build;

import androidx.core.content.ContextCompat;

import com.hardcodecoder.pulsemusic.shortcuts.AppShortcutsManager;
import com.hardcodecoder.pulsemusic.storage.AppFileManager;
import com.hardcodecoder.pulsemusic.utils.AppSettings;

public class PulseApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AppFileManager.initDataDir(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            final AppShortcutsManager manager = new AppShortcutsManager(getApplicationContext());
            manager.initDynamicShortcuts(false);
        }
        if (AppSettings.isBluetoothDeviceDetectionEnabled(this)) {
            Intent intent = new Intent(this, AudioDeviceService.class);
            ContextCompat.startForegroundService(this, intent);
        }
    }

    @Override
    public void onLowMemory() {
        MediaArtCache.flushCache();
        super.onLowMemory();
    }
}