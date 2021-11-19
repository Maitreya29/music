package com.radiant.music.views;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.radiant.music.R;
import com.radiant.music.themes.ThemeColors;
import com.radiant.music.themes.TintHelper;
import com.radiant.music.utils.DimensionsUtil;

import me.zhanghai.android.fastscroll.FastScrollerBuilder;

public class RadiantScrollerBuilder extends FastScrollerBuilder {

    private final Context mContext;

    public RadiantScrollerBuilder(@NonNull ViewGroup view) {
        super(view);
        mContext = view.getContext();
    }

    public RadiantScrollerBuilder useRadiantPopupStyle() {
        Drawable track = ContextCompat.getDrawable(mContext, R.drawable.fast_scroller_track);
        if (null != track) setTrackDrawable(track);

        Drawable thumb = ContextCompat.getDrawable(mContext, R.drawable.fast_scroller_thumb);
        if (null != thumb) setThumbDrawable(TintHelper.setAccentTintTo(thumb, false));

        setPadding(0, 0, DimensionsUtil.getDimensionPixelSize(mContext, 1), 0);

        setPopupStyle(popupView -> {
            Resources resources = mContext.getResources();
            popupView.setMinimumWidth(resources.getDimensionPixelSize(R.dimen.afs_md2_popup_min_width));
            popupView.setMinimumHeight(resources.getDimensionPixelSize(R.dimen.afs_md2_popup_min_height));
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) popupView.getLayoutParams();
            layoutParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
            layoutParams.setMarginEnd(resources.getDimensionPixelOffset(R.dimen.afs_md2_popup_margin_end));
            popupView.setLayoutParams(layoutParams);

            Drawable background = ContextCompat.getDrawable(mContext, R.drawable.fast_scroller_popup_background);
            if (null != background)
                popupView.setBackground(TintHelper.setAccentTintTo(background, false));

            popupView.setElevation(resources.getDimensionPixelOffset(R.dimen.afs_md2_popup_elevation));
            popupView.setEllipsize(TextUtils.TruncateAt.MIDDLE);
            popupView.setGravity(Gravity.CENTER);
            popupView.setIncludeFontPadding(false);
            popupView.setSingleLine(true);
            popupView.setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimensionPixelSize(R.dimen.afs_md2_popup_text_size));
            popupView.setTextColor(ThemeColors.getCurrentColorOnPrimary());
        });
        return this;
    }
}