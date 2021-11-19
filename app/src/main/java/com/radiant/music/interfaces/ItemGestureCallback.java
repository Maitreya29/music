package com.radiant.music.interfaces;

import androidx.annotation.NonNull;

public interface ItemGestureCallback<V> {

    void onItemDismissed(@NonNull V dismissedItem, int itemPosition);

    void onItemMove(int fromPosition, int toPosition);
}