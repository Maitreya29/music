package com.hardcodecoder.pulsemusic.storage;

import android.content.Context;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hardcodecoder.pulsemusic.TaskRunner;
import com.hardcodecoder.pulsemusic.TaskRunner.Callback;
import com.hardcodecoder.pulsemusic.helper.DataModelHelper;
import com.hardcodecoder.pulsemusic.loaders.LoaderCache;
import com.hardcodecoder.pulsemusic.model.HistoryModel;
import com.hardcodecoder.pulsemusic.model.MusicModel;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AppFileManager {

    private static final Handler sHandler = new Handler();
    private static Map<Integer, Short> mHistoryMap = null;
    private static Set<Integer> mFavoritesSet = null;
    private static IgnoreListProvider sIgnoreListProvider = null;
    private static String mFilesDir;

    public static void initDataDir(@NonNull Context context) {
        mFilesDir = context.getFilesDir().getAbsolutePath();
        deleteOldHistoryFiles(100);
        TaskRunner.executeAsync(() -> {
            StorageUtils.createDir(new File(StorageStructure.getAbsoluteHistoryPath(mFilesDir)));
            StorageUtils.createDir(new File(StorageStructure.getAbsolutePlaylistsFolderPath(mFilesDir)));
        });
        sIgnoreListProvider = new IgnoreListProvider(mFilesDir);
    }

    public static void addItemToHistory(@NonNull MusicModel md) {
        if (null == mHistoryMap)
            mHistoryMap = new HashMap<>();
        TaskRunner.executeAsync(() -> {
            Short count = mHistoryMap.get(md.getTrackName().hashCode());
            if (null == count) count = 1;
            else count++;
            StorageUtils.writeRawHistory(
                    StorageStructure.getAbsoluteHistoryPath(mFilesDir),
                    md,
                    count);

            mHistoryMap.put(md.getTrackName().hashCode(), count);
        });
    }

    public static void getHistory(boolean defSort, @NonNull Callback<List<HistoryModel>> callback) {
        if (null == mHistoryMap)
            mHistoryMap = new HashMap<>();
        TaskRunner.executeAsync(() -> {
            String dirPth = StorageStructure.getAbsoluteHistoryPath(mFilesDir);
            File[] files = new File(dirPth).listFiles();
            List<HistoryModel> historyList = new ArrayList<>();
            if (null != files) {
                if (defSort)
                    StorageUtils.sortFilesByLastModified(files);
                for (File file : files) {
                    HistoryModel hm = StorageUtils.readRawHistory(file);
                    if (null != hm) {
                        historyList.add(hm);
                        mHistoryMap.put(hm.getTitle().hashCode(), hm.getPlayCount());
                    }
                }
            }
            callback.onComplete(historyList);
        });
    }

    public static boolean addItemToFavorites(@NonNull MusicModel item) {
        if (item.getId() < 0) return false;
        if (null == mFavoritesSet)
            mFavoritesSet = new HashSet<>();
        if (mFavoritesSet.add(item.getTrackName().hashCode()))
            TaskRunner.executeAsync(() ->
                    StorageUtils.writeRawFavorite(
                            StorageStructure.getAbsoluteFavoritesPath(mFilesDir),
                            item.getTrackName()));
        return true;
    }

    private static List<String> loadFavorites() {
        String favoritesPath = StorageStructure.getAbsoluteFavoritesPath(mFilesDir);
        List<String> rawFavoritesList = StorageUtils.readRawFavorites(favoritesPath);
        if (null == mFavoritesSet) {
            mFavoritesSet = new HashSet<>();
            for (String str : rawFavoritesList) {
                mFavoritesSet.add(str.hashCode());
            }
        }
        return rawFavoritesList;
    }

    public static void getFavorites(@NonNull Callback<List<MusicModel>> callback) {
        TaskRunner.executeAsync(() -> {
            List<String> favoritesRaw = new ArrayList<>(loadFavorites());
            List<MusicModel> favorites = DataModelHelper.getModelsObjectFromTitlesList(favoritesRaw);
            callback.onComplete(favorites);
        });
    }

    public static void deleteFavorite(@NonNull MusicModel md) {
        if (null == mFavoritesSet)
            return;
        if (mFavoritesSet.remove(md.getTrackName().hashCode())) {
            TaskRunner.executeAsync(() -> {
                List<String> newFavorites = new ArrayList<>(loadFavorites());
                newFavorites.remove(md.getTrackName());
                StorageUtils.writeRawFavorites(
                        StorageStructure.getAbsoluteFavoritesPath(mFilesDir),
                        newFavorites,
                        false);
            });
        }
    }

    public static void deleteAllFavorites() {
        TaskRunner.executeAsync(() -> {
            mFavoritesSet = null;
            StorageUtils.deleteFile(new File(
                    StorageStructure.getAbsoluteFavoritesPath(mFilesDir)));
        });
    }

    public static void isItemAFavorite(@NonNull MusicModel item, @NonNull Callback<Boolean> callback) {
        if (null == mFavoritesSet) {
            TaskRunner.executeAsync(() -> {
                loadFavorites();
                sHandler.post(() -> callback.onComplete(mFavoritesSet.contains(item.getTrackName().hashCode())));
            });
        } else callback.onComplete(mFavoritesSet.contains(item.getTrackName().hashCode()));
    }

    public static void savePlaylist(@NonNull String playlistName) {
        TaskRunner.executeAsync(() ->
                StorageUtils.writeRawPlaylist(
                        StorageStructure.getAbsolutePlaylistsFolderPath(mFilesDir) +
                                playlistName));
    }

    public static void getPlaylists(@NonNull Callback<List<String>> callback) {
        TaskRunner.executeAsync(() -> {
            File[] files = new File(
                    StorageStructure.getAbsolutePlaylistsFolderPath(mFilesDir))
                    .listFiles();
            List<String> playlistTitles = null;
            if (null != files && files.length > 0) {
                StorageUtils.sortFilesByLastModified(files);
                playlistTitles = new ArrayList<>();
                for (File file : files)
                    playlistTitles.add(file.getName());
            }
            callback.onComplete(playlistTitles);
        });
    }

    public static void renamePlaylist(@NonNull String oldPlaylistName, @NonNull String newPlaylistName) {
        TaskRunner.executeAsync(() -> {
            String playlistDir = StorageStructure.getAbsolutePlaylistsFolderPath(mFilesDir);
            File oldName = new File(playlistDir + oldPlaylistName);
            File newName = new File(playlistDir + newPlaylistName);
            StorageUtils.renameFile(oldName, newName);
        });
    }

    public static void addItemToPlaylist(@NonNull String playlistName, @NonNull MusicModel itemToAdd) {
        TaskRunner.executeAsync(() -> StorageUtils.writeTrackToPlaylist(
                StorageStructure.getAbsolutePlaylistsFolderPath(mFilesDir) +
                        playlistName, itemToAdd.getTrackName()));
    }

    public static void addItemsToPlaylist(@NonNull String playlistName,
                                          @NonNull List<MusicModel> playlistTracks,
                                          boolean append,
                                          @Nullable Callback<Boolean> callback) {
        TaskRunner.executeAsync(() -> {
            List<String> tracksTitleRaw = new ArrayList<>();
            for (MusicModel musicModel : playlistTracks)
                tracksTitleRaw.add(musicModel.getTrackName());
            StorageUtils.writeTracksToPlaylist(
                    StorageStructure.getAbsolutePlaylistsFolderPath(mFilesDir) +
                            playlistName, tracksTitleRaw, append);
            if (null != callback) sHandler.post(() -> callback.onComplete(true));
        });
    }

    public static void getPlaylistTracks(@NonNull String playlistName, @NonNull Callback<List<MusicModel>> callback) {
        TaskRunner.executeAsync(() -> {
            String playlistPath = StorageStructure.getAbsolutePlaylistsFolderPath(mFilesDir);
            List<String> playlistTracksRaw = StorageUtils.readRawPlaylistTracks(playlistPath + playlistName);
            List<MusicModel> playlistTracks = DataModelHelper.getModelsObjectFromTitlesList(playlistTracksRaw);
            callback.onComplete(playlistTracks);
        });
    }

    public static void updatePlaylistItems(@NonNull String playlistName, @NonNull List<MusicModel> newTracksList) {
        addItemsToPlaylist(playlistName, newTracksList, false, null);
    }

    public static void deletePlaylist(@NonNull String playlistName) {
        StorageUtils.deleteFile(
                new File(StorageStructure.getAbsolutePlaylistsFolderPath(
                        mFilesDir) +
                        playlistName));
    }

    public static File getPlaylistFolderFile() {
        return new File(StorageStructure.getAbsolutePlaylistsFolderPath(mFilesDir));
    }

    public static void deleteObsoleteHistoryFiles() {
        TaskRunner.executeAsync(() -> {
            String hisToryDir = StorageStructure.getAbsoluteHistoryPath(mFilesDir);
            File[] files = new File(hisToryDir).listFiles();
            if (null != files && files.length > 0) {
                List<MusicModel> masterList = LoaderCache.getAllTracksList();
                if (null != masterList && masterList.size() > 0) {
                    Set<Integer> currentList = new HashSet<>();
                    for (MusicModel md : masterList) currentList.add(md.getTrackName().hashCode());
                    for (File f : files)
                        if (!currentList.contains(Integer.parseInt(f.getName())))
                            StorageUtils.deleteFile(f);
                } else {
                    for (File f : files) StorageUtils.deleteFile(f);
                }
            }
        });
    }

    public static void addToIgnoredList(String folderPath) {
        TaskRunner.executeAsync(() ->
                sIgnoreListProvider.addToIgnoreList(folderPath));
    }

    public static void removeFromIgnoredList(String folderPath) {
        TaskRunner.executeAsync(() ->
                sIgnoreListProvider.deleteFromIgnoreList(folderPath));
    }

    public static void getIgnoredList(Callback<List<String>> callback) {
        TaskRunner.executeAsync(() -> {
            List<String> ignoredList = sIgnoreListProvider.getIgnoredList();
            sHandler.post(() -> callback.onComplete(ignoredList));
        });
    }

    public static List<String> getIgnoredList() {
        return sIgnoreListProvider.getIgnoredList();
    }

    /**
     * Helper method to delete recent/history tracks
     *
     * @param maxPermittedHistoryCount gives the maximum record to store
     */
    public static void deleteOldHistoryFiles(int maxPermittedHistoryCount) {
        TaskRunner.executeAsync(() -> {
            String hisToryDir = StorageStructure.getAbsoluteHistoryPath(mFilesDir);
            File[] files = new File(hisToryDir).listFiles();
            if (null != files) {
                int size = files.length;
                if (maxPermittedHistoryCount == 0) {
                    for (File deleteFile : files) StorageUtils.deleteFile(deleteFile);
                } else if (size > maxPermittedHistoryCount) {
                    // Sorts in descending order by modified date
                    StorageUtils.sortFilesByLastModified(files);
                    for (int i = maxPermittedHistoryCount; i < size; i++)
                        StorageUtils.deleteFile(files[i]);
                }
            }
        });
    }

    public static List<MusicModel> deleteAllDuplicatesInPlaylist(@NonNull String playlistName, @NonNull List<MusicModel> playlist) {
        Set<Integer> set = new HashSet<>();
        List<MusicModel> sanitizedList = new ArrayList<>();
        for (MusicModel md : playlist) {
            if (set.add(md.getId()))
                sanitizedList.add(md);
        }
        if (playlist.size() != sanitizedList.size())
            updatePlaylistItems(playlistName, sanitizedList);
        return sanitizedList;
    }
}