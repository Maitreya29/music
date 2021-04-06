package com.nezukoos.music.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.nezukoos.music.service.AudioDeviceService;
import com.nezukoos.music.utils.AppSettings;

public class BootCompletedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(@NonNull Context context, @NonNull Intent eventIntent) {
        String action = eventIntent.getAction();
        if (Intent.ACTION_BOOT_COMPLETED.equals(action)) {
            boolean startService = AppSettings.isBluetoothAutoPlayEnabled(context);
            if (startService)
                ContextCompat.startForegroundService(context, new Intent(context, AudioDeviceService.class));
        }
    }
}