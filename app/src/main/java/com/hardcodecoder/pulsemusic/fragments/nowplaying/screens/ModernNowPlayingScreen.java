package com.hardcodecoder.pulsemusic.fragments.nowplaying.screens;

import android.media.MediaMetadata;
import android.media.session.PlaybackState;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.slider.Slider;
import com.google.android.material.textview.MaterialTextView;
import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.fragments.nowplaying.base.BaseNowPlayingScreen;

public class ModernNowPlayingScreen extends BaseNowPlayingScreen {

    public static final String TAG = ModernNowPlayingScreen.class.getSimpleName();
    private Slider mProgressSlider;
    private ImageView mFavoriteBtn;
    private ImageView mRepeatBtn;
    private ImageView mPlayPauseBtn;
    private MaterialTextView mTitle;
    private MaterialTextView mStartTime;
    private MaterialTextView mEndTime;
    private MaterialTextView mSubTitle;
    private MaterialTextView mUpNext;

    public static ModernNowPlayingScreen getInstance() {
        return new ModernNowPlayingScreen();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_now_playing_modern, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setUpPagerAlbumArt(view.findViewById(R.id.modern_nps_album_container),
                R.layout.modern_nps_media_art,
                getMediaImageViewShapeAppearanceModel());

        mTitle = view.findViewById(R.id.modern_nps_title);
        mSubTitle = view.findViewById(R.id.modern_nps_sub_title);
        mProgressSlider = view.findViewById(R.id.modern_nps_slider);
        mStartTime = view.findViewById(R.id.modern_nps_start_time);
        mEndTime = view.findViewById(R.id.modern_nps_end_time);
        mRepeatBtn = view.findViewById(R.id.modern_nps_repeat_btn);
        mPlayPauseBtn = view.findViewById(R.id.modern_nps_play_pause_btn);
        mFavoriteBtn = view.findViewById(R.id.modern_nps_fav_btn);
        mUpNext = view.findViewById(R.id.modern_nps_up_next);
        setGotToCurrentQueueCLickListener(mUpNext);
        view.findViewById(R.id.modern_nps_close_btn).setOnClickListener(v -> {
            if (null != getActivity())
                getActivity().finish();
        });
        setUpSliderControls(mProgressSlider);
        setUpSkipControls(
                view.findViewById(R.id.modern_nps_prev_btn),
                view.findViewById(R.id.modern_nps_next_btn));
        mRepeatBtn.setOnClickListener(v -> toggleRepeatMode());
        mPlayPauseBtn.setOnClickListener(v -> togglePlayPause());
        mFavoriteBtn.setOnClickListener(v -> toggleFavorite());
        setDefaultTintToPlayBtn(mPlayPauseBtn);
    }

    @Override
    public void onRepeatStateChanged(boolean repeat) {
        handleRepeatStateChanged(mRepeatBtn, repeat);
    }

    @Override
    public void onFavoriteStateChanged(boolean isFavorite) {
        handleFavoriteStateChanged(mFavoriteBtn, isFavorite);
    }

    @Override
    public void onMetadataDataChanged(MediaMetadata metadata) {
        super.onMetadataDataChanged(metadata);
        long seconds = metadata.getLong(MediaMetadata.METADATA_KEY_DURATION) / 1000;
        resetSliderValues(mProgressSlider, seconds);
        mStartTime.setText(getFormattedElapsedTime(0));
        mEndTime.setText(getFormattedElapsedTime(seconds));
        mSubTitle.setText(String.format("%s● %s", getString(R.string.artist), metadata.getString(MediaMetadata.METADATA_KEY_ARTIST)));
        mTitle.setText(metadata.getText(MediaMetadata.METADATA_KEY_TITLE));
        mUpNext.setText(getUpNextText());
    }

    @Override
    public void onPlaybackStateChanged(PlaybackState state) {
        super.onPlaybackStateChanged(state);
        togglePlayPauseAnimation(mPlayPauseBtn, state);
    }

    @Override
    public void onProgressValueChanged(int progressInSec) {
        mProgressSlider.setValue(progressInSec);
        mStartTime.setText(getFormattedElapsedTime(progressInSec));
    }
}