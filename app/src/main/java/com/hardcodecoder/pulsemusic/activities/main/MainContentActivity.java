package com.hardcodecoder.pulsemusic.activities.main;

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
import com.hardcodecoder.pulsemusic.PMS;
import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.activities.base.DraggableNowPlayingSheetActivity;
import com.hardcodecoder.pulsemusic.dialog.HomeBottomSheetFragment;
import com.hardcodecoder.pulsemusic.fragments.main.AlbumsFragment;
import com.hardcodecoder.pulsemusic.fragments.main.ArtistFragment;
import com.hardcodecoder.pulsemusic.fragments.main.HomeFragment;
import com.hardcodecoder.pulsemusic.fragments.main.LibraryFragment;
import com.hardcodecoder.pulsemusic.fragments.main.PlaylistFragment;
import com.hardcodecoder.pulsemusic.fragments.main.base.PulseFragment;
import com.hardcodecoder.pulsemusic.helper.DataModelHelper;
import com.hardcodecoder.pulsemusic.loaders.LoaderCache;
import com.hardcodecoder.pulsemusic.loaders.LoaderHelper;
import com.hardcodecoder.pulsemusic.model.MusicModel;
import com.hardcodecoder.pulsemusic.playback.PlaybackManager;
import com.hardcodecoder.pulsemusic.utils.AppSettings;
import com.hardcodecoder.pulsemusic.utils.LogUtils;
import com.hardcodecoder.pulsemusic.views.PulseToolbar;

import java.util.ArrayList;
import java.util.List;

public class MainContentActivity extends DraggableNowPlayingSheetActivity {

    public static final String TAG = MainContentActivity.class.getSimpleName();
    public static final String ACTION_OPEN_NOW_PLAYING = "com.hardcodecoder.pulsemusic.activities.main.MainContentActivity.ActionOpenNPS";
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
    private PulseFragment homeFrag = null;
    private PulseFragment libraryFrag = null;
    private PulseFragment artistFrag = null;
    private PulseFragment albumsFrag = null;
    private PulseFragment activeFrag = null;
    private PulseFragment playlistCardFrag = null;
    private AppBarLayout mAppBar;
    private PulseToolbar mPulseToolbar;
    private MediaController mController;
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

    @LayoutRes
    @Override
    public int getContentLayout() {
        return R.layout.activity_main_contents;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        connectToMediaService();
        setUpToolbar();
        if (null == savedInstanceState || null == LoaderCache.getAllTracksList())
            LoaderHelper.loadAllTracks(this, result -> setUpMainContents(savedInstanceState));
        else setUpMainContents(savedInstanceState);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private boolean handleIntent(@NonNull Intent intent) {
        if (intent.getAction() != null && intent.getAction().equals(ACTION_OPEN_NOW_PLAYING)) {
            updateDraggableSheet(true);
            expandBottomSheet();
            return true;
        } else if (intent.hasExtra(TRACK_URI)) {
            try {
                String path = intent.getStringExtra(TRACK_URI);
                if (null == path) return false;
                Uri data = Uri.parse(path);
                MusicModel md = DataModelHelper.buildMusicModelFrom(this, data);
                if (null != md) {
                    List<MusicModel> singlePickedItemList = new ArrayList<>();
                    singlePickedItemList.add(md);
                    mPulseController.setPlaylist(singlePickedItemList, 0);
                    mRemote.play();
                    return true;
                }
            } catch (Exception e) {
                LogUtils.logException(TAG, "Handling intent", e);
            }
        }
        return false;
    }

    @Override
    public void onNavigationItemSelected(@NonNull MenuItem menuItem) {
        final int id = menuItem.getItemId();
        if (id == R.id.nav_home) {
            if (activeFrag != homeFrag) switchFragment(homeFrag, HomeFragment.TAG);
        } else if (id == R.id.nav_library) {
            if (activeFrag != libraryFrag) switchFragment(libraryFrag, LibraryFragment.TAG);
        } else if (id == R.id.nav_album) {
            if (activeFrag != albumsFrag) switchFragment(albumsFrag, AlbumsFragment.TAG);
        } else if (id == R.id.nav_artist) {
            if (activeFrag != artistFrag) switchFragment(artistFrag, ArtistFragment.TAG);
        } else if (id == R.id.nav_playlist) {
            if (activeFrag != playlistCardFrag)
                switchFragment(playlistCardFrag, PlaylistFragment.TAG);
        }
    }

    private void connectToMediaService() {
        Intent intent = new Intent(this, PMS.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private void setUpToolbar() {
        mAppBar = findViewById(R.id.main_app_bar);
        mPulseToolbar = mAppBar.findViewById(R.id.pulse_toolbar);
        mPulseToolbar.setNavigationIcon(R.drawable.ic_menu);
        mPulseToolbar.setQuickActionIcon(R.drawable.ic_search);
        mPulseToolbar.setOverflowIcon(R.drawable.ic_options);

        mPulseToolbar.setNavigationIconOnClickListener(v -> {
            HomeBottomSheetFragment homeBottomSheetFragment = HomeBottomSheetFragment.getInstance();
            homeBottomSheetFragment.show(getSupportFragmentManager(), HomeBottomSheetFragment.TAG);
        });

        mPulseToolbar.setQuickActionIconOnClickListener(v ->
                startActivity(new Intent(this, SearchActivity.class)));
        mPulseToolbar.setOverflowIconOnClickListener(v -> {
            if (null != activeFrag) activeFrag.showOptionsMenu();
        });
    }

    private void setUpMainContents(Bundle savedInstanceState) {
        if (savedInstanceState == null) switchFragment(homeFrag, HomeFragment.TAG);
        else switchFragment(activeFrag, savedInstanceState.getString(ACTIVE, HomeFragment.TAG));
    }

    private void switchFragment(@Nullable PulseFragment switchTo, @NonNull String tag) {
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
                    Log.e(TAG, "SwitchTo fragment is not a member of Pulse fragments");
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
            mPulseToolbar.setTitle(activeFrag.getFragmentTitle(this), true);
            mPulseToolbar.showOverflowIcon(activeFrag.hasToolbarContextMenu());
        });
    }

    public void onControllerReady() {
        mController.registerCallback(mCallback);
        if (handleIntent(getIntent())) return;

        if (mController.getPlaybackState() != null && mController.getPlaybackState().getState() != PlaybackState.STATE_STOPPED)
            updateDraggableSheet(true);

        else if (mController.getPlaybackState() == null && AppSettings.rememberPlaylistEnabled(this)) {
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
        if (null != serviceConnection) unbindService(serviceConnection);
        if (null != mController) mController.unregisterCallback(mCallback);
        super.onDestroy();
    }
}