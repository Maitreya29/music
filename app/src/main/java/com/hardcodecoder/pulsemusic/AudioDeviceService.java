package com.hardcodecoder.pulsemusic;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.hardcodecoder.pulsemusic.receivers.BluetoothBroadcastReceiver;
import com.hardcodecoder.pulsemusic.utils.AppSettings;

public class AudioDeviceService extends Service {

    private static final String CHANNEL_ID = "com.hardcodecoder.pulsemusic.BLUETOOTH_CHANNEL_ID";
    private boolean mNotificationPosted = false;
    private BroadcastReceiver mBluetoothReceiver;

    @Override
    public void onCreate() {
        if (AppSettings.isBluetoothDeviceDetectionEnabled(this)) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
            filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
            mBluetoothReceiver = new BluetoothBroadcastReceiver();
            registerReceiver(mBluetoothReceiver, filter);
        } else {
            // no valid reason to keep service alive
            stopForeground(true);
            stopSelf();
        }
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!mNotificationPosted) postNotificationForeground();
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        mNotificationPosted = false;
        if (null != mBluetoothReceiver)
            unregisterReceiver(mBluetoothReceiver);
        super.onDestroy();
    }

    private void postNotificationForeground() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            createNotificationChannel();

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(getString(R.string.audio_bluetooth_auto_play_title))
                .setOngoing(true)
                .setSmallIcon(R.drawable.ic_notification)
                .build();
        startForeground(1, notification);
        mNotificationPosted = true;
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private void createNotificationChannel() {
        NotificationManager mNotificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if (mNotificationManager.getNotificationChannel(CHANNEL_ID) == null) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    CHANNEL_ID,
                    getApplicationContext().getString(R.string.audio_detection_channel_name),
                    NotificationManager.IMPORTANCE_NONE);
            notificationChannel.setDescription("no sound");
            notificationChannel.enableVibration(false);
            notificationChannel.setShowBadge(false);
            notificationChannel.setSound(null, null);
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
    }
}