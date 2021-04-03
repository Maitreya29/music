package com.hardcodecoder.pulsemusic;

import android.media.session.MediaController;
import android.media.session.MediaController.TransportControls;
import android.media.session.PlaybackState;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hardcodecoder.pulsemusic.model.MusicModel;
import com.hardcodecoder.pulsemusic.playback.PlaybackManager;
import com.hardcodecoder.pulsemusic.providers.ProviderManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PulseController {

    private static final PulseController sInstance;

    static {
        sInstance = new PulseController();
    }

    private final Handler mMainHandler = new Handler(Looper.getMainLooper());
    private final List<Callback> mCallbacksList = new ArrayList<>();
    private final QueueManager mQueueManager = new QueueManager();
    private final PulseRemote mRemote = new PulseRemote();
    private List<OnControllerReadyListener> mOnControllerReadyListeners;
    private MediaController mMediaController;
    private int mAudioSessionId = -1;
    private boolean mRememberPlaylist = false;

    @NonNull
    public static PulseController getInstance() {
        return sInstance;
    }

    @Nullable
    public MediaController getController() {
        return mMediaController;
    }

    // This is called from PMS#onCreate when media session is created
    // We need not call this from every activity, however, if an activity
    // that binds to PMS wants to, can update the media controller
    public void setController(@NonNull MediaController mediaController) {
        mMediaController = mediaController;
        mRemote.setTransportControls(mediaController.getTransportControls());
        if (null != mOnControllerReadyListeners) {
            for (OnControllerReadyListener onControllerReadyListener : mOnControllerReadyListeners)
                mMainHandler.post(() -> onControllerReadyListener.onControllerReady(mediaController));
        }
    }

    public int getAudioSessionId() {
        return mAudioSessionId;
    }

    public void setAudioSessionId(int audioSessionId) {
        mAudioSessionId = audioSessionId;
    }

    public boolean isPlaying() {
        if (null == mMediaController || null == mMediaController.getPlaybackState()) return false;
        PlaybackState state = mMediaController.getPlaybackState();
        return state.getState() == PlaybackState.STATE_PLAYING;
    }

    public void setRememberPlaylist(boolean remember, boolean saveNow) {
        mRememberPlaylist = remember;
        if (mRememberPlaylist && saveNow) {
            if (mQueueManager.getQueue().isEmpty())
                ProviderManager.getPreviousPlaylistProvider().deletePlaylist();
            else
                ProviderManager.getPreviousPlaylistProvider().savePlaylist(mQueueManager.getQueue());
        }
    }

    public void addConnectionCallback(@NonNull OnControllerReadyListener callback) {
        if (null == mOnControllerReadyListeners) mOnControllerReadyListeners = new ArrayList<>();
        mOnControllerReadyListeners.add(callback);
        if (null != mMediaController) {
            // The controller is already ready, so notify the listener
            mMainHandler.post(() -> callback.onControllerReady(mMediaController));
        }
    }

    public void removeConnectionCallback(@NonNull OnControllerReadyListener callback) {
        if (null == mOnControllerReadyListeners) return;
        mOnControllerReadyListeners.remove(callback);
        if (mOnControllerReadyListeners.size() == 0) mOnControllerReadyListeners = null;
    }

    public void registerCallback(@NonNull Callback callback) {
        if (null != mMediaController) {
            mMediaController.registerCallback(callback);
            mCallbacksList.add(callback);
        }
    }

    public void unregisterCallback(@NonNull Callback callback) {
        if (null != mMediaController) {
            mMediaController.unregisterCallback(callback);
            mCallbacksList.remove(callback);
        }
    }

    public void setPlaylist(@NonNull List<MusicModel> playlist) {
        setPlaylist(playlist, 0);
    }

    public void setPlaylist(@NonNull List<MusicModel> playlist, int startIndex) {
        mQueueManager.setQueue(playlist, startIndex);
        for (Callback callback : mCallbacksList)
            mMainHandler.post(() -> callback.onTrackListChanged(playlist));
        if (mRememberPlaylist)
            ProviderManager.getPreviousPlaylistProvider().savePlaylist(playlist);
    }

    public void playNext(@NonNull MusicModel item) {
        if (mQueueManager.playNext(item)) {
            final int position = mQueueManager.getActiveIndex() + 1;
            for (Callback callback : mCallbacksList)
                mMainHandler.post(() -> callback.onTrackItemAdded(item, position));
        }
    }

    public void addToQueue(@NonNull MusicModel item) {
        addToQueue(item, Math.max(mQueueManager.getQueue().size() - 1, 0));
    }

    public void addToQueue(@NonNull MusicModel item, int position) {
        mQueueManager.addToQueue(position, item);
        for (Callback callback : mCallbacksList)
            mMainHandler.post(() -> callback.onTrackItemAdded(item, position));
    }

    public void moveQueueItem(int from, int to) {
        mQueueManager.swapItem(from, to);
        for (Callback callback : mCallbacksList)
            mMainHandler.post(() -> callback.onTrackItemMoved(from, to));
    }

    public void removeItemFromQueue(int position) {
        mQueueManager.removeItemFromQueue(position);
        for (Callback callback : mCallbacksList)
            mMainHandler.post(() -> callback.onTrackItemRemoved(position));
    }

    public void releaseController() {
        if (null != mMediaController) {
            for (Callback callback : mCallbacksList)
                mMediaController.unregisterCallback(callback);
            mMediaController = null;
        }
        mCallbacksList.clear();
        if (null != mOnControllerReadyListeners) mOnControllerReadyListeners.clear();
        mQueueManager.resetQueue();
    }

    @NonNull
    public PulseRemote getRemote() {
        return mRemote;
    }

    @NonNull
    public QueueManager getQueueManager() {
        return mQueueManager;
    }

    public interface OnControllerReadyListener {
        void onControllerReady(@NonNull MediaController controller);
    }

    public static class PulseRemote {

        private TransportControls mTransportControls;

        private void setTransportControls(@NonNull TransportControls transportControls) {
            mTransportControls = transportControls;
        }

        public void play() {
            if (null != mTransportControls) mTransportControls.play();
        }

        public void pause() {
            if (null != mTransportControls) mTransportControls.pause();
        }

        public void stop() {
            if (null != mTransportControls) mTransportControls.stop();
        }

        public void skipToNextTrack() {
            if (null != mTransportControls) mTransportControls.skipToNext();
        }

        public void skipToPreviousTrack() {
            if (null != mTransportControls) mTransportControls.skipToPrevious();
        }

        public void seekTo(long position) {
            if (null != mTransportControls) mTransportControls.seekTo(position);
        }
    }

    public static class QueueManager {

        private final List<MusicModel> mActiveQueue = new ArrayList<>();
        private int mActiveIndex = -1;
        private boolean mRepeatCurrentTrack = false;

        public List<MusicModel> getQueue() {
            return mActiveQueue;
        }

        public int getActiveIndex() {
            return mActiveIndex;
        }

        public void setActiveIndex(int index) {
            mActiveIndex = index;
        }

        @NonNull
        public MusicModel getActiveQueueItem() {
            return mActiveQueue.get(mActiveIndex);
        }

        @Nullable
        public MusicModel getNextQueueItem() {
            if (mActiveIndex + 1 < mActiveQueue.size())
                return mActiveQueue.get(mActiveIndex + 1);
            return null;
        }

        public void repeatCurrentTrack(boolean repeat) {
            mRepeatCurrentTrack = repeat;
        }

        public boolean isCurrentTrackInRepeatMode() {
            return mRepeatCurrentTrack;
        }

        public boolean canSkipTrack(short direction) {
            if (mRepeatCurrentTrack) {
                return true;
            }
            if (direction == PlaybackManager.PLAY_NEXT && mActiveIndex < mActiveQueue.size() - 1) {
                setActiveIndex(++mActiveIndex);
                return true;
            } else if (direction == PlaybackManager.PLAY_PREV && mActiveIndex > 0) {
                setActiveIndex(--mActiveIndex);
                return true;
            }
            return false;
        }

        public void resetQueue() {
            mActiveIndex = -1;
            mActiveQueue.clear();
            mRepeatCurrentTrack = false;
        }

        private void setQueue(@NonNull List<MusicModel> playlist, int startIndex) {
            resetQueue();
            mActiveQueue.addAll(playlist);
            setActiveIndex(startIndex);
        }

        private boolean playNext(@NonNull MusicModel item) {
            if (mActiveIndex + 1 < mActiveQueue.size()) {
                if (mActiveQueue.get(mActiveIndex + 1).getId() != item.getId()) {
                    mActiveQueue.add(mActiveIndex + 1, item);
                    return true;
                }
                return false;
            } else {
                mActiveQueue.add(item);
                return true;
            }
        }

        private void addToQueue(int position, @NonNull MusicModel musicModel) {
            mActiveQueue.add(position, musicModel);
            if (position < mActiveIndex) // Track is added above active item, so now the active item index is +1
                mActiveIndex++;
        }

        private void swapItem(int from, int to) {
            if (mActiveQueue.isEmpty()) return;
            Collections.swap(mActiveQueue, from, to);
            if (mActiveIndex == from) // Active active is moved
                mActiveIndex = to;
            else if (mActiveIndex == to) // Item is moved in place of active item
                mActiveIndex = from;
        }

        private void removeItemFromQueue(int position) {
            if (mActiveQueue.size() > position) {
                if (mActiveIndex == position) mRepeatCurrentTrack = false;
                mActiveQueue.remove(position);
                if (position < mActiveIndex) {
                    // Track above active item is removed, so now the active item index is mIndex-1
                    mActiveIndex--;
                }
            }
        }
    }

    public static class Callback extends MediaController.Callback {

        public void onTrackListChanged(@NonNull List<MusicModel> newTracks) {
        }

        public void onTrackItemAdded(@NonNull MusicModel trackItem, int position) {
        }

        public void onTrackItemRemoved(int position) {
        }

        public void onTrackItemMoved(int from, int to) {
        }
    }
}