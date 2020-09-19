package com.hardcodecoder.pulsemusic.receivers;

import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import androidx.core.content.ContextCompat;

import com.hardcodecoder.pulsemusic.BDS;
import com.hardcodecoder.pulsemusic.PMS;
import com.hardcodecoder.pulsemusic.utils.AppSettings;

public class BluetoothBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent eventIntent) {
        String action = eventIntent.getAction();
        if (Intent.ACTION_BOOT_COMPLETED.equals(action)) {
            Intent intent = new Intent(context, BDS.class);
            ContextCompat.startForegroundService(context, intent);
        } else if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
            BluetoothDevice bluetoothDevice = eventIntent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            if (bluetoothDevice == null || bluetoothDevice.getBluetoothClass().getMajorDeviceClass() != BluetoothClass.Device.Major.AUDIO_VIDEO)
                return;
            if (AppSettings.isBluetoothDeviceDetectionEnabled(context)) {
                Handler handler = new Handler();
                // ACTION_ACL_CONNECTED indicates the device is connected, but does not
                // tell us whether the device can immediately start receiving audio stream
                // as some devices are able to play audio only after a few seconds
                // so we delay playback by 5 sec
                handler.postDelayed(() -> {
                    Intent intent = new Intent(context.getApplicationContext(), PMS.class);
                    intent.putExtra(PMS.PLAY_KEY, PMS.PLAY_CONTINUE);
                    ContextCompat.startForegroundService(context, intent);
                }, 5000);
            }
        }
    }
}