package com.nezukoos.music.providers;

import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.nezukoos.music.TaskRunner;
import com.nezukoos.music.TaskRunner.Callback;
import com.nezukoos.music.helper.DataModelHelper;
import com.nezukoos.music.model.MusicModel;
import com.nezukoos.music.utils.StorageUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PlaylistProvider {

    private final Handler mHandler;
    private final String mPlaylistDirPath;

    public PlaylistProvider(@NonNull String baseDir, @NonNull Handler handler) {
        mHandler = handler;
        mPlaylistDirPath = baseDir + File.separator + "playlists" + File.separator;
        StorageUtil.createDir(new File(mPlaylistDirPath));
    }

    public void addPlaylistItem(@NonNull String playlistTitle) {
        TaskRunner.executeAsync(() -> {
            if (playlistTitle.contains("/")) return;
            File file = new File(mPlaylistDirPath + playlistTitle);
            StorageUtil.createFile(file);
        });
    }

    public void getAllPlaylistItem(@NonNull Callback<List<String>> callback) {
        TaskRunner.executeAsync(() -> {
            File[] files = new File(mPlaylistDirPath).listFiles();
            if (null != files && files.length > 0) {
                Arrays.sort(files, (f1, f2) -> Long.compare(f2.lastModified(), f1.lastModified()));
                List<String> playlistItems = new ArrayList<>(files.length);
                for (File file : files)
                    playlistItems.add(file.getName());
                mHandler.post(() -> callback.onComplete(playlistItems));
            } else mHandler.post(() -> callback.onComplete(null));
        });
    }

    public File getPlaylistParentFolder() {
        return new File(mPlaylistDirPath);
    }

    public boolean renamePlaylistItem(@NonNull String oldTitle, @NonNull String newTitle) {
        return StorageUtil.renameFile(
                new File(mPlaylistDirPath + oldTitle),
                new File(mPlaylistDirPath + newTitle));
    }

    public void deletePlaylistItem(@NonNull String playlistTitle) {
        StorageUtil.deleteFile(new File(mPlaylistDirPath + playlistTitle));
    }

    public void addTracksToPlaylist(@NonNull List<MusicModel> tracks, @NonNull String playlistTitle, boolean append) {
        TaskRunner.executeAsync(() -> {
            List<Integer> trackIds = new ArrayList<>(tracks.size());
            for (MusicModel md : tracks) trackIds.add(md.getId());

            StorageUtil.writePlaylistIdsToFile(
                    new File(mPlaylistDirPath + playlistTitle),
                    trackIds,
                    append);
        });
    }

    public void updatePlaylistTracks(@NonNull String playlistTitle, @NonNull List<MusicModel> tracks) {
        addTracksToPlaylist(tracks, playlistTitle, false);
    }

    public void getTrackForPlaylist(@NonNull String playlistTitle, @NonNull Callback<List<MusicModel>> callback) {
        TaskRunner.executeAsync(() -> {
            List<Integer> trackIds = StorageUtil.readPlaylistIdsFromFile(new File(mPlaylistDirPath + playlistTitle));
            List<MusicModel> tracks = DataModelHelper.getModelObjectFromId(trackIds);
            mHandler.post(() -> callback.onComplete(tracks));
        });
    }

    public void deleteAllDuplicatesInPlaylist(@NonNull String playlistTitle, @NonNull List<MusicModel> tracks, @Nullable Callback<List<MusicModel>> callback) {
        TaskRunner.executeAsync(() -> {
            final List<MusicModel> sanitizedList = new ArrayList<>();
            if (!tracks.isEmpty()) {
                Set<Integer> set = new HashSet<>();
                for (MusicModel md : tracks) {
                    if (set.add(md.getId()))
                        sanitizedList.add(md);
                }
                if (tracks.size() != sanitizedList.size())
                    updatePlaylistTracks(playlistTitle, sanitizedList);
            }
            if (null != callback) mHandler.post(() -> callback.onComplete(sanitizedList));
        });
    }
}