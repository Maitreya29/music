package com.nezukoos.music.shortcuts.types;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.nezukoos.music.shortcuts.ShortcutsLauncher;

@RequiresApi(api = Build.VERSION_CODES.N_MR1)
public abstract class BaseShortcutType {

    static final String ID_PREFIX = "com.nezukoos.music.shortcuts.types.id.";
    private final Context mContext;

    BaseShortcutType(Context context) {
        mContext = context;
    }

    @NonNull
    public static String getId() {
        return ID_PREFIX.concat("invalid");
    }

    @NonNull
    Intent getShortcutIntent(int shortcutType) {
        Intent intent = new Intent(mContext, ShortcutsLauncher.class);
        intent.setAction(Intent.ACTION_VIEW);
        intent.putExtra(ShortcutsLauncher.KEY_SHORTCUT_TYPE, shortcutType);
        return intent;
    }

    public abstract ShortcutInfo getShortcutInfo();
}