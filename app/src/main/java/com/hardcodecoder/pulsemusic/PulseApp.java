package com.hardcodecoder.pulsemusic;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.core.content.ContextCompat;

import com.hardcodecoder.pulsemusic.shortcuts.AppShortcutsManager;
import com.hardcodecoder.pulsemusic.storage.AppFileManager;
import com.hardcodecoder.pulsemusic.themes.ThemeManagerUtils;
import com.hardcodecoder.pulsemusic.utils.AppSettings;

public class PulseApp extends Application {

    private SharedPreferences.OnSharedPreferenceChangeListener mListener;

    @Override
    public void onCreate() {
        super.onCreate();
        AppFileManager.initDataDir(this);
        ThemeManagerUtils.init(getApplicationContext());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            final AppShortcutsManager manager = new AppShortcutsManager(getApplicationContext());
            manager.initDynamicShortcuts(false);
            mListener = (sharedPreferences, key) -> {
                if (key.equals(Preferences.ACCENTS_COLOR_PRESET_KEY) ||
                        key.equals(Preferences.ACCENTS_COLOR_CUSTOM_KEY) ||
                        key.equals(Preferences.ACCENTS_COLOR_DESATURATED_KEY) ||
                        key.equals(Preferences.ACCENTS_MODE_USING_PRESET_KEY)) {
                    manager.initDynamicShortcuts(true);
                }
            };
            getSharedPreferences(Preferences.PULSE_THEMES_PREFS, MODE_PRIVATE).registerOnSharedPreferenceChangeListener(mListener);
        }
        if (AppSettings.isBluetoothDeviceDetectionEnabled(this)) {
            Intent intent = new Intent(this, BDS.class);
            ContextCompat.startForegroundService(this, intent);
        }
    }

    @Override
    public void onLowMemory() {
        getSharedPreferences(Preferences.PULSE_THEMES_PREFS, MODE_PRIVATE).unregisterOnSharedPreferenceChangeListener(mListener);
        MediaArtCache.flushCache();
        super.onLowMemory();
    }
}