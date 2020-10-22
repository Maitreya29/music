package com.hardcodecoder.pulsemusic.fragments.nowplaying.base;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.ColorStateList;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
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
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.color.MaterialColors;
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
    private boolean mCurrentItemFavorite = false;
    private boolean mShouldAnimateMediaArt = false;

    @CallSuper
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        connectToService();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (null != mMediaArtAdapter && requestCode == CurrentPlaylistActivity.REQUEST_UPDATE_TRACK && resultCode == RESULT_OK) {
            if (null != data && data.getBooleanExtra(CurrentPlaylistActivity.TRACK_CHANGED, false)) {
                List<MusicModel> modifiedTracks = mTrackManager.getActiveQueue();
                if (null != modifiedTracks && !modifiedTracks.isEmpty()) {
                    mMediaArtAdapter.notifyTracksChanged(mTrackManager.getActiveQueue());
                    mMediaArtPager.setCurrentItem(mTrackManager.getActiveIndex());
                } else {
                    // Since playlist is empty (or the user cleared the playlist)
                    // There is no point in showing a blank NowPlayingScreen
                    // We finish the activity itself.
                    getActivity().finish();
                }
            }
        }
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
    }

    @CallSuper
    @Override
    public void onPlaybackStateChanged(PlaybackState state) {
        updateRepeat();
    }

    protected void setUpPagerAlbumArt(ViewPager2 pager, @LayoutRes int redId, ShapeAppearanceModel model) {
        mMediaArtPager = pager;
        mMediaArtAdapter = new MediaArtPagerAdapter(getContext(), TrackManager.getInstance().getActiveQueue(), redId, model);
        mMediaArtPager.setAdapter(mMediaArtAdapter);
        mMediaArtPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (position == mTrackManager.getActiveIndex()) return;
                mTrackManager.setActiveIndex(position);
                mTransportControls.play();
            }
        });
        mMediaArtPager.setCurrentItem(mTrackManager.getActiveIndex(), true);
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

    protected void setUpSliderControls(Slider progressSlider) {
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

    protected void setUpSeekBarControls(AppCompatSeekBar seekBar) {
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

    protected void resetSliderValues(Slider slider) {
        slider.setValue(0);
        slider.setValueTo(mDuration);
    }

    protected void setUpSkipControls(ImageView skipPrev, ImageView skipNext) {
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

    protected void setDefaultTintToPlayBtn(ImageView playPauseBtn) {
        playPauseBtn.setBackgroundTintList(ColorStateList.valueOf(ThemeColors.getAccentColorForCurrentTheme()));
        playPauseBtn.setImageTintList(ColorStateList.valueOf(MaterialColors.getColor(playPauseBtn, R.attr.colorOnPrimary)));
    }

    protected void togglePlayPause() {
        if (null == mController.getPlaybackState()) return;
        PlaybackState state = mController.getPlaybackState();

        if (state.getState() == PlaybackState.STATE_PLAYING) mTransportControls.pause();
        else mTransportControls.play();
    }

    protected void togglePlayPauseAnimation(ImageView playPauseBtn, PlaybackState state) {
        if (null == state || null == getContext() || null == playPauseBtn) return;

        if (state.getState() == PlaybackState.STATE_PLAYING) {
            Drawable d = ContextCompat.getDrawable(getContext(), R.drawable.play_to_pause_linear_out_slow_in);
            playPauseBtn.setImageDrawable(d);
            if (d instanceof AnimatedVectorDrawable) ((AnimatedVectorDrawable) d).start();
        } else {
            Drawable d = ContextCompat.getDrawable(getContext(), R.drawable.pause_to_play);
            playPauseBtn.setImageDrawable(d);
            if (d instanceof AnimatedVectorDrawable) ((AnimatedVectorDrawable) d).start();
        }
    }

    protected String getUpNextText() {
        MusicModel nextItem = TrackManager.getInstance().getNextQueueItem();
        String upNextText;
        if (null != nextItem)
            upNextText = getString(R.string.up_next_title).concat(" ").concat(nextItem.getTrackName());
        else upNextText = getString(R.string.up_next_title_none);
        return upNextText;
    }

    protected String getFormattedElapsedTime(long elapsedTime) {
        return DateUtils.formatElapsedTime(elapsedTime);
    }

    protected long getDurationSeconds() {
        return mDuration;
    }

    protected void setGotToCurrentQueueCLickListener(View view) {
        view.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), CurrentPlaylistActivity.class);
            intent.putExtra(CurrentPlaylistActivity.TRANSITION_STYLE_KEY, CurrentPlaylistActivity.TRANSITION_STYLE_SLIDE);
            startActivityForResult(intent, CurrentPlaylistActivity.REQUEST_UPDATE_TRACK);
        });
    }

    private void updateRepeat() {
        onRepeatStateChanged(mTrackManager.isCurrentTrackInRepeatMode());
    }

    protected void handleRepeatStateChanged(ImageView imageView, boolean repeating) {
        if (repeating) {
            imageView.setImageTintList(ThemeColors.getAccentColorStateList());
            imageView.setImageResource(R.drawable.ic_repeat_one);
        } else {
            imageView.setImageTintList(ThemeColors.getColorControlNormalTintList());
            imageView.setImageResource(R.drawable.ic_repeat);
        }
    }

    private void updateFavoriteItem() {
        ProviderManager.getFavoritesProvider().isTemFavorite(mTrackManager.getActiveQueueItem(), isFavorite ->
                onFavoriteStateChanged((mCurrentItemFavorite = isFavorite)));
    }

    protected void handleFavoriteStateChanged(ImageView imageView, boolean favorite) {
        if (favorite) {
            imageView.setImageTintList(ThemeColors.getAccentColorStateList());
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

    @Override
    public void onDestroy() {
        if (null != mUpdateHelper)
            mUpdateHelper.destroy();
        if (null != getActivity())
            getActivity().unbindService(mServiceConnection);
        super.onDestroy();
    }

    public abstract void onRepeatStateChanged(boolean repeat);

    public abstract void onFavoriteStateChanged(boolean isFavorite);
}