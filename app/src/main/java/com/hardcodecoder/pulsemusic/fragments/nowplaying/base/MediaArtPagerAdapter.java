package com.hardcodecoder.pulsemusic.fragments.nowplaying.base;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.shape.ShapeAppearanceModel;
import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.TaskRunner;
import com.hardcodecoder.pulsemusic.TaskRunner.Callback;
import com.hardcodecoder.pulsemusic.helper.DiffCb;
import com.hardcodecoder.pulsemusic.model.MusicModel;
import com.hardcodecoder.pulsemusic.views.MediaArtImageView;

import java.util.ArrayList;
import java.util.List;

public class MediaArtPagerAdapter extends RecyclerView.Adapter<MediaArtPagerAdapter.AlbumArtSVH> {

    private final ShapeAppearanceModel mShapeModel;
    private final Context mContext;
    private final List<MusicModel> mTracksList = new ArrayList<>();
    @LayoutRes
    private final int mLLayoutRes;

    public MediaArtPagerAdapter(Context context, List<MusicModel> tracks, @LayoutRes int layoutRes, ShapeAppearanceModel appearanceModel) {
        mContext = context;
        mTracksList.addAll(tracks);
        mLLayoutRes = layoutRes;
        mShapeModel = appearanceModel;
    }

    public void notifyTracksChanged(@NonNull List<MusicModel> updatedTracks, @Nullable Callback<Void> callback) {
        final Handler handler = new Handler();
        TaskRunner.executeAsync(() -> {
            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffCb(mTracksList, updatedTracks));
            handler.post(() -> {
                diffResult.dispatchUpdatesTo(MediaArtPagerAdapter.this);
                mTracksList.clear();
                mTracksList.addAll(updatedTracks);
                if (callback != null) callback.onComplete(null);
            });
        });
    }

    @Override
    public int getItemCount() {
        return mTracksList.size();
    }

    @NonNull
    @Override
    public AlbumArtSVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AlbumArtSVH(LayoutInflater.from(mContext).inflate(mLLayoutRes, parent, false), mShapeModel);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumArtSVH holder, int position) {
        MusicModel md = mTracksList.get(position);
        holder.mMediaArtImageView.loadAlbumArt(md.getAlbumArtUrl(), md.getAlbumId());
    }

    public static class AlbumArtSVH extends RecyclerView.ViewHolder {

        public final MediaArtImageView mMediaArtImageView;

        public AlbumArtSVH(@NonNull View itemView, ShapeAppearanceModel shapeModel) {
            super(itemView);
            mMediaArtImageView = itemView.findViewById(R.id.nps_media_art_image_view);
            if (null != shapeModel) mMediaArtImageView.setShapeAppearanceModel(shapeModel);
        }
    }
}