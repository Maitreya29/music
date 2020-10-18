package com.hardcodecoder.pulsemusic.providers;

import android.annotation.SuppressLint;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hardcodecoder.pulsemusic.TaskRunner;
import com.hardcodecoder.pulsemusic.TaskRunner.Callback;
import com.hardcodecoder.pulsemusic.helper.DataModelHelper;
import com.hardcodecoder.pulsemusic.loaders.LoaderCache;
import com.hardcodecoder.pulsemusic.model.HistoryRecord;
import com.hardcodecoder.pulsemusic.model.MusicModel;
import com.hardcodecoder.pulsemusic.storage.StorageUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HistoryProvider {

    private final Handler mHandler;
    private final String mHistoryDir;
    private Map<Integer, Short> mHistoryMap = null;

    HistoryProvider(String baseDir, Handler handler) {
        mHandler = handler;
        mHistoryDir = baseDir + File.separator + "history" + File.separator;
        StorageUtil.createDir(new File(mHistoryDir));
    }

    private synchronized void loadHistoryRecords() {
        if (mHistoryMap != null) return;
        mHistoryMap = new HashMap<>();
        File[] files = new File(mHistoryDir).listFiles();
        for (File file : files) {
            HistoryRecord hr = getHistoryRecord(file);
            mHistoryMap.put(Integer.parseInt(file.getName()), hr.getPlayCount());
        }
    }

    @SuppressLint("DefaultLocale")
    private String getWriteableHistoryRecord(MusicModel md, short playCount) {
        return String.format("%s\n%s\n%s\n%s\n%d\n%d",
                md.getTrackPath(),
                md.getTrackName(),
                md.getAlbum(),
                md.getArtist(),
                md.getAlbumId(),
                playCount);
    }

    private HistoryRecord getHistoryRecord(File file) {
        String[] lines = StorageUtil.readLinesFromFile(file, 6);
        return new HistoryRecord(
                lines[0],
                lines[1],
                lines[2],
                lines[3],
                Integer.parseInt(lines[4]),
                Short.parseShort(lines[5]),
                file.lastModified());
    }

    public void addToHistory(@NonNull MusicModel musicModel) {
        TaskRunner.executeAsync(() -> {
            loadHistoryRecords();
            Short count = mHistoryMap.get(musicModel.hashCode());
            if (count == null) count = 1;
            else count++;
            StorageUtil.writeStringToFile(
                    new File(mHistoryDir + musicModel.hashCode()),
                    getWriteableHistoryRecord(musicModel, count),
                    false);
        });
    }

    public void getHistoryTracks(@NonNull Callback<List<MusicModel>> callback) {
        TaskRunner.executeAsync(() -> {
            File[] files = new File(mHistoryDir).listFiles();
            if (null != files && files.length > 0) {
                List<String> trackPaths = new ArrayList<>();
                for (File file : files)
                    trackPaths.add(getHistoryRecord(file).getTrackPath());
                List<MusicModel> historyTracks = DataModelHelper.getModelsObjectFromTrackPath(trackPaths);
                mHandler.post(() -> callback.onComplete(historyTracks));
            } else callback.onComplete(null);
        });
    }

    @Nullable
    public List<HistoryRecord> getHistoryRecords() {
        File[] files = new File(mHistoryDir).listFiles();
        if (null != files && files.length > 0) {
            List<HistoryRecord> records = new ArrayList<>();
            for (File file : files)
                records.add(getHistoryRecord(file));
            return records;
        } else return null;
    }

    public void deleteHistoryFiles(int maxPermittedHistoryCount) {
        TaskRunner.executeAsync(() -> {
            File[] files = new File(mHistoryDir).listFiles();
            if (null != files) {
                int size = files.length;
                if (maxPermittedHistoryCount == 0) {
                    for (File deleteFile : files) StorageUtil.deleteFile(deleteFile);
                } else if (size > maxPermittedHistoryCount) {
                    // Sorts in descending order by modified date
                    StorageUtil.sortFiles(files, (o1, o2) ->
                            Long.compare(o2.lastModified(), o1.lastModified()));

                    for (int i = maxPermittedHistoryCount; i < size; i++)
                        StorageUtil.deleteFile(files[i]);
                }
            }
        });
    }

    public void deleteObsoleteHistoryFiles() {
        TaskRunner.executeAsync(() -> {
            File[] files = new File(mHistoryDir).listFiles();
            if (null != files && files.length > 0) {
                List<MusicModel> masterList = LoaderCache.getAllTracksList();
                if (null != masterList && !masterList.isEmpty()) {
                    Set<Integer> currentList = new HashSet<>();
                    for (MusicModel md : masterList) currentList.add(md.hashCode());
                    for (File f : files)
                        if (!currentList.contains(Integer.parseInt(f.getName())))
                            StorageUtil.deleteFile(f);
                } else for (File f : files) StorageUtil.deleteFile(f);
            }
        });
    }
}