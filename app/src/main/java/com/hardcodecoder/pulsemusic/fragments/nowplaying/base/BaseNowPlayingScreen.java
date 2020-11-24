package com.hardcodecoder.pulsemusic.fragments.nowplaying.base;

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
import android.text.format.DateUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.CallSuper;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.shape.CornerFamily;
import com.google.android.material.shape.ShapeAppearanceModel;
import com.google.android.material.slider.Slider;
import com.hardcodecoder.pulsemusic.PMS;
import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.activities.CurrentPlaylistActivity;
import com.hardcodecoder.pulsemusic.helper.MediaProgressUpdateHelper;
import com.hardcodecoder.pulsemusic.model.MusicModel;
import com.hardcodecoder.pulsemusic.providers.ProviderManager;
import com.hardcodecoder.pulsemusic.singleton.TrackManager;
import com.hardcodecoder.pulsemusic.themes.ThemeColors;
import com.hardcodecoder.pulsemusic.utils.AppSettings;

import java.util.List;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;

public abstract class BaseNowPlayingScreen extends Fragment implements MediaProgressUpdateHelper.Callback {

    private final TrackManager mTrackManager = TrackManager.getInstance();
    private MediaController mController;
    private MediaController.TransportControls mTransportControls;
    private MediaProgressUpdateHelper mUpdateHelper;
    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            PMS.ServiceBinder serviceBinder = (PMS.ServiceBinder) service;
            mController = serviceBinder.getMediaController();
            mTransportControls = mController.getTransportControls();
            mUpdateHelper = new MediaProgressUpdateHelper(mController, BaseNowPlayingScreen.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };
    private ViewPager2 mMediaArtPager;
    private MediaArtPagerAdapter mMediaArtAdapter;
    private long mDuration = 1;
    private int previousState;
    private boolean userScrollChange = false;
    private boolean mCurrentItemFavorite = false;
    private boolean mShouldAnimateMediaArt = false;

    @CallSuper
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        connectToService();
    }

    @CallSuper
    @Override
    public void onMetadataDataChanged(MediaMetadata metadata) {
        if (null != mMediaArtPager)
            mMediaArtPager.setCurrentItem(mTrackManager.getActiveIndex(), mShouldAnimateMediaArt);
        updateFavoriteItem();
        updateRepeat();
        long seconds = metadata.getLong(MediaMetadata.METADATA_KEY_DURATION) / 1000;
        mDuration = seconds == 0 ? 1 : seconds;
        mShouldAnimateMediaArt = true;
        onUpNextItemChanged(getUpNextText());
    }

    @CallSuper
    @Override
    public void onPlaybackStateChanged(PlaybackState state) {
        if (null != state && state.getState() == PlaybackState.STATE_STOPPED)
            finishActivity();
        updateRepeat();
    }

    protected void setUpPagerAlbumArt(ViewPager2 pager, @LayoutRes int redId, ShapeAppearanceModel model) {
        mMediaArtPager = pager;
        mMediaArtAdapter = new MediaArtPagerAdapter(getContext(), TrackManager.getInstance().getActiveQueue(), redId, model);
        mMediaArtPager.setAdapter(mMediaArtAdapter);
        mMediaArtPager.setCurrentItem(mTrackManager.getActiveIndex(), false);
        mMediaArtPager.setSaveEnabled(false);
        mMediaArtPager.setSaveFromParentEnabled(false);
        mMediaArtPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
                if (previousState == ViewPager2.SCROLL_STATE_DRAGGING && state == ViewPager2.SCROLL_STATE_SETTLING)
                    userScrollChange = true;
                else if (previousState == ViewPager2.SCROLL_STATE_SETTLING && state == ViewPager2.SCROLL_STATE_IDLE)
                    userScrollChange = false;
                previousState = state;
            }

            @Override
            public void onPageSelected(int position) {
                if (position == mTrackManager.getActiveIndex()) return;

                if (userScrollChange) {
                    // User changed page, check the active index to determine
                    // whether to skip next or previous
                    final int activeIndex = mTrackManager.getActiveIndex();
                    if (null != mTransportControls) {
                        if (position > activeIndex) mTransportControls.skipToNext();
                        else if (position < activeIndex) mTransportControls.skipToPrevious();
                    }
                } else {
                    // For some reason onPageSelected is triggered
                    // after a call to notifyDataSetChanged with arbitrary position
                    // We set it back to whatever is the current active track index
                    mMediaArtPager.setCurrentItem(mTrackManager.getActiveIndex(), false);
                }
            }
        });
    }

    protected ShapeAppearanceModel getMediaImageViewShapeAppearanceModel() {
        float factor = (float) getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT;
        int[] radiusDP = AppSettings.getNowPlayingAlbumCoverCornerRadius(Objects.requireNonNull(getContext()));
        float tl = radiusDP[0] * factor;
        float tr = radiusDP[1] * factor;
        float bl = radiusDP[2] * factor;
        float br = radiusDP[3] * factor;
        int cornerFamily = CornerFamily.ROUNDED;
        return new ShapeAppearanceModel.Builder()
                .setTopLeftCorner(cornerFamily, tl)
                .setTopRightCorner(cornerFamily, tr)
                .setBottomLeftCorner(cornerFamily, bl)
                .setBottomRightCorner(cornerFamily, br)
                .build();
    }

    protected void setUpSliderControls(@NonNull Slider progressSlider) {
        progressSlider.addOnSliderTouchListener(new Slider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(@NonNull Slider slider) {
                mUpdateHelper.stop();
            }

            @Override
            public void onStopTrackingTouch(@NonNull Slider slider) {
                // Pass progress in milli seconds
                mTransportControls.seekTo((long) slider.getValue() * 1000);
                onProgressValueChanged((int) slider.getValue());
            }
        });
        progressSlider.setLabelFormatter(value -> DateUtils.formatElapsedTime((long) value));
    }

    protected void setUpSeekBarControls(@NonNull AppCompatSeekBar seekBar) {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mUpdateHelper.stop();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Pass progress in milli seconds
                mTransportControls.seekTo((long) seekBar.getProgress() * 1000);
                onProgressValueChanged(seekBar.getProgress());
            }
        });
    }

    protected void resetSliderValues(@NonNull Slider slider) {
        slider.setValue(0);
        slider.setValueTo(mDuration);
    }

    protected void setUpSkipControls(@NonNull ImageView skipPrev, @NonNull ImageView skipNext) {
        skipPrev.setOnClickListener(v -> mTransportControls.skipToPrevious());
        skipNext.setOnClickListener(v -> mTransportControls.skipToNext());
    }

    protected void toggleRepeatMode() {
        boolean repeat = !mTrackManager.isCurrentTrackInRepeatMode();
        mTrackManager.repeatCurrentTrack(repeat);
        onRepeatStateChanged(repeat);
    }

    protected void toggleFavorite() {
        if (mCurrentItemFavorite) {
            ProviderManager.getFavoritesProvider().removeFromFavorite(mTrackManager.getActiveQueueItem());
            Toast.makeText(getContext(), getString(R.string.removed_from_fav), Toast.LENGTH_SHORT).show();
        } else {
            MusicModel md = mTrackManager.getActiveQueueItem();
            if (md.getId() < 0)
                Toast.makeText(getContext(), getString(R.string.cannot_add_to_fav), Toast.LENGTH_SHORT).show();
            else {
                ProviderManager.getFavoritesProvider().addToFavorites(md);
                Toast.makeText(getContext(), getString(R.string.added_to_fav), Toast.LENGTH_SHORT).show();
            }
        }
        updateFavoriteItem();
    }

    protected void setDefaultTintToPlayBtn(@NonNull ImageView playPauseBtn) {
        playPauseBtn.setBackgroundTintList(ThemeColors.getPrimaryColorStateList());
        playPauseBtn.setImageTintList(ColorStateList.valueOf(ThemeColors.getCurrentColorOnPrimary()));
    }

    protected void togglePlayPause() {
        if (null == mController.getPlaybackState()) return;
        PlaybackState state = mController.getPlaybackState();

        if (state.getState() == PlaybackState.STATE_PLAYING) mTransportControls.pause();
        else mTransportControls.play();
    }

    protected void togglePlayPauseAnimation(View playPauseBtn, PlaybackState state) {
        if (null == state || null == getContext() || null == playPauseBtn) return;
        playPauseBtn.setSelected(state.getState() == PlaybackState.STATE_PLAYING);
    }

    protected String getFormattedElapsedTime(long elapsedTime) {
        return DateUtils.formatElapsedTime(elapsedTime);
    }

    protected long getDurationSeconds() {
        return mDuration;
    }

    protected void setGotToCurrentQueueCLickListener(@NonNull View view) {
        view.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), CurrentPlaylistActivity.class);
            intent.putExtra(CurrentPlaylistActivity.TRANSITION_STYLE_KEY, CurrentPlaylistActivity.TRANSITION_STYLE_SLIDE);
            startActivityForResult(intent, CurrentPlaylistActivity.REQUEST_UPDATE_TRACK);
        });
    }

    protected void onUpNextItemChanged(String upNextTitle) {
    }

    private String getUpNextText() {
        MusicModel nextItem = mTrackManager.getNextQueueItem();
        String upNextText;
        if (null != nextItem)
            upNextText = getString(R.string.up_next_title).concat(" ").concat(nextItem.getTrackName());
        else upNextText = getString(R.string.up_next_title_none);
        return upNextText;
    }

    private void updateRepeat() {
        onRepeatStateChanged(mTrackManager.isCurrentTrackInRepeatMode());
    }

    protected void handleRepeatStateChanged(ImageView imageView, boolean repeating) {
        if (repeating) {
            imageView.setImageTintList(ThemeColors.getPrimaryColorStateList());
        } else {
            imageView.setImageTintList(ThemeColors.getColorControlNormalTintList());
        }
    }

    private void updateFavoriteItem() {
        ProviderManager.getFavoritesProvider().isTemFavorite(mTrackManager.getActiveQueueItem(), isFavorite ->
                onFavoriteStateChanged((mCurrentItemFavorite = isFavorite != null && isFavorite)));
    }

    protected void handleFavoriteStateChanged(ImageView imageView, boolean favorite) {
        if (favorite) {
            imageView.setImageTintList(ThemeColors.getPrimaryColorStateList());
            imageView.setImageResource(R.drawable.ic_favorite);
        } else {
            imageView.setImageTintList(ThemeColors.getColorControlNormalTintList());
            imageView.setImageResource(R.drawable.ic_favorite_border);
        }
    }

    private void connectToService() {
        if (null != getActivity()) {
            Intent intent = new Intent(getActivity(), PMS.class);
            getActivity().bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
        }
    }

    private void finishActivity() {
        if (getActivity() != null) getActivity().finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (null != mMediaArtAdapter && requestCode == CurrentPlaylistActivity.REQUEST_UPDATE_TRACK && resultCode == RESULT_OK) {
            if (null != data && data.getBooleanExtra(CurrentPlaylistActivity.TRACK_CHANGED, false)) {

                List<MusicModel> modifiedTracks = mTrackManager.getActiveQueue();
                int trackIndex = mTrackManager.getActiveIndex();
                int currentItemIndex = mMediaArtPager.getCurrentItem();

                if (null != modifiedTracks && !modifiedTracks.isEmpty()) {
                    onUpNextItemChanged(getUpNextText());
                    mMediaArtAdapter.notifyTracksChanged(mTrackManager.getActiveQueue(), completed -> {
                        if (currentItemIndex != trackIndex)
                            mMediaArtPager.setCurrentItem(mTrackManager.getActiveIndex(), false);
                    });
                } else {
                    // Since playlist is null or empty maybe because the
                    // user cleared the last active playlist item (which triggers playback stop)
                    // There is no point in showing a blank NowPlayingScreen
                    // We finish the activity itself.
                    finishActivity();
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        if (null != mMediaArtPager)
            mMediaArtPager.requestDisallowInterceptTouchEvent(false);
        if (null != mUpdateHelper)
            mUpdateHelper.destroy();
        if (null != getActivity())
            getActivity().unbindService(mServiceConnection);
        super.onDestroy();
    }

    public abstract void onRepeatStateChanged(boolean repeat);

    public abstract void onFavoriteStateChanged(boolean isFavorite);
}