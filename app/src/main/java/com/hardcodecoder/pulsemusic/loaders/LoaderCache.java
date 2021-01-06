package com.hardcodecoder.pulsemusic.loaders;

import androidx.annotation.Nullable;

import com.hardcodecoder.pulsemusic.model.MusicModel;

import java.util.ArrayList;
import java.util.List;

public class LoaderCache {

    private static List<MusicModel> mAllTracksList = null;
    private static List<MusicModel> mRediscoverList = null;
    private static List<MusicModel> mSuggestions = null;
    private static List<MusicModel> mLatestTracks = null;

    @Nullable
    public static List<MusicModel> getAllTracksList() {
        return mAllTracksList;
    }

    static void setAllTracksList(List<MusicModel> allTracksList) {
        if (null != mAllTracksList) {
            // Let gc do its work
            mAllTracksList.clear();
            mAllTracksList = null;
        }
        if (null != allTracksList)
            mAllTracksList = new ArrayList<>(allTracksList);
    }

    @Nullable
    public static List<MusicModel> getRediscoverList() { return mRediscoverList; }

    static void setRediscoverList(List<MusicModel> rediscoverList) {
        if (null != mRediscoverList) {
            // Let gc do its work
            mRediscoverList.clear();
            mRediscoverList = null;
        }
        if (null != rediscoverList)
            mRediscoverList = new ArrayList<>(rediscoverList);
    }

    @Nullable
    public static List<MusicModel> getSuggestions() {
        return mSuggestions;
    }

    static void setSuggestions(List<MusicModel> suggestions) {
        if (null != mSuggestions) {
            // Let gc do its work
            mSuggestions.clear();
            mSuggestions = null;
        }
        if (null != suggestions)
            mSuggestions = new ArrayList<>(suggestions);
    }

    @Nullable
    public static List<MusicModel> getLatestTracks() {
        return mLatestTracks;
    }

    static void setLatestTracks(List<MusicModel> latestTracks) {
        if (null != mLatestTracks) {
            // Let gc do its work
            mLatestTracks.clear();
            mLatestTracks = null;
        }
        if (null != latestTracks)
            mLatestTracks = new ArrayList<>(latestTracks);
    }

    static void releaseCache() {
        if (mAllTracksList != null) mAllTracksList.clear();
        mAllTracksList = null;
        if (mSuggestions != null) mSuggestions.clear();
        mSuggestions = null;
        if (mLatestTracks != null) mLatestTracks.clear();
        mLatestTracks = null;
    }
}