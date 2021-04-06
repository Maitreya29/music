package com.nezukoos.music.providers;

import android.os.Handler;

import androidx.annotation.NonNull;

import com.nezukoos.music.TaskRunner;
import com.nezukoos.music.TaskRunner.Callback;
import com.nezukoos.music.helper.DataModelHelper;
import com.nezukoos.music.model.MusicModel;
import com.nezukoos.music.utils.StorageUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FavoritesProvider {

    private final Handler mHandler;
    private final String mFavoritesFilePath;
    private Set<Integer> mFavoritesSet;
    private List<FavoritesProviderCallback> mCallbacks = null;

    public FavoritesProvider(@NonNull String baseDir, @NonNull Handler handler) {
        mHandler = handler;
        mFavoritesFilePath = baseDir + File.separator + "favorites.txt";
        StorageUtil.createFile(new File(mFavoritesFilePath));
    }

    public void addToFavorites(@NonNull MusicModel musicModel) {
        if (musicModel.getId() < 0) return;
        TaskRunner.executeAsync(() -> {
            loadFavorites();
            if (mFavoritesSet.add(musicModel.getId())) {
                StorageUtil.writeStringToFile(
                        new File(mFavoritesFilePath),
                        musicModel.getId() + System.lineSeparator(),
                        true);

                if (null != mCallbacks) {
                    for (FavoritesProviderCallback callback : mCallbacks)
                        mHandler.post(() -> callback.onFavoriteAdded(musicModel));
                }
            }
        });
    }

    public void isTemFavorite(@NonNull MusicModel musicModel, @NonNull Callback<Boolean> callback) {
        TaskRunner.executeAsync(() -> {
            loadFavorites();
            if (mFavoritesSet.contains(musicModel.getId()))
                mHandler.post(() -> callback.onComplete(true));
            else mHandler.post(() -> callback.onComplete(false));
        });
    }

    public void getFavoriteTracks(@NonNull Callback<List<MusicModel>> callback) {
        TaskRunner.executeAsync(() -> {
            File file = new File(mFavoritesFilePath);
            if (file.exists()) {
                List<Integer> trackIds = StorageUtil.readPlaylistIdsFromFile(file);
                List<MusicModel> favoriteTracks = DataModelHelper.getModelObjectFromId(trackIds);
                mHandler.post(() -> callback.onComplete(favoriteTracks));
            } else mHandler.post(() -> callback.onComplete(null));
        });
    }

    public void removeFromFavorite(@NonNull MusicModel musicModel) {
        TaskRunner.executeAsync(() -> {
            File file = new File(mFavoritesFilePath);
            Integer id = musicModel.getId();
            loadFavorites();
            if (mFavoritesSet.remove(id)) {
                List<Integer> trackIds = StorageUtil.readPlaylistIdsFromFile(file);
                if (trackIds != null) {
                    trackIds.remove(id);
                    StorageUtil.writePlaylistIdsToFile(file, trackIds, false);
                    if (null != mCallbacks) {
                        for (FavoritesProviderCallback callback : mCallbacks)
                            mHandler.post(() -> callback.onFavoriteRemoved(musicModel));
                    }
                }
            }
        });
    }

    public void clearAllFavorites() {
        TaskRunner.executeAsync(() -> {
            StorageUtil.deleteFile(new File(mFavoritesFilePath));
            if (null != mFavoritesSet) mFavoritesSet.clear();
            if (null != mCallbacks) {
                for (FavoritesProviderCallback callback : mCallbacks)
                    mHandler.post(callback::onFavoritesCleared);
            }
        });
    }

    public void addCallback(@NonNull FavoritesProviderCallback callback) {
        if (null == mCallbacks) mCallbacks = new ArrayList<>();
        mCallbacks.add(callback);
    }

    public void removeCallback(@NonNull FavoritesProviderCallback callback) {
        if (null == mCallbacks) return;
        mCallbacks.remove(callback);
    }

    private synchronized void loadFavorites() {
        if (null != mFavoritesSet) return;
        File file = new File(mFavoritesFilePath);
        List<Integer> favoriteRecords = null;
        if (file.exists())
            favoriteRecords = StorageUtil.readPlaylistIdsFromFile(file);
        mFavoritesSet = new HashSet<>();
        if (null != favoriteRecords)
            mFavoritesSet.addAll(favoriteRecords);
    }

    public interface FavoritesProviderCallback {
        void onFavoriteAdded(@NonNull MusicModel item);

        void onFavoriteRemoved(@NonNull MusicModel item);

        void onFavoritesCleared();
    }
}