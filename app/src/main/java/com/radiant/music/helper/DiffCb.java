package com.radiant.music.helper;

import androidx.recyclerview.widget.DiffUtil;

import com.radiant.music.model.MusicModel;

import java.util.List;

public class DiffCb extends DiffUtil.Callback {

    private final List<MusicModel> oldItems;
    private final List<MusicModel> newItems;

    public DiffCb(List<MusicModel> oldItems, List<MusicModel> newItems) {
        this.oldItems = oldItems;
        this.newItems = newItems;
    }

    @Override
    public int getOldListSize() {
        return oldItems.size();
    }

    @Override
    public int getNewListSize() {
        return newItems.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldItems.get(oldItemPosition).equals(newItems.get(newItemPosition));
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldItems.get(oldItemPosition).getId() == newItems.get(newItemPosition).getId();
    }
}