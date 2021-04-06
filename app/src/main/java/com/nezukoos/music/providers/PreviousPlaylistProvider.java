package com.nezukoos.music.providers;

import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.nezukoos.music.TaskRunner;
import com.nezukoos.music.helper.DataModelHelper;
import com.nezukoos.music.model.MusicModel;
import com.nezukoos.music.utils.StorageUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PreviousPlaylistProvider {

    private final Handler mHandler;
    private final String mPreviousPlaylistFilePath;

    public PreviousPlaylistProvider(@NonNull String baseDir, @NonNull Handler handler) {
        mHandler = handler;
        mPreviousPlaylistFilePath = baseDir + File.separator + "previousPlaylist.txt";
    }

    public void savePlaylist(@Nullable List<MusicModel> playlist) {
        TaskRunner.executeAsync(() -> {
            if (null == playlist || playlist.isEmpty()) return;
            List<Integer> trackIds = new ArrayList<>(playlist.size());
            for (MusicModel md : playlist) trackIds.add(md.getId());
            StorageUtil.writePlaylistIdsToFile(new File(mPreviousPlaylistFilePath),
                    trackIds,
                    false);
        });
    }

    @Nullable
    public List<MusicModel> getPlaylist() {
        List<Integer> trackIds = StorageUtil.readPlaylistIdsFromFile(new File(mPreviousPlaylistFilePath));
        return DataModelHelper.getModelObjectFromId(trackIds);
    }

    public void getPlaylistAsync(@NonNull TaskRunner.Callback<List<MusicModel>> callback) {
        TaskRunner.executeAsync(() -> {
            List<Integer> trackIds = StorageUtil.readPlaylistIdsFromFile(new File(mPreviousPlaylistFilePath));
            List<MusicModel> playlist = DataModelHelper.getModelObjectFromId(trackIds);
            mHandler.post(() -> callback.onComplete(playlist));
        });
    }

    public void deletePlaylist() {
        TaskRunner.executeAsync(() ->
                StorageUtil.deleteFile(new File(mPreviousPlaylistFilePath)));
    }
}