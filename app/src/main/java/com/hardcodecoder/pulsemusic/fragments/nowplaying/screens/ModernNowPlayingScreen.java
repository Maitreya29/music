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

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.slider.Slider;
import com.google.android.material.textview.MaterialTextView;
import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.fragments.nowplaying.base.BaseNowPlayingScreen;

public class ModernNowPlayingScreen extends BaseNowPlayingScreen {

    public static final String TAG = ModernNowPlayingScreen.class.getSimpleName();
    private Slider mProgressSlider;
    private ImageView mFavoriteBtn;
    private ImageView mRepeatBtn;
    private FloatingActionButton mPlayPauseBtn;
    private MaterialTextView mTitle;
    private MaterialTextView mStartTime;
    private MaterialTextView mEndTime;
    private MaterialTextView mSubTitle;
    private MaterialTextView mUpNext;

    @NonNull
    public static ModernNowPlayingScreen getInstance() {
        return new ModernNowPlayingScreen();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_now_playing_modern, container, false);
    }

    @Override
    public void onInitializeViews(@NonNull View view) {
        ViewPager2 pager = view.findViewById(R.id.modern_nps_album_container);
        setUpPagerAlbumArt(pager,
                R.layout.modern_nps_media_art,
                getMediaImageViewShapeAppearanceModel());

        mTitle = view.findViewById(R.id.modern_nps_title);
        mSubTitle = view.findViewById(R.id.modern_nps_sub_title);
        mProgressSlider = view.findViewById(R.id.modern_nps_slider);
        mStartTime = view.findViewById(R.id.modern_nps_start_time);
        mEndTime = view.findViewById(R.id.modern_nps_end_time);
        mRepeatBtn = view.findViewById(R.id.modern_nps_repeat_btn);
        ImageView skipPrev = view.findViewById(R.id.modern_nps_prev_btn);
        mPlayPauseBtn = view.findViewById(R.id.modern_nps_play_pause_btn);
        ImageView skipNext = view.findViewById(R.id.modern_nps_next_btn);
        mFavoriteBtn = view.findViewById(R.id.modern_nps_fav_btn);
        mUpNext = view.findViewById(R.id.modern_nps_up_next);

        view.findViewById(R.id.modern_nps_close_btn).setOnClickListener(v -> {
            if (null != getActivity())
                getActivity().finishAfterTransition();
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
                Animation.RELATIVE_TO_PARENT, 0.3f,
                Animation.RELATIVE_TO_PARENT, 0);

        translateAnimation.setDuration(500);
        translateAnimation.setInterpolator(new DecelerateInterpolator(1.1f));

        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
        alphaAnimation.setInterpolator(new DecelerateInterpolator(1.1f));
        alphaAnimation.setDuration(800);

        AnimationSet set = new AnimationSet(false);
        set.addAnimation(alphaAnimation);
        set.addAnimation(translateAnimation);

        pager.startAnimation(set);
        mTitle.startAnimation(set);
        mSubTitle.startAnimation(set);
        mProgressSlider.startAnimation(set);
        mStartTime.startAnimation(set);
        mEndTime.startAnimation(set);
        mRepeatBtn.startAnimation(set);
        skipPrev.startAnimation(set);
        mPlayPauseBtn.startAnimation(set);
        skipNext.startAnimation(set);
        mFavoriteBtn.startAnimation(set);
        mUpNext.startAnimation(set);
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
        mSubTitle.setText(String.format("%s%s", mArtistTitle, metadata.getString(MediaMetadata.METADATA_KEY_ARTIST)));
        mTitle.setText(metadata.getText(MediaMetadata.METADATA_KEY_TITLE));
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

    @Override
    protected void onUpNextItemChanged(String upNextTitle) {
        mUpNext.setText(upNextTitle);
    }
}