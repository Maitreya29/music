package com.hardcodecoder.pulsemusic.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.shape.CornerFamily;
import com.hardcodecoder.pulsemusic.GlideApp;
import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.helper.MediaArtHelper;

public class MediaArtImageView extends ShapeableImageView {

    private static final String BACKGROUND_COLOR = "#1E1E1E";

    public MediaArtImageView(Context context) {
        this(context, null, 0);
    }

    public MediaArtImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MediaArtImageView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MediaArtImageView);

        int cornerFamily = CornerFamily.ROUNDED;
        float topLeft;
        float topRight;
        float bottomLeft;
        float bottomRight;

        if (typedArray.getInt(R.styleable.MediaArtImageView_cornerType, 0) == 1) {
            cornerFamily = CornerFamily.CUT;
        }

        if (typedArray.hasValue(R.styleable.MediaArtImageView_radius)) {
            topLeft = topRight = bottomLeft = bottomRight = typedArray.getDimension(
                    R.styleable.MediaArtImageView_radius,
                    context.getResources().getDimension(R.dimen.rounding_radius_4dp));
        } else {
            float defaultCornerSize = context.getResources().getDimension(R.dimen.rounding_radius_4dp);
            topLeft = typedArray.getDimension(R.styleable.MediaArtImageView_radiusTopLeft, defaultCornerSize);
            topRight = typedArray.getDimension(R.styleable.MediaArtImageView_radiusTopRight, defaultCornerSize);
            bottomLeft = typedArray.getDimension(R.styleable.MediaArtImageView_radiusBottomLeft, defaultCornerSize);
            bottomRight = typedArray.getDimension(R.styleable.MediaArtImageView_radiusBottomRight, defaultCornerSize);
        }

        setShapeAppearanceModel(
                getShapeAppearanceModel()
                        .toBuilder()
                        .setTopLeftCorner(cornerFamily, topLeft)
                        .setTopRightCorner(cornerFamily, topRight)
                        .setBottomLeftCorner(cornerFamily, bottomLeft)
                        .setBottomRightCorner(cornerFamily, bottomRight)
                        .build());
        typedArray.recycle();
    }

    public void loadAlbumArt(final String albumArtUrl, final int albumId) {
        GlideApp.with(this)
                .load(albumArtUrl)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        post(() -> {
                            setBackgroundColor(Color.parseColor(BACKGROUND_COLOR));
                            setImageDrawable(MediaArtHelper.getDefaultAlbumArt(getContext(), albumId));
                        });
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