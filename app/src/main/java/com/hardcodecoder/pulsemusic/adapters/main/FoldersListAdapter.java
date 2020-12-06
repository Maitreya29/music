package com.hardcodecoder.pulsemusic.adapters.main;

import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.textview.MaterialTextView;
import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.adapters.base.SelectableItemAdapter;
import com.hardcodecoder.pulsemusic.interfaces.ItemSelectorListener;
import com.hardcodecoder.pulsemusic.model.Folder;
import com.hardcodecoder.pulsemusic.themes.ThemeColors;

import java.util.ArrayList;
import java.util.List;

public class FoldersListAdapter extends SelectableItemAdapter<Folder, FoldersListAdapter.FolderItemHolder> {

    private final LayoutInflater mInflater;
    private final ItemSelectorListener mListener;

    public FoldersListAdapter(@NonNull LayoutInflater inflater,
                              @NonNull List<Folder> list,
                              @NonNull ItemSelectorListener listener) {
        super(list);
        mInflater = inflater;
        mListener = listener;
    }

    @NonNull
    @Override
    public FolderItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FolderItemHolder(mInflater.inflate(R.layout.rv_folder_item, parent, false), mListener);
    }

    @Nullable
    @Override
    protected CharSequence getSectionText(@NonNull Folder data) {
        return data.getPaths().substring(0, 1);
    }

    @Nullable
    public ArrayList<String> getSelectedFoldersPath() {
        ArrayList<String> folderPaths = null;
        if (!getSelectedData().isEmpty()) {
            folderPaths = new ArrayList<>();
            for (Folder folder : getSelectedData())
                folderPaths.add(folder.toString());
        }
        return folderPaths;
    }

    static class FolderItemHolder extends SelectableItemAdapter.SelectableItemHolder<Folder> {

        private final MaterialTextView mFolderPath;

        public FolderItemHolder(@NonNull View itemView, @NonNull ItemSelectorListener listener) {
            super(itemView);
            mFolderPath = itemView.findViewById(R.id.folder_path);
            itemView.setOnClickListener(v ->
                    listener.onItemClick(this, getAdapterPosition(), shouldEnableSelection()));

        }

        @Override
        public void bindData(@NonNull Folder data, boolean selectThisItem) {
            SpannableString spannableString = new SpannableString(data.toString());
            spannableString.setSpan(new ForegroundColorSpan(ThemeColors.getCurrentSecondaryTextColor()),
                    0,
                    data.getVolumeName().length(),
                    SpannableString.SPAN_INCLUSIVE_INCLUSIVE);

            mFolderPath.setText(spannableString);
            super.bindData(data, selectThisItem);
        }

        @Override
        public void unBindData() {
            mFolderPath.setText(null);
        }
    }
}