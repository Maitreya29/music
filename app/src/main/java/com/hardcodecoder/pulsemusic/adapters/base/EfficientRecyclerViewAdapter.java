package com.hardcodecoder.pulsemusic.adapters.base;

import android.view.View;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import me.zhanghai.android.fastscroll.PopupTextProvider;

public abstract class EfficientRecyclerViewAdapter<T, SVH extends EfficientRecyclerViewAdapter.SmartViewHolder<T>>
        extends RecyclerView.Adapter<SVH> implements PopupTextProvider {

    private final List<T> mDataList;

    public EfficientRecyclerViewAdapter(@NonNull List<T> list) {
        mDataList = list;
    }

    @CallSuper
    @Override
    public void onBindViewHolder(@NonNull SVH holder, int position) {
        holder.bindData(mDataList.get(position));
    }

    @Override
    public void onViewRecycled(@NonNull SVH holder) {
        holder.unbindData();
    }

    @Override
    public boolean onFailedToRecycleView(@NonNull SVH holder) {
        holder.itemView.clearAnimation();
        holder.itemView.clearFocus();
        return true;
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull SVH holder) {
        holder.itemView.clearAnimation();
        holder.itemView.clearFocus();
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        mDataList.clear();
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
    public List<T> getDataList() {
        return mDataList;
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public abstract static class SmartViewHolder<T> extends RecyclerView.ViewHolder {

        public SmartViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public abstract void bindData(@NonNull T data);

        public abstract void unbindData();
    }
}