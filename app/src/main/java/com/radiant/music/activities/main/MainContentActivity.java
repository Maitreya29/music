package com.radiant.music.activities.main;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaMetadata;
import android.media.session.MediaController;
import android.media.session.PlaybackState;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.appbar.AppBarLayout;
import com.radiant.music.MediaArtCache;
import com.radiant.music.R;
import com.radiant.music.activities.base.DraggableNowPlayingSheetActivity;
import com.radiant.music.dialog.MainActivityMenu;
import com.radiant.music.fragments.main.AlbumsFragment;
import com.radiant.music.fragments.main.ArtistFragment;
import com.radiant.music.fragments.main.HomeFragment;
import com.radiant.music.fragments.main.LibraryFragment;
import com.radiant.music.fragments.main.PlaylistFragment;
import com.radiant.music.fragments.main.base.RadiantFragment;
import com.radiant.music.helper.DataModelHelper;
import com.radiant.music.loaders.LoaderManager;
import com.radiant.music.model.MusicModel;
import com.radiant.music.playback.PlaybackManager;
import com.radiant.music.playback.RadiantController;
import com.radiant.music.service.PMS;
import com.radiant.music.utils.AppSettings;
import com.radiant.music.utils.LogUtils;
import com.radiant.music.views.RadiantToolbar;

import java.util.ArrayList;
import java.util.List;

public class MainContentActivity extends DraggableNowPlayingSheetActivity implements RadiantController.OnControllerReadyListener {

    public static final String TAG = MainContentActivity.class.getSimpleName();
    public static final String ACTION_OPEN_NOW_PLAYING = "com.radiant.music.activities.main.MainContentActivity.ActionOpenNPS";
    public static final String ACTION_PLAY_FROM_URI = "com.radiant.music.activities.main.MainContentActivity.ActionPlayFromUri";
    public static final String TRACK_URI = "TrackUri";
    private static final String ACTIVE = "ActiveFragment";
    private final MediaController.Callback mCallback = new MediaController.Callback() {
        @Override
        public void onMetadataChanged(@Nullable MediaMetadata metadata) {
            updateDraggableSheet(true);
        }

        @Override
        public void onPlaybackStateChanged(@Nullable PlaybackState state) {
            if (null == state || state.getState() == PlaybackState.STATE_STOPPED)
                updateDraggableSheet(false);
        }
    };
    private RadiantFragment homeFrag = null;
    private RadiantFragment libraryFrag = null;
    private RadiantFragment artistFrag = null;
    private RadiantFragment albumsFrag = null;
    private RadiantFragment activeFrag = null;
    private RadiantFragment playlistCardFrag = null;
    private AppBarLayout mAppBar;
    private RadiantToolbar mRadiantToolbar;
    private MediaController mController;
    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            PMS.ServiceBinder serviceBinder = (PMS.ServiceBinder) binder;
            onControllerReady(serviceBinder.getMediaController());
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            finish();
        }
    };

    @LayoutRes
    @Override
    public int getContentLayout() {
        return R.layout.activity_main_contents;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        bindService(new Intent(this, PMS.class), mServiceConnection, Context.BIND_AUTO_CREATE);
        setUpToolbar();
        if (null == savedInstanceState || LoaderManager.isMasterListEmpty())
            LoaderManager.loadMaster(this, success -> setUpMainContents(savedInstanceState));
        else setUpMainContents(savedInstanceState);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (null == intent) return;
        handleIntent(intent);
    }

    private boolean handleIntent(@NonNull Intent intent) {
        String action = intent.getAction();
        if (action == null || action.isEmpty()) return false;
        boolean intentHandled = true;

        switch (action) {
            case ACTION_OPEN_NOW_PLAYING:
                updateDraggableSheet(true);
                // Delay expanding bottom sheet
                // Fixes bottom sheet callback issues and reduces stuttering
                mAppBar.postOnAnimationDelayed(this::expandBottomSheet, 260);
                break;
            case ACTION_PLAY_FROM_URI:
                if (!intent.hasExtra(TRACK_URI)) return false;
                try {
                    String path = intent.getStringExtra(TRACK_URI);
                    if (null == path || path.isEmpty()) return false;
                    Uri data = Uri.parse(path);
                    MusicModel md = DataModelHelper.buildMusicModelFrom(this, data);
                    if (null != md) {
                        List<MusicModel> singlePickedItemList = new ArrayList<>();
                        singlePickedItemList.add(md);
                        mRadiantController.getQueueManager().setPlaylist(singlePickedItemList, 0);
                        mRemote.play();
                    }
                } catch (Exception e) {
                    LogUtils.logException(LogUtils.Type.GENERAL, TAG, "Handling intent", e);
                    intentHandled = false;
                }
                break;
            default:
                intentHandled = false;
        }

        // Don't want to act open the same action on activity recreate
        intent.setAction(null);
        return intentHandled;
    }

    @Override
    public void onNavigationItemSelected(@NonNull MenuItem menuItem) {
        final int id = menuItem.getItemId();
        if (id == R.id.nav_homescreen) {
            if (activeFrag != homeFrag) switchFragment(homeFrag, HomeFragment.TAG);
        } else if (id == R.id.nav_library) {
            if (activeFrag != libraryFrag) switchFragment(libraryFrag, LibraryFragment.TAG);
        } else if (id == R.id.nav_home) {
            if (activeFrag != albumsFrag) switchFragment(albumsFrag, AlbumsFragment.TAG);
        } else if (id == R.id.nav_artist) {
            if (activeFrag != artistFrag) switchFragment(artistFrag, ArtistFragment.TAG);
        } else if (id == R.id.nav_playlist) {
            if (activeFrag != playlistCardFrag)
                switchFragment(playlistCardFrag, PlaylistFragment.TAG);
        }
    }

    private void setUpToolbar() {
        mAppBar = findViewById(R.id.main_app_bar);
        mRadiantToolbar = mAppBar.findViewById(R.id.radiant_toolbar);

        mRadiantToolbar.setNavigationIconOnClickListener(v -> {
            MainActivityMenu mainActivityMenu = MainActivityMenu.getInstance();
            mainActivityMenu.show(getSupportFragmentManager(), MainActivityMenu.TAG);
        });

        mRadiantToolbar.setQuickActionIconOnClickListener(v ->
                startActivity(new Intent(this, SearchActivity.class)));

        mRadiantToolbar.setOverflowIconOnClickListener(v -> {
            if (null != activeFrag) activeFrag.showOptionsMenu();
        });
    }

    private void setUpMainContents(Bundle savedInstanceState) {
        if (savedInstanceState == null) switchFragment(albumsFrag, AlbumsFragment.TAG);
        else switchFragment(activeFrag, savedInstanceState.getString(ACTIVE, AlbumsFragment.TAG));
    }

    private void switchFragment(@Nullable RadiantFragment switchTo, @NonNull String tag) {
        FragmentManager fm = getSupportFragmentManager();
        if (null == switchTo) {
            switch (tag) {
                case HomeFragment.TAG:
                    homeFrag = HomeFragment.getInstance();
                    switchTo = homeFrag;
                    break;

                case LibraryFragment.TAG:
                    libraryFrag = LibraryFragment.getInstance();
                    switchTo = libraryFrag;
                    break;

                case AlbumsFragment.TAG:
                    albumsFrag = AlbumsFragment.getInstance();
                    switchTo = albumsFrag;
                    break;

                case ArtistFragment.TAG:
                    artistFrag = ArtistFragment.getInstance();
                    switchTo = artistFrag;
                    break;

                case PlaylistFragment.TAG:
                    playlistCardFrag = PlaylistFragment.getInstance();
                    switchTo = playlistCardFrag;
                    break;

                default:
                    Log.e(TAG, "SwitchTo fragment is not a member of Radiant fragments");
            }
            if (switchTo != null && activeFrag != null) {
                fm.beginTransaction()
                        .setCustomAnimations(R.anim.translate_y_enter, R.anim.translate_y_exit)
                        .add(R.id.main_fragment_content, switchTo, tag)
                        .hide(activeFrag)
                        .show(switchTo)
                        .commit();
            } else if (switchTo != null) {
                fm.beginTransaction()
                        .setCustomAnimations(R.anim.translate_y_enter, R.anim.translate_y_exit)
                        .add(R.id.main_fragment_content, switchTo, tag)
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
        mAppBar.post(() -> {
            mAppBar.setExpanded(true);
            mRadiantToolbar.setTitle(activeFrag.getFragmentTitle(this));
            mRadiantToolbar.showOverflowIcon(activeFrag.hasToolbarContextMenu());
        });
    }

    public void onControllerReady(@NonNull MediaController controller) {
        mController = controller;
        mController.registerCallback(mCallback);
        if (handleIntent(getIntent())) return;

        if (mController.getPlaybackState() != null && mController.getPlaybackState().getState() != PlaybackState.STATE_STOPPED)
            updateDraggableSheet(true);

        else if (mController.getPlaybackState() == null && AppSettings.isRememberPlaylistEnabled(this)) {
            Bundle extras = new Bundle();
            extras.putBoolean(PlaybackManager.START_PLAYBACK, false);
            mController.getTransportControls().sendCustomAction(
                    PlaybackManager.ACTION_LOAD_LAST_PLAYLIST,
                    extras);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (null != activeFrag) outState.putString(ACTIVE, activeFrag.getTag());
    }

    @Override
    protected void onDestroy() {
        unbindService(mServiceConnection);
        if (null != mController) mController.unregisterCallback(mCallback);
        // This is our root activity, clear ui related caches when ui is not visible
        MediaArtCache.flushCache();
        super.onDestroy();
    }
}