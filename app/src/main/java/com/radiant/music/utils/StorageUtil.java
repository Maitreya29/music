package com.radiant.music.utils;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static com.radiant.music.utils.LogUtils.Type.IO;

public class StorageUtil {

    private static final String TAG = StorageUtil.class.getSimpleName();

    @NonNull
    public static String[] readLinesFromFile(@NonNull File file, int lines) {
        String[] linesArray = new String[lines];
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            for (int i = 0; i < lines; i++)
                linesArray[i] = reader.readLine();
        } catch (Exception e) {
            LogUtils.logException(IO, TAG, "at: readLinesFromFile(): " + file.getAbsolutePath(), e);
        }
        return linesArray;
    }

    @Nullable
    public static List<String> readLinesFromFile(@NonNull File file) {
        if (!file.exists()) return null;
        List<String> linesList = null;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            linesList = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null)
                linesList.add(line);
        } catch (Exception e) {
            LogUtils.logException(IO, TAG, "at: List<String> readLinesFromFile(): " + file.getAbsolutePath(), e);
        }
        return linesList;
    }

    public static void writeStringToFile(@NonNull File file, @NonNull String data, boolean append) {
        try (FileOutputStream fos = new FileOutputStream(file, append)) {
            fos.write(data.getBytes());
        } catch (IOException e) {
            LogUtils.logException(IO, TAG, "at: writeStringToFile(): " + file.getAbsolutePath(), e);
        }
    }

    @Nullable
    public static List<Integer> readPlaylistIdsFromFile(@NonNull File file) {
        if (!file.exists()) return null;
        List<Integer> idList = null;
        try (Scanner input = new Scanner(file)) {
            idList = new ArrayList<>();
            while (input.hasNextInt())
                idList.add(input.nextInt());
        } catch (Exception e) {
            LogUtils.logException(IO, TAG, "at: readPlaylistIdsFromFile(): " + file.getAbsolutePath(), e);
        }
        return idList;
    }

    public static void writePlaylistIdsToFile(@NonNull File file, @NonNull List<Integer> idList, boolean append) {
        StringBuilder builder = new StringBuilder(idList.size());
        String lineSeparator = System.lineSeparator();
        for (Integer id : idList)
            builder.append(id).append(lineSeparator);
        writeStringToFile(file, builder.toString(), append);
    }

    public static boolean createFile(@NonNull File file) {
        try {
            return file.createNewFile();
        } catch (IOException e) {
            LogUtils.logException(IO, TAG, "at: createFile(): " + file.getAbsolutePath(), e);
        }
        return false;
    }

    public static void createDir(@NonNull File file) {
        if (file.exists()) return;
        if (!file.mkdir())
            Log.e(TAG, "Unable to create folder: " + file.getAbsolutePath());
    }

    public static boolean renameFile(@NonNull File from, @NonNull File to) {
        return from.renameTo(to);
    }

    public static void deleteFile(@NonNull File fileToDelete) {
        if (fileToDelete.exists() && !fileToDelete.delete())
            Log.e(TAG, "Unable to delete file: " + fileToDelete.getAbsolutePath());
    }
}