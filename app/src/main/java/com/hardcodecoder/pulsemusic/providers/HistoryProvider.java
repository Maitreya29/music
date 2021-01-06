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
import com.hardcodecoder.pulsemusic.utils.StorageUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
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

    public void addToHistory(@NonNull MusicModel musicModel) {
        TaskRunner.executeAsync(() -> {
            loadHistoryRecords();
            Short count = mHistoryMap.get(musicModel.getId());
            if (count == null) count = 1;
            else count++;
            StorageUtil.writeStringToFile(
                    new File(mHistoryDir + musicModel.getId()),
                    getWriteableHistoryRecord(musicModel, count),
                    false);
            mHistoryMap.put(musicModel.getId(), count);
        });
    }

    @Nullable
    public List<MusicModel> getHistoryTracks() {
        List<MusicModel> historyTracks = null;
        File[] files = new File(mHistoryDir).listFiles();
        if (null != files && files.length > 0) {
            sortHistory(files);
            List<Integer> trackIds = new ArrayList<>();
            for (File file : files)
                trackIds.add(Integer.parseInt(file.getName()));
            historyTracks = DataModelHelper.getModelObjectFromId(trackIds);
        }
        return historyTracks;
    }

    public void getHistoryTracks(@NonNull Callback<List<MusicModel>> callback) {
        TaskRunner.executeAsync(() -> {
            List<MusicModel> historyTracks = getHistoryTracks();
            mHandler.post(() -> callback.onComplete(historyTracks));
        });
    }

    @Nullable
    public List<HistoryRecord> getHistoryRecords() {
        File[] files = new File(mHistoryDir).listFiles();
        if (null != files && files.length > 0) {
            sortHistory(files);
            List<HistoryRecord> records = new ArrayList<>();
            for (File file : files)
                records.add(getHistoryRecord(file));
            return records;
        } else return null;
    }

    public void deleteHistoryFiles(int maxPermittedHistoryCount, @Nullable Callback<Boolean> callback) {
        TaskRunner.executeAsync(() -> {
            File[] files = new File(mHistoryDir).listFiles();
            if (null != files) {
                int size = files.length;
                if (maxPermittedHistoryCount == 0) {
                    for (File deleteFile : files) StorageUtil.deleteFile(deleteFile);
                    if (null != mHistoryMap)
                        mHistoryMap.clear();
                } else if (size > maxPermittedHistoryCount) {
                    sortHistory(files);
                    for (int i = maxPermittedHistoryCount; i < size; i++)
                        StorageUtil.deleteFile(files[i]);
                }
            }
            if (null != callback) mHandler.post(() -> callback.onComplete(true));
        });
    }

    public void deleteObsoleteHistoryFiles(@Nullable Callback<Boolean> callback) {
        TaskRunner.executeAsync(() -> {
            File[] files = new File(mHistoryDir).listFiles();
            if (null != files && files.length > 0) {
                List<MusicModel> masterList = LoaderCache.getAllTracksList();
                if (null != masterList && !masterList.isEmpty()) {
                    Set<Integer> currentList = new HashSet<>();
                    for (MusicModel md : masterList) currentList.add(md.getId());

                    for (File f : files) {
                        if (!currentList.contains(Integer.parseInt(f.getName())))
                            StorageUtil.deleteFile(f);
                    }

                } else for (File f : files) StorageUtil.deleteFile(f);
            }
            if (null != callback) mHandler.post(() -> callback.onComplete(true));
        });
    }

    private synchronized void loadHistoryRecords() {
        if (mHistoryMap != null) return;
        mHistoryMap = new HashMap<>();
        File[] files = new File(mHistoryDir).listFiles();
        if (null == files || files.length == 0) return;
        for (File file : files) {
            HistoryRecord hr = getHistoryRecord(file);
            mHistoryMap.put(Integer.parseInt(file.getName()), hr.getPlayCount());
        }
    }

    private void sortHistory(File[] files) {
        // Sorts in descending order by modified date
        Arrays.sort(files, (f1, f2) -> Long.compare(f2.lastModified(), f1.lastModified()));
    }

    @SuppressLint("DefaultLocale")
    private String getWriteableHistoryRecord(MusicModel md, short playCount) {
        StringBuilder builder = new StringBuilder();
        String lineSeparator = System.lineSeparator();
        return builder
                .append(md.getTrackName()).append(lineSeparator)
                .append(md.getAlbum()).append(lineSeparator)
                .append(md.getArtist()).append(lineSeparator)
                .append(md.getAlbumId()).append(lineSeparator)
                .append(playCount).toString();
    }

    private HistoryRecord getHistoryRecord(File file) {
        String[] lines = StorageUtil.readLinesFromFile(file, 5);
        return new HistoryRecord(
                lines[0],
                lines[1],
                lines[2],
                Integer.parseInt(lines[3]),
                Short.parseShort(lines[4]),
                file.lastModified());
    }
}