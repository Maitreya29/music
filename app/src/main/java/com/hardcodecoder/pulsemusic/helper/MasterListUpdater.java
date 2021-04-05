package com.hardcodecoder.pulsemusic.helper;

import android.os.Handler;

import androidx.annotation.NonNull;

import com.hardcodecoder.pulsemusic.TaskRunner;
import com.hardcodecoder.pulsemusic.loaders.LoaderManager;
import com.hardcodecoder.pulsemusic.model.MusicModel;

import java.util.ArrayList;
import java.util.List;

public class MasterListUpdater {

    private static MasterListUpdater mInstance = null;
    private final Handler mMainHandler = TaskRunner.getMainHandler();
    private final List<OnMasterListUpdateListener> mListeners = new ArrayList<>(10);

    private MasterListUpdater() {
    }

    @NonNull
    public static synchronized MasterListUpdater getInstance() {
        if (mInstance == null) mInstance = new MasterListUpdater();
        return mInstance;
    }

    public void addMasterListListener(@NonNull OnMasterListUpdateListener listener) {
        mListeners.add(listener);
    }

    public void removeMasterListListener(@NonNull OnMasterListUpdateListener listener) {
        mListeners.remove(listener);
    }

    public void removeDeletedTrack(@NonNull MusicModel track) {
        List<MusicModel> masterList = LoaderManager.getCachedMasterList();
        if (null == masterList || masterList.isEmpty()) return;
        boolean success = masterList.remove(track);
        if (!success) return;
        for (OnMasterListUpdateListener listener : mListeners)
            mMainHandler.post(() -> listener.onItemDeleted(track));
    }

    public interface OnMasterListUpdateListener {

        void onItemDeleted(@NonNull MusicModel item);
    }
}