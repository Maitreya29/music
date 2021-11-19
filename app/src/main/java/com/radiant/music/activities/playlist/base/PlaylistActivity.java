package com.radiant.music.activities.playlist.base;

import android.content.Intent;
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
import com.radiant.music.R;
import com.radiant.music.activities.base.ControllerActivity;
import com.radiant.music.activities.main.TrackPickerActivity;
import com.radiant.music.model.MusicModel;
import com.radiant.music.themes.ThemeColors;
import com.radiant.music.utils.DimensionsUtil;
import com.radiant.music.utils.PlaylistUtil;
import com.radiant.music.views.AccentColorMaterialButton;
import com.radiant.music.views.CustomToolbar;
import com.radiant.music.views.MediaArtImageView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class PlaylistActivity extends ControllerActivity {

    public static final String PLAYLIST_TITLE_KEY = "playlist title";
    public static final short REQUEST_CODE_PICK_TRACKS = 120;
    private AppBarLayout mAppBarLayout;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private MaterialTextView mPlaylistInfo;
    private String mPlaylistTitle = null;
    private long mCurrentPlaylistDuration = 0;

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

    protected long getCurrentPlaylistDuration() {
        return mCurrentPlaylistDuration;
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

        final ViewStub recyclerStub = findViewById(R.id.stub_playlist_rv);
        RecyclerView recyclerView;
        if (recyclerStub != null) recyclerView = (RecyclerView) (recyclerStub).inflate();
        else recyclerView = findViewById(R.id.playlist_rv);
        recyclerView.setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

        loadRecyclerList(recyclerView, list);
        PlaylistUtil.loadPlaylistArtInto(findViewById(R.id.playlist_media_art), list);
        updateTracksInfo(list.size(), getPlaylistDurationFor(list));
    }

    protected long getPlaylistDurationFor(@Nullable List<MusicModel> playlist) {
        if (null == playlist) return 0;
        return PlaylistUtil.calculatePlaylistDuration(playlist);
    }

    protected void updateTracksInfo(int size, long duration) {
        mCurrentPlaylistDuration = duration;
        String text = "● "
                + size + "\t"
                + getString(R.string.suffix_tracks)
                + " ● "
                + DateUtils.formatElapsedTime(mCurrentPlaylistDuration / 1000);
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
            PlaylistUtil.loadDefaultPlaylistArt(playlistArt, null);

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
            playlistArt.clearLoadedArt();

            // Enable appbar scrolling
            AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) mCollapsingToolbarLayout.getLayoutParams();
            params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL |
                    AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS_COLLAPSED |
                    AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED |
                    AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP);
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
        String text = getString(R.string.message_empty_playlist);
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
        mRadiantController.getQueueManager().setPlaylist(playlist, startIndex);
        mRemote.play();
    }

    protected void shuffleTrackAndPlay(@Nullable List<MusicModel> playlist) {
        if (null == playlist || playlist.isEmpty()) return;
        List<MusicModel> playListToPlay = new ArrayList<>(playlist);
        Collections.shuffle(playListToPlay);
        mRadiantController.getQueueManager().setPlaylist(playListToPlay);
        mRemote.play();
        Toast.makeText(this, getString(R.string.toast_playlist_shuffle_success), Toast.LENGTH_SHORT).show();
    }

    protected void openTrackPicker() {
        startActivityForResult(new Intent(this, TrackPickerActivity.class), REQUEST_CODE_PICK_TRACKS);
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