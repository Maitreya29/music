package com.radiant.music.utils;

import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.radiant.music.TaskRunner;
import com.radiant.music.helper.MediaArtHelper;
import com.radiant.music.loaders.PlaylistArtLoader;
import com.radiant.music.model.MusicModel;
import com.radiant.music.themes.ThemeColors;
import com.radiant.music.themes.ThemeManagerUtils;

import java.util.List;

public class PlaylistUtil {

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