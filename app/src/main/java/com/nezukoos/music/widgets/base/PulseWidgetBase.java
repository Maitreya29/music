package com.nezukoos.music.widgets.base;

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

import com.nezukoos.music.R;
import com.nezukoos.music.activities.main.SplashActivity;
import com.nezukoos.music.playback.PulseController;
import com.nezukoos.music.themes.PresetColors;
import com.nezukoos.music.utils.ImageUtil;
import com.nezukoos.music.widgets.PulseWidgetControlReceiver;
import com.nezukoos.music.widgets.PulseWidgetsHelper;

public abstract class PulseWidgetBase extends AppWidgetProvider {

    public static final String TAG = PulseWidgetBase.class.getSimpleName();
    private final PulseController mPulseController = PulseController.getInstance();

    @Override
    public void onEnabled(Context context) {
        PulseWidgetsHelper.enableWidgets(context, true);
    }

    @Override
    public void onDisabled(Context context) {
        PulseWidgetsHelper.enableWidgets(context, false);
    }

    @Override
    public void onUpdate(@NonNull Context context, @NonNull AppWidgetManager appWidgetManager, @NonNull int[] appWidgetIds) {
        updateWidget(context, appWidgetManager, appWidgetIds);
    }

    public void updateWidget(@NonNull Context context, @NonNull AppWidgetManager appWidgetManager, @Nullable int[] appWidgetIds) {
        RemoteViews views = new RemoteViews(context.getPackageName(), getLayoutId());
        Intent launchIntent = new Intent(context, SplashActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, launchIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        setUpLayout(context, views, pendingIntent);
        if (null != appWidgetIds) {
            appWidgetManager.updateAppWidget(appWidgetIds, views);
        } else {
            appWidgetManager.updateAppWidget(new ComponentName(context, getClass()), views);
        }
    }

    @NonNull
    protected PendingIntent buildPendingControlBroadcast(@NonNull Context context, final String action) {
        Intent intent = new Intent(context, PulseWidgetControlReceiver.class);
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @NonNull
    protected String getTitle(@NonNull Context context) {
        MediaController controller = mPulseController.getController();
        if (null != controller && null != controller.getMetadata())
            return controller.getMetadata().getString(MediaMetadata.METADATA_KEY_TITLE);
        return context.getString(R.string.app_name);
    }

    @Nullable
    protected Bitmap getAlbumArt(@NonNull Context context) {
        MediaController controller = mPulseController.getController();
        if (null != controller && null != controller.getMetadata())
            return controller.getMetadata().getBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART);
        Drawable drawable = ImageUtil.generateTintedDefaultAlbumArt(context, PresetColors.NEZUKO_PINK);
        if (null == drawable) return null;
        return ImageUtil.getBitmapFromDrawable(drawable);
    }

    protected boolean isPlaying() {
        return mPulseController.isPlaying();
    }

    @LayoutRes
    public abstract int getLayoutId();

    public abstract void setUpLayout(@NonNull Context context, @NonNull RemoteViews layoutView, @NonNull PendingIntent defaultClickAction);
}