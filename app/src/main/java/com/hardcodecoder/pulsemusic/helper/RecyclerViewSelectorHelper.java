package com.hardcodecoder.pulsemusic.helper;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hardcodecoder.pulsemusic.interfaces.ItemSelectorAdapterCallback;
import com.hardcodecoder.pulsemusic.interfaces.ItemTouchHelperViewHolder;

public class RecyclerViewSelectorHelper {

    private final ItemSelectorAdapterCallback mAdapter;

    public RecyclerViewSelectorHelper(ItemSelectorAdapterCallback adapter) {
        mAdapter = adapter;
    }

    public void onItemSelected(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        mAdapter.onItemSelected(position);

        if (viewHolder instanceof ItemTouchHelperViewHolder) {
            ItemTouchHelperViewHolder itemTouchHelperViewHolder = (ItemTouchHelperViewHolder) viewHolder;
            itemTouchHelperViewHolder.onItemSelected();
        }
    }

    public void onItemUnSelected(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        mAdapter.onItemUnselected(position);

        if (viewHolder instanceof ItemTouchHelperViewHolder) {
            ItemTouchHelperViewHolder itemTouchHelperViewHolder = (ItemTouchHelperViewHolder) viewHolder;
            itemTouchHelperViewHolder.onItemClear();
        }
    }
}