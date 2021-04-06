package com.nezukoos.music.glide;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.request.RequestOptions;
import com.nezukoos.music.BuildConfig;

@GlideModule
public final class PulseAppGlideModule extends AppGlideModule {

    @Override
    public void applyOptions(@NonNull Context context, @NonNull GlideBuilder builder) {
        super.applyOptions(context, builder);
        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions.diskCacheStrategy(DiskCacheStrategy.RESOURCE);
        builder.setDefaultRequestOptions(requestOptions);
        if (BuildConfig.DEBUG) builder.setLogLevel(Log.WARN);
        else builder.setLogLevel(Log.ERROR);
    }
}