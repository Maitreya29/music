package com.hardcodecoder.pulsemusic.activities.main;

import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.hardcodecoder.pulsemusic.Preferences;
import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.fragments.nowplaying.screens.EdgeNowPlayingScreen;
import com.hardcodecoder.pulsemusic.fragments.nowplaying.screens.LandscapeModeNowPlayingScreen;
import com.hardcodecoder.pulsemusic.fragments.nowplaying.screens.ModernNowPlayingScreen;
import com.hardcodecoder.pulsemusic.fragments.nowplaying.screens.StylishNowPlayingScreen;
import com.hardcodecoder.pulsemusic.themes.ThemeManagerUtils;
import com.hardcodecoder.pulsemusic.utils.AppSettings;

public class NowPlayingScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // Apply custom theme to override transparent theme used while loading activity
        setTheme(ThemeManagerUtils.getThemeToApply());
        // Apply transparent theme on top of custom theme to make background transparent while dragging
        getTheme().applyStyle(R.style.TransparentThemeOverlay, true);

        overridePendingTransition(R.anim.slide_in_bottom, R.anim.activity_open_exit);

        Fragment screenFragment;
        String tag;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            super.onCreate(null);
            screenFragment = LandscapeModeNowPlayingScreen.getInstance();
            tag = LandscapeModeNowPlayingScreen.TAG;
        } else {
            super.onCreate(savedInstanceState);
            int id = AppSettings.getNowPlayingScreenStyle(this);
            switch (id) {
                case Preferences.NOW_PLAYING_SCREEN_STYLISH:
                    screenFragment = StylishNowPlayingScreen.getInstance();
                    tag = StylishNowPlayingScreen.TAG;
                    break;
                case Preferences.NOW_PLAYING_SCREEN_EDGE:
                    setWindowFullScreen();
                    screenFragment = EdgeNowPlayingScreen.getInstance();
                    tag = EdgeNowPlayingScreen.TAG;
                    break;
                case Preferences.NOW_PLAYING_SCREEN_MODERN:
                default:
                    screenFragment = ModernNowPlayingScreen.getInstance();
                    tag = ModernNowPlayingScreen.TAG;
            }
        }
        setContentView(R.layout.activity_now_playing_screen);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, screenFragment, tag)
                .commit();
        initBottomSheetBehavior();
    }

    private void initBottomSheetBehavior() {
        FrameLayout contentFrame = findViewById(R.id.content_frame);
        BottomSheetBehavior<FrameLayout> bottomSheetBehavior = BottomSheetBehavior.from(contentFrame);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        bottomSheetBehavior.setSkipCollapsed(true);
        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN)
                    finish();
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                float alpha = (slideOffset >= 0 ? (slideOffset + 1) / 2 : slideOffset / 2 + 0.5f) + 0.2f;
                bottomSheet.setAlpha(alpha);
            }
        });
    }

    private void setWindowFullScreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            w.getDecorView().setSystemUiVisibility(w.getDecorView().getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    private void overrideExitTransition() {
        overridePendingTransition(R.anim.activity_close_enter, R.anim.slide_out_bottom);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overrideExitTransition();
    }

    @Override
    public void finish() {
        super.finish();
        overrideExitTransition();
    }
}