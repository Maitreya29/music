package com.nezukoos.music.providers;

import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.nezukoos.music.TaskRunner;
import com.nezukoos.music.TaskRunner.Callback;
import com.nezukoos.music.utils.StorageUtil;

import java.io.File;
import java.util.List;

public class IgnoreListProvider {

    private final Handler mHandler;
    private final String mIgnoredFilePath;

    public IgnoreListProvider(@NonNull String baseFilesDir, @NonNull Handler handler) {
        mHandler = handler;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            mIgnoredFilePath = baseFilesDir + File.separator + "ignoredList_Q.txt";
        else
            mIgnoredFilePath = baseFilesDir + File.separator + "ignoredList_pre_Q.txt";
        createFileIfNotExists();
    }

    public void addToIgnoreList(@NonNull String folderToAdd) {
        TaskRunner.executeAsync(() -> {
            if (createFileIfNotExists()) {
                File ignoredListFile = new File(mIgnoredFilePath);
                StorageUtil.writeStringToFile(ignoredListFile, folderToAdd + System.lineSeparator(), true);
            }
        });
    }

    public void addToIgnoreList(@NonNull List<String> foldersToAdd) {
        TaskRunner.executeAsync(() -> {
            if (createFileIfNotExists()) {
                StringBuilder builder = new StringBuilder();
                for (String s : foldersToAdd)
                    builder.append(s).append(System.lineSeparator());
                File ignoredListFile = new File(mIgnoredFilePath);
                StorageUtil.writeStringToFile(ignoredListFile, builder.toString(), true);
            }
        });
    }

    public void getIgnoredList(@NonNull Callback<List<String>> callback) {
        TaskRunner.executeAsync(() -> {
            File file = new File(mIgnoredFilePath);
            if (file.exists()) {
                List<String> ignoredList = StorageUtil.readLinesFromFile(file);
                mHandler.post(() -> callback.onComplete(ignoredList));
            } else mHandler.post(() -> callback.onComplete(null));
        });
    }

    @Nullable
    public List<String> getIgnoredList() {
        File file = new File(mIgnoredFilePath);
        if (file.exists())
            return StorageUtil.readLinesFromFile(file);
        return null;
    }

    public void removeFromIgnoreList(@NonNull String folderToRemove) {
        TaskRunner.executeAsync(() -> {
            File file = new File(mIgnoredFilePath);
            if (file.exists()) {
                getIgnoredList(result -> {
                    if (null == result) return;
                    StringBuilder stringBuilder = new StringBuilder();
                    String remove = folderToRemove.trim();
                    for (String folder : result) {
                        if (remove.equals(folder)) continue;
                        stringBuilder.append(folder).append(System.lineSeparator());
                    }
                    StorageUtil.writeStringToFile(file, stringBuilder.toString(), false);
                });
            }
        });
    }

    private synchronized boolean createFileIfNotExists() {
        File ignoredListFile = new File(mIgnoredFilePath);

        if (ignoredListFile.exists()) return true;

        if (StorageUtil.createFile(ignoredListFile)) {
            // Default ignored folders
            String folders;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                String volume = MediaStore.VOLUME_EXTERNAL_PRIMARY;
                folders = volume + ":" + Environment.DIRECTORY_ALARMS + File.separator +
                        System.lineSeparator() +
                        volume + ":" + Environment.DIRECTORY_RINGTONES + File.separator +
                        System.lineSeparator() +
                        volume + ":" + Environment.DIRECTORY_NOTIFICATIONS + File.separator +
                        System.lineSeparator();
            } else {
                folders = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_ALARMS) +
                        System.lineSeparator() +
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_RINGTONES) +
                        System.lineSeparator() +
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_NOTIFICATIONS) +
                        System.lineSeparator();
            }
            StorageUtil.writeStringToFile(ignoredListFile, folders, false);
            return true;
        }
        return false;
    }
}