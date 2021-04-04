package com.hardcodecoder.pulsemusic.playback;

import android.media.session.MediaController;
import android.media.session.MediaController.TransportControls;
import android.media.session.PlaybackState;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hardcodecoder.pulsemusic.TaskRunner;

import java.util.ArrayList;
import java.util.List;

public class PulseController {

    private static final PulseController sInstance;

    static {
        sInstance = new PulseController();
    }

    private final Handler mMainHandler = TaskRunner.getMainHandler();
    private final QueueManager mQueueManager = new QueueManager(mMainHandler);
    private final PulseRemote mRemote = new PulseRemote();
    private List<OnControllerReadyListener> mOnControllerReadyListeners;
    private MediaController mMediaController;
    private int mAudioSessionId = -1;

    @NonNull
    public static PulseController getInstance() {
        return sInstance;
    }

    @Nullable
    public MediaController getController() {
        return mMediaController;
    }

    // This is called from PMS#onCreate when media session is created
    // We need not call this from every activity. However, if an activity
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

    public void releaseController() {
        mMediaController = null;
        if (null != mOnControllerReadyListeners) mOnControllerReadyListeners.clear();
        mQueueManager.release();
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

    public static final class PulseRemote {

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
}