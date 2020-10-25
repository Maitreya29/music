package com.hardcodecoder.pulsemusic.singleton;

import com.hardcodecoder.pulsemusic.model.MusicModel;
import com.hardcodecoder.pulsemusic.playback.PlaybackManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TrackManager {

    private static final TrackManager ourInstance = new TrackManager();
    private final List<MusicModel> mActiveList = new ArrayList<>();
    private int mIndex = -1;
    private boolean mRepeatCurrentTrack = false;

    private TrackManager() {
    }

    public static TrackManager getInstance() {
        return ourInstance;
    }

    public void buildDataList(List<MusicModel> newList, int index) {
        mActiveList.clear();
        mActiveList.addAll(newList);
        setActiveIndex(index);
    }

    public int getActiveIndex() {
        return mIndex;
    }

    public void setActiveIndex(int index) {
        mIndex = index;
    }

    public List<MusicModel> getActiveQueue() {
        return mActiveList;
    }

    public MusicModel getActiveQueueItem() {
        return mActiveList.get(mIndex);
    }

    public MusicModel getNextQueueItem() {
        if (mIndex + 1 < mActiveList.size())
            return mActiveList.get(mIndex + 1);
        return null;
    }

    public void repeatCurrentTrack(boolean b) {
        mRepeatCurrentTrack = b;
    }

    public boolean isCurrentTrackInRepeatMode() {
        return mRepeatCurrentTrack;
    }

    public boolean canSkipTrack(short direction) {
        if (mRepeatCurrentTrack) {
            return true;
        }
        if (direction == PlaybackManager.ACTION_PLAY_NEXT && mIndex < mActiveList.size() - 1) {
            setActiveIndex(++mIndex);
            return true;
        } else if (direction == PlaybackManager.ACTION_PLAY_PREV && mIndex > 0) {
            setActiveIndex(--mIndex);
            return true;
        }
        return false;
    }

    public void playNext(MusicModel md) {
        if (mIndex + 1 < mActiveList.size()) {
            if (mActiveList.get(mIndex + 1).getId() != md.getId()) {
                mActiveList.add(mIndex + 1, md);
            }
        } else
            mActiveList.add(mIndex + 1, md);
    }

    public void addToActiveQueue(MusicModel md) {
        mActiveList.add(md);
    }

    public void updateActiveQueue(int from, int to) {
        if (mActiveList.isEmpty()) return;
        Collections.swap(mActiveList, from, to);
        if (mIndex == from) // Active active is moved
            mIndex = to;
        else if (mIndex == to) // Item is moved in place of active item
            mIndex = from;
    }

    public void removeItemFromActiveQueue(int position) {
        if (mActiveList.size() > position) {
            if (mIndex == position) mRepeatCurrentTrack = false;
            mActiveList.remove(position);
            if (position < mIndex) {
                // Track above active item is removed, so now the active item index is mIndex-1
                mIndex--;
            }
        }
    }

    public void restoreItem(int deletedQueueIndex, MusicModel musicModel) {
        mActiveList.add(deletedQueueIndex, musicModel);
        if (deletedQueueIndex < mIndex) // Track above active item is restored, so now the active item index is +1
            mIndex++;
    }

    public void resetTrackManager() {
        mActiveList.clear();
        mIndex = -1;
        mRepeatCurrentTrack = false;
    }
}