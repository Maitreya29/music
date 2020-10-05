package com.hardcodecoder.pulsemusic.adapters;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;
import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.TaskRunner;
import com.hardcodecoder.pulsemusic.helper.PMBGridAdapterDiffCallback;
import com.hardcodecoder.pulsemusic.interfaces.GridAdapterCallback;
import com.hardcodecoder.pulsemusic.interfaces.SimpleItemClickListener;
import com.hardcodecoder.pulsemusic.loaders.SortOrder;
import com.hardcodecoder.pulsemusic.model.MusicModel;
import com.hardcodecoder.pulsemusic.utils.SortUtil;
import com.hardcodecoder.pulsemusic.views.MediaArtImageView;
import com.l4digital.fastscroll.FastScroller;

import java.util.ArrayList;
import java.util.List;

public class LibraryAdapter extends RecyclerView.Adapter<LibraryAdapter.MyLibraryViewHolder> implements FastScroller.SectionIndexer {

    private List<MusicModel> mList;
    private SimpleItemClickListener mListener;
    private GridAdapterCallback mCallback;
    private LayoutInflater mInflater;
    private int lastPosition = -1;

    public LibraryAdapter(List<MusicModel> list, LayoutInflater inflater, SimpleItemClickListener listener, GridAdapterCallback callback) {
        this.mList = list;
        this.mListener = listener;
        this.mInflater = inflater;
        this.mCallback = callback;
    }

    public void updateSortOrder(SortOrder sortOrder) {
        final Handler handler = new Handler();
        TaskRunner.executeAsync(() -> {
            List<MusicModel> oldSortedTracks = new ArrayList<>(mList);
            List<MusicModel> updatedTracks = SortUtil.sortLibraryList(mList, sortOrder);
            final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new PMBGridAdapterDiffCallback(oldSortedTracks, updatedTracks) {
                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    return oldSortedTracks.get(oldItemPosition).getTrackName().equals(updatedTracks.get(newItemPosition).getTrackName());
                }
            });
            handler.post(() -> {
                diffResult.dispatchUpdatesTo(LibraryAdapter.this);
                if (null != mCallback)
                    mCallback.onSortUpdateComplete();
            });
        });
    }

    @Override
    public CharSequence getSectionText(int position) {
        return mList.get(position).getTrackName().substring(0, 1);
    }

    @NonNull
    @Override
    public MyLibraryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyLibraryViewHolder(mInflater.inflate(R.layout.list_item_with_options, parent, false), mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyLibraryViewHolder holder, int position) {
        holder.setItemData(mList.get(position));
        holder.itemView.startAnimation(AnimationUtils.loadAnimation(holder.itemView.getContext(),
                (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top));
        lastPosition = position;
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull MyLibraryViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.itemView.clearAnimation();
    }

    @Override
    public int getItemCount() {
        if (mList != null)
            return mList.size();
        return 0;
    }

    /*
     * Custom View holder class
     */
    static class MyLibraryViewHolder extends RecyclerView.ViewHolder {

        private MaterialTextView songName, artist;
        private MediaArtImageView albumArt;

        MyLibraryViewHolder(View itemView, SimpleItemClickListener listener) {
            super(itemView);
            songName = itemView.findViewById(R.id.list_item_title);
            artist = itemView.findViewById(R.id.list_item_sub_title);
            albumArt = itemView.findViewById(R.id.list_item_option_album_art);

            itemView.setOnClickListener(v -> listener.onItemClick(getAdapterPosition()));

            itemView.findViewById(R.id.list_item_option_options_btn).setOnClickListener(v -> v.post(() -> listener.onOptionsClick(getAdapterPosition())));
        }

        void setItemData(MusicModel md) {
            songName.setText(md.getTrackName());
            artist.setText(md.getArtist());
            albumArt.loadAlbumArt(md.getAlbumArtUrl(), md.getAlbumId());
        }
    }
}