package com.hardcodecoder.pulsemusic;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hardcodecoder.pulsemusic.utils.LogUtils;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TaskRunner {

    private static final Executor CUSTOM_THREAD_POOL_EXECUTOR =
            new ThreadPoolExecutor(1, 5, 1, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
    private static final Handler handler = new Handler(Looper.getMainLooper());

    private TaskRunner() {
    }

    public static <V> void executeAsync(@NonNull Callable<V> callable, @NonNull Callback<V> callback) {
        CUSTOM_THREAD_POOL_EXECUTOR.execute(() -> {
            try {
                final V result = callable.call();
                handler.post(() -> callback.onComplete(result));
            } catch (Exception e) {
                if (BuildConfig.DEBUG) e.printStackTrace();
                else // We log critical exceptions
                    LogUtils.logException(callable.getClass().getCanonicalName(), "at TaskRunner.executeAsync(): callable", e);

                // Callback is necessary to trigger
                // any fallback event that happen if load fails
                handler.post(() -> callback.onComplete(null));
            }
        });
    }

    public static void executeAsync(@NonNull Runnable runnable) {
        CUSTOM_THREAD_POOL_EXECUTOR.execute(runnable);
    }

    public interface Callback<V> {
        void onComplete(@Nullable V result);
    }
}