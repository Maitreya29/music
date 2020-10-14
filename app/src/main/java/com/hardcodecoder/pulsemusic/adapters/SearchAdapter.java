package com.hardcodecoder.pulsemusic.adapters;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;
import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.TaskRunner;
import com.hardcodecoder.pulsemusic.helper.DiffCb;
import com.hardcodecoder.pulsemusic.interfaces.SimpleItemClickListener;
import com.hardcodecoder.pulsemusic.model.MusicModel;
import com.hardcodecoder.pulsemusic.views.MediaArtImageView;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.MyViewHolderLibrary> {

    private final Deque<List<MusicModel>> pendingUpdates = new ArrayDeque<>();
    private final SimpleItemClickListener mListener;
    private final LayoutInflater mInflater;
    private final Handler mMainHandler = new Handler();
    protected List<MusicModel> list = new ArrayList<>();

    public SearchAdapter(LayoutInflater inflater, SimpleItemClickListener clickListener) {
        this.mListener = clickListener;
        this.mInflater = inflater;
    }

    @NonNull
    @Override
    public MyViewHolderLibrary onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolderLibrary(mInflater.inflate(R.layout.list_item_with_options, parent, false), mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolderLibrary holder, int position) {
        holder.setItemData(list.get(position));
    }

    @Override
    public int getItemCount() {
        if (list != null)
            return list.size();
        else
            return 0;
    }

    public void updateItems(final List<MusicModel> newItems) {
        pendingUpdates.push(newItems);
        if (pendingUpdates.size() > 1) {
            return;
        }
        updateItemsInternal(newItems);
    }


    private void updateItemsInternal(final List<MusicModel> newItems) {
        TaskRunner.executeAsync(() -> {
            final List<MusicModel> oldItems = new ArrayList<>(this.list);
            final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffCb(oldItems, newItems));
            mMainHandler.post(() -> applyDiffResult(newItems, diffResult));
        });
    }

    private void applyDiffResult(List<MusicModel> newItems, DiffUtil.DiffResult diffResult) {
        pendingUpdates.remove(newItems);
        dispatchUpdates(newItems, diffResult);
        if (pendingUpdates.size() > 0) {
            List<MusicModel> latest = pendingUpdates.pop();
            pendingUpdates.clear();
            updateItemsInternal(latest);
        }
    }

    private void dispatchUpdates(List<MusicModel> newItems, DiffUtil.DiffResult diffResult) {
        diffResult.dispatchUpdatesTo(this);
        list.clear();
        list.addAll(newItems);
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        pendingUpdates.clear();
        list.clear();
    }

    /*
     * Custom View holder class
     */
    static class MyViewHolderLibrary extends RecyclerView.ViewHolder {

        private final MaterialTextView title;
        private final MaterialTextView subTitle;
        private final MediaArtImageView albumArt;

        MyViewHolderLibrary(View itemView, SimpleItemClickListener listener) {
            super(itemView);
            albumArt = itemView.findViewById(R.id.list_item_option_album_art);
            title = itemView.findViewById(R.id.list_item_title);
            subTitle = itemView.findViewById(R.id.list_item_sub_title);
            itemView.setOnClickListener(v -> listener.onItemClick(getAdapterPosition()));
            itemView.findViewById(R.id.list_item_option_options_btn)
                    .setOnClickListener(v -> listener.onOptionsClick(getAdapterPosition()));
        }

        void setItemData(MusicModel md) {
            title.setText(md.getTrackName());
            subTitle.setText(md.getArtist());
            albumArt.loadAlbumArt(md.getAlbumArtUrl(), md.getAlbumId());
        }
    }
}