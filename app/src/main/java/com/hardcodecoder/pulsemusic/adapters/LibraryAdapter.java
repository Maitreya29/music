package com.hardcodecoder.pulsemusic.adapters;

import android.os.Handler;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class LibraryAdapter extends RecyclerView.Adapter<LibraryAdapter.MyLibraryViewHolder> implements FastScroller.SectionIndexer {

    private SimpleDateFormat mDateFormatter = null;
    private List<MusicModel> mList;
    private SimpleItemClickListener mListener;
    private GridAdapterCallback mCallback;
    private LayoutInflater mInflater;
    private SortOrder mSortOrder;
    private int lastPosition = -1;

    public LibraryAdapter(@NonNull List<MusicModel> list,
                          @NonNull LayoutInflater inflater,
                          @NonNull SortOrder sortOrder,
                          @NonNull SimpleItemClickListener listener,
                          @Nullable GridAdapterCallback callback) {
        mList = list;
        mListener = listener;
        mSortOrder = sortOrder;
        mInflater = inflater;
        mCallback = callback;
    }

    public void updateSortOrder(SortOrder sortOrder) {
        mSortOrder = sortOrder;
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
        final MusicModel md = mList.get(position);
        switch (mSortOrder) {
            case DURATION_ASC:
            case DURATION_DESC:
                return DateUtils.formatElapsedTime(md.getTrackDuration() / 1000);
            case DATE_ADDED_ASC:
            case DATE_ADDED_DESC:
                return getDate(md.getDateAdded());
            case DATE_MODIFIED_ASC:
            case DATE_MODIFIED_DESC:
                return getDate(md.getDateModified());
            case TRACK_NUMBER_ASC:
            case TRACK_NUMBER_DESC:
                return String.valueOf(md.getTrackNumber());
            case TITLE_ASC:
            case TITLE_DESC:
            default:
                return md.getTrackName().substring(0, 1);
        }
    }

    private CharSequence getDate(long seconds) {
        if (null == mDateFormatter)
            mDateFormatter = new SimpleDateFormat("MMM dd, yy", Locale.getDefault());
        return mDateFormatter.format(new Date(seconds * 1000));
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