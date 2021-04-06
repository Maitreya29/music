package com.nezukoos.music.playback;

import androidx.annotation.NonNull;

import com.nezukoos.music.model.MusicModel;

public interface Playback {

    /**
     * Called when we want to start a media playback
     *
     * @param startPosition the portion to start playback from
     * @param startPlaying  if false, media will be prepared to play but will not start playback
     */
    void onPlay(int startPosition, boolean startPlaying);

    /**
     * Called when we want to pause media playback
     */
    void onPause();

    /**
     * Called to notify actual media player about seek event
     *
     * @param position to seek to
     */
    void onSeekTo(int position);

    /**
     * Called to stop media player playback
     *
     * @param abandonAudioFocus whether we want to release audio focus
     */
    void onStop(boolean abandonAudioFocus);

    /**
     * Returns the id of the track currently being played
     *
     * @return int id of the track
     */
    int getActiveMediaId();

    /**
     * @return the current media player playback position
     */
    long getCurrentStreamingPosition();

    /**
     * Sets callback for the PlaybackManager
     *
     * @param callback the instance to receive the callbacks
     */
    void setCallback(Callback callback);

    /**
     * Notifies Playback Manager about events from Local Playback
     */
    interface Callback {
        /**
         * Notifies when playback completes
         */
        void onPlaybackCompletion();

        /**
         * Called to notify about playback state changed
         *
         * @param state the new playback state
         */
        void onPlaybackStateChanged(int state);

        /**
         * Notifies about audio focus changes
         *
         * @param resumePlayback whether to resume playback at current audio focus state
         */
        void onFocusChanged(boolean resumePlayback);

        /**
         * Called when player has identified {@param trackItem} as the track to be played
         *
         * @param trackItem indicates the track that will be played
         */
        void onTrackConfigured(@NonNull MusicModel trackItem);
    }
}