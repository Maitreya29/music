package com.hardcodecoder.pulsemusic.receivers;

import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.hardcodecoder.pulsemusic.PMS;
import com.hardcodecoder.pulsemusic.utils.AppSettings;

public class BluetoothBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent eventIntent) {
        String action = eventIntent.getAction();
        if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
            BluetoothDevice bluetoothDevice = eventIntent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            if (bluetoothDevice == null || bluetoothDevice.getBluetoothClass().getMajorDeviceClass() != BluetoothClass.Device.Major.AUDIO_VIDEO)
                return;
            if (AppSettings.isBluetoothDeviceDetectionEnabled(context)) {
                Intent intent = new Intent(context.getApplicationContext(), PMS.class);
                intent.putExtra(PMS.PLAY_KEY, PMS.PLAY_CONTINUE);
                context.startService(intent);
            }
        }
    }
}