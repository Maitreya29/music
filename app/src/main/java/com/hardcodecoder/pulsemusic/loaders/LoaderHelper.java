package com.hardcodecoder.pulsemusic.loaders;

import android.content.ContentResolver;
import android.content.Context;

import androidx.annotation.NonNull;

import com.hardcodecoder.pulsemusic.TaskRunner;
import com.hardcodecoder.pulsemusic.TaskRunner.Callback;
import com.hardcodecoder.pulsemusic.model.AlbumModel;
import com.hardcodecoder.pulsemusic.model.ArtistModel;
import com.hardcodecoder.pulsemusic.model.MusicModel;
import com.hardcodecoder.pulsemusic.model.TopAlbumModel;
import com.hardcodecoder.pulsemusic.model.TopArtistModel;
import com.hardcodecoder.pulsemusic.providers.ProviderManager;

import java.util.List;

public class LoaderHelper {

    public static void loadAllTracks(@NonNull Context context, @NonNull Callback<List<MusicModel>> callback) {
        // Since we are loading the master list
        // Any previous cached tracks must be made invalid
        LoaderCache.releaseCache();
        TaskRunner.executeAsync(new LibraryLoader(context, SortOrder.TITLE_ASC), result -> {
            LoaderCache.setAllTracksList(result);
            callback.onComplete(LoaderCache.getAllTracksList());
        });
    }

    public static void loadAlbumsList(@NonNull ContentResolver contentResolver, SortOrder.ALBUMS sortOrder, @NonNull Callback<List<AlbumModel>> callback) {
        TaskRunner.executeAsync(new AlbumsLoader(contentResolver, sortOrder), callback);
    }

    public static void loadArtistsList(@NonNull ContentResolver contentResolver, SortOrder.ARTIST sortOrder, @NonNull Callback<List<ArtistModel>> callback) {
        TaskRunner.executeAsync(new ArtistsLoader(contentResolver, sortOrder), callback);
    }

    public static void loadSuggestionsList(@NonNull Callback<List<MusicModel>> callback) {
        if (null != LoaderCache.getSuggestions())
            callback.onComplete(LoaderCache.getSuggestions());
        else TaskRunner.executeAsync(new SuggestionsLoader(), callback);
    }

    public static void loadRecentTracks(@NonNull Callback<List<MusicModel>> callback) {
        ProviderManager.getHistoryProvider().getHistoryTracks(callback);
    }

    public static void loadLatestTracks(@NonNull Callback<List<MusicModel>> callback) {
        if (null != LoaderCache.getLatestTracks())
            callback.onComplete(LoaderCache.getLatestTracks());
        else TaskRunner.executeAsync(new LatestTracksLoader(), callback);
    }

    public static void loadTopAlbums(@NonNull Callback<List<TopAlbumModel>> callback) {
        TaskRunner.executeAsync(new TopAlbumsLoader(), callback);
    }

    public static void loadTopArtist(@NonNull Callback<List<TopArtistModel>> callback) {
        TaskRunner.executeAsync(new TopArtistsLoader(), callback);
    }

    public static void loadAlbumTracks(@NonNull Context context,
                                       @NonNull SortOrder sortOrder,
                                       long albumId,
                                       @NonNull Callback<List<MusicModel>> callback) {
        TaskRunner.executeAsync(new AlbumTracksLoader(context, sortOrder, albumId), callback);
    }

    public static void loadArtistAlbums(@NonNull ContentResolver contentResolver,
                                        @NonNull String artistTitle,
                                        @NonNull SortOrder.ALBUMS sortOrder,
                                        @NonNull Callback<List<AlbumModel>> callback) {
        TaskRunner.executeAsync(new ArtistAlbumsLoader(contentResolver, artistTitle, sortOrder), callback);
    }
}