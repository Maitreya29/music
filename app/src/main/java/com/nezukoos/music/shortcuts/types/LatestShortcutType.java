package com.nezukoos.music.shortcuts.types;

import android.content.Context;
import android.content.pm.ShortcutInfo;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.nezukoos.music.R;
import com.nezukoos.music.shortcuts.ShortcutIconGenerator;
import com.nezukoos.music.shortcuts.ShortcutsLauncher;

@RequiresApi(api = Build.VERSION_CODES.N_MR1)
public class LatestShortcutType extends BaseShortcutType {

    private final Context mContext;

    public LatestShortcutType(Context context) {
        super(context);
        mContext = context;
    }

    @NonNull
    public static String getId() {
        return ID_PREFIX.concat("latest");
    }

    @Override
    public ShortcutInfo getShortcutInfo() {
        return new ShortcutInfo.Builder(mContext, getId())
                .setShortLabel(mContext.getString(R.string.shortcut_latest))
                .setLongLabel(mContext.getString(R.string.shortcut_latest_desc))
                .setIcon(ShortcutIconGenerator.getThemedIcon(mContext, R.drawable.ic_app_shortcut_latest))
                .setIntent(getShortcutIntent(ShortcutsLauncher.SHORTCUT_TYPE_LATEST))
                .build();
    }
}