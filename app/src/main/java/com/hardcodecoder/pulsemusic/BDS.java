package com.hardcodecoder.pulsemusic;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

public class BDS extends Service {

    private static final String CHANNEL_ID = "com.hardcodecoder.pulsemusic.BLUETOOTH_CHANNEL_ID";
    private boolean mNotificationPosted = false;

    @Override
    public void onCreate() {
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
        super.onDestroy();
    }

    private void postNotificationForeground() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            createNotificationChannel();

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(getString(R.string.audio_category_enable_connected_start))
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
                    getApplicationContext().getString(R.string.bluetooth_channel_name),
                    NotificationManager.IMPORTANCE_NONE);
            notificationChannel.setDescription("no sound");
            notificationChannel.enableVibration(false);
            notificationChannel.setShowBadge(false);
            notificationChannel.setSound(null, null);
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
    }
}