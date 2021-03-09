package com.hardcodecoder.pulsemusic.helper;

import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hardcodecoder.pulsemusic.TaskRunner;
import com.hardcodecoder.pulsemusic.loaders.PlaylistArtLoader;
import com.hardcodecoder.pulsemusic.model.MusicModel;
import com.hardcodecoder.pulsemusic.themes.ThemeColors;
import com.hardcodecoder.pulsemusic.themes.ThemeManagerUtils;

import java.util.List;

public class PlaylistHelper {

    public static long calculatePlaylistDuration(@NonNull List<MusicModel> list) {
        long duration = 0;
        for (MusicModel md : list) duration += md.getTrackDuration();
        return duration;
    }

    public static void loadPlaylistArtInto(@NonNull ImageView imageView, @NonNull List<MusicModel> list) {
        TaskRunner.executeAsync(new PlaylistArtLoader(imageView.getContext(), list), bitmap -> {
            if (null != bitmap) imageView.setImageBitmap(bitmap);
            else loadDefaultPlaylistArt(imageView, list.get(0));
        });
    }

    public static void loadDefaultPlaylistArt(@NonNull ImageView imageView, @Nullable MusicModel md) {
        int color;
        if (ThemeManagerUtils.isDarkModeEnabled())
            color = ThemeColors.getCurrentColorWindowBackground();
        else color = ThemeColors.getCurrentColorBackgroundHighlight();
        imageView.setBackgroundColor(color);
        imageView.setImageDrawable(MediaArtHelper.getDefaultAlbumArt(
                imageView.getContext(),
                null == md ? -1 : md.getAlbumId()));
    }
}