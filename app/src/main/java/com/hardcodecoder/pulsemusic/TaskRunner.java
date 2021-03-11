package com.hardcodecoder.pulsemusic;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Process;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hardcodecoder.pulsemusic.utils.LogUtils;

import java.util.concurrent.Callable;

import static com.hardcodecoder.pulsemusic.utils.LogUtils.Type.BACKGROUND;

public class TaskRunner {

    private static final HandlerThread sWorkerThread;
    private static final Handler sWorkerThreadHandler;
    private static final Handler sMainHandler;

    static {
        sWorkerThread = new HandlerThread("TaskRunnerWorkerThread", Process.THREAD_PRIORITY_BACKGROUND);
        sWorkerThread.start();
        sWorkerThreadHandler = new Handler(sWorkerThread.getLooper());
        sMainHandler = new Handler(Looper.getMainLooper());
    }

    private TaskRunner() {
    }

    public static <V> void executeAsync(@NonNull Callable<V> callable, @NonNull Callback<V> callback) {
        sWorkerThreadHandler.post(() -> {
            try {
                final V result = callable.call();
                sMainHandler.post(() -> callback.onComplete(result));
            } catch (Exception e) {
                LogUtils.logException(BACKGROUND, callable.getClass().getCanonicalName(), "at: executeAsync(): callable", e);

                // Callback is necessary to trigger
                // any fallback event that happen if load fails
                sMainHandler.post(() -> callback.onComplete(null));
            }
        });
    }

    public static void executeAsync(@NonNull Runnable runnable) {
        try {
            sWorkerThreadHandler.post(runnable);
        } catch (Exception e) {
            LogUtils.logException(BACKGROUND, TaskRunner.class.getSimpleName(), "at: executeAsync(): runnable", e);
        }
    }

    public interface Callback<V> {
        void onComplete(@Nullable V result);
    }
}