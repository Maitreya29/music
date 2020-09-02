package com.hardcodecoder.pulsemusic.views;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

import com.bumptech.glide.GenericTransitionOptions;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.hardcodecoder.pulsemusic.GlideApp;
import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.helper.MediaArtHelper;

public class MediaArtImageView extends ModernImageView {

    private static final String BACKGROUND_COLOR = "#1E1E1E";

    public MediaArtImageView(Context context) {
        super(context);
    }

    public MediaArtImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MediaArtImageView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void loadAlbumArt(final String albumArtUrl, final long albumId) {
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
                .transition(GenericTransitionOptions.with(R.anim.fade_in_image))
                .into(this);
    }
}
