package com.hardcodecoder.pulsemusic.storage;

import android.util.Log;

import androidx.annotation.NonNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// This is going to be the replacement for StorageUtils class
// To enable read/writes more independently
public class StorageUtil {

    private static final String TAG = "StorageUtil";

    public static void writeStringToFile(@NonNull File fileToWrite, @NonNull String dataToWrite, boolean append) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(fileToWrite, append);
            fos.write(dataToWrite.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != fos) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static List<String> readLinesFromFile(@NonNull File fileToReadFrom) {
        List<String> linesList = new ArrayList<>();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(fileToReadFrom));
            String line;
            while ((line = reader.readLine()) != null)
                linesList.add(line);
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != reader) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return linesList;
    }

    public static void deleteFile(@NonNull File fileToDelete) {
        if (fileToDelete.exists() && !fileToDelete.delete())
            Log.e(TAG, "Unable to delete file: " + fileToDelete.getAbsolutePath());
    }
}