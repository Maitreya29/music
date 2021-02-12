package com.hardcodecoder.pulsemusic.utils;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hardcodecoder.pulsemusic.TaskRunner;

import java.io.File;
import java.io.PrintStream;
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

    public static void logException(@NonNull Exception exception) {
        logException(null, null, exception);
    }

    public static void logException(@Nullable String tag, @Nullable String msg, @NonNull Exception exception) {
        TaskRunner.executeAsync(() -> {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd_hh:mm:ss", Locale.ENGLISH);
            long currentTime = Calendar.getInstance().getTimeInMillis();
            String text = simpleDateFormat.format(new Date(currentTime));
            File file = new File(mExternalFilesDirPath, "logcat_" + text + ".txt");
            try (PrintStream ps = new PrintStream(file)) {
                if (null != tag) ps.print(tag + ": ");
                if (null != msg) ps.println(msg);
                exception.printStackTrace(ps);
            } catch (Exception ex) {
                Log.e("PulseMusic: LogHelper", "Failed to write log to file: ", ex);
            }
        });
    }
}