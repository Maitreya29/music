package com.hardcodecoder.pulsemusic.activities.base;

import android.content.Intent;
import android.media.session.MediaController;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.format.DateUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewStub;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textview.MaterialTextView;
import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.activities.MediaSessionActivity;
import com.hardcodecoder.pulsemusic.activities.TrackPickerActivity;
import com.hardcodecoder.pulsemusic.model.MusicModel;
import com.hardcodecoder.pulsemusic.singleton.TrackManager;
import com.hardcodecoder.pulsemusic.themes.ThemeColors;
import com.hardcodecoder.pulsemusic.utils.DimensionsUtil;
import com.hardcodecoder.pulsemusic.views.AccentColorMaterialButton;
import com.hardcodecoder.pulsemusic.views.CustomToolbar;
import com.hardcodecoder.pulsemusic.views.MediaArtImageView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class PlaylistActivity extends MediaSessionActivity {

    public static final String PLAYLIST_TITLE_KEY = "playlist title";
    public static final short REQUEST_CODE_PICK_TRACKS = 120;
    private AppBarLayout mAppBarLayout;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private MaterialTextView mPlaylistInfo;
    private String mPlaylistTitle = null;
    private long mTotalPlaylistDuration = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_playlist);
        mAppBarLayout = findViewById(R.id.playlist_app_bar);
        mPlaylistInfo = findViewById(R.id.playlist_info);

        if (getIntent().getExtras() != null)
            mPlaylistTitle = getIntent().getExtras().getString(PLAYLIST_TITLE_KEY);

        setUpHeader();
        loadContent();
    }

    @Nullable
    public String getPlaylistTitle() {
        return mPlaylistTitle;
    }

    public void setPlaylistTitle(@Nullable String title) {
        mPlaylistTitle = title;
        mCollapsingToolbarLayout.setTitle(title);
    }

    protected long getTotalPlaylistDuration() {
        return mTotalPlaylistDuration;
    }

    private void setUpHeader() {
        mCollapsingToolbarLayout = mAppBarLayout.findViewById(R.id.playlist_collapsing_toolbar);
        mCollapsingToolbarLayout.setTitle(mPlaylistTitle);
        CustomToolbar toolbar = findViewById(R.id.playlist_toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_backspace);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
        onInitializeDynamicButtons();
    }

    protected void setUpContent(@Nullable List<MusicModel> list) {
        boolean isListEmpty = null == list || list.isEmpty();
        showEmptyListUI(isListEmpty);
        if (isListEmpty) return;

        final RecyclerView recyclerView = (RecyclerView) ((ViewStub) findViewById(R.id.stub_playlist_rv)).inflate();
        recyclerView.setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

        loadRecyclerList(recyclerView, list);
        loadPlaylistMediaArt(list);
        updateTracksInfo(list.size(), calculatePlaylistDuration(list));
    }

    protected long calculatePlaylistDuration(@NonNull List<MusicModel> list) {
        long duration = 0;
        for (MusicModel md : list)
            duration += md.getTrackDuration();
        return duration;
    }

    private void loadPlaylistMediaArt(@NonNull List<MusicModel> list) {
        MediaArtImageView playlistArt = findViewById(R.id.playlist_media_art);
        MusicModel md = list.get(0);
        playlistArt.loadAlbumArt(md.getAlbumArtUrl(), md.getAlbumId());
    }

    protected void updateTracksInfo(int size, long duration) {
        mTotalPlaylistDuration = duration;
        String text = "● "
                + size + "\t"
                + getString(R.string.tracks)
                + " ● "
                + DateUtils.formatElapsedTime(mTotalPlaylistDuration / 1000);
        mPlaylistInfo.setText(text);
    }

    protected void showEmptyListUI(boolean show) {
        ViewStub stub = findViewById(R.id.stub_empty_playlist_layout);
        if (show) {
            if (stub != null) stub.inflate();
            // Show empty ui
            MaterialTextView emptyPlaylistTextView = findViewById(R.id.empty_list_text);
            emptyPlaylistTextView.setVisibility(View.VISIBLE);
            emptyPlaylistTextView.setText(getEmptyPlaylistText());

            // Set playlist art
            MediaArtImageView playlistArt = findViewById(R.id.playlist_media_art);
            playlistArt.loadAlbumArt("", 1);
            playlistArt.setImageTintList(ThemeColors.getPrimaryColorStateList());

            // Disable appbar scrolling
            AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) mCollapsingToolbarLayout.getLayoutParams();
            params.setScrollFlags(0);
            mCollapsingToolbarLayout.setLayoutParams(params);

            updateTracksInfo(0, 0);
        } else {
            if (stub == null) {
                // Stub was previously inflated so change the visibility
                MaterialTextView emptyPlaylistTextView = findViewById(R.id.empty_list_text);
                emptyPlaylistTextView.setVisibility(View.GONE);
            }

            // Clear Playlist art
            MediaArtImageView playlistArt = findViewById(R.id.playlist_media_art);
            playlistArt.setImageDrawable(null);
            playlistArt.setImageTintList(null);

            // Enable appbar scrolling
            AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) mCollapsingToolbarLayout.getLayoutParams();
            params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL |
                    AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS_COLLAPSED |
                    AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED);
            mCollapsingToolbarLayout.setLayoutParams(params);
        }
    }

    @SuppressWarnings("SameParameterValue")
    protected void setPlaylistDynamicFabButton(@DrawableRes int resId, @Nullable View.OnClickListener listener) {
        FloatingActionButton playlistDynamicFab = mAppBarLayout.findViewById(R.id.playlist_dynamic_fab);
        playlistDynamicFab.setImageDrawable(ContextCompat.getDrawable(this, resId));
        playlistDynamicFab.setOnClickListener(listener);
        playlistDynamicFab.setSupportImageTintList(ThemeColors.getPrimaryColorStateList());
    }

    @SuppressWarnings("SameParameterValue")
    protected void setPlaylistDynamicButton1(@NonNull String btnText, @DrawableRes int resId, @Nullable View.OnClickListener listener) {
        AccentColorMaterialButton materialButton = mAppBarLayout.findViewById(R.id.playlist_dynamic_btn1);
        materialButton.setText(btnText);
        materialButton.setIcon(ContextCompat.getDrawable(this, resId));
        materialButton.setOnClickListener(listener);
    }

    protected void setPlaylistDynamicButton2(@NonNull String btnText, @DrawableRes int resId, @Nullable View.OnClickListener listener) {
        AccentColorMaterialButton materialButton = mAppBarLayout.findViewById(R.id.playlist_dynamic_btn2);
        materialButton.setText(btnText);
        materialButton.setIcon(ContextCompat.getDrawable(this, resId));
        materialButton.setOnClickListener(listener);
    }

    protected SpannableString getEmptyPlaylistText() {
        String text = getString(R.string.no_playlist_tracks_found);
        SpannableString spannableString = new SpannableString(text);
        int lineEnd = text.indexOf("\n");
        spannableString.setSpan(new ForegroundColorSpan(ThemeColors.getCurrentPrimaryTextColor()),
                0,
                lineEnd,
                Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        spannableString.setSpan(new AbsoluteSizeSpan(DimensionsUtil.getDimensionPixelSize(this, 20)),
                0,
                lineEnd,
                Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        return spannableString;
    }

    protected void setTrackAndPlay(@Nullable List<MusicModel> playlist, int startIndex) {
        if (null == playlist || playlist.isEmpty()) return;
        TrackManager.getInstance().buildDataList(playlist, startIndex);
        playMedia();
    }

    protected void shuffleTrackAndPlay(@Nullable List<MusicModel> playlist) {
        if (null == playlist || playlist.isEmpty()) return;
        List<MusicModel> playListToPlay = new ArrayList<>(playlist);
        Collections.shuffle(playListToPlay);
        TrackManager.getInstance().buildDataList(playListToPlay, 0);
        playMedia();
        Toast.makeText(this, getString(R.string.playlist_shuffled_success_toast), Toast.LENGTH_SHORT).show();
    }

    protected void openTrackPicker() {
        startActivityForResult(new Intent(this, TrackPickerActivity.class), REQUEST_CODE_PICK_TRACKS);
    }

    @Override
    public void onMediaServiceConnected(MediaController controller) {
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_PICK_TRACKS) {
            Object object;
            if (null != data && null != (object = data.getSerializableExtra(TrackPickerActivity.ID_PICKED_TRACKS))) {
                ArrayList<MusicModel> selectedTracks = (ArrayList<MusicModel>) object;
                if (selectedTracks.size() > 0) onReceivedTracks(selectedTracks);
            }
        }
    }

    protected void onReceivedTracks(@NonNull List<MusicModel> list) {
    }

    protected abstract void loadContent();

    protected abstract void loadRecyclerList(@NonNull RecyclerView recyclerView, @NonNull List<MusicModel> list);

    protected abstract void onInitializeDynamicButtons();
}