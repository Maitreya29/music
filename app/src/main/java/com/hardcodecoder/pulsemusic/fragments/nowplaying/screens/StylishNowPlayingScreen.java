package com.hardcodecoder.pulsemusic.fragments.nowplaying.screens;

import android.media.MediaMetadata;
import android.media.session.PlaybackState;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.slider.Slider;
import com.google.android.material.textview.MaterialTextView;
import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.fragments.nowplaying.base.BaseNowPlayingScreen;

public class StylishNowPlayingScreen extends BaseNowPlayingScreen {

    public static final String TAG = StylishNowPlayingScreen.class.getSimpleName();
    private Slider mProgressSlider;
    private ImageView mFavoriteBtn;
    private ImageView mRepeatBtn;
    private ImageView mPlayPauseBtn;
    private MaterialTextView mTitle;
    private MaterialTextView mSubTitle;
    private MaterialTextView mStartTime;
    private MaterialTextView mEndTime;
    private MaterialTextView mUpNext;

    public static StylishNowPlayingScreen getInstance() {
        return new StylishNowPlayingScreen();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragmen_now_playing_stylish, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ViewPager2 pager = view.findViewById(R.id.stylish_nps_album_container);
        setUpPagerAlbumArt(pager,
                R.layout.stylish_nps_media_art,
                getMediaImageViewShapeAppearanceModel());

        mTitle = view.findViewById(R.id.stylish_nps_title);
        mSubTitle = view.findViewById(R.id.stylish_nps_sub_title);
        mProgressSlider = view.findViewById(R.id.stylish_nps_slider);
        mStartTime = view.findViewById(R.id.stylish_nps_start_time);
        mEndTime = view.findViewById(R.id.stylish_nps_end_time);
        ImageView skipPrev = view.findViewById(R.id.stylish_nps_prev_btn);
        ImageView skipNext = view.findViewById(R.id.stylish_nps_next_btn);
        mRepeatBtn = view.findViewById(R.id.stylish_nps_repeat_btn);
        mPlayPauseBtn = view.findViewById(R.id.stylish_nps_play_pause_btn);
        mFavoriteBtn = view.findViewById(R.id.stylish_nps_favourite_btn);
        mUpNext = view.findViewById(R.id.stylish_nps_up_next);

        view.findViewById(R.id.stylish_nps_close_btn).setOnClickListener(v -> {
            if (null != getActivity())
                getActivity().finish();
        });
        mRepeatBtn.setOnClickListener(v -> toggleRepeatMode());
        mPlayPauseBtn.setOnClickListener(v -> togglePlayPause());
        mFavoriteBtn.setOnClickListener(v -> toggleFavorite());

        setGotToCurrentQueueCLickListener(mUpNext);
        setUpSliderControls(mProgressSlider);
        setUpSkipControls(skipPrev, skipNext);
        setDefaultTintToPlayBtn(mPlayPauseBtn);

        TranslateAnimation translateAnimation = new TranslateAnimation(
                Animation.ABSOLUTE, 0,
                Animation.ABSOLUTE, 0,
                Animation.RELATIVE_TO_PARENT, 0.36f,
                Animation.RELATIVE_TO_PARENT, 0);

        translateAnimation.setDuration(500);
        translateAnimation.setInterpolator(new DecelerateInterpolator(1.1f));

        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
        alphaAnimation.setInterpolator(new DecelerateInterpolator(1.1f));
        alphaAnimation.setDuration(800);

        AnimationSet set = new AnimationSet(false);
        set.addAnimation(alphaAnimation);
        set.addAnimation(translateAnimation);

        mTitle.startAnimation(set);
        mUpNext.startAnimation(set);
        pager.startAnimation(set);
        mSubTitle.startAnimation(set);
        mProgressSlider.startAnimation(set);
        mStartTime.startAnimation(set);
        mEndTime.startAnimation(set);
        mRepeatBtn.startAnimation(set);
        skipPrev.startAnimation(set);
        mPlayPauseBtn.startAnimation(set);
        skipNext.startAnimation(set);
        mFavoriteBtn.startAnimation(set);
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
        long seconds = getDurationSeconds();
        resetSliderValues(mProgressSlider);
        mStartTime.setText(getFormattedElapsedTime(0));
        mEndTime.setText(getFormattedElapsedTime(seconds));
        mTitle.setText(metadata.getText(MediaMetadata.METADATA_KEY_TITLE));
        mSubTitle.setText(String.format("%s● %s", getString(R.string.artist), metadata.getString(MediaMetadata.METADATA_KEY_ARTIST)));
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