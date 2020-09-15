package com.hardcodecoder.pulsemusic;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

public class BDS extends IntentService {

    private PowerManager.WakeLock wakeLock;
    private static final String CHANNEL_ID = "com.hardcodecoder.pulsemusic.BLUETOOTH_CHANNEL_ID";


    public BDS() {
        super("PulseMusic");
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private void createNotificationChannel() {
        NotificationManager mNotificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if (mNotificationManager.getNotificationChannel(CHANNEL_ID) == null) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    CHANNEL_ID,
                    getApplicationContext().getString(R.string.bluetooth_channel_name),
                    NotificationManager.IMPORTANCE_LOW);
            notificationChannel.setDescription("no sound");
            notificationChannel.enableVibration(false);
            notificationChannel.setShowBadge(false);
            notificationChannel.setSound(null, null);
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel();
        }
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(getString(R.string.audio_category_enable_connected_start))
                .setOngoing(true)
                .setSmallIcon(R.drawable.ic_notification)
                .build();
        startForeground(1, notification);


        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "PulseMusic:BluetoothDetectionService");
        wakeLock.acquire();

        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        wakeLock.release();
        super.onDestroy();
    }
}
