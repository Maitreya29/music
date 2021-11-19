package com.radiant.music.model;

import androidx.annotation.NonNull;

public class TrackFileModel {

    private final String mDisplayName;
    private final String mFileType;
    private final long mFileSize;
    private final int mBitRate;
    private final int mSampleRate;
    private final int mChannelCount;

    public TrackFileModel(@NonNull String displayName, @NonNull String fileType, long fileSize, int bitRate, int sampleRate, int channelCount) {
        mDisplayName = displayName;
        mFileType = fileType;
        mFileSize = fileSize;
        mBitRate = bitRate;
        mSampleRate = sampleRate;
        mChannelCount = channelCount;
    }

    @NonNull
    public String getDisplayName() {
        return mDisplayName;
    }

    @NonNull
    public String getFileType() {
        return mFileType;
    }

    public long getFileSize() {
        return mFileSize;
    }

    public int getBitRate() {
        return mBitRate;
    }

    public int getSampleRate() {
        return mSampleRate;
    }

    public int getChannelCount() {
        return mChannelCount;
    }
}