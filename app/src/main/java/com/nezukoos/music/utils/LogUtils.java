package com.nezukoos.music.utils;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.nezukoos.music.BuildConfig;
import com.nezukoos.music.TaskRunner;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class LogUtils {

    private static String mExternalFilesDirPath = null;

    private LogUtils() {
    }

    public static void init(@NonNull Context context) {
        mExternalFilesDirPath = context.getExternalFilesDir(null).getAbsolutePath();
    }

    public static void logInfo(@Nullable String tag, @Nullable String msg) {
        if (BuildConfig.DEBUG) {
            Log.i(tag, msg);
            return;
        }
        writeLog("INFO", tag, msg, null);
    }

    public static void logException(@NonNull Type type, @Nullable String tag, @Nullable String msg, @NonNull Exception exception) {
        if (BuildConfig.DEBUG) {
            Log.e(tag, msg, exception);
            return;
        }
        String fileName;
        switch (type) {
            case IO:
                fileName = "IO";
                break;
            case GENERAL:
                fileName = "GENERAL";
                break;
            case BACKGROUND:
                fileName = "TASK_RUNNER";
                break;
            default:
                fileName = "OTHERS";
        }
        writeLog(fileName, tag, msg, exception);
    }


    private static void writeLog(@NonNull String fileName, @Nullable String tag, @Nullable String msg, @Nullable Exception exception) {
        TaskRunner.executeAsync(() -> {
            File file = new File(mExternalFilesDirPath, fileName + "_logcat" + ".txt");
            try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(file, true)), true)) {
                writer.println("\n\n*********** BEGINNING OF CRASH ***********");

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd_hh:mm:ss", Locale.ENGLISH);
                long currentTime = Calendar.getInstance().getTimeInMillis();
                writer.println(simpleDateFormat.format(new Date(currentTime)));

                if (null != tag) writer.print(tag + " ");
                if (null != msg) writer.println(msg);
                if (null != exception) exception.printStackTrace(writer);
            } catch (Exception ex) {
                Log.e("PulseMusic: LogHelper", "Failed to write log to file: ", ex);
            }
        });
    }

    public enum Type {
        IO,
        GENERAL,
        BACKGROUND
    }
}