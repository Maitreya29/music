package com.radiant.music.widgets.base;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadata;
import android.media.session.MediaController;
import android.widget.RemoteViews;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.radiant.music.R;
import com.radiant.music.activities.main.SplashActivity;
import com.radiant.music.playback.RadiantController;
import com.radiant.music.themes.PresetColors;
import com.radiant.music.utils.ImageUtil;
import com.radiant.music.widgets.RadiantWidgetControlReceiver;
import com.radiant.music.widgets.RadiantWidgetsHelper;

public abstract class RadiantWidgetBase extends AppWidgetProvider {

    public static final String TAG = RadiantWidgetBase.class.getSimpleName();
    private final RadiantController mRadiantController = RadiantController.getInstance();

    @Override
    public void onEnabled(Context context) {
        RadiantWidgetsHelper.enableWidgets(context, true);
    }

    @Override
    public void onDisabled(Context context) {
        RadiantWidgetsHelper.enableWidgets(context, false);
    }

    @Override
    public void onUpdate(@NonNull Context context, @NonNull AppWidgetManager appWidgetManager, @NonNull int[] appWidgetIds) {
        updateWidget(context, appWidgetManager, appWidgetIds);
    }

    public void updateWidget(@NonNull Context context, @NonNull AppWidgetManager appWidgetManager, @Nullable int[] appWidgetIds) {
        RemoteViews views = new RemoteViews(context.getPackageName(), getLayoutId());
        Intent launchIntent = new Intent(context, SplashActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, launchIntent, PendingIntent.FLAG_MUTABLE);
        setUpLayout(context, views, pendingIntent);
        if (null != appWidgetIds) {
            appWidgetManager.updateAppWidget(appWidgetIds, views);
        } else {
            appWidgetManager.updateAppWidget(new ComponentName(context, getClass()), views);
        }
    }

    @NonNull
    protected PendingIntent buildPendingControlBroadcast(@NonNull Context context, final String action) {
        Intent intent = new Intent(context, RadiantWidgetControlReceiver.class);
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_MUTABLE);
    }

    @NonNull
    protected String getTitle(@NonNull Context context) {
        MediaController controller = mRadiantController.getController();
        if (null != controller && null != controller.getMetadata())
            return controller.getMetadata().getString(MediaMetadata.METADATA_KEY_TITLE);
        return context.getString(R.string.app_name);
    }

    @Nullable
    protected Bitmap getAlbumArt(@NonNull Context context) {
        MediaController controller = mRadiantController.getController();
        if (null != controller && null != controller.getMetadata())
            return controller.getMetadata().getBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART);
        Drawable drawable = ImageUtil.generateTintedDefaultAlbumArt(context, PresetColors.RADIANT_PINK);
        if (null == drawable) return null;
        return ImageUtil.getBitmapFromDrawable(drawable);
    }

    protected boolean isPlaying() {
        return mRadiantController.isPlaying();
    }

    @LayoutRes
    public abstract int getLayoutId();

    public abstract void setUpLayout(@NonNull Context context, @NonNull RemoteViews layoutView, @NonNull PendingIntent defaultClickAction);
}