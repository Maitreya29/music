package com.radiant.music.shortcuts.types;

import android.content.Context;
import android.content.pm.ShortcutInfo;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.radiant.music.R;
import com.radiant.music.shortcuts.ShortcutIconGenerator;
import com.radiant.music.shortcuts.ShortcutsLauncher;

@RequiresApi(api = Build.VERSION_CODES.N_MR1)
public class ShuffleShortcutType extends BaseShortcutType {

    private final Context mContext;

    public ShuffleShortcutType(Context context) {
        super(context);
        mContext = context;
    }

    @NonNull
    public static String getId() {
        return ID_PREFIX.concat("shuffle");
    }

    @RequiresApi(api = Build.VERSION_CODES.N_MR1)
    @Override
    public ShortcutInfo getShortcutInfo() {
        return new ShortcutInfo.Builder(mContext, getId())
                .setShortLabel(mContext.getString(R.string.shortcut_shuffle))
                .setLongLabel(mContext.getString(R.string.shortcut_shuffle_desc))
                .setIcon(ShortcutIconGenerator.getThemedIcon(mContext, R.drawable.ic_app_shortcut_shuffle))
                .setIntent(getShortcutIntent(ShortcutsLauncher.SHORTCUT_TYPE_SHUFFLE))
                .build();
    }
}