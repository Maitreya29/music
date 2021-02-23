package com.hardcodecoder.pulsemusic.fragments.nowplaying.base;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
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
import com.hardcodecoder.pulsemusic.Preferences;
import com.hardcodecoder.pulsemusic.PulseController;
import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.activities.base.DraggableNowPlayingSheetActivity;
import com.hardcodecoder.pulsemusic.dialog.CurrentQueueBottomSheet;
import com.hardcodecoder.pulsemusic.helper.MediaProgressUpdateHelper;
import com.hardcodecoder.pulsemusic.helper.UIHelper;
import com.hardcodecoder.pulsemusic.model.MusicModel;
import com.hardcodecoder.pulsemusic.providers.FavoritesProvider;
import com.hardcodecoder.pulsemusic.providers.ProviderManager;
import com.hardcodecoder.pulsemusic.themes.ThemeColors;
import com.hardcodecoder.pulsemusic.utils.AppSettings;

import java.util.List;

public abstract class BaseNowPlayingScreen extends Fragment
        implements MediaProgressUpdateHelper.Callback,
        FavoritesProvider.FavoritesProviderCallback,
        SharedPreferences.OnSharedPreferenceChangeListener {

    private final boolean mRequiresStatusBarPadding;
    protected String mUpNextTitle = "";
    protected String mArtistTitle = "";
    private PulseController mPulseController;
    private PulseController.QueueManager mQueueManager;
    private PulseController.PulseRemote mRemote;
    private MediaProgressUpdateHelper mUpdateHelper;
    private ServiceConnection mServiceConnection = null;
    private ViewPager2 mMediaArtPager;
    private MediaArtPagerAdapter mMediaArtAdapter;
    private final PulseController.Callback mControllerCallback = new PulseController.Callback() {
        @Override
        public void onTrackListChanged(@NonNull List<MusicModel> newTracks) {
            int trackIndex = mQueueManager.getActiveIndex();
            int currentItemIndex = mMediaArtPager.getCurrentItem();
            mMediaArtAdapter.notifyTracksChanged(newTracks, completed -> {
                if (currentItemIndex != trackIndex)
                    mMediaArtPager.setCurrentItem(mQueueManager.getActiveIndex(), false);
            });
        }

        @Override
        public void onTrackItemAdded(@NonNull MusicModel trackItem, int position) {
            mMediaArtAdapter.notifyTrackAdded(trackItem, position);
            if (position == mMediaArtPager.getCurrentItem() + 1)
                onUpNextItemChanged(getUpNextText());
        }

        @Override
        public void onTrackItemRemoved(int position) {
            mMediaArtAdapter.notifyTrackRemoved(position);
            if (position == mMediaArtPager.getCurrentItem() + 1)
                onUpNextItemChanged(getUpNextText());
        }

        @Override
        public void onTrackItemMoved(int from, int to) {
            mMediaArtAdapter.notifyTracksSwapped(from, to);
            int upNextPosition = mMediaArtPager.getCurrentItem() + 1;
            if (from == upNextPosition || to == upNextPosition)
                onUpNextItemChanged(getUpNextText());
        }
    };
    private long mCurrentTrackDuration = 1;
    private long mCurrentStreamingPosition = 0;
    private long mForwardSeekDuration = 10000;
    private long mBackwardsSeekDuration = 10000;
    private int previousState;
    private boolean userScrollChange = false;
    private boolean mCurrentItemFavorite = false;
    private boolean mShouldAnimateMediaArt = false;

    public BaseNowPlayingScreen(boolean requiresStatusBarPadding) {
        mRequiresStatusBarPadding = requiresStatusBarPadding;
    }

    @CallSuper
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (mRequiresStatusBarPadding) {
            view.setOnApplyWindowInsetsListener((v, insets) -> {
                v.setPadding(
                        0,
                        insets.getSystemWindowInsetTop(),
                        0,
                        0);
                return insets.replaceSystemWindowInsets(
                        insets.getSystemWindowInsetLeft(),
                        0,
                        insets.getStableInsetRight(),
                        insets.getStableInsetBottom());
            });
            view.requestApplyInsets();
        }

        mPulseController = PulseController.getInstance();
        mQueueManager = mPulseController.getQueueManager();
        mRemote = mPulseController.getRemote();

        mUpNextTitle = getString(R.string.coming_up_next) + " ● ";
        mArtistTitle = getString(R.string.nav_artists) + " ● ";

        onInitializeViews(view);

        if (mPulseController.getController() != null) {
            mUpdateHelper = new MediaProgressUpdateHelper(mPulseController.getController(), BaseNowPlayingScreen.this);
            mPulseController.registerCallback(mControllerCallback);
        } else {
            mServiceConnection = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    PMS.ServiceBinder serviceBinder = (PMS.ServiceBinder) service;
                    mPulseController.setController(serviceBinder.getMediaController());
                    mUpdateHelper = new MediaProgressUpdateHelper(mPulseController.getController(), BaseNowPlayingScreen.this);
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {
                }
            };
            if (null != getActivity()) {
                Intent intent = new Intent(getActivity(), PMS.class);
                getActivity().bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
            }
        }

        // Convert the int values to mills
        mForwardSeekDuration = 1000 * AppSettings.getSeekButtonDuration(requireContext(), Preferences.NOW_PLAYING_SEEK_DURATION_FORWARD);
        mBackwardsSeekDuration = 1000 * AppSettings.getSeekButtonDuration(requireContext(), Preferences.NOW_PLAYING_SEEK_DURATION_BACKWARD);

        requireActivity().getSharedPreferences(Preferences.NOW_PLAYING_CONTROLS, Context.MODE_PRIVATE)
                .registerOnSharedPreferenceChangeListener(this);

        ProviderManager.getFavoritesProvider().addCallback(this);
    }

    @Override
    public void onSharedPreferenceChanged(@NonNull SharedPreferences sharedPreferences, @NonNull String key) {
        switch (key) {
            case Preferences.NOW_PLAYING_SEEK_DURATION_FORWARD:
                mForwardSeekDuration = 1000 * sharedPreferences.getInt(
                        Preferences.NOW_PLAYING_SEEK_DURATION_FORWARD,
                        Preferences.NOW_PLAYING_SEEK_DURATION_DEF);
                break;
            case Preferences.NOW_PLAYING_SEEK_DURATION_BACKWARD:
                mBackwardsSeekDuration = 1000 * sharedPreferences.getInt(
                        Preferences.NOW_PLAYING_SEEK_DURATION_BACKWARD,
                        Preferences.NOW_PLAYING_SEEK_DURATION_DEF);
                break;
            case Preferences.NOW_PLAYING_CONTROLS_SEEK_ENABLED:
                onTrackControlButtonsChanged();
                break;
        }
    }

    @CallSuper
    @Override
    public void onMetadataDataChanged(MediaMetadata metadata) {
        if (null != mMediaArtPager)
            mMediaArtPager.setCurrentItem(mQueueManager.getActiveIndex(), mShouldAnimateMediaArt);
        updateFavoriteItem();
        updateRepeat();
        long seconds = metadata.getLong(MediaMetadata.METADATA_KEY_DURATION) / 1000;
        mCurrentTrackDuration = seconds == 0 ? 1 : seconds;
        mShouldAnimateMediaArt = true;
        onUpNextItemChanged(getUpNextText());
    }

    @CallSuper
    @Override
    public void onPlaybackStateChanged(PlaybackState state) {
        updateRepeat();
    }

    @Override
    public void onProgressValueChanged(long progress) {
        mCurrentStreamingPosition = progress;
        onProgressUpdated((int) mCurrentStreamingPosition / 1000);
    }

    @Override
    public void onFavoriteAdded(@NonNull MusicModel item) {
        if (item.getId() == mQueueManager.getActiveQueueItem().getId()) {
            onFavoriteStateChanged(mCurrentItemFavorite = true);
        }
    }

    @Override
    public void onFavoriteRemoved(@NonNull MusicModel item) {
        if (item.getId() == mQueueManager.getActiveQueueItem().getId()) {
            onFavoriteStateChanged(mCurrentItemFavorite = false);
        }
    }

    @Override
    public void onFavoritesCleared() {
        onFavoriteStateChanged(mCurrentItemFavorite = false);
    }

    protected void setUpPagerAlbumArt(@NonNull ViewPager2 pager, @LayoutRes int redId, ShapeAppearanceModel model) {
        mMediaArtPager = pager;
        // Workaround to disable over scroll mode
        mMediaArtPager.getChildAt(0).setOverScrollMode(ViewPager2.OVER_SCROLL_NEVER);
        mMediaArtAdapter = new MediaArtPagerAdapter(pager.getContext(), mQueueManager.getQueue(), redId, model);
        mMediaArtPager.setAdapter(mMediaArtAdapter);
        mMediaArtPager.setCurrentItem(mQueueManager.getActiveIndex(), false);
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
                if (position == mQueueManager.getActiveIndex()) return;

                if (userScrollChange) {
                    // User changed page, check the active index to determine
                    // whether to skip next or previous
                    final int activeIndex = mQueueManager.getActiveIndex();
                    if (position > activeIndex) mRemote.skipToNextTrack();
                    else if (position < activeIndex) mRemote.skipToPreviousTrack();

                } else {
                    // For some reason onPageSelected is triggered
                    // after a call to notifyDataSetChanged with arbitrary position
                    // We set it back to whatever is the current active track index
                    mMediaArtPager.setCurrentItem(mQueueManager.getActiveIndex(), false);
                }
            }
        });
    }

    protected ShapeAppearanceModel getMediaImageViewShapeAppearanceModel() {
        float factor = (float) getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT;
        int[] radiusDP = AppSettings.getNowPlayingAlbumCoverCornerRadius(requireContext());
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
                seekTo((long) slider.getValue() * 1000);
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
                seekTo((long) seekBar.getProgress() * 1000);
            }
        });
    }

    protected void resetSliderValues(@NonNull Slider slider) {
        slider.setValue(0);
        slider.setValueTo(mCurrentTrackDuration);
    }

    protected void setUpTrackControls(@NonNull ImageView trackControls1, @NonNull ImageView trackControls2) {
        if (isSeekButtonsEnabled()) {
            trackControls1.setImageResource(R.drawable.ic_fast_forward);
            trackControls1.setRotation(180);
            trackControls2.setImageResource(R.drawable.ic_fast_forward);
            setUpSeekControls(trackControls1, trackControls2);
        } else {
            trackControls1.setImageResource(R.drawable.ic_round_skip_previous);
            trackControls1.setRotation(0);
            trackControls2.setImageResource(R.drawable.ic_round_skip_next);
            setUpSkipControls(trackControls1, trackControls2);
        }
    }

    protected void onTrackControlButtonsChanged() {
    }

    protected boolean isSeekButtonsEnabled() {
        return AppSettings.isSeekButtonsEnabled(requireContext());
    }

    protected void setUpSkipControls(@NonNull ImageView skipPrev, @NonNull ImageView skipNext) {
        skipPrev.setOnClickListener(v -> mRemote.skipToPreviousTrack());
        skipNext.setOnClickListener(v -> mRemote.skipToNextTrack());
    }

    protected void setUpSeekControls(@NonNull ImageView seekBackward, @NonNull ImageView seekForward) {
        seekBackward.setOnClickListener(v -> seekTo(mCurrentStreamingPosition - mBackwardsSeekDuration));
        seekForward.setOnClickListener(v -> seekTo(mCurrentStreamingPosition + mForwardSeekDuration));
    }

    protected void toggleRepeatMode() {
        boolean repeat = !mQueueManager.isCurrentTrackInRepeatMode();
        mQueueManager.repeatCurrentTrack(repeat);
        onRepeatStateChanged(repeat);
    }

    protected void toggleFavorite() {
        if (mCurrentItemFavorite) {
            ProviderManager.getFavoritesProvider().removeFromFavorite(mQueueManager.getActiveQueueItem());
            Toast.makeText(requireContext(), getString(R.string.toast_removed_from_favorites), Toast.LENGTH_SHORT).show();
        } else {
            MusicModel md = mQueueManager.getActiveQueueItem();
            if (md.getId() < 0)
                Toast.makeText(requireContext(), getString(R.string.toast_cannot_add_to_favorites), Toast.LENGTH_SHORT).show();
            else {
                ProviderManager.getFavoritesProvider().addToFavorites(md);
                Toast.makeText(requireContext(), getString(R.string.toast_added_to_favorites), Toast.LENGTH_SHORT).show();
            }
        }
        updateFavoriteItem();
    }

    protected void setDefaultTintToPlayBtn(@NonNull ImageView playPauseBtn) {
        playPauseBtn.setBackgroundTintList(ThemeColors.getPrimaryColorStateList());
        playPauseBtn.setImageTintList(ColorStateList.valueOf(ThemeColors.getCurrentColorOnPrimary()));
    }

    protected void togglePlayPause() {
        MediaController controller = mPulseController.getController();
        if (null == controller || null == controller.getPlaybackState()) return;
        PlaybackState state = controller.getPlaybackState();
        if (state.getState() == PlaybackState.STATE_PLAYING) mRemote.pause();
        else mRemote.play();
    }

    protected void togglePlayPauseAnimation(View playPauseBtn, PlaybackState state) {
        if (null == state || null == playPauseBtn) return;
        playPauseBtn.setSelected(state.getState() == PlaybackState.STATE_PLAYING);
    }

    protected String getFormattedElapsedTime(long elapsedTime) {
        return DateUtils.formatElapsedTime(elapsedTime);
    }

    protected long getDurationSeconds() {
        return mCurrentTrackDuration;
    }

    protected void setGotToCurrentQueueCLickListener(@NonNull View view) {
        view.setOnClickListener(v -> {
            CurrentQueueBottomSheet currentQueueBottomSheet = CurrentQueueBottomSheet.getInstance();
            currentQueueBottomSheet.show(requireFragmentManager(), CurrentQueueBottomSheet.TAG);
        });
    }

    protected void setShowOptionsClickMenuListener(@NonNull View view) {
        view.setOnClickListener(v ->
                UIHelper.showMenuForLibraryTracks(requireActivity(), mQueueManager.getActiveQueueItem()));
    }

    protected void onUpNextItemChanged(String upNextTitle) {
    }

    private void seekTo(long milliseconds) {
        long currentTrackDurationMills = mCurrentTrackDuration * 1000;
        if (milliseconds > currentTrackDurationMills)
            mCurrentStreamingPosition = currentTrackDurationMills;
        else mCurrentStreamingPosition = Math.max(milliseconds, 0);
        mRemote.seekTo(mCurrentStreamingPosition);
        onProgressUpdated((int) mCurrentStreamingPosition / 1000);
    }

    @NonNull
    private String getUpNextText() {
        MusicModel nextItem = mQueueManager.getNextQueueItem();
        String upNextText;
        if (null != nextItem)
            upNextText = mUpNextTitle + nextItem.getTrackName();
        else upNextText = getString(R.string.playlist_completed);
        return upNextText;
    }

    private void updateRepeat() {
        onRepeatStateChanged(mQueueManager.isCurrentTrackInRepeatMode());
    }

    protected void handleRepeatStateChanged(ImageView imageView, boolean repeating) {
        if (repeating) {
            imageView.setImageTintList(ThemeColors.getPrimaryColorStateList());
        } else {
            imageView.setImageTintList(ThemeColors.getColorControlNormalTintList());
        }
    }

    private void updateFavoriteItem() {
        ProviderManager.getFavoritesProvider().isTemFavorite(mQueueManager.getActiveQueueItem(), isFavorite ->
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

    protected void dismiss() {
        ((DraggableNowPlayingSheetActivity) requireActivity()).collapseBottomSheet();
    }

    @Override
    public void onDestroy() {
        if (null != mMediaArtPager)
            mMediaArtPager.requestDisallowInterceptTouchEvent(false);
        if (null != mUpdateHelper)
            mUpdateHelper.destroy();
        if (null != getActivity()) {
            requireActivity().getSharedPreferences(Preferences.NOW_PLAYING_CONTROLS, Context.MODE_PRIVATE)
                    .unregisterOnSharedPreferenceChangeListener(this);
            if (null != mServiceConnection) getActivity().unbindService(mServiceConnection);
        }
        mPulseController.unregisterCallback(mControllerCallback);
        ProviderManager.getFavoritesProvider().removeCallback(this);
        super.onDestroy();
    }

    public abstract void onInitializeViews(@NonNull View view);

    public abstract void onRepeatStateChanged(boolean repeat);

    public abstract void onFavoriteStateChanged(boolean isFavorite);

    public abstract void onProgressUpdated(int progressInSeconds);
}