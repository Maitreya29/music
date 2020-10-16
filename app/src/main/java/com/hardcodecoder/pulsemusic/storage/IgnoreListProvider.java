package com.hardcodecoder.pulsemusic.storage;

import android.os.Environment;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class IgnoreListProvider {

    private static final String TAG = "IgnoreListProvider";
    private static final String IGNORED_LIST_FILE = "ignoredList.txt";
    private final String mFilesDir;

    public IgnoreListProvider(String baseFilesDir) {
        mFilesDir = baseFilesDir;
        createFileIfNotExists();
    }

    private boolean createFileIfNotExists() {
        File ignoredListFile = new File(mFilesDir + File.separator + IGNORED_LIST_FILE);
        if (!ignoredListFile.exists()) {
            try {
                if (ignoredListFile.createNewFile()) {
                    // Default ignored folders
                    String folders =
                            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_ALARMS) +
                                    System.lineSeparator() +
                                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_RINGTONES) +
                                    System.lineSeparator() +
                                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_NOTIFICATIONS) +
                                    System.lineSeparator();
                    StorageUtil.writeStringToFile(ignoredListFile, folders, false);
                    return true;
                }
                return false;
            } catch (IOException e) {
                Log.e(TAG, "Failed to create Ignore List file in: " + ignoredListFile.getAbsolutePath());
                return false;
            }
        } else return true;
    }

    @Nullable
    public List<String> getIgnoredList() {
        File ignoredListFile = new File(mFilesDir + File.separator + IGNORED_LIST_FILE);
        return StorageUtil.readLinesFromFile(ignoredListFile);
    }

    public void addToIgnoreList(@NonNull String folderToAdd) {
        if (createFileIfNotExists()) {
            File ignoredListFile = new File(mFilesDir + File.separator + IGNORED_LIST_FILE);
            StorageUtil.writeStringToFile(ignoredListFile, folderToAdd + System.lineSeparator(), true);
        } else Log.e(TAG, "Unable to add path to ignore list: File does not exists");
    }

    public void deleteFromIgnoreList(@NonNull String folderToRemove) {
        if (createFileIfNotExists()) {
            List<String> foldersList = getIgnoredList();
            if (null == foldersList) return;
            StringBuilder stringBuilder = new StringBuilder();
            folderToRemove = folderToRemove.trim();
            for (String folder : foldersList) {
                if (folderToRemove.equals(folder)) continue;
                stringBuilder.append(folder).append(System.lineSeparator());
            }
            File ignoredListFile = new File(mFilesDir + File.separator + IGNORED_LIST_FILE);
            StorageUtil.writeStringToFile(ignoredListFile, stringBuilder.toString(), false);
        }
    }
}