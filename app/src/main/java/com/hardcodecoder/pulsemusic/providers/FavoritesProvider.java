package com.hardcodecoder.pulsemusic.providers;

import android.os.Handler;

import androidx.annotation.NonNull;

import com.hardcodecoder.pulsemusic.TaskRunner;
import com.hardcodecoder.pulsemusic.TaskRunner.Callback;
import com.hardcodecoder.pulsemusic.helper.DataModelHelper;
import com.hardcodecoder.pulsemusic.model.MusicModel;
import com.hardcodecoder.pulsemusic.storage.StorageUtil;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FavoritesProvider {

    private final Handler mHandler;
    private final String mFavoritesFilePath;
    private Set<Integer> mFavoritesSet;

    public FavoritesProvider(String baseDir, Handler handler) {
        mHandler = handler;
        mFavoritesFilePath = baseDir + File.separator + "favorites.txt";
        StorageUtil.createFile(new File(mFavoritesFilePath));
    }

    public void addToFavorites(@NonNull MusicModel musicModel) {
        if (musicModel.getId() < 0) return;
        TaskRunner.executeAsync(() -> {
            loadFavorites();
            if (mFavoritesSet.add(musicModel.hashCode())) {
                StorageUtil.writeStringToFile(
                        new File(mFavoritesFilePath),
                        musicModel.getTrackPath() + System.lineSeparator(),
                        true);
            }
        });
    }

    public void isTemFavorite(MusicModel musicModel, Callback<Boolean> callback) {
        TaskRunner.executeAsync(() -> {
            loadFavorites();
            if (mFavoritesSet.contains(musicModel.hashCode()))
                mHandler.post(() -> callback.onComplete(true));
            else mHandler.post(() -> callback.onComplete(false));
        });
    }

    public void getFavoriteTracks(Callback<List<MusicModel>> callback) {
        TaskRunner.executeAsync(() -> {
            List<String> trackPaths = StorageUtil.readLinesFromFile(new File(mFavoritesFilePath));
            List<MusicModel> favoriteTracks = DataModelHelper.getModelsObjectFromTrackPath(trackPaths);
            mHandler.post(() -> callback.onComplete(favoriteTracks));
        });
    }

    public void removeFromFavorite(@NonNull MusicModel musicModel) {
        TaskRunner.executeAsync(() -> {
            File file = new File(mFavoritesFilePath);
            List<String> trackPaths = StorageUtil.readLinesFromFile(file);
            trackPaths.remove(musicModel.getTrackPath());
            StorageUtil.writerLinesToFile(file, trackPaths, false);
        });
    }

    public void clearAllFavorites() {
        TaskRunner.executeAsync(() ->
                StorageUtil.deleteFile(new File(mFavoritesFilePath)));
    }

    private synchronized void loadFavorites() {
        if (null != mFavoritesSet) return;
        File file = new File(mFavoritesFilePath);
        List<String> favoriteRecords = StorageUtil.readLinesFromFile(file);
        mFavoritesSet = new HashSet<>(favoriteRecords.size());
        for (String s : favoriteRecords)
            mFavoritesSet.add(s.hashCode());
    }
}