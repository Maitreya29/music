package com.nezukoos.music.shortcuts;

import android.content.Context;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.nezukoos.music.shortcuts.types.LatestShortcutType;
import com.nezukoos.music.shortcuts.types.ShuffleShortcutType;
import com.nezukoos.music.shortcuts.types.SuggestedShortcutType;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@RequiresApi(Build.VERSION_CODES.N_MR1)
public class AppShortcutsManager {

    private final Context mContext;
    private final ShortcutManager shortcutManager;

    public AppShortcutsManager(@NonNull Context context) {
        mContext = context;
        shortcutManager = mContext.getSystemService(ShortcutManager.class);
    }

    static void reportShortcutUsed(@NonNull Context context, String shortcutId) {
        Objects.requireNonNull(context.getSystemService(ShortcutManager.class)).reportShortcutUsed(shortcutId);
    }

    public void initDynamicShortcuts(boolean forceRecreate) {
        ShortcutsThemeManager.init(mContext);
        if (forceRecreate || ShortcutsThemeManager.isRequiredRecreate())
            shortcutManager.removeAllDynamicShortcuts();
        if (shortcutManager.getDynamicShortcuts().size() == 0)
            shortcutManager.setDynamicShortcuts(getDefaultShortcuts());
    }

    @NonNull
    private List<ShortcutInfo> getDefaultShortcuts() {
        return (Arrays.asList(
                new LatestShortcutType(mContext).getShortcutInfo(),
                new SuggestedShortcutType(mContext).getShortcutInfo(),
                new ShuffleShortcutType(mContext).getShortcutInfo()
        ));
    }
}