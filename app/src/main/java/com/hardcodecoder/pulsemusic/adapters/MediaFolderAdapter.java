package com.hardcodecoder.pulsemusic.adapters;

import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;
import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.interfaces.ItemSelectorAdapterCallback;
import com.hardcodecoder.pulsemusic.interfaces.ItemSelectorListener;
import com.hardcodecoder.pulsemusic.interfaces.ItemTouchHelperViewHolder;
import com.hardcodecoder.pulsemusic.model.Folder;
import com.hardcodecoder.pulsemusic.themes.ThemeColors;
import com.hardcodecoder.pulsemusic.utils.ImageUtil;
import com.l4digital.fastscroll.FastScroller;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MediaFolderAdapter extends RecyclerView.Adapter<MediaFolderAdapter.FolderItemSVH>
        implements ItemSelectorAdapterCallback, FastScroller.SectionIndexer {

    private final Set<String> mSelectedFolders = new HashSet<>();
    private final List<Folder> mFoldersList;
    private final LayoutInflater mInflater;
    private final ItemSelectorListener mListener;

    public MediaFolderAdapter(List<Folder> foldersList, LayoutInflater inflater, ItemSelectorListener listener) {
        mFoldersList = foldersList;
        mInflater = inflater;
        mListener = listener;
    }

    @NonNull
    @Override
    public FolderItemSVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FolderItemSVH(mInflater.inflate(R.layout.rv_folder_item, parent, false), mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull FolderItemSVH holder, int position) {
        boolean isSelected = mSelectedFolders.contains(mFoldersList.get(position).toString());
        holder.setData(mFoldersList.get(position), isSelected);
    }

    @Override
    public int getItemCount() {
        if (mFoldersList == null)
            return 0;
        return mFoldersList.size();
    }

    @Override
    public void onItemSelected(int position) {
        mSelectedFolders.add(mFoldersList.get(position).toString());
    }

    @Override
    public void onItemUnselected(int position) {
        mSelectedFolders.remove(mFoldersList.get(position).toString());
    }

    @Override
    public CharSequence getSectionText(int position) {
        return mFoldersList.get(position).getPaths().substring(0, 1);
    }

    public Set<String> getSelectedItems() {
        return mSelectedFolders;
    }

    public static class FolderItemSVH extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {

        private final MaterialTextView mTitle;
        private boolean mItemSelected = false;

        public FolderItemSVH(@NonNull View itemView, ItemSelectorListener listener) {
            super(itemView);
            mTitle = itemView.findViewById(R.id.folder_path);
            itemView.setOnClickListener(v ->
                    listener.onItemClick(this, getAdapterPosition(), !mItemSelected));
        }

        void setData(Folder folder, boolean selected) {
            SpannableString spannableString = new SpannableString(folder.toString());
            spannableString.setSpan(new ForegroundColorSpan(ThemeColors.getCurrentSecondaryTextColor()),
                    0,
                    folder.getVolumeName().length(),
                    SpannableString.SPAN_INCLUSIVE_INCLUSIVE);

            mTitle.setText(spannableString);
            if (mItemSelected == selected) return;
            if (selected) onItemSelected();
            else onItemClear();
        }

        @Override
        public void onItemSelected() {
            itemView.setBackground(ImageUtil.getAccentTintedSelectedItemBackground(itemView.getContext()));
            mItemSelected = true;
        }

        @Override
        public void onItemClear() {
            itemView.setBackground(ContextCompat.getDrawable(itemView.getContext(), android.R.color.transparent));
            mItemSelected = false;
        }
    }
}