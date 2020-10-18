package com.hardcodecoder.pulsemusic.storage;

import android.util.Log;

import androidx.annotation.NonNull;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class StorageUtil {

    private static final String TAG = "StorageUtil";

    public static void writeStringToFile(@NonNull File fileToWrite, @NonNull String data, boolean append) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(fileToWrite, append);
            fos.write(data.getBytes());
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

    public static void writerLinesToFile(@NonNull File file, @NonNull List<String> lines, boolean append) {
        BufferedWriter bufferedWriter = null;
        try {
            FileWriter writer = new FileWriter(file, append);
            bufferedWriter = new BufferedWriter(writer);
            for (String s : lines)
                bufferedWriter.write(s + "\n");
            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != bufferedWriter) {
                try {
                    bufferedWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String[] readLinesFromFile(@NonNull File file, int lines) {
        String[] linesArray = new String[lines];
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            for (int i = 0; i < lines; i++)
                linesArray[i] = reader.readLine();

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
        return linesArray;
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

    public static boolean createFile(@NonNull File file) {
        try {
            return file.createNewFile();
        } catch (IOException e) {
            Log.e(TAG, "Failed to create file in: " + file.getAbsolutePath());
        }
        return false;
    }

    public static void createDir(@NonNull File file) {
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

    public static void sortFiles(@NonNull File[] files, @NonNull Comparator<File> comparator) {
        Arrays.sort(files, comparator);
    }
}