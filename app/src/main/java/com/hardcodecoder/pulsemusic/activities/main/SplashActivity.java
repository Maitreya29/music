package com.hardcodecoder.pulsemusic.activities.main;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.hardcodecoder.pulsemusic.Preferences;
import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.TaskRunner;
import com.hardcodecoder.pulsemusic.activities.base.ThemeActivity;
import com.hardcodecoder.pulsemusic.providers.ProviderManager;
import com.hardcodecoder.pulsemusic.service.PMS;
import com.hardcodecoder.pulsemusic.shortcuts.AppShortcutsManager;
import com.hardcodecoder.pulsemusic.themes.TintHelper;
import com.hardcodecoder.pulsemusic.utils.AppSettings;

public class SplashActivity extends ThemeActivity {

    private static final int REQUEST_CODE = 69;
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ProviderManager.init(this);
        TintHelper.setAccentTintTo((ImageView) findViewById(R.id.splash_logo));
        getPermission();
    }

    private void getPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            doStartUpInitialization();
            startHomeActivity();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length >= 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                doStartUpInitialization();
                startHomeActivity();
            } else {
                // Permission was not granted
                Toast.makeText(this, getString(R.string.toast_requires_storage_access), Toast.LENGTH_LONG).show();
                mHandler.postDelayed(this::finish, 1500);
            }
        }
    }

    private void doStartUpInitialization() {
        TaskRunner.executeAsync(() -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                // Initialize app shortcuts
                AppShortcutsManager manager = new AppShortcutsManager(getApplicationContext());
                manager.initDynamicShortcuts(false);
            }

            if (AppSettings.isFirstRun(this)) {
                AppSettings.setPlaylistSectionEnabled(this, Preferences.HOME_PLAYLIST_TOP_ALBUMS, true);
                AppSettings.setPlaylistSectionEnabled(this, Preferences.HOME_PLAYLIST_FOR_YOU, true);
                AppSettings.setPlaylistSectionEnabled(this, Preferences.HOME_PLAYLIST_NEW_IN_LIBRARY, true);
                AppSettings.setFirstRun(this, false);
            }
        });
    }

    private void startHomeActivity() {
        mHandler.postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, MainContentActivity.class);
            Uri uri = getIntent().getData();
            if (uri != null) {
                intent.setAction(MainContentActivity.ACTION_PLAY_FROM_URI);
                intent.putExtra(MainContentActivity.TRACK_URI, uri.toString());
            }
            startService(new Intent(this, PMS.class));
            startActivity(intent);
            finish();
        }, 400);
    }
}