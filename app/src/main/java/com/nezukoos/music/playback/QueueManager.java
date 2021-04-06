package com.nezukoos.music.playback;

import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.nezukoos.music.model.MusicModel;
import com.nezukoos.music.providers.PreviousPlaylistProvider;
import com.nezukoos.music.providers.ProviderManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class QueueManager {

    private final PreviousPlaylistProvider mPreviousPlaylistProvider;
    private final List<MusicModel> mActiveQueue;
    private final Handler mHandler;
    private List<OnQueueChangedListener> mListeners;
    private int mActiveIndex = -1;
    private boolean mRememberPlaylist = false;
    private boolean mRepeatCurrentTrack = false;
    private boolean mShuffleMode = false;

    QueueManager(@NonNull Handler handler) {
        mPreviousPlaylistProvider = ProviderManager.getPreviousPlaylistProvider();
        mActiveQueue = new ArrayList<>();
        mHandler = handler;
        mListeners = new ArrayList<>(4);
    }

    public void addQueueChangedListener(@NonNull OnQueueChangedListener listener) {
        mListeners.add(listener);
    }

    public void removeQueueChangedListener(@NonNull OnQueueChangedListener listener) {
        mListeners.remove(listener);
    }

    public List<MusicModel> getPlaylist() {
        return mActiveQueue;
    }

    public void setPlaylist(@NonNull List<MusicModel> playlist) {
        setPlaylist(playlist, 0);
    }

    public void setPlaylist(@NonNull List<MusicModel> playlist, int startIndex) {
        resetPlaylist();
        mActiveQueue.addAll(playlist);
        setActiveIndex(startIndex);
        notifyPlaylistChanged();
    }

    public void playNext(@NonNull MusicModel item) {
        if (addToPlayNext(item)) {
            final int position = mActiveIndex + 1;
            for (OnQueueChangedListener listener : mListeners)
                mHandler.post(() -> listener.onPlaylistItemAdded(item, position));
        }
    }

    public void addToPlaylist(@NonNull MusicModel item) {
        addToPlaylist(item, Math.max(mActiveQueue.size() - 1, 0));
    }

    public void addToPlaylist(@NonNull MusicModel item, int position) {
        insertItem(item, position);
        for (OnQueueChangedListener listener : mListeners)
            mHandler.post(() -> listener.onPlaylistItemAdded(item, position));
    }

    public void swapPlaylistItem(int from, int to) {
        swapItem(from, to);
        for (OnQueueChangedListener listener : mListeners)
            mHandler.post(() -> listener.onPlaylistItemSwapped(from, to));
    }

    public void deletePlaylistItem(@NonNull MusicModel item, int position) {
        removeItemFromQueue(position);
        for (OnQueueChangedListener listener : mListeners)
            mHandler.post(() -> listener.onPlaylistItemDeleted(item, position));
    }

    public void setRememberPlaylist(boolean remember, boolean saveNow) {
        mRememberPlaylist = remember;
        if (mRememberPlaylist && saveNow) {
            if (mActiveQueue.isEmpty())
                ProviderManager.getPreviousPlaylistProvider().deletePlaylist();
            else ProviderManager.getPreviousPlaylistProvider().savePlaylist(mActiveQueue);
        }
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

    public void setShuffleMode(boolean enable) {
        mShuffleMode = enable;
    }

    public boolean isShuffleModeEnabled() {
        return mShuffleMode;
    }

    public boolean skipTrack(short skipDirection) {
        if (mRepeatCurrentTrack) {
            return true;
        }
        if (skipDirection == PlaybackManager.PLAY_NEXT && mActiveIndex < mActiveQueue.size() - 1) {
            setActiveIndex(++mActiveIndex);
            if (mShuffleMode) {
                // Shuffle tracks from mActiveIndex
                Collections.shuffle(mActiveQueue.subList(mActiveIndex, mActiveQueue.size()));
                notifyPlaylistChanged();
            }
            return true;
        } else if (skipDirection == PlaybackManager.PLAY_PREV && mActiveIndex > 0) {
            setActiveIndex(--mActiveIndex);
            return true;
        }
        return false;
    }

    public void resetPlaylist() {
        mActiveIndex = -1;
        mActiveQueue.clear();
        mRepeatCurrentTrack = false;
        mShuffleMode = false;
    }

    public void release() {
        resetPlaylist();
        if (null != mListeners) {
            mListeners.clear();
            mListeners = null;
        }
    }

    private void notifyPlaylistChanged() {
        if (mRememberPlaylist) mPreviousPlaylistProvider.savePlaylist(mActiveQueue);
        for (OnQueueChangedListener listener : mListeners)
            mHandler.post(() -> listener.onPlaylistChanged(mActiveQueue));
    }

    private boolean addToPlayNext(@NonNull MusicModel item) {
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

    private void insertItem(@NonNull MusicModel musicModel, int position) {
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

    public interface OnQueueChangedListener {

        void onPlaylistChanged(@NonNull List<MusicModel> newPlaylist);

        void onPlaylistItemAdded(@NonNull MusicModel newItem, int index);

        void onPlaylistItemDeleted(@NonNull MusicModel deletedItem, int index);

        void onPlaylistItemSwapped(int from, int to);
    }
}