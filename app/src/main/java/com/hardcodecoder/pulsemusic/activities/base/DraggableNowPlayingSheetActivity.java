package com.hardcodecoder.pulsemusic.activities.base;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.hardcodecoder.pulsemusic.Preferences;
import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.fragments.main.ControlsFragment;
import com.hardcodecoder.pulsemusic.fragments.nowplaying.screens.EdgeNowPlayingScreen;
import com.hardcodecoder.pulsemusic.fragments.nowplaying.screens.LandscapeModeNowPlayingScreen;
import com.hardcodecoder.pulsemusic.fragments.nowplaying.screens.ModernNowPlayingScreen;
import com.hardcodecoder.pulsemusic.fragments.nowplaying.screens.StylishNowPlayingScreen;
import com.hardcodecoder.pulsemusic.themes.ThemeColors;
import com.hardcodecoder.pulsemusic.themes.ThemeManagerUtils;
import com.hardcodecoder.pulsemusic.utils.AppSettings;
import com.hardcodecoder.pulsemusic.utils.DimensionsUtil;

public abstract class DraggableNowPlayingSheetActivity extends ControllerActivity
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    private View mMainContent;
    private FrameLayout mPeekingFrame;
    private FrameLayout mExpandedFrame;
    private BottomSheetBehavior<FrameLayout> mBehaviour;
    private BottomSheetBehavior.BottomSheetCallback mBehaviourCallback;
    private Fragment mPeekingFragment = null;
    private Fragment mExpandedFragment = null;
    private BottomNavigationView mBottomNavBar;
    private int mPaddingBottomDefault;
    private int mPaddingBottomWhenPeeking;
    private int mCurrentOrientation;
    private int mDefaultSystemUiVisibility = -1;
    private boolean mSafeToCommit = true;
    private boolean mPendingInitializeBottomSheet = false;
    private boolean mPendingUpdateExpandedFragment = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(null);
        setContentView(R.layout.activity_draggable_now_playing);
        ViewGroup root = findViewById(R.id.draggable_activity_root);
        mMainContent = LayoutInflater.from(this).inflate(getContentLayout(), root, false);
        root.addView(mMainContent, 0);
        onViewCreated(mMainContent, savedInstanceState);
        setUpBottomNavigationView();
    }

    private void setUpBottomNavigationView() {
        mBottomNavBar = findViewById(R.id.bottom_nav_bar);
        ColorStateList colorStateList = ThemeColors.getEnabledSelectedColorStateList();
        mBottomNavBar.setItemIconTintList(colorStateList);
        mBottomNavBar.setItemTextColor(colorStateList);
        mBottomNavBar.setItemRippleColor(ThemeColors.getBottomNavigationViewRippleColor());
        mBottomNavBar.setOnNavigationItemSelectedListener(menuItem -> {
            mBottomNavBar.postOnAnimation(() -> onNavigationItemSelected(menuItem));
            return true;
        });
    }

    private boolean initializeBottomSheet() {
        if (!mSafeToCommit) {
            mPendingInitializeBottomSheet = true;
            return false;
        }

        FrameLayout draggableFrame = findViewById(R.id.draggable_frame);
        draggableFrame.setVisibility(View.VISIBLE);
        // We provide a custom implementation as to how insets is handled for the
        // draggable frame layout, to eliminate top padding in some devices
        // We will not apply any top insets to this view (no matter what)
        // and return the insets unchanged
        draggableFrame.setOnApplyWindowInsetsListener((v, insets) -> {
            draggableFrame.setPadding(
                    draggableFrame.getPaddingLeft(),
                    0,
                    draggableFrame.getPaddingRight(),
                    draggableFrame.getPaddingBottom());
            return insets;
        });
        // Request to force apply our implementation of insets
        draggableFrame.requestApplyInsets();

        mBehaviour = BottomSheetBehavior.from(draggableFrame);
        final int peekHeight = DimensionsUtil.getDimensionPixelSize(this, 104);
        mBehaviour.setPeekHeight(peekHeight, true);
        mBehaviour.setHideable(true);
        mPaddingBottomDefault = DimensionsUtil.getDimensionPixelSize(this, 56);
        mPaddingBottomWhenPeeking = peekHeight;
        updateMainContentBottomPadding(mPaddingBottomWhenPeeking);

        mPeekingFrame = findViewById(R.id.peeking_content_frame);
        mExpandedFrame = findViewById(R.id.expanded_content_frame);

        if (null == mPeekingFragment) {
            mPeekingFragment = ControlsFragment.getInstance();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.peeking_content_frame, mPeekingFragment, ControlsFragment.TAG)
                    .commit();
            mPeekingFrame.setOnClickListener(v -> expandBottomSheet());
        }

        if (null == mExpandedFragment) {
            // ViewPager2 need to initialize once before it can be set to View.GONE
            // Setting View.GONE here causes ViewPager2 to not correctly update active album art
            mExpandedFrame.setVisibility(View.INVISIBLE);
            createExpandedFragment(false);
            getSharedPreferences(Preferences.NOW_PLAYING_SCREEN_STYLE_KEY, Context.MODE_PRIVATE)
                    .registerOnSharedPreferenceChangeListener(this);
        }

        final float bottomNavBarHeight = DimensionsUtil.getDimension(this, 56);
        mBehaviourCallback = new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_EXPANDED:
                        if (mPeekingFrame.getVisibility() != View.GONE)
                            mPeekingFrame.setVisibility(View.GONE);
                        if (mExpandedFrame.getVisibility() != View.VISIBLE)
                            mExpandedFrame.setVisibility(View.VISIBLE);
                        if (needsLightStatusBarIcons()) setLightStatusBarIcons(true);
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        if (mExpandedFrame.getVisibility() != View.GONE)
                            mExpandedFrame.setVisibility(View.GONE);
                        if (mPeekingFrame.getVisibility() != View.VISIBLE)
                            mPeekingFrame.setVisibility(View.VISIBLE);
                        updateBottomBarElevation(true);
                        if (mDefaultSystemUiVisibility != -1) setLightStatusBarIcons(false);
                        break;
                    case BottomSheetBehavior.STATE_HIDDEN:
                        mRemote.stop();
                        updateMainContentBottomPadding(mPaddingBottomDefault);
                        updateBottomBarElevation(false);
                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        if (mExpandedFrame.getVisibility() != View.VISIBLE)
                            mExpandedFrame.setVisibility(View.VISIBLE);
                        if (mPeekingFrame.getVisibility() != View.VISIBLE)
                            mPeekingFrame.setVisibility(View.VISIBLE);
                        break;
                    case BottomSheetBehavior.STATE_HALF_EXPANDED:
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                final float inverseAlpha = 1 - slideOffset;
                mExpandedFrame.setAlpha(slideOffset);
                mPeekingFrame.setAlpha(inverseAlpha);
                mBottomNavBar.setAlpha(inverseAlpha);
                mBottomNavBar.setTranslationY(bottomNavBarHeight * Math.max(slideOffset, 0));
            }
        };
        mBehaviour.addBottomSheetCallback(mBehaviourCallback);
        mPendingInitializeBottomSheet = false;
        return true;
    }

    private void createExpandedFragment(boolean force) {
        if (!mSafeToCommit) {
            mPendingUpdateExpandedFragment = true;
            return;
        }

        if ((null != mExpandedFragment && !force) || null == mBehaviour) return;
        String tag;
        mCurrentOrientation = getResources().getConfiguration().orientation;
        if (mCurrentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            mExpandedFragment = LandscapeModeNowPlayingScreen.getInstance();
            tag = LandscapeModeNowPlayingScreen.TAG;
        } else {
            int id = AppSettings.getNowPlayingScreenStyle(this);
            switch (id) {
                case Preferences.NOW_PLAYING_SCREEN_STYLISH:
                    mExpandedFragment = StylishNowPlayingScreen.getInstance();
                    tag = StylishNowPlayingScreen.TAG;
                    break;
                case Preferences.NOW_PLAYING_SCREEN_EDGE:
                    mExpandedFragment = EdgeNowPlayingScreen.getInstance();
                    tag = EdgeNowPlayingScreen.TAG;
                    break;
                case Preferences.NOW_PLAYING_SCREEN_MODERN:
                default:
                    mExpandedFragment = ModernNowPlayingScreen.getInstance();
                    tag = ModernNowPlayingScreen.TAG;
            }
        }
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.expanded_content_frame, mExpandedFragment, tag)
                .commit();
        mPendingUpdateExpandedFragment = false;
    }

    private void updateBottomBarElevation(boolean isPeeking) {
        mBottomNavBar.setElevation(isPeeking ? 0f : DimensionsUtil.getDimensionPixelSize(this, 4));
    }

    /**
     * Changes the status bar icon colors to a light color
     * to account for a darker status bar background
     *
     * @param setLightIcons If true change the status bar icon colors to light
     *                      else we revert the system ui visibility to account
     *                      for dark and light themes
     */
    @SuppressWarnings("deprecation")
    private void setLightStatusBarIcons(boolean setLightIcons) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window w = getWindow();
            if (setLightIcons) {
                if (mDefaultSystemUiVisibility == -1)
                    mDefaultSystemUiVisibility = w.getDecorView().getSystemUiVisibility();
                w.getDecorView().setSystemUiVisibility(mDefaultSystemUiVisibility & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            } else {
                // Instead of toggling back the View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                // We revert to default system ui visibility to account for light and dark themes
                w.getDecorView().setSystemUiVisibility(mDefaultSystemUiVisibility);
                mDefaultSystemUiVisibility = -1;
            }
        }
    }

    private void updateMainContentBottomPadding(int bottomPadding) {
        ValueAnimator animator = ValueAnimator.ofInt(mMainContent.getPaddingBottom(), bottomPadding);
        animator.addUpdateListener(valueAnimator ->
                mMainContent.setPadding(
                        mMainContent.getPaddingLeft(),
                        mMainContent.getPaddingTop(),
                        mMainContent.getPaddingRight(),
                        (Integer) valueAnimator.getAnimatedValue()
                ));
        animator.setDuration(getResources().getInteger(android.R.integer.config_shortAnimTime));
        animator.start();
    }

    private boolean needsLightStatusBarIcons() {
        return null != mExpandedFragment && !ThemeManagerUtils.isDarkModeEnabled()
                && mExpandedFragment instanceof EdgeNowPlayingScreen;
    }

    protected void updateDraggableSheet(boolean show) {
        if (show) {
            if (null == mBehaviour && !initializeBottomSheet()) return;
            if (mBehaviour.getState() != BottomSheetBehavior.STATE_HIDDEN) return;
            collapseBottomSheet();
            updateMainContentBottomPadding(mPaddingBottomWhenPeeking);
        } else {
            if (null == mBehaviour) {
                // We need to hide bottom sheet before it's initialized
                // Maybe because user started playlist from somewhere else
                // and then stopped the playback causing to hide it before it's initialized
                // Simply drop everything (Don't initialize so we don't have to hide)
                mPendingInitializeBottomSheet = false;
                return;
            }
            if (mBehaviour.getState() == BottomSheetBehavior.STATE_HIDDEN) return;
            mBehaviour.setState(BottomSheetBehavior.STATE_HIDDEN);
            updateMainContentBottomPadding(mPaddingBottomDefault);
        }
    }

    public void collapseBottomSheet() {
        if (null != mBehaviour) mBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
        mPeekingFrame.setVisibility(View.VISIBLE);
    }

    public void expandBottomSheet() {
        if (null != mBehaviour) mBehaviour.setState(BottomSheetBehavior.STATE_EXPANDED);
        mPeekingFrame.setVisibility(View.GONE);
        mExpandedFrame.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mSafeToCommit = true;
        if (mPendingInitializeBottomSheet) initializeBottomSheet();
        else if (mPendingUpdateExpandedFragment) createExpandedFragment(true);
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation != mCurrentOrientation) {
            createExpandedFragment(true);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, @NonNull String key) {
        if (key.equals(Preferences.NOW_PLAYING_SCREEN_STYLE_KEY))
            createExpandedFragment(true);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        mSafeToCommit = false;
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        int state = BottomSheetBehavior.STATE_COLLAPSED;
        if (null != mBehaviour) state = mBehaviour.getState();
        if (state == BottomSheetBehavior.STATE_COLLAPSED || state == BottomSheetBehavior.STATE_HIDDEN)
            super.onBackPressed();
        else collapseBottomSheet();
    }

    @Override
    protected void onDestroy() {
        getSharedPreferences(Preferences.NOW_PLAYING_SCREEN_STYLE_KEY, Context.MODE_PRIVATE)
                .unregisterOnSharedPreferenceChangeListener(this);
        if (null != mBehaviour && null != mBehaviourCallback)
            mBehaviour.removeBottomSheetCallback(mBehaviourCallback);
        super.onDestroy();
    }

    @LayoutRes
    public abstract int getContentLayout();

    public abstract void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState);

    public abstract void onNavigationItemSelected(@NonNull MenuItem menuItem);
}