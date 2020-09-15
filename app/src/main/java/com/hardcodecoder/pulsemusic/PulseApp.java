package com.hardcodecoder.pulsemusic;

import android.app.Application;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import com.hardcodecoder.pulsemusic.shortcuts.AppShortcutsManager;
import com.hardcodecoder.pulsemusic.receivers.BluetoothBroadcastReceiver;
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
                if (key.equals(Preferences.ACCENTS_COLOR_KEY)) manager.initDynamicShortcuts(true);
            };
            getSharedPreferences(Preferences.ACCENTS_COLOR_KEY, MODE_PRIVATE).registerOnSharedPreferenceChangeListener(mListener);
        }
        if (AppSettings.isBluetoothDeviceDetection(this)){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(new Intent(getApplicationContext(), BDS.class));
            }
            else{
                startService(new Intent(getApplicationContext(), BDS.class));
            }
        }
    }

    @Override
    public void onLowMemory() {
        getSharedPreferences(Preferences.ACCENTS_COLOR_KEY, MODE_PRIVATE).unregisterOnSharedPreferenceChangeListener(mListener);
        MediaArtCache.flushCache();
        super.onLowMemory();
    }
}
