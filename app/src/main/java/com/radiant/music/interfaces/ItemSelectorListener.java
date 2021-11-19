package com.radiant.music.interfaces;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public interface ItemSelectorListener {
    void onItemClick(@NonNull RecyclerView.ViewHolder viewHolder, int position, boolean selected);
}
