package com.nezukoos.music.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.shape.CornerFamily;
import com.nezukoos.music.R;
import com.nezukoos.music.glide.GlideApp;
import com.nezukoos.music.helper.MediaArtHelper;
import com.nezukoos.music.themes.ThemeColors;

public class MediaArtImageView extends ShapeableImageView {

    private Target<Drawable> target = null;

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

    /**
     * Clears the loaded image if {@param albumArtUrl} is null else
     * Loads image from {@param albumArtUrl} into this ImageView
     *
     * @param albumArtUrl the image url to load from, can be uri string or a url or a file path
     * @param albumId     the albumId of the track used to generate fallback image if load from {@param albumArtUrl} fails
     *                    If this is -1 and load from {@param albumArtUrl} fails or is null,
     *                    fallback image wil be tinted with ThemeColor#getCurrentColorPrimary
     */
    public void loadAlbumArt(@Nullable final String albumArtUrl, final long albumId) {
        target = GlideApp
                .with(this)
                .load(albumArtUrl)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        post(() -> {
                            setBackgroundColor(android.R.color.transparent);
                            setImageDrawable(MediaArtHelper.getDefaultAlbumArt(getContext(), albumId));
                        });
                        return true;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        if (null != getBackground()) setBackgroundColor(0);
                        return false;
                    }
                })
                .into(this);
    }

    public void clearLoadedArt() {
        GlideApp.with(this).clear(target);
        setBackgroundColor(0);
    }
}