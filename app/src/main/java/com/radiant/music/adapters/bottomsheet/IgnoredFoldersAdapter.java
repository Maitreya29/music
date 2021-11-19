package com.radiant.music.adapters.bottomsheet;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;
import com.radiant.music.R;
import com.radiant.music.interfaces.SingleClickListener;

import java.util.ArrayList;
import java.util.List;

public class IgnoredFoldersAdapter extends RecyclerView.Adapter<IgnoredFoldersAdapter.IgnoreFolderSVH> {

    private final LayoutInflater mInflater;
    private final SingleClickListener mListener;
    private List<String> mIgnoredFoldersList;

    public IgnoredFoldersAdapter(List<String> ignoredFoldersList, LayoutInflater inflater, SingleClickListener listener) {
        mIgnoredFoldersList = ignoredFoldersList;
        mInflater = inflater;
        mListener = listener;
    }

    @NonNull
    @Override
    public IgnoreFolderSVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new IgnoreFolderSVH(mInflater.inflate(R.layout.rv_ignored_folder_item, parent, false), mListener);
    }

    public void addItem(@NonNull String item) {
        if (null == mIgnoredFoldersList) mIgnoredFoldersList = new ArrayList<>();
        mIgnoredFoldersList.add(item);
        notifyItemInserted(mIgnoredFoldersList.size() - 1);
    }

    public void addItems(@NonNull List<String> items) {
        if (null == mIgnoredFoldersList) mIgnoredFoldersList = new ArrayList<>();
        int oldSize = mIgnoredFoldersList.size();
        mIgnoredFoldersList.addAll(items);
        notifyItemRangeInserted(oldSize, mIgnoredFoldersList.size());
    }

    public void deleteItem(int position) {
        mIgnoredFoldersList.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public void onBindViewHolder(@NonNull IgnoreFolderSVH holder, int position) {
        holder.setData(mIgnoredFoldersList.get(position));
    }

    @Override
    public int getItemCount() {
        if (null != mIgnoredFoldersList) return mIgnoredFoldersList.size();
        return 0;
    }

    public static class IgnoreFolderSVH extends RecyclerView.ViewHolder {

        private final MaterialTextView mTitle;

        public IgnoreFolderSVH(@NonNull View itemView, SingleClickListener listener) {
            super(itemView);
            mTitle = itemView.findViewById(R.id.ignored_folder_item_title);
            itemView.findViewById(R.id.ignored_folder_item_delete_btn)
                    .setOnClickListener(v -> listener.onItemCLick(getAdapterPosition()));
        }

        void setData(String title) {
            mTitle.setText(title);
        }
    }
}