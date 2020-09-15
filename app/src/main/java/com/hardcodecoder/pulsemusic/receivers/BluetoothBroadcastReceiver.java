package com.hardcodecoder.pulsemusic.receivers;

import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.hardcodecoder.pulsemusic.PMS;

public class BluetoothBroadcastReceiver extends BroadcastReceiver {

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onReceive(Context context, Intent eventIntent) {
        String action = eventIntent.getAction();
        if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
            BluetoothDevice bluetoothDevice = eventIntent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            if (bluetoothDevice.getBluetoothClass().getMajorDeviceClass() != BluetoothClass.Device.Major.AUDIO_VIDEO)return;
            Intent intent = new Intent(context, PMS.class);
            intent.putExtra(PMS.PLAY_KEY, PMS.PLAY_CONTINUE);
            context.startService(intent);

        }
        else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)){
            BluetoothDevice bluetoothDevice = eventIntent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            if (bluetoothDevice.getBluetoothClass().getMajorDeviceClass() != BluetoothClass.Device.Major.AUDIO_VIDEO)return;
        }
    }


}
