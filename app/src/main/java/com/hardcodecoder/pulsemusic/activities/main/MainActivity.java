package com.hardcodecoder.pulsemusic.activities.main;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.ColorStateList;
import android.media.MediaMetadata;
import android.media.session.MediaController;
import android.media.session.PlaybackState;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.hardcodecoder.pulsemusic.PMS;
import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.activities.base.ThemeActivity;
import com.hardcodecoder.pulsemusic.dialog.HomeBottomSheetFragment;
import com.hardcodecoder.pulsemusic.fragments.main.AlbumsFragment;
import com.hardcodecoder.pulsemusic.fragments.main.ArtistFragment;
import com.hardcodecoder.pulsemusic.fragments.main.ControlsFragment;
import com.hardcodecoder.pulsemusic.fragments.main.HomeFragment;
import com.hardcodecoder.pulsemusic.fragments.main.LibraryFragment;
import com.hardcodecoder.pulsemusic.fragments.main.PlaylistFragment;
import com.hardcodecoder.pulsemusic.loaders.LoaderHelper;
import com.hardcodecoder.pulsemusic.playback.PlaybackManager;
import com.hardcodecoder.pulsemusic.themes.ThemeColors;
import com.hardcodecoder.pulsemusic.utils.AppSettings;

public class MainActivity extends ThemeActivity {

    public static final String TAG = "MainActivity";
    private static final String HOME = "HomeFragment";
    private static final String LIBRARY = "LibraryFragment";
    private static final String ALBUMS = "AlbumsFragment";
    private static final String PLAYLIST_CARDS = "PlaylistCardFragment";
    private static final String ARTIST = "ArtistFragment";
    private static final String ACTIVE = "ActiveFragment";
    private final FragmentManager fm = getSupportFragmentManager();
    private Fragment homeFrag = null;
    private Fragment libraryFrag = null;
    private Fragment artistFrag = null;
    private Fragment albumsFrag = null;
    private Fragment activeFrag = null;
    private Fragment playlistCardFrag = null;
    private Fragment controlsFrag = null;
    private final MediaController.Callback mCallback = new MediaController.Callback() {
        @Override
        public void onMetadataChanged(@Nullable MediaMetadata metadata) {
            showControlsFragment();
        }

        @Override
        public void onPlaybackStateChanged(@Nullable PlaybackState state) {
            if (null == state || state.getState() == PlaybackState.STATE_STOPPED)
                hideControlsFragment();
        }
    };
    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            PMS.ServiceBinder serviceBinder = (PMS.ServiceBinder) binder;
            mController = serviceBinder.getMediaController();
            onControllerReady();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };
    private AppBarLayout mAppBar;
    private MediaController mController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(null); // Pass null to prevent restoration of fragments on activity recreate
        connectToMediaService();
        setContentView(R.layout.activity_main);
        setUpToolbar();
        setUpMainContents(savedInstanceState);
    }

    private void connectToMediaService() {
        Intent intent = new Intent(this, PMS.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private void setUpToolbar() {
        mAppBar = findViewById(R.id.main_app_bar);
        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> {
            HomeBottomSheetFragment homeBottomSheetFragment = HomeBottomSheetFragment.getInstance();
            homeBottomSheetFragment.show(getSupportFragmentManager(), HomeBottomSheetFragment.TAG);
        });
        toolbar.setOnClickListener(v -> startActivity(new Intent(this, SearchActivity.class)));
    }

    private void setUpMainContents(Bundle savedInstanceState) {
        if (savedInstanceState == null) switchFragment(homeFrag, HOME);
        else switchFragment(activeFrag, savedInstanceState.getString(ACTIVE, HOME));
        setUpBottomNavigationView();
    }

    private void setUpBottomNavigationView() {
        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_nav_bar);
        ColorStateList colorStateList = ThemeColors.getEnabledSelectedColorStateList();
        bottomNavigation.setItemIconTintList(colorStateList);
        bottomNavigation.setItemTextColor(colorStateList);
        bottomNavigation.setItemRippleColor(ThemeColors.getBottomNavigationViewRippleColor());
        bottomNavigation.setOnNavigationItemSelectedListener(menuItem -> {
            bottomNavigation.postOnAnimation(() -> {
                final int id = menuItem.getItemId();
                if (id == R.id.nav_home) {
                    if (activeFrag != homeFrag) switchFragment(homeFrag, HOME);
                } else if (id == R.id.nav_library) {
                    if (activeFrag != libraryFrag) switchFragment(libraryFrag, LIBRARY);
                } else if (id == R.id.nav_album) {
                    if (activeFrag != albumsFrag) switchFragment(albumsFrag, ALBUMS);
                } else if (id == R.id.nav_artist) {
                    if (activeFrag != artistFrag) switchFragment(artistFrag, ARTIST);
                } else if (id == R.id.nav_playlist) {
                    if (activeFrag != playlistCardFrag)
                        switchFragment(playlistCardFrag, PLAYLIST_CARDS);
                }
            });
            return true;
        });
    }

    private void switchFragment(Fragment switchTo, String tag) {
        if (null == switchTo) {
            switch (tag) {
                case HOME:
                    homeFrag = HomeFragment.getInstance();
                    switchTo = homeFrag;
                    break;

                case LIBRARY:
                    libraryFrag = LibraryFragment.getInstance();
                    switchTo = libraryFrag;
                    break;

                case ALBUMS:
                    albumsFrag = AlbumsFragment.getInstance();
                    switchTo = albumsFrag;
                    break;

                case ARTIST:
                    artistFrag = ArtistFragment.getInstance();
                    switchTo = artistFrag;
                    break;

                case PLAYLIST_CARDS:
                    playlistCardFrag = PlaylistFragment.getInstance();
                    switchTo = playlistCardFrag;
                    break;

                default:
                    Log.e(TAG, "SwitchTo fragment is not a member of defined fragments");
            }

            if (switchTo != null && activeFrag != null) {
                fm.beginTransaction()
                        .setCustomAnimations(R.anim.translate_y_enter, R.anim.translate_y_exit)
                        .add(R.id.fragment_container, switchTo, tag)
                        .hide(activeFrag)
                        .show(switchTo)
                        .commit();
            } else if (switchTo != null) {
                fm.beginTransaction()
                        .setCustomAnimations(R.anim.translate_y_enter, R.anim.translate_y_exit)
                        .add(R.id.fragment_container, switchTo, tag)
                        .show(switchTo)
                        .commit();
            }
        } else
            fm.beginTransaction()
                    .setCustomAnimations(R.anim.translate_y_enter, R.anim.translate_y_exit)
                    .hide(activeFrag)
                    .show(switchTo)
                    .commit();
        activeFrag = switchTo;
        mAppBar.setExpanded(true);
    }

    private void showControlsFragment() {
        if (controlsFrag == null) {
            controlsFrag = ControlsFragment.getInstance();
            findViewById(R.id.controls_fragment_container).setVisibility(View.VISIBLE);
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_bottom, R.anim.slide_out_bottom)
                    .replace(R.id.controls_fragment_container, controlsFrag, ControlsFragment.TAG)
                    .commit();
        }
    }

    private void hideControlsFragment() {
        if (null == controlsFrag) return;
        findViewById(R.id.controls_fragment_container).setVisibility(View.GONE);
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_in_bottom, R.anim.slide_out_bottom)
                .remove(controlsFrag)
                .commitAllowingStateLoss();
        controlsFrag = null;
    }

    public void onControllerReady() {
        mController.registerCallback(mCallback);
        if (mController.getMetadata() != null &&
                mController.getPlaybackState() != null &&
                mController.getPlaybackState().getState() != PlaybackState.STATE_STOPPED)
            showControlsFragment();

        if (mController.getPlaybackState() == null && AppSettings.isRememberLastTrack(this)) {
            LoaderHelper.loadLastPlayedTrackBundled(this, bundle -> {
                if (null == bundle) return;
                mController.getTransportControls().sendCustomAction(PlaybackManager.ACTION_LOAD_LAST_TRACK, bundle);
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (null != mController) {
            mController.registerCallback(mCallback);
            if (null != mController.getPlaybackState()) {
                if (mController.getPlaybackState().getState() == PlaybackState.STATE_STOPPED) {
                    hideControlsFragment();
                } else if (null != mController.getMetadata())
                    showControlsFragment();
            }
        }
    }

    @Override
    protected void onStop() {
        if (null != mController)
            mController.unregisterCallback(mCallback);
        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ACTIVE, activeFrag.getTag());
    }

    @Override
    protected void onDestroy() {
        if (null != serviceConnection) unbindService(serviceConnection);
        super.onDestroy();
    }
}