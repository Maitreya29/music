package com.hardcodecoder.pulsemusic.fragments.nowplaying.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.shape.ShapeAppearanceModel;
import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.model.MusicModel;
import com.hardcodecoder.pulsemusic.views.MediaArtImageView;

import java.util.List;

public class MediaArtPagerAdapter extends RecyclerView.Adapter<MediaArtPagerAdapter.AlbumArtSVH> {

    private final ShapeAppearanceModel mShapeModel;
    private Context mContext;
    private List<MusicModel> mTracksList;
    @LayoutRes
    private int mLLayoutRes;

    public MediaArtPagerAdapter(Context context, List<MusicModel> tracks, @LayoutRes int layoutRes, ShapeAppearanceModel appearanceModel) {
        mContext = context;
        mTracksList = tracks;
        mLLayoutRes = layoutRes;
        mShapeModel = appearanceModel;
    }

    public void notifyTracksChanged(List<MusicModel> tracks) {
        mTracksList = tracks;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (null != mTracksList) return mTracksList.size();
        return 0;
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

        public MediaArtImageView mMediaArtImageView;

        public AlbumArtSVH(@NonNull View itemView, ShapeAppearanceModel shapeModel) {
            super(itemView);
            mMediaArtImageView = itemView.findViewById(R.id.nps_media_art_image_view);
            if (null != shapeModel) mMediaArtImageView.setShapeAppearanceModel(shapeModel);
        }
    }
}