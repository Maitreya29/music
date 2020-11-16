package com.hardcodecoder.pulsemusic.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class Folder {

    private final String mOriginalVolumeName;
    private final String mPath;

    public Folder(@NonNull String originalVolumeName, @NonNull String relativePath) {
        mOriginalVolumeName = originalVolumeName;
        mPath = relativePath;
    }

    public String getVolumeName() {
        return mOriginalVolumeName;
    }

    public String getPaths() {
        return mPath;
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof Folder) {
            Folder folder = (Folder) obj;
            return toString().equals(folder.toString());
        }
        return false;
    }

    @NonNull
    @Override
    public String toString() {
        return mOriginalVolumeName + ":" + mPath;
    }
}