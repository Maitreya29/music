package com.hardcodecoder.pulsemusic.adapters;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.TaskRunner;
import com.hardcodecoder.pulsemusic.helper.PMBGridAdapterDiffCallback;
import com.hardcodecoder.pulsemusic.interfaces.GridAdapterCallback;
import com.hardcodecoder.pulsemusic.interfaces.SimpleTransitionClickListener;
import com.hardcodecoder.pulsemusic.loaders.SortOrder;
import com.hardcodecoder.pulsemusic.model.AlbumModel;
import com.hardcodecoder.pulsemusic.utils.SortUtil;
import com.hardcodecoder.pulsemusic.views.MediaArtImageView;
import com.l4digital.fastscroll.FastScroller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AlbumsAdapter extends RecyclerView.Adapter<AlbumsAdapter.AlbumsSVH>
        implements FastScroller.SectionIndexer {

    private SimpleDateFormat mDateFormatter = null;
    private List<AlbumModel> mList;
    private LayoutInflater mInflater;
    private SimpleTransitionClickListener mListener;
    private GridAdapterCallback mCallback;
    private SortOrder.ALBUMS mSortOrder;

    public AlbumsAdapter(List<AlbumModel> list,
                         LayoutInflater inflater,
                         SortOrder.ALBUMS sortOrder,
                         SimpleTransitionClickListener listener,
                         GridAdapterCallback callback) {
        mList = list;
        mInflater = inflater;
        mSortOrder = sortOrder;
        mListener = listener;
        mCallback = callback;
    }

    public void updateSortOrder(SortOrder.ALBUMS sortOrder) {
        mSortOrder = sortOrder;
        final Handler handler = new Handler();
        TaskRunner.executeAsync(() -> {
            List<AlbumModel> oldSortedTracks = new ArrayList<>(mList);
            List<AlbumModel> updatedTracks = SortUtil.sortAlbumList(mList, sortOrder);
            final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new PMBGridAdapterDiffCallback(oldSortedTracks, updatedTracks) {
                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    return oldSortedTracks.get(oldItemPosition).getAlbumName().equals(updatedTracks.get(newItemPosition).getAlbumName());
                }
            });
            handler.post(() -> {
                diffResult.dispatchUpdatesTo(AlbumsAdapter.this);
                if (null != mCallback)
                    mCallback.onSortUpdateComplete();
            });
        });
    }

    @Override
    public CharSequence getSectionText(int position) {
        AlbumModel am = mList.get(position);
        switch (mSortOrder) {
            case ARTIST_ASC:
            case ARTIST_DESC:
                return am.getAlbumArtist().substring(0, 1);
            case ALBUM_DATE_FIRST_YEAR_ASC:
            case ALBUM_DATE_FIRST_YEAR_DESC:
                return getDate(am.getFirstYear());
            case ALBUM_DATE_LAST_YEAR_ASC:
            case ALBUM_DATE_LAST_YEAR_DESC:
                return getDate(am.getLastYear());
            case TITLE_ASC:
            case TITLE_DESC:
            default:
                return am.getAlbumName().substring(0, 1);
        }
    }

    private CharSequence getDate(long seconds) {
        if (null == mDateFormatter)
            mDateFormatter = new SimpleDateFormat("yyyy", Locale.getDefault());
        return mDateFormatter.format(new Date(seconds * 1000));
    }

    @NonNull
    @Override
    public AlbumsSVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AlbumsSVH(mInflater.inflate(R.layout.rv_big_album_item, parent, false), /*mAddOverlay,*/ mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumsSVH holder, int position) {
        holder.setData(mList.get(position));
    }

    @Override
    public int getItemCount() {
        if (null != mList)
            return mList.size();
        return 0;
    }

    static class AlbumsSVH extends RecyclerView.ViewHolder {

        private MediaArtImageView albumArt;
        private TextView title;

        AlbumsSVH(@NonNull View itemView, SimpleTransitionClickListener mListener) {
            super(itemView);
            albumArt = itemView.findViewById(R.id.big_album_item_art);
            title = itemView.findViewById(R.id.big_album_item_title);
            itemView.setOnClickListener(v -> mListener.onItemClick(albumArt, getAdapterPosition()));
        }

        void setData(AlbumModel am) {
            title.setText(am.getAlbumName());
            albumArt.setTransitionName("shared_transition_album_iv_" + getAdapterPosition());
            albumArt.loadAlbumArt(am.getAlbumArt(), am.getAlbumId());
        }
    }
}