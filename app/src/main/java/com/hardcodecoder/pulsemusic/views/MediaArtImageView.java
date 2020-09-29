package com.hardcodecoder.pulsemusic.views;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.hardcodecoder.pulsemusic.GlideApp;
import com.hardcodecoder.pulsemusic.helper.MediaArtHelper;

public class MediaArtImageView extends ModernImageView {

    private static final String BACKGROUND_COLOR = "#1E1E1E";

    public MediaArtImageView(Context context) {
        this(context, null, 0);
    }

    public MediaArtImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MediaArtImageView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void loadAlbumArt(final String albumArtUrl, final int albumId) {
        GlideApp.with(this)
                .load(albumArtUrl)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        setBackgroundColor(Color.parseColor(BACKGROUND_COLOR));
                        setImageDrawable(MediaArtHelper.getDefaultAlbumArt(getContext(), albumId));
                        return true;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                })
                .into(this);
    }
}