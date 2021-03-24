package com.hardcodecoder.pulsemusic.adapters.base;

import android.view.View;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hardcodecoder.pulsemusic.interfaces.ItemSelectorAdapterCallback;
import com.hardcodecoder.pulsemusic.interfaces.ItemTouchHelperViewHolder;
import com.hardcodecoder.pulsemusic.utils.ImageUtil;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import me.zhanghai.android.fastscroll.PopupTextProvider;

public abstract class SelectableItemAdapter<T, SIH extends SelectableItemAdapter.SelectableItemHolder<T>> extends RecyclerView.Adapter<SIH>
        implements ItemSelectorAdapterCallback, PopupTextProvider {

    private final Set<T> mSelectedData;
    private final List<T> mDataList;

    public SelectableItemAdapter(@NonNull List<T> list) {
        mDataList = list;
        mSelectedData = new LinkedHashSet<>(mDataList.size());
    }

    @Override
    public void onBindViewHolder(@NonNull SIH holder, int position) {
        T data = mDataList.get(position);
        boolean isSelected = mSelectedData.contains(data);
        holder.bindData(data, isSelected);
    }

    @Override
    public void onViewRecycled(@NonNull SIH holder) {
        holder.unBindData();
    }

    @Override
    public boolean onFailedToRecycleView(@NonNull SIH holder) {
        holder.itemView.clearAnimation();
        holder.itemView.clearFocus();
        return true;
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull SIH holder) {
        holder.itemView.clearAnimation();
        holder.itemView.clearFocus();
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        mSelectedData.clear();
        mDataList.clear();
    }

    @Override
    public void onItemSelected(int position) {
        mSelectedData.add(mDataList.get(position));
    }

    @Override
    public void onItemUnselected(int position) {
        mSelectedData.remove(mDataList.get(position));
    }

    @NonNull
    @Override
    public String getPopupText(int position) {
        return getSectionText(mDataList.get(position));
    }

    @NonNull
    protected String getSectionText(@NonNull T data) {
        return "";
    }

    @NonNull
    public Set<T> getSelectedData() {
        return mSelectedData;
    }

    @NonNull
    public List<T> getDataList() {
        return mDataList;
    }

    @Override
    public int getItemCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    public static class SelectableItemHolder<T> extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {

        private boolean mItemSelected = false;

        public SelectableItemHolder(@NonNull View itemView) {
            super(itemView);
        }

        @CallSuper
        public void bindData(@NonNull T data, boolean selectThisItem) {
            if (mItemSelected == selectThisItem) return;
            if (selectThisItem) onItemSelected();
            else onItemClear();
        }

        public void unBindData() {
        }

        @CallSuper
        @Override
        public void onItemSelected() {
            itemView.setBackground(ImageUtil.getSelectedItemDrawable(itemView.getContext()));
            mItemSelected = true;
        }

        @CallSuper
        @Override
        public void onItemClear() {
            itemView.setBackground(null);
            mItemSelected = false;
        }

        public boolean shouldEnableSelection() {
            return !mItemSelected;
        }
    }
}