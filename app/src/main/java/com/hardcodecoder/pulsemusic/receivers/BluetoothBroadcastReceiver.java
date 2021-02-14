package com.hardcodecoder.pulsemusic.receivers;

import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.hardcodecoder.pulsemusic.PMS;
import com.hardcodecoder.pulsemusic.Preferences;
import com.hardcodecoder.pulsemusic.utils.AppSettings;

public class BluetoothBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(@NonNull Context context, Intent eventIntent) {
        String action = eventIntent.getAction();
        if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
            BluetoothDevice bluetoothDevice = eventIntent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            if (bluetoothDevice == null || bluetoothDevice.getBluetoothClass().getMajorDeviceClass() != BluetoothClass.Device.Major.AUDIO_VIDEO)
                return;

            // ACTION_ACL_CONNECTED indicates the device is connected, but does not
            // tell us whether the device can immediately start receiving audio stream
            // as some devices are able to play audio only after a few seconds
            // so we delay playback by 5 sec
            new Handler().postDelayed(() -> {
                int bluetoothAction = AppSettings.getBluetoothDeviceDetectionAction(context);
                int pmsAction;
                switch (bluetoothAction) {
                    case Preferences.ACTION_PLAY_LATEST:
                        pmsAction = PMS.DEFAULT_ACTION_PLAY_LATEST;
                        break;
                    case Preferences.ACTION_PLAY_SUGGESTED:
                        pmsAction = PMS.DEFAULT_ACTION_PLAY_SUGGESTED;
                        break;
                    case Preferences.ACTION_PLAY_SHUFFLE:
                    default:
                        pmsAction = PMS.DEFAULT_ACTION_PLAY_SHUFFLE;
                }
                Intent intent = new Intent(context.getApplicationContext(), PMS.class);
                intent.setAction(PMS.ACTION_PLAY_CONTINUE);
                intent.putExtra(PMS.KEY_PLAY_CONTINUE, pmsAction);
                ContextCompat.startForegroundService(context, intent);
            }, 5000);
        }
    }
}