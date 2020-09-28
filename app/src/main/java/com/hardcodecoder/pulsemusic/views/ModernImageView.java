package com.hardcodecoder.pulsemusic.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.shape.CornerFamily;
import com.hardcodecoder.pulsemusic.R;

public class ModernImageView extends ShapeableImageView {

    public ModernImageView(Context context) {
        this(context, null, 0);
    }

    public ModernImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ModernImageView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ModernImageView);

        int cornerFamily = CornerFamily.ROUNDED;
        float topLeft;
        float topRight;
        float bottomLeft;
        float bottomRight;

        if (typedArray.getInt(R.styleable.ModernImageView_cornerType, 0) == 1) {
            cornerFamily = CornerFamily.CUT;
        }

        if (typedArray.hasValue(R.styleable.ModernImageView_radius)) {
            topLeft = topRight = bottomLeft = bottomRight = typedArray.getDimension(
                    R.styleable.ModernImageView_radius,
                    context.getResources().getDimension(R.dimen.rounding_radius_4dp));
        } else {
            float defaultCornerSize = context.getResources().getDimension(R.dimen.rounding_radius_4dp);
            topLeft = typedArray.getDimension(R.styleable.ModernImageView_radiusTopLeft, defaultCornerSize);
            topRight = typedArray.getDimension(R.styleable.ModernImageView_radiusTopRight, defaultCornerSize);
            bottomLeft = typedArray.getDimension(R.styleable.ModernImageView_radiusBottomLeft, defaultCornerSize);
            bottomRight = typedArray.getDimension(R.styleable.ModernImageView_radiusBottomRight, defaultCornerSize);
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
}